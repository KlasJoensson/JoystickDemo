package com.knightecgroup;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Board extends JPanel implements ActionListener {

    private Timer timer;
    private SpaceShip spaceShip;
    private Bar bar;
    private final int DELAY = 10;
    private final int WIDTH = 1000;
    private final int HEIGHT = 750;
    private int barSpeed = 10;
    private int barTick = 0;
    private Graphics2D g2d;

    public Board() {
        initBoard();
    }

    private void initBoard() {
        setBackground(Color.black);
        setFocusable(true);
        setSize(WIDTH, HEIGHT);
        spaceShip = new SpaceShip(WIDTH, HEIGHT);
        bar = new Bar(WIDTH, HEIGHT);
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

        g2d = (Graphics2D) g;

        g2d.drawImage(spaceShip.getImage(), spaceShip.getX(),
                spaceShip.getY(), this);

        drawBar();
    }

    private void drawBar() {

        for (int i = 0; i <= bar.getNumberOfBarParts(); i++) {
            Image im = bar.getBarPart(i);
            if (im != null) {
                g2d.drawImage(im, bar.getWidth() * i, bar.getY(), this);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        step();
        if (bar != null) {
            if (barTick % barSpeed == 0) {
                moveBar();
            }
            barTick++;
        }
    }

    private void step() {
        repaint(0, bar.getY() - bar.getHeight(), WIDTH, bar.getHeight() * 2);
        repaint(spaceShip.getX()-30, spaceShip.getY()-30,
                spaceShip.getWidth()+50, spaceShip.getHeight()+50);

    }

    private void moveBar() {
        bar.move();
        if (collisionWithBar()) {
            IO.println("CRACH!");
            bar = new Bar(WIDTH, HEIGHT);
        } else if (bar.hasPastedBoard()) {
            IO.println("Clear for next bar");
            bar = new Bar(WIDTH, HEIGHT);
            drawBar();
        }
    }

    private boolean collisionWithBar() {
        if (spaceShip.getY() < bar.getY()+bar.getHeight() || spaceShip.getY() + spaceShip.getHeight() < bar.getY()) {
            return spaceShip.getX()-spaceShip.getWidth() < bar.getHoleStart() || spaceShip.getX() > bar.getHoleEnd();
        }
        return false;
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
