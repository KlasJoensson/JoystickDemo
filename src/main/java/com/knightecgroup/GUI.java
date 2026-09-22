package com.knightecgroup;

import javax.swing.*;

public class GUI {
    private JFrame mainFrame;
    private JLabel headerLabel;
    private JLabel scoreLabel;
    private Board gameBoard;
    private boolean gameOn = false;

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

        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);
    }

    private void startGame() {
        if (gameBoard == null) {
            gameBoard = new Board(this);
            mainFrame.add(gameBoard);
        } else {
            gameBoard.initBoard();
            mainFrame.repaint();
        }
        mainFrame.setVisible(true);
    }

    public void disconnected() {
        gameOn = false;
        headerLabel.setText("Disconnected...");
    }

    public void updateGUI(boolean isRunning){
        gameOn = isRunning;
        if (gameOn) {
            scoreLabel.setText("Score: " + gameBoard.getScore());
        } else {
            headerLabel.setText("GAME OVER!!! Press fire to start...");
        }
    }

    public void controlGame(int mode) {
        if (gameOn) {
            gameBoard.moveSpaceShip(mode);
        } else {
            if (mode == 42) {
                headerLabel.setText("Ready! Press fire to start...");
                scoreLabel.setText("Score: 0");
            }
            if (mode == 16) {
                headerLabel.setText("");
                scoreLabel.setText("Score: 0");
                gameOn = true;
                startGame();
            }
        }

    }

}


