package com.knightecgroup;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class Monster {

    private int y;
    private int x;
    private int monsterWidth;
    private int monsterHeight;
    private int boardHeight;
    private final int STEP_LENGTH = 5;
    private boolean hasPasted;
    private Image monsterImage;

    public Monster(int boardWidth, int boardHeight) {
        loadImage();
        Random rand = new Random();
        y = rand.nextInt(150)+50;
        int maxNumberOfMonstersOnRow = boardWidth/50;
        x = rand.nextInt(maxNumberOfMonstersOnRow)*monsterWidth;
        hasPasted = false;
        this.boardHeight = boardHeight;
    }

    private void loadImage() {
        ImageIcon ii;
        Random rand = new Random();
        int type = rand.nextInt(8);
        ii = switch (type) {
            case 0 -> new ImageIcon("src/main/resources/monster0.png");
            case 1 -> new ImageIcon("src/main/resources/monster1.png");
            case 2 -> new ImageIcon("src/main/resources/monster2.png");
            case 3 -> new ImageIcon("src/main/resources/monster3.png");
            case 4 -> new ImageIcon("src/main/resources/monster4.png");
            case 5 -> new ImageIcon("src/main/resources/monster5.png");
            case 6 -> new ImageIcon("src/main/resources/monster6.png");
            case 7 -> new ImageIcon("src/main/resources/monster7.png");
            default -> new ImageIcon("src/main/resources/bar.png");
        };

        monsterImage = ii.getImage();

        monsterWidth = monsterImage.getWidth(null);
        monsterHeight = monsterImage.getHeight(null);
    }

    // The monster will only move from the top to the bottom...
    public void move() {
        if (y<boardHeight) {
            y += STEP_LENGTH;
        } else {
            hasPasted = true;
        }
    }

    public boolean hasPastedBoard() {
        return hasPasted;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    public int getWidth() {
        return monsterWidth;
    }

    public int getHeight() {
        return monsterHeight;
    }


    public Image getImage() throws IllegalArgumentException {
        return monsterImage;
    }
}
