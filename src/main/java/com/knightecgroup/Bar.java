package com.knightecgroup;

import java.awt.Image;
import java.util.Random;
import javax.swing.ImageIcon;

public class Bar {

    private int y=0;
    private int barWidth;
    private int barHeight;
    private final int numberOfBarParts;
    private final int holePosition;
    private final int boardHeight;
    private final int STEP_LENGTH = 5;
    private boolean hasPasted;
    private Image barImage;

    public Bar(int boardWidth, int boardHeight) {
        loadImages();
        Random rand = new Random();
        numberOfBarParts = boardWidth/barWidth;
        holePosition = rand.nextInt(numberOfBarParts-1);
        hasPasted = false;
        this.boardHeight = boardHeight;
    }

    private void loadImages() {
        ImageIcon ii = new ImageIcon("src/main/resources/bar.png");
        barImage = ii.getImage();

        barWidth = barImage.getWidth(null);
        barHeight = barImage.getHeight(null);
    }

    // The bar will only move from the top to the bottom...
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

    public int getWidth() {
        return barWidth;
    }

    public int getHeight() {
        return barHeight;
    }

    public int getHoleStart() {
        return barWidth *(holePosition-1);
    }

    public int getHoleEnd() {
        return barWidth *(holePosition+1);
    }

    public int getNumberOfBarParts() {
        return numberOfBarParts;
    }

    public Image getBarPart(int part) throws IllegalArgumentException {
        if (part <= numberOfBarParts) {
            if(part==holePosition || part==(holePosition+1)) {
                return null;
            } else {
                return barImage;
            }
        } else {
            throw new IllegalArgumentException("Max number of parts: "+numberOfBarParts);
        }

    }
}
