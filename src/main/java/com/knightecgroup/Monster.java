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
        y = 100;
        x = rand.nextInt(boardWidth-monsterWidth)+monsterWidth;
        hasPasted = false;
        this.boardHeight = boardHeight;
    }

    private void loadImage() {
        ImageIcon ii = new ImageIcon("src/main/resources/monster0.png");
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
