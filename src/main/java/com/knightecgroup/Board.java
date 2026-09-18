package com.knightecgroup;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Board extends JPanel implements ActionListener {

    private Timer timer;
    private SpaceShip spaceShip;
    private final int DELAY = 10;

    public Board() {
        initBoard();
    }

    private void initBoard() {

        setBackground(Color.black);
        setFocusable(true);
        setSize(1000, 750);
        spaceShip = new SpaceShip();

        timer = new Timer(DELAY, this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        doDrawing(g);

        Toolkit.getDefaultToolkit().sync();
    }

    private void doDrawing(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;

        g2d.drawImage(spaceShip.getImage(), spaceShip.getX(),
                spaceShip.getY(), this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        step();
    }

    private void step() {
        repaint(spaceShip.getX()-30, spaceShip.getY()-30,
                spaceShip.getWidth()+50, spaceShip.getHeight()+50);
    }

    public void moveSpaceShip(int input) {
        switch (input) {
            case 1:
                spaceShip.moveUp();
                break;
            case 2:
                spaceShip.moveDown();
                break;
            case 4:
                spaceShip.moveLeft();
                break;
            case 8:
                spaceShip.moveRight();
                break;
            case 16:
                // Fire! (not implemented yet)
                break;
            default:
                // Do nothing...
                break;
        }
    }
}
