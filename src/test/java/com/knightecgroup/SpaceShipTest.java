package com.knightecgroup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class SpaceShipTest {

    private static final int STEP = 5;
    private static int START_X;
    private static int START_Y;
    private static int MAX_X;
    private static int MAX_Y;
    private static final int MIN_COORD = 0;
    private static final int BOARD_WIDTH = 1000;
    private static final int BOARD_HEIGHT = 750;

    private SpaceShip ship;

    @BeforeEach
    void setUp() {
        ship = new SpaceShip(BOARD_WIDTH, BOARD_HEIGHT);
        MAX_X = BOARD_WIDTH-ship.getWidth();
        MAX_Y = BOARD_HEIGHT-ship.getHeight();
        START_Y = MAX_Y;
        START_X = BOARD_WIDTH/2;
    }

    private void repeat(int times, Runnable move) {
        for (int i = 0; i < times; i++) {
            move.run();
        }
    }

    @Nested
    @DisplayName("initial state")
    class InitialState {

        @Test
        void startsAtConfiguredPosition() {
            assertEquals(START_X, ship.getX());
            assertEquals(START_Y, ship.getY());
        }

        @Test
        void loadsSprite() {
            assertNotNull(ship.getImage());
        }
    }

    @Nested
    @DisplayName("single steps")
    class SingleSteps {

        @Test
        void moveUpDecreasesYByOneStep() {
            ship.moveUp();
            assertEquals(START_Y - STEP, ship.getY());
        }

        @Test
        void moveLeftDecreasesXByOneStep() {
            ship.moveLeft();
            assertEquals(START_X - STEP, ship.getX());
        }

        @Test
        void moveRightIncreasesXByOneStep() {
            ship.moveRight();
            assertEquals(START_X + STEP, ship.getX());
        }

        @Test
        @DisplayName("moveDown is a no-op at the starting position, which is already the bottom bound")
        void moveDownAtStartDoesNothing() {
            ship.moveDown();
            assertEquals(START_Y, ship.getY());
        }
    }

    @Nested
    @DisplayName("axis independence")
    class AxisIndependence {

        @Test
        void horizontalMovesLeaveYUnchanged() {
            ship.moveLeft();
            ship.moveRight();
            ship.moveRight();
            assertEquals(START_Y, ship.getY());
        }

        @Test
        void verticalMovesLeaveXUnchanged() {
            ship.moveUp();
            ship.moveUp();
            ship.moveDown();
            assertEquals(START_X, ship.getX());
        }
    }

    @Nested
    @DisplayName("boundary clamping")
    class BoundaryClamping {

        @Test
        void stopsAtLeftEdge() {
            repeat(START_X / STEP, ship::moveLeft);
            int shipXPosition = ship.getX();
            assertTrue(shipXPosition >= MIN_COORD, "Ship X position is out of bounds: "+shipXPosition+ "<" + MIN_COORD);

            ship.moveLeft();
            assertTrue(shipXPosition == ship.getX(),  "Ship moved: "+shipXPosition+"!="+ship.getX());
        }

        @Test
        void stopsAtRightEdge() {
            repeat(((MAX_X - START_X) / STEP)+1, ship::moveRight);
            int shipXPosition = ship.getX();
            assertTrue(shipXPosition <= MAX_X, "Ship X position is out of bounds: "+shipXPosition+ ">" + MAX_X);

            ship.moveRight();
            assertTrue(shipXPosition == ship.getX(),  "Ship moved: "+shipXPosition+"!="+ship.getX());
        }

        @Test
        void stopsAtTopEdge() {
            repeat(START_Y / STEP, ship::moveUp);
            int shipYPosition = ship.getY();
            assertTrue(MIN_COORD < shipYPosition);

            ship.moveUp();
            assertTrue(shipYPosition == ship.getY(), "Ship moved: "+shipYPosition+"!="+ship.getY());
        }

        @Test
        void stopsAtBottomEdgeAfterReturningFromTop() {
            repeat(START_Y / STEP, ship::moveUp);
            repeat(START_Y / STEP, ship::moveDown);
            assertEquals(MAX_Y, ship.getY());

            ship.moveDown();
            assertEquals(MAX_Y, ship.getY());
        }

        @Test
        @DisplayName("position stays within bounds across a long mixed run")
        void neverLeavesBoundsUnderMixedMovement() {
            for (int i = 0; i < 300; i++) {
                ship.moveLeft();
                ship.moveUp();
                if (i % 3 == 0) {
                    ship.moveRight();
                    ship.moveDown();
                }
                assertTrue(ship.getX() >= MIN_COORD && ship.getX() <= MAX_X,
                        "x out of bounds: " + ship.getX());
                assertTrue(ship.getY() >= MIN_COORD && ship.getY() <= MAX_Y,
                        "y out of bounds: " + ship.getY());
            }
        }
    }
}
