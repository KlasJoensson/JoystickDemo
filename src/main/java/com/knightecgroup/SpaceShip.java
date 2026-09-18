package com.knightecgroup;

import java.awt.Image;
import javax.swing.ImageIcon;

// Spaceship image is 47x47 pixels
public class SpaceShip {

    private int x = 500;
    private int y = 700;
    private int w;
    private int h;
    private Image image;

    public SpaceShip() {

        loadImage();
    }

    private void loadImage() {

        ImageIcon ii = new ImageIcon("src/main/resources/spaceship.png");
        image = ii.getImage();

        w = image.getWidth(null);
        h = image.getHeight(null);
    }

    public void moveUp() {
        if (y>0) {
            y -= 5;
        }
      IO.println("Spaceship at ["+this.getX()+","+this.getY()+"]");
    }

    public void moveDown() {
        if (y<700) {
            y += 5;
        }
        IO.println("Spaceship at ["+this.getX()+","+this.getY()+"]");
    }

    public void moveRight() {
        if (x<940) {
            x += 5;
        }
        IO.println("Spaceship at ["+this.getX()+","+this.getY()+"]");
    }

    public void moveLeft() {
        if (x>0) {
            x -= 5;
        }
        IO.println("Spaceship at ["+this.getX()+","+this.getY()+"]");
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
