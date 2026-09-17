package org.example;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GUI {
    private JFrame mainFrame;
    private JPanel controlPanel;
    private JLabel headerLabel;
    private JLabel joystickLabel;

    public void createWindow() {
        mainFrame = new JFrame("Joystick demo");
        mainFrame.setSize(400,400);
        mainFrame.setLayout(new GridLayout(2, 1));
        headerLabel = new JLabel("Connecting...",JLabel.CENTER );

        controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());
        joystickLabel = new JLabel("",JLabel.CENTER );
        controlPanel.add(joystickLabel);

        mainFrame.add(headerLabel);
        mainFrame.add(controlPanel);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);
    }

    public void updateLabel(int movement) {
        if (joystickLabel == null) {
            return;
        }

        switch (movement) {
            case 1:
                joystickLabel.setText("Stick up!");
                break;
            case 2:
                joystickLabel.setText("Stick down!");
                break;
            case 4:
                joystickLabel.setText("Stick left!");
                break;
            case 8:
                joystickLabel.setText("Stick Right!");
                break;
            case 16:
                joystickLabel.setText("Fire!");
                break;
            case 42:
                headerLabel.setText("Move the Joystick!");
                break;
            case 2042:
                joystickLabel.setText("Button 0 on HW pressed!");
                break;
            default:
                IO.println("WTF! (got: " + movement + ")");

        }
    }

}


