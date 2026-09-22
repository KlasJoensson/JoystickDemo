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
    private int barSpeed;
    private Graphics2D g2d;
    private int score = 0;
    private int barTick;
    private boolean onGame;
    private GUI myGUI;

    public Board(GUI gui) {
        setBackground(Color.black);
        setFocusable(true);
        setSize(WIDTH, HEIGHT);
        myGUI = gui;

        initBoard();
    }

    public void initBoard() {
        spaceShip = new SpaceShip(WIDTH, HEIGHT);
        bar = new Bar(WIDTH, HEIGHT);
        if (timer != null) {
            timer.stop();
        }
        timer = new Timer(DELAY, this);
        onGame = true;
        score = 0;
        barSpeed = 10;
        barTick = 0;
        timer.start();
        IO.println("Starting the game. Score = " + score + " Speed = " + barSpeed+" Tick = "+barTick);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (onGame) {
            doDrawing(g);
        }

        Toolkit.getDefaultToolkit().sync();
    }

    private void doDrawing(Graphics g) {

        g2d = (Graphics2D) g;

        g2d.drawImage(spaceShip.getImage(), spaceShip.getX(), spaceShip.getY(), this);

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
        if (onGame) {
            step();
            if (barTick % barSpeed == 0) {
                moveBar();
            }
            barTick++;

            if (barTick>1000 && bar.getY()<50) {
                IO.println("Bartick running... "+barTick+" Bar at: "+bar.getY());
            }
        }
    }

    private void step() {
        myGUI.updateGUI(onGame);
        repaint(spaceShip.getX()-30, spaceShip.getY()-30,
                spaceShip.getWidth()+50, spaceShip.getHeight()+50);

    }

    private void moveBar() {
        bar.move();
        repaint(0, bar.getY() - bar.getHeight(), WIDTH, bar.getHeight() * 2);
        if (collisionWithBar()) {
            IO.println("CRACH! Score = " + score + " Speed = " + barSpeed+" Tick = "+barTick);
            onGame = false;
            barTick = 0;
            bar = null;
            myGUI.updateGUI(onGame);
        } else if (bar.hasPastedBoard()) {
            score += 10;
            if ((score/10)%3 == 0 && barSpeed>5) {
                barSpeed--;
                IO.println("Updating bar speed: "+barSpeed);
            }
            IO.println("Clear for next bar. Score = " + score + " Speed = " + barSpeed+" Tick = "+barTick);
            bar = new Bar(WIDTH, HEIGHT);
            barTick = 0;
            drawBar();
        }
    }

    public int getScore() {
        return score;
    }

    private boolean collisionWithBar() {
        if (spaceShip.getY() < bar.getY()+bar.getHeight() || spaceShip.getY() + spaceShip.getHeight() < bar.getY()) {
            return spaceShip.getX()-spaceShip.getWidth() < bar.getHoleStart() || spaceShip.getX() > bar.getHoleEnd();
        }
        return false;
    }

    public boolean moveSpaceShip(int input) {
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
                if (!onGame) {
                    IO.println("Starting next game. Score = " + score + " Speed = " + barSpeed+" Tick = "+barTick);
                    initBoard();
                }
                break;
            default:
                // Do nothing...
                break;
        }
        return onGame;
    }
}
