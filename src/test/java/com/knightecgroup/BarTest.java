package com.knightecgroup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Image;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class BarTest {

    private static final int STEP = 5;
    private static final int BOARD_WIDTH = 1000;
    private static final int BOARD_HEIGHT = 750;
    private static final int HOLE_POSITION = 9;

    private Bar bar;

    @BeforeEach
    void setUp() {
        bar = new Bar(BOARD_WIDTH, BOARD_HEIGHT);
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
        void startsAtTop() {
            assertEquals(0, bar.getY());
        }

        @Test
        void loadsSpriteDimensions() {
            assertTrue(bar.getWidth() > 0);
            assertTrue(bar.getHeight() > 0);
        }

        @Test
        void splitsBoardWidthIntoParts() {
            assertEquals(BOARD_WIDTH / bar.getWidth(), bar.getNumberOfBarParts());
        }
    }

    @Nested
    @DisplayName("movement")
    class Movement {

        @Test
        void moveIncreasesYByOneStep() {
            bar.move();
            assertEquals(STEP, bar.getY());
        }

        @Test
        void moveOnlyGoesDownward() {
            int before = bar.getY();
            bar.move();
            assertTrue(bar.getY() > before);
        }
    }

    @Nested
    @DisplayName("boundary clamping")
    class BoundaryClamping {

        @Test
        void stopsAtBottomEdge() {
            repeat((BOARD_HEIGHT / STEP) + 5, bar::move);
            assertEquals(BOARD_HEIGHT, bar.getY());
        }

        @Test
        void staysAtBottomEdgeOnceReached() {
            repeat(BOARD_HEIGHT / STEP, bar::move);
            int atBottom = bar.getY();
            assertEquals(BOARD_HEIGHT, atBottom);

            bar.move();
            assertEquals(atBottom, bar.getY());
        }

        @Test
        @DisplayName("y never exceeds the board height across a long run")
        void neverExceedsBoardHeight() {
            for (int i = 0; i < 300; i++) {
                bar.move();
                assertTrue(bar.getY() <= BOARD_HEIGHT,
                        "y out of bounds: " + bar.getY());
            }
        }
    }

    @Nested
    @DisplayName("bar parts")
    class BarParts {

        @Test
        void regularPartsUseBarImage() {
            Image part = bar.getBarPart(0);
            assertNotNull(part);
            assertSame(part, bar.getBarPart(0));
        }

        @Test
        @DisplayName("hole occupies exactly the configured position and its successor")
        void holeSpansTwoParts() {
            Image regular = bar.getBarPart(0);
            Image hole = bar.getBarPart(HOLE_POSITION);

            assertNotNull(hole);
            assertNotSame(regular, hole);
            assertSame(hole, bar.getBarPart(HOLE_POSITION + 1));
        }

        @Test
        void partsSurroundingHoleUseBarImage() {
            Image regular = bar.getBarPart(0);
            assertSame(regular, bar.getBarPart(HOLE_POSITION - 1));
            assertSame(regular, bar.getBarPart(HOLE_POSITION + 2));
        }

        @Test
        void partBeyondNumberOfPartsIsNull() {
            assertNull(bar.getBarPart(bar.getNumberOfBarParts() + 1));
        }

        @Test
        void lastValidPartIsNotNull() {
            assertNotNull(bar.getBarPart(bar.getNumberOfBarParts()));
        }

        @Test
        void holeBoundsMatchHolePosition() {
            assertEquals(bar.getWidth() * (HOLE_POSITION - 1), bar.getHoleStart());
            assertEquals(bar.getWidth() * (HOLE_POSITION + 1), bar.getHoleEnd());
        }
    }
}
