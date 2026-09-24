package com.knightecgroup;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Board extends JPanel implements ActionListener {

    private Timer timer;
    private SpaceShip spaceShip;
    private Bar bar;
    //private Monster monster;
    private final int DELAY = 10;
    private final int WIDTH = 1000;
    private final int HEIGHT = 750;
    private int barSpeed;
    private Graphics2D g2d;
    private int score = 0;
    private int barTick;
    private boolean onGame;
    private boolean monstersOn;
    private ArrayList<Monster> monsters;
    private int numberOfMonsters;
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
            timer.restart();
        } else {
            timer = new Timer(DELAY, this);
            timer.start();
        }
        onGame = true;
        monstersOn = false;
        score = 0;
        barSpeed = 10;
        barTick = 0;
        monsters = new ArrayList<>();
        numberOfMonsters = 2;
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

        if (monstersOn) {
           drawMonster();
        }
    }

    private void drawBar() {

        for (int i = 0; i <= bar.getNumberOfBarParts(); i++) {
            Image im = bar.getBarPart(i);
            if (im != null) {
                g2d.drawImage(im, bar.getWidth() * i, bar.getY(), this);
            }
        }
    }

    private void drawMonster() {
        for (Monster monster : monsters) {
            if (monster != null) {
                g2d.drawImage(monster.getImage(), monster.getX(), monster.getY(), this);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (onGame) {
            step();
            if (barTick % barSpeed == 0) {
                moveBar();
                if (monstersOn && !monsters.isEmpty()) {
                    moveMonster();
                }
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
            collision();
        } else if(monstersOn && collisionWithMonsters()) {
            collision();
        } else if (bar.hasPastedBoard()) {
            score += 10;
            if ((score/10)%3 == 0 && barSpeed>5) {
                barSpeed--;
                IO.println("Updating bar speed: "+barSpeed);
            }
            // Let's add a monster if the player past the first bar...
            if (score == 10) {
                monstersOn = true;
            }
            if (monstersOn) {
                if ((score / 10) % 2 == 0 && numberOfMonsters <= monsters.getFirst().getMaxNumberOfMonsters()/2) {
                    numberOfMonsters++;
                    IO.println("Adding a on more monster: "+  numberOfMonsters+"/"+monsters.getFirst().getMaxNumberOfMonsters()/2);
                }
            }
            IO.println("Clear for next bar. Score = " + score + " Speed = " + barSpeed+" Tick = "+barTick);
            bar = new Bar(WIDTH, HEIGHT);
            if (monstersOn) {
                createMonsters();
            }
            barTick = 0;
            drawBar();
        }
    }

    private void collision() {
        IO.println("CRACH!");
        onGame = false;
        timer.stop();
        barTick = 0;
        bar = null;
        myGUI.updateGUI(onGame);
    }

    private void moveMonster() {
        for (Monster monster : monsters) {
            monster.move();
            repaint(monster.getX(), monster.getY() - 20, monster.getWidth(), monster.getHeight());

            /*if (monster.hasPastedBoard()) {
                monsters.remove(monster);
            }*/
            if (monsters.isEmpty()) {
                createMonsters();
            }
        }
    }

    private void createMonsters() {
        monsters.clear();

        for (int i = 0; i < numberOfMonsters; i++) {
            Monster monster = new Monster(WIDTH, HEIGHT);
            monsters.add(monster);
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

    private boolean collisionWithMonsters() {
        boolean collision = false;
        Rectangle shipBounds = new Rectangle(spaceShip.getX(), spaceShip.getY(),
                spaceShip.getWidth(), spaceShip.getHeight());
        for (Monster monster : monsters) {
            if (monster == null) {
                return false;
            }
            Rectangle monsterBounds = new Rectangle(monster.getX(), monster.getY(),
                    monster.getWidth(), monster.getHeight());
            collision = shipBounds.intersects(monsterBounds);
        }
        return collision;
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
                if (!onGame) {
                    IO.println("Starting next game. Score = " + score + " Speed = " + barSpeed+" Tick = "+barTick);
                    initBoard();
                }
                break;
            default:
                // Do nothing...
                break;
        }

    }
}
