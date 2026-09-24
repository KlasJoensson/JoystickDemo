package com.knightecgroup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    /** Index of the first null (hole) part; the hole always spans this index and the next. */
    private int findHolePosition(Bar b) {
        for (int i = 0; i < b.getNumberOfBarParts(); i++) {
            if (b.getBarPart(i) == null) {
                return i;
            }
        }
        throw new IllegalStateException("No hole found in bar parts");
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

        @Test
        void hasNotPastedTheBoardYet() {
            assertFalse(bar.hasPastedBoard());
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

        @Test
        @DisplayName("hasPastedBoard becomes true once y has reached the board height")
        void marksAsPastedWhenAtOrBeyondBoardHeight() {
            Bar atBottom = new Bar(BOARD_WIDTH, 0);

            atBottom.move();

            assertTrue(atBottom.hasPastedBoard());
            assertEquals(0, atBottom.getY(), "y should not advance once past the board");
        }

        @Test
        @DisplayName("stays marked as pasted on further moves")
        void staysMarkedAsPastedOnFurtherMoves() {
            Bar atBottom = new Bar(BOARD_WIDTH, 0);
            atBottom.move();
            atBottom.move();
            atBottom.move();

            assertTrue(atBottom.hasPastedBoard());
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
        void regularPartsReturnTheSameBarImage() {
            // The last valid index is never the hole: holePosition tops out at
            // numberOfBarParts - 2, so holePosition + 1 is always < numberOfBarParts.
            int regularIndex = bar.getNumberOfBarParts();

            Image part = bar.getBarPart(regularIndex);
            assertNotNull(part);
            assertSame(part, bar.getBarPart(regularIndex));
        }

        @Test
        @DisplayName("hole spans exactly two consecutive parts and both are null")
        void holeSpansTwoConsecutiveNullParts() {
            int holePosition = findHolePosition(bar);

            assertNull(bar.getBarPart(holePosition));
            assertNull(bar.getBarPart(holePosition + 1));
        }

        @Test
        void partsImmediatelyOutsideTheHoleUseBarImage() {
            int holePosition = findHolePosition(bar);
            Image regular = bar.getBarPart(bar.getNumberOfBarParts());

            if (holePosition > 0) {
                assertSame(regular, bar.getBarPart(holePosition - 1));
            }
            // holePosition + 2 is always <= numberOfBarParts, so this side is always valid.
            assertSame(regular, bar.getBarPart(holePosition + 2));
            assertNotSame(regular, bar.getBarPart(holePosition));
        }

        @Test
        void partBeyondNumberOfPartsThrows() {
            assertThrows(IllegalArgumentException.class,
                    () -> bar.getBarPart(bar.getNumberOfBarParts() + 1));
        }

        @Test
        void lastValidPartIsNeverPartOfTheHole() {
            assertNotNull(bar.getBarPart(bar.getNumberOfBarParts()));
        }

        @Test
        void holeBoundsMatchHolePosition() {
            int holePosition = findHolePosition(bar);
            assertEquals(bar.getWidth() * (holePosition - 1), bar.getHoleStart());
            assertEquals(bar.getWidth() * (holePosition + 1), bar.getHoleEnd());
        }

        @Test
        @DisplayName("hole position stays within valid bounds across many instances")
        void holePositionInvariantsHoldAcrossManyInstances() {
            for (int i = 0; i < 100; i++) {
                Bar b = new Bar(BOARD_WIDTH, BOARD_HEIGHT);
                int holePosition = findHolePosition(b);

                assertTrue(holePosition >= 0 && holePosition < b.getNumberOfBarParts() - 1,
                        "hole position out of range: " + holePosition);
                assertNull(b.getBarPart(holePosition));
                assertNull(b.getBarPart(holePosition + 1));
            }
        }
    }
}
