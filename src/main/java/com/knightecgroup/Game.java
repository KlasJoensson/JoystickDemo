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
import java.util.logging.Logger;

public class Game implements KeyListener {

    private final Logger log = Logger.getLogger(Game.class.getName());

    private boolean gameOn = false;
    private boolean keyboardControl = false;
    private boolean writeName = false;
    private final Timer timer;
    private HashMap<String, Integer> highScores;
    private ArrayList<String> lowerCase;
    private ArrayList<String> upperCase;
    private ArrayList<String> numbers;
    private final String storedHighScore = "src/main/resources/highscores.txt";
    private static Game thisGame;
    private static GUI myGUI;


    /* Init and creation methods for the game */

    public static Game initGame() {
        myGUI = GUI.initGUI();
        if (thisGame == null) {
            thisGame = new Game();
        }

        return thisGame;
    }

    private Game() {
        ActionListener stopTimer = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timer.stop();
            }
        };

        timer = new Timer(250, stopTimer);
        if (highScores == null) {
            highScores = initHighScores();
        }
        highScores = sortByValue(highScores);
        initLetterArrays();
    }

    /* Helper methods used to init the game */

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
                log.info("High scores loaded from file");
            } catch (IOException e) {
                log.severe("Error reading high scores.txt file: " + e.getMessage());
            }
        } else {
            try {
                if (!file.createNewFile()) {
                    log.severe("Error creating high scores.txt: File already exists...");
                } else {
                    log.info("High scores file has been created.");
                }
            } catch (IOException e) {
                log.severe("Error creating high scores.txt file: " + e.getMessage());
            }
        }

        return highScores;
    }

    private void initLetterArrays() {
        upperCase = new ArrayList<>();
        for (char l = 'A'; l <= 'Z'; l++) { upperCase.add(String.valueOf(l)); }
        upperCase.add("<-");
        upperCase.add("End");

        lowerCase = new ArrayList<>();
        for (char l = 'a'; l <= 'z'; l++) { lowerCase.add(String.valueOf(l)); }
        lowerCase.add("<-");
        lowerCase.add("End");

        numbers = new ArrayList<>();
        for (int i = 0; i < 10; i++) { numbers.add(String.valueOf(i)); }
        numbers.add("<-");
        numbers.add("End");

        myGUI.setLetters(upperCase);
    }


    /* Methods for controlling the game */

    public void disconnected() {
        gameOn = false;
        myGUI.updateHeaderText("Disconnected...");
        log.severe("Got disconnected for Bluetooth device");
    }

    public void update(int gameStatus, int score){
        if (gameStatus == 0) {
            gameOn = true;
            myGUI.updateScoreLabel(score);
        } else if (gameStatus == -1) {
            gameOn = false;
            myGUI.gameOver();
            int minHighScore = highScores.values().stream().min(Comparator.naturalOrder()).orElse(0);
            if (minHighScore < score) {
                writeName = true;
                myGUI.writeNewName();
            } else {
                myGUI.showLeaderBoard(highScores);
            }
        } else if (gameStatus == 1) {
            myGUI.showLeaderBoard(highScores);
            if (timer.isRunning()) {
                timer.restart();
            } else {
                timer.start();
            }
        }
    }

    public void addNewPlayer(String name, int score) {
        log.info("Adding player " + name + " with score " + score);
        highScores = sortByValue(highScores);
        highScores.remove(highScores.entrySet().stream().toList().getLast().getKey());
        highScores.put(name, score);
        updateHighScoreFile();
    }

    private void updateHighScoreFile() {
        try (FileWriter newHighScores = new FileWriter(storedHighScore)) {
            for (Map.Entry<String, Integer> entry : highScores.entrySet()) {
                newHighScores.write(entry.getKey() + "," + entry.getValue().toString() + "\n");
                newHighScores.flush();
            }
            log.info("High scores file has been updated.");
        } catch (IOException e) {
            log.severe("Can't write to file: " + e.getMessage());
        }
    }

    private static HashMap<String, Integer> sortByValue(HashMap<String, Integer> hm) {
        List<Map.Entry<String, Integer>> list =
                new LinkedList<>(hm.entrySet());

        list.sort(Map.Entry.comparingByValue());

        HashMap<String, Integer> temp = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> aa : list.reversed()) {
            temp.put(aa.getKey(), aa.getValue());
        }

        return temp;
    }

    public void controlGame(int mode) {
        if (gameOn) {
            myGUI.controlSpaceship(mode);
        } else if (writeName) {
            if (keyboardControl || !timer.isRunning()) {
                timer.start();
                switch (mode) {
                    case 4:
                        myGUI.updateNamePanel(-1);
                        break;
                    case 8:
                        myGUI.updateNamePanel(1);
                        break;
                    case 1:
                        switch (myGUI.getFirstLetter()) {
                            case "A" -> {
                                myGUI.setLetters(lowerCase);
                                myGUI.updateNamePanel(0);
                            }
                            case "a" -> {
                                myGUI.setLetters(numbers);
                                myGUI.updateNamePanel(0);
                            }
                            case "0" -> {
                                myGUI.setLetters(upperCase);
                                myGUI.updateNamePanel(0);
                            }
                        }
                        break;
                    case 2:
                        switch (myGUI.getFirstLetter()) {
                            case "A" -> {
                                myGUI.setLetters(numbers);
                                myGUI.updateNamePanel(0);
                            }
                            case "a" -> {
                                myGUI.setLetters(upperCase);
                                myGUI.updateNamePanel(0);
                            }
                            case "0" -> {
                                myGUI.setLetters(lowerCase);
                                myGUI.updateNamePanel(0);
                            }
                        }
                        break;
                    case 16:
                        int res = myGUI.updatePlayerName(thisGame);
                        if (res==0) {
                            writeName = false;
                            update(1,0);
                        }
                        break;
                    default:
                        break;
                }
            }
        } else {
            if (mode == 42) {
                myGUI.updateHeaderText("Ready! Press fire to start...");
            }
            if (mode == 21) {
                myGUI.createWindow(highScores);
                myGUI.updateHeaderText("Ready! Press fire to start...");
                myGUI.addKeyListener(thisGame);
                keyboardControl = true;
            }
            if (mode == 22) {
                myGUI.createWindow(highScores);
            }
            if (mode == 16 && !timer.isRunning()) {
                timer.start();
                myGUI.updateHeaderText("");
                myGUI.updateScoreLabel(0);
                gameOn = true;
                myGUI.startGame(thisGame);
            }
        }
    }


    /* Methods for controlling the game with the keyboard */

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


