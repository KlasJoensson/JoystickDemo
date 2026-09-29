package com.knightecgroup;

import java.awt.Image;
import javax.swing.ImageIcon;

public class SpaceShip {

    private int x;
    private int y;
    private int w;
    private int h;
    private final int maxWidth;
    private final int maxHeight;
    private final int STEP_LENGTH = 5;
    private Image image;

    public SpaceShip(int boardWidth, int boardHeight) {
        loadImage();
        maxWidth = boardWidth-this.getWidth();
        maxHeight = boardHeight-this.getHeight();
        y=maxHeight;
        x=boardWidth/2;
    }

    private void loadImage() {

        ImageIcon ii = new ImageIcon("src/main/resources/spaceship.png");
        image = ii.getImage();

        w = image.getWidth(null);
        h = image.getHeight(null);
    }

    public void moveUp() {
        if (y>STEP_LENGTH) {
            y -= STEP_LENGTH;
        }
    }

    public void moveDown() {
        if (y<maxHeight) {
            y += STEP_LENGTH;
        }
    }

    public void moveRight() {
        if (x<(maxWidth-STEP_LENGTH)) {
            x += STEP_LENGTH;
        }
    }

    public void moveLeft() {
        if (x>STEP_LENGTH) {
            x -= STEP_LENGTH;
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return w;
    }

    public int getHeight() {
        return h;
    }

    public Image getImage() {
        return image;
    }
}
