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

class MonsterTest {

    private static final int STEP = 5;
    private static final int BOARD_WIDTH = 1000;
    private static final int BOARD_HEIGHT = 750;
    private static final int MIN_START_Y = 50;
    private static final int MAX_START_Y = 199;
    private static final int GRID_CELL = 50;

    private Monster monster;

    @BeforeEach
    void setUp() {
        monster = new Monster(BOARD_WIDTH, BOARD_HEIGHT);
    }

    @Nested
    @DisplayName("initial state")
    class InitialState {

        @Test
        void startsWithinExpectedYRange() {
            assertTrue(monster.getY() >= MIN_START_Y && monster.getY() <= MAX_START_Y,
                    "y out of expected range: " + monster.getY());
        }

        @Test
        void startsAlignedToTheHorizontalGrid() {
            assertTrue(monster.getX() >= 0 && monster.getX() < BOARD_WIDTH,
                    "x out of board bounds: " + monster.getX());
            assertEquals(0, monster.getX() % monster.getWidth(),
                    "x is not aligned to a grid cell of width " + monster.getWidth());
        }

        @Test
        void loadsASprite() {
            assertNotNull(monster.getImage());
            assertTrue(monster.getWidth() > 0);
            assertTrue(monster.getHeight() > 0);
        }

        @Test
        void imageDimensionsMatchReportedDimensions() {
            Image image = monster.getImage();
            assertEquals(monster.getWidth(), image.getWidth(null));
            assertEquals(monster.getHeight(), image.getHeight(null));
        }

        @Test
        void hasNotPastedTheBoardYet() {
            assertFalse(monster.hasPastedBoard());
        }

        @Test
        @DisplayName("x stays aligned and in bounds, y stays in range, across many instances")
        void positionInvariantsHoldAcrossManyInstances() {
            for (int i = 0; i < 200; i++) {
                Monster m = new Monster(BOARD_WIDTH, BOARD_HEIGHT);
                assertTrue(m.getX() >= 0 && m.getX() < BOARD_WIDTH,
                        "x out of board bounds: " + m.getX());
                assertEquals(0, m.getX() % GRID_CELL, "x is not grid-aligned: " + m.getX());
                assertTrue(m.getY() >= MIN_START_Y && m.getY() <= MAX_START_Y,
                        "y out of expected range: " + m.getY());
            }
        }
    }

    @Nested
    @DisplayName("movement")
    class Movement {

        @Test
        void moveIncreasesYByOneStep() {
            int before = monster.getY();
            monster.move();
            assertEquals(before + STEP, monster.getY());
        }

        @Test
        void moveOnlyGoesDownward() {
            int before = monster.getY();
            monster.move();
            assertTrue(monster.getY() > before);
        }

        @Test
        void moveDoesNotAffectX() {
            int beforeX = monster.getX();
            monster.move();
            assertEquals(beforeX, monster.getX());
        }

        @Test
        @DisplayName("hasPastedBoard becomes true once y has reached the board height")
        void marksAsPastedWhenAtOrBeyondBoardHeight() {
            Monster atBottom = new Monster(BOARD_WIDTH, 1);
            int yBefore = atBottom.getY();

            atBottom.move();

            assertTrue(atBottom.hasPastedBoard());
            assertEquals(yBefore, atBottom.getY(), "y should not advance once past the board");
        }

        @Test
        @DisplayName("stays marked as pasted on further moves")
        void staysMarkedAsPastedOnFurtherMoves() {
            Monster atBottom = new Monster(BOARD_WIDTH, 1);
            atBottom.move();
            atBottom.move();
            atBottom.move();

            assertTrue(atBottom.hasPastedBoard());
        }
    }
}
