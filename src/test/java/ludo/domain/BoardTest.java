package ludo.domain;

import ludo.domain.enums.Colour;
import ludo.domain.model.Board;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board();
    }

    @Test
    void shouldContainFiftyTwoStandardPathCells() {
        assertEquals(52, Board.STANDARD_PATH_SIZE);
    }

    @Test
    void shouldContainFiveHomeStraightCellsPerColour() {
        assertEquals(5, Board.HOME_STRAIGHT_SIZE);
    }

    @Test
    void shouldUseYellowStartingPositionAsZero() {
        assertEquals(
                0,
                board.getStartPosition(Colour.YELLOW)
        );
    }

    @Test
    void shouldReturnCorrectStartingPositions() {
        assertEquals(0, board.getStartPosition(Colour.YELLOW));
        assertEquals(13, board.getStartPosition(Colour.BLUE));
        assertEquals(26, board.getStartPosition(Colour.RED));
        assertEquals(39, board.getStartPosition(Colour.GREEN));
    }

    @Test
    void shouldReturnCorrectApproachPositions() {
        assertEquals(51, board.getApproachPosition(Colour.YELLOW));
        assertEquals(12, board.getApproachPosition(Colour.BLUE));
        assertEquals(25, board.getApproachPosition(Colour.RED));
        assertEquals(38, board.getApproachPosition(Colour.GREEN));
    }

    @Test
    void shouldAcceptPositionsBetweenZeroAndFiftyOne() {
        assertTrue(board.isValidStandardPosition(0));
        assertTrue(board.isValidStandardPosition(25));
        assertTrue(board.isValidStandardPosition(51));
    }

    @Test
    void shouldRejectPositionsOutsideStandardPath() {
        assertFalse(board.isValidStandardPosition(-1));
        assertFalse(board.isValidStandardPosition(52));
    }

    @Test
    void shouldRejectNullColour() {
        assertThrows(
                IllegalArgumentException.class,
                () -> board.getStartPosition(null)
        );
    }
}