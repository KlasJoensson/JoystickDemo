package com.knightecgroup;

import javax.swing.*;
import java.awt.*;

public class Missile {

        private int y;
        private final int x;
        private int missileWidth;
        private int missileHeight;
        private final int STEP_LENGTH = 15;
        private boolean hasPasted;
        private Image missileImage;

        public Missile(int startX, int startY) {
            loadImage();
            y = startY-missileHeight;
            x = startX;
            hasPasted = false;
        }

        private void loadImage() {
            ImageIcon ii = new ImageIcon("src/main/resources/missile.png");
            missileImage = ii.getImage();

            missileWidth = missileImage.getWidth(null);
            missileHeight = missileImage.getHeight(null);
        }

        // The missile will only move from the spacecraft and up...
        public void move() {
            if (y>0) {
                y -= STEP_LENGTH;
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
            return missileWidth;
        }

        public int getHeight() {
            return missileHeight;
        }


        public Image getImage() throws IllegalArgumentException {
            return missileImage;
        }


}
