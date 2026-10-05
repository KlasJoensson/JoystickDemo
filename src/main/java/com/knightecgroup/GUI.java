package com.knightecgroup;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.util.*;
import java.util.List;

public class GUI implements KeyListener {
    private JFrame mainFrame;
    private JLabel headerLabel;
    private JLabel playerNameLabel;
    private JLabel preLetter;
    private JLabel letter;
    private JLabel nextLetter;
    private JLabel scoreLabel;
    private JPanel scorePanel;
    private JPanel namePanel;
    private Board gameBoard;
    private boolean gameOn = false;
    private boolean keyboardControl = false;
    private boolean writeName = false;
    private int namePointer;
    private Timer timer;
    private HashMap<String, Integer> highScores;
    private ArrayList<String> letters;
    private ArrayList<String> lowerCase;
    private ArrayList<String> upperCase;
    private ArrayList<String> numbers;
    private final String storedHighScore = "src/main/resources/highscores.txt";

    public void createWindow() {
        mainFrame = new JFrame("Joystick demo");
        mainFrame.setSize(1000,800);
        BoxLayout boxLayout = new BoxLayout(mainFrame.getContentPane(), BoxLayout.Y_AXIS);
        mainFrame.setLayout(boxLayout);

        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
        headerLabel = new JLabel("Connecting...",JLabel.CENTER );
        scoreLabel = new JLabel("",JLabel.RIGHT);
        statusPanel.add(headerLabel);
        statusPanel.add(scoreLabel);

        mainFrame.add(statusPanel);
        scorePanel = createLeaderBoard();
        mainFrame.add(scorePanel);

        timer = new Timer(250, stopTimer);

        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);
    }

    private final ActionListener stopTimer = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            timer.stop();
        }
    };

    private JPanel createLeaderBoard() {
        JPanel board = new JPanel();
        board.setLayout(new GridLayout(3, 1));
        JLabel leaderBoardLabel = new JLabel("High scores",JLabel.CENTER);
        leaderBoardLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        leaderBoardLabel.setFont(new Font("Consolas", Font.BOLD, 50));
        board.add(leaderBoardLabel);

        if (highScores == null) {
            highScores = initHighScores();
        }
        highScores = sortByValue(highScores);

        JPanel tableWrapper = getTableWrapper();
        board.add(tableWrapper);
        board.add(new JLabel(" ", JLabel.CENTER));


        return board;
    }

    private JPanel getTableWrapper() {
        JPanel tablePanel = new JPanel(new GridLayout(highScores.size(), 2));
        for (Map.Entry<String, Integer> entry : highScores.entrySet()) {
            JLabel nameLabel = new JLabel(entry.getKey(), JLabel.LEFT);
            nameLabel.setFont(new Font("Consolas", Font.PLAIN, 25));
            tablePanel.add(nameLabel);

            JLabel scoreValueLabel = new JLabel(entry.getValue().toString(), JLabel.RIGHT);
            scoreValueLabel.setFont(new Font("Consolas", Font.PLAIN, 25));
            tablePanel.add(scoreValueLabel);
        }
        JPanel tableWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        tableWrapper.add(tablePanel);
        return tableWrapper;
    }

    private HashMap<String, Integer> initHighScores() {
        HashMap <String, Integer> highScores = new HashMap<>();
        File file = new File(storedHighScore);
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    highScores.put(parts[0], Integer.parseInt(parts[1]));
                }
            } catch (IOException e) {
                System.err.println("Error reading high scores.txt file: " + e.getMessage());
            }
        } else {
            try {
                if (!file.createNewFile()) {
                    System.err.println("Error creating high scores.txt: File already exists...");
                }
            } catch (IOException e) {
                System.err.println("Error creating high scores.txt file: " + e.getMessage());
            }
        }

        upperCase = new ArrayList<>();
        for (char l = 'A'; l <= 'Z'; l++) { upperCase.add(String.valueOf(l)); }
        upperCase.add("End");

        lowerCase = new ArrayList<>();
        for (char l = 'a'; l <= 'z'; l++) { lowerCase.add(String.valueOf(l)); }
        lowerCase.add("End");

        numbers = new ArrayList<>();
        for (int i = 0; i < 10; i++) { numbers.add(String.valueOf(i)); }
        numbers.add("End");

        letters = upperCase;

        return highScores;
    }

    private void startGame() {
        mainFrame.remove(scorePanel);
        if (gameBoard == null) {
            gameBoard = new Board(this);
        } else {
            gameBoard.initBoard();
        }
        mainFrame.add(gameBoard);
        mainFrame.repaint();
        mainFrame.setVisible(true);
    }

    public void disconnected() {
        gameOn = false;
        headerLabel.setText("Disconnected...");
    }

    public void updateGUI(int gameStatus){
        if (gameStatus == 0) {
            gameOn = true;
            scoreLabel.setText("Score: " + gameBoard.getScore());
        } else if (gameStatus == -1) {
            gameOn = false;
            mainFrame.remove(gameBoard);
            headerLabel.setText("GAME OVER!!!");
            namePanel = createNamePanel();
            int minHighScore = highScores.values().stream().min(Comparator.naturalOrder()).orElse(0);
            if (minHighScore < gameBoard.getScore()) {
                writeName = true;
                highScores = sortByValue(highScores);
                highScores.remove(highScores.entrySet().stream().toList().getLast().getKey());
                mainFrame.add(namePanel);
                mainFrame.repaint();
            } else {
                if (!writeName) {
                    headerLabel.setText("GAME OVER!!! Press fire to start...");
                    scorePanel = createLeaderBoard();
                    mainFrame.remove(namePanel);
                    mainFrame.add(scorePanel);
                }
            }
        } else if (gameStatus == 1) {
            headerLabel.setText("GAME OVER!!! Press fire to start...");
            scorePanel = createLeaderBoard();
            mainFrame.remove(namePanel);
            mainFrame.add(scorePanel);
        }
    }

    private JPanel createNamePanel() {
        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
        namePanel.add(new JLabel("Write your name", JLabel.CENTER));
        playerNameLabel = new JLabel("    ", JLabel.CENTER);
        playerNameLabel.setFont(new Font("Consolas", Font.PLAIN, 25));
        namePanel.add(playerNameLabel);
        JPanel letterPanel = new JPanel();
        letterPanel.setLayout(new BoxLayout(letterPanel, BoxLayout.X_AXIS));

        namePointer = letters.size()-1;

        preLetter = new JLabel("", JLabel.CENTER);
        preLetter.setFont(new Font("Consolas", Font.PLAIN, 15));
        letterPanel.add(preLetter);

        letter = new JLabel("", JLabel.CENTER);
        letter.setFont(new Font("Consolas", Font.PLAIN, 25));
        letterPanel.add(letter);

        nextLetter = new JLabel("", JLabel.CENTER);
        nextLetter.setFont(new Font("Consolas", Font.PLAIN, 15));
        letterPanel.add(nextLetter);

        updateNamePanel(0);

        namePanel.add(letterPanel);

        return namePanel;
    }

    private void updateNamePanel(int move) {
        namePointer += move;
        if (namePointer >= letters.size()) {
            namePointer = 0;
        }
        if (namePointer < 0) {
            namePointer = letters.size()-1;
        }

        if (namePointer-1 < 0) {
            preLetter.setText(letters.getLast());
        } else {
            preLetter.setText(letters.get(namePointer-1));
        }

        letter.setText("  "+letters.get(namePointer)+"   ");

        if (namePointer+1 >= letters.size()) {
            nextLetter.setText(letters.getFirst());
        } else {
            nextLetter.setText(letters.get(namePointer+1));
        }
    }

    private void updatePlayerName() {
        if (letters.get(namePointer).strip().equals("End")) {
            writeName = false;
            highScores.put(playerNameLabel.getText().strip(), gameBoard.getScore());
            updateHighScoreFile();
            updateGUI(1);
        } else {
            String name = playerNameLabel.getText();
            name += letters.get(namePointer).strip();
            playerNameLabel.setText(name);
        }
    }

    private void updateHighScoreFile() {
        try (FileWriter newHighScores = new FileWriter(storedHighScore)) {
            for (Map.Entry<String, Integer> entry : highScores.entrySet()) {
                newHighScores.write(entry.getKey() + "," + entry.getValue().toString() + "\n");
                newHighScores.flush();
            }
        } catch (IOException e) {
            System.err.println("Can't write to file: " + e.getMessage());
        }
    }

    private static HashMap<String, Integer> sortByValue(HashMap<String, Integer> hm) {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Integer>> list =
                new LinkedList<>(hm.entrySet());

        // Sort the list
        list.sort(Map.Entry.comparingByValue());

        // put data from sorted list to hashmap
        HashMap<String, Integer> temp = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> aa : list.reversed()) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    public void controlGame(int mode) {
        if (gameOn) {
            gameBoard.moveSpaceShip(mode);
        } else if (writeName) {
            if (keyboardControl || !timer.isRunning()) {
                timer.start();
                switch (mode) {
                    case 4:
                        updateNamePanel(-1);
                        break;
                    case 8:
                        updateNamePanel(1);
                        break;
                    case 1:
                        switch (letters.getFirst()) {
                            case "A" -> {
                                letters = lowerCase;
                                updateNamePanel(0);
                            }
                            case "a" -> {
                                letters = numbers;
                                updateNamePanel(0);
                            }
                            case "0" -> {
                                letters = upperCase;
                                updateNamePanel(0);
                            }
                        }
                        break;
                    case 2:
                        switch (letters.getFirst()) {
                            case "A" -> {
                                letters = numbers;
                                updateNamePanel(0);
                            }
                            case "a" -> {
                                letters = upperCase;
                                updateNamePanel(0);
                            }
                            case "0" -> {
                                letters = lowerCase;
                                updateNamePanel(0);
                            }
                        }
                        break;
                    case 16:
                        updatePlayerName();
                        break;
                    default:
                        break;
                }
            }
        } else {
            if (mode == 42) {
                headerLabel.setText("Ready! Press fire to start...");
                scoreLabel.setText("Score: 0");
            }
            if (mode == 21) {
                headerLabel.setText("Ready! Press space to start...");
                scoreLabel.setText("Score: 0");
                mainFrame.addKeyListener(this);
                keyboardControl = true;
            }
            if (mode == 16 && !timer.isRunning()) {
                timer.start();
                headerLabel.setText("");
                scoreLabel.setText("Score: 0");
                gameOn = true;
                startGame();
            }
        }

    }
    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (keyboardControl) {
            char key = e.getKeyChar();
            switch (key) {
                case 'a':
                case 'A':
                    controlGame(4);
                    break;
                case 'l':
                case 'L':
                    controlGame(8);
                    break;
                case 't':
                case 'T':
                    controlGame(1);
                    break;
                case 'b':
                case 'B':
                    controlGame(2);
                    break;
                case ' ':
                    controlGame(16);
                    break;
                default:
                    break;

            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}


