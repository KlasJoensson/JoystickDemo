package com.knightecgroup;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GUI {
    private JFrame mainFrame;
    private JLabel headerLabel;
    private Board gameBoard;

    public void createWindow() {
        mainFrame = new JFrame("Joystick demo");
        mainFrame.setSize(1000,800);
        BoxLayout boxLayout = new BoxLayout(mainFrame.getContentPane(), BoxLayout.Y_AXIS);
        mainFrame.setLayout(boxLayout);
        headerLabel = new JLabel("Connecting...",JLabel.CENTER );

        mainFrame.add(headerLabel);

        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);
    }

    private void startGame() {
        gameBoard = new Board();
        mainFrame.add(gameBoard);
        mainFrame.setVisible(true);
    }

    public void moveSpaceShip(int mode) {
        if (mode == 42) {
            headerLabel.setText("Ready!");
            startGame();
        } else {
            gameBoard.moveSpaceShip(mode);
        }
    }

}


