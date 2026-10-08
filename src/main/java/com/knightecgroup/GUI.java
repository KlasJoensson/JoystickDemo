package com.knightecgroup;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class GUI {

    private final Logger log = Logger.getLogger(GUI.class.getName());

    private static GUI me;

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

    private ArrayList<String> letters;
    private int namePointer;

    public static GUI initGUI() {
        if (me == null) {
            me = new GUI();
        }
        return me;
    }

    private GUI() {}

    /* Methods to create the GUI */

    public void createWindow(HashMap<String, Integer> highScores) {
        mainFrame = new JFrame("Joystick demo");
        mainFrame.setSize(1000,800);
        BoxLayout boxLayout = new BoxLayout(mainFrame.getContentPane(), BoxLayout.Y_AXIS);
        mainFrame.setLayout(boxLayout);

        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        headerLabel = new JLabel("Connecting...");
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        scoreLabel = new JLabel("");
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusPanel.add(headerLabel);
        statusPanel.add(scoreLabel);

        mainFrame.add(statusPanel);
        scorePanel = createLeaderBoard(highScores);
        mainFrame.add(scorePanel);

        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);
        log.info("Main window created and showed");
    }

    private JPanel createLeaderBoard(HashMap<String, Integer> highScores) {
        JPanel board = new JPanel();
        board.setLayout(new GridLayout(3, 1));
        JLabel leaderBoardLabel = new JLabel("High scores",JLabel.CENTER);
        leaderBoardLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        leaderBoardLabel.setFont(new Font("Consolas", Font.BOLD, 50));
        board.add(leaderBoardLabel);

        JPanel tableWrapper = getTableWrapper(highScores);
        board.add(tableWrapper);
        board.add(new JLabel(" ", JLabel.CENTER));

        log.info("New high score board created");

        return board;
    }

    private JPanel getTableWrapper(HashMap<String, Integer> highScores) {
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

    private JPanel createNamePanel() {
        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
        namePanel.add(new JLabel("Write your name", JLabel.CENTER));
        playerNameLabel = new JLabel("    ", JLabel.CENTER);
        playerNameLabel.setFont(new Font("Consolas", Font.PLAIN, 25));
        namePanel.add(playerNameLabel);
        JPanel letterPanel = new JPanel();
        letterPanel.setLayout(new BoxLayout(letterPanel, BoxLayout.X_AXIS));

        preLetter = new JLabel("", JLabel.CENTER);
        preLetter.setFont(new Font("Consolas", Font.PLAIN, 15));
        letterPanel.add(preLetter);

        letter = new JLabel("", JLabel.CENTER);
        letter.setFont(new Font("Consolas", Font.PLAIN, 25));
        letterPanel.add(letter);

        nextLetter = new JLabel("", JLabel.CENTER);
        nextLetter.setFont(new Font("Consolas", Font.PLAIN, 15));
        letterPanel.add(nextLetter);

        namePointer = letters.size()-1;

        updateNamePanel(0);

        namePanel.add(letterPanel);

        log.info("New name panel created");

        return namePanel;
    }

    /* Setters and update methods */

    public void setLetters(ArrayList<String> letters) {
        this.letters = letters;
    }

    public void updateHeaderText(String text) {
        headerLabel.setText(text);
    }

    public void updateScoreLabel(int score) {
        scoreLabel.setText("Score: " + score);
    }

    public void updateNamePanel(int move) {
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

    public int updatePlayerName(Game mygame) {
        if (letters.get(namePointer).strip().equals("End")) {
            mygame.addNewPlayer(playerNameLabel.getText().strip(), gameBoard.getScore());
            return 0;
        } else {
            String name = playerNameLabel.getText();
            name += letters.get(namePointer).strip();
            playerNameLabel.setText(name);
            return 1;
        }
    }

    public void addKeyListener(KeyListener kl) {
        mainFrame.addKeyListener(kl);
    }

    public String getFirstLetter() {
        return letters.getFirst();
    }


    /* Methods to control the GUI */

    public void showLeaderBoard(HashMap<String, Integer> highScores) {
        headerLabel.setText("GAME OVER!!! Press fire to start...");
        scorePanel = createLeaderBoard(highScores);
        if (namePanel != null) {
            mainFrame.remove(namePanel);
            namePanel = null;
        }
        if (gameBoard != null) {
            mainFrame.remove(gameBoard);
            gameBoard = null;
        }
        mainFrame.add(scorePanel);
        mainFrame.repaint();
    }

    public void gameOver() {
        mainFrame.remove(gameBoard);
        headerLabel.setText("GAME OVER!!!");
    }


    public void writeNewName() {
        namePanel = createNamePanel();
        mainFrame.add(namePanel);
        mainFrame.repaint();
    }

    public void controlSpaceship(int mode) {
        gameBoard.moveSpaceShip(mode);
    }


    /* Here starts the game */

    public void startGame(Game myGame) {
        mainFrame.remove(scorePanel);
        if (gameBoard == null) {
            gameBoard = new Board(myGame);
        } else {
            gameBoard.initBoard();
        }
        mainFrame.add(gameBoard);
        mainFrame.repaint();
        mainFrame.setVisible(true);

        log.info("Game on");
    }

}
