package com.knightecgroup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Image;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class MissileTest {

    private static final int STEP = 15;
    private static final int START_X = 500;
    private static final int START_Y = 700;

    private Missile missile;
    private int startingY;

    @BeforeEach
    void setUp() {
        missile = new Missile(START_X, START_Y);
        startingY = missile.getY();
    }

    @Nested
    @DisplayName("initial state")
    class InitialState {

        @Test
        void startsAtConfiguredXPosition() {
            assertEquals(START_X, missile.getX());
        }

        @Test
        @DisplayName("y is offset above the given start position by the sprite height")
        void startsAboveConfiguredYPositionBySpriteHeight() {
            assertEquals(START_Y - missile.getHeight(), missile.getY());
        }

        @Test
        void loadsASprite() {
            assertNotNull(missile.getImage());
            assertTrue(missile.getWidth() > 0);
            assertTrue(missile.getHeight() > 0);
        }

        @Test
        void imageDimensionsMatchReportedDimensions() {
            Image image = missile.getImage();
            assertEquals(missile.getWidth(), image.getWidth(null));
            assertEquals(missile.getHeight(), image.getHeight(null));
        }

        @Test
        void hasNotPastedTheBoardYet() {
            assertFalse(missile.hasPastedBoard());
        }
    }

    @Nested
    @DisplayName("movement")
    class Movement {

        @Test
        void moveDecreasesYByOneStep() {
            missile.move();
            assertEquals(startingY - STEP, missile.getY());
        }

        @Test
        void moveDoesNotAffectX() {
            missile.move();
            assertEquals(START_X, missile.getX());
        }

        @Test
        void doesNotMarkAsPastedWhileStillAboveTheTop() {
            missile.move();
            assertFalse(missile.hasPastedBoard());
        }

        @Test
        @DisplayName("hasPastedBoard becomes true once y has reached the top of the board")
        void marksAsPastedWhenAtOrBeyondTheTop() {
            Missile atTop = new Missile(START_X, 0);

            atTop.move();

            assertTrue(atTop.hasPastedBoard());
        }

        @Test
        @DisplayName("y no longer changes once the missile has pasted the board")
        void doesNotMoveFurtherOncePasted() {
            Missile atTop = new Missile(START_X, 0);
            atTop.move();
            int yAfterPasting = atTop.getY();

            atTop.move();
            atTop.move();

            assertTrue(atTop.hasPastedBoard());
            assertEquals(yAfterPasting, atTop.getY());
        }

        @Test
        @DisplayName("repeated moves eventually mark the missile as having pasted the board")
        void eventuallyPastesTheBoardUnderRepeatedMoves() {
            for (int i = 0; i < 100 && !missile.hasPastedBoard(); i++) {
                missile.move();
            }

            assertTrue(missile.hasPastedBoard());
        }
    }
}
