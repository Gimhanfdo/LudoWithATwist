package ludo.domain;

import ludo.domain.enums.Colour;
import ludo.domain.model.Board;
import ludo.domain.enums.Direction;

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
                board.getStartPosition(Colour.YELLOW));
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
                () -> board.getStartPosition(null));
    }

    @Test
    void shouldCalculateClockwiseDistanceToApproach() {

        Board board = new Board();

        int distance = board.getDistanceToApproach(
                22,
                Colour.RED,
                Direction.CLOCKWISE);

        assertEquals(3, distance);
    }

    @Test
    void shouldCalculateCounterclockwiseDistanceToApproach() {

        Board board = new Board();

        int distance = board.getDistanceToApproach(
                28,
                Colour.RED,
                Direction.COUNTERCLOCKWISE);

        assertEquals(3, distance);
    }

    @Test
    void shouldCalculateClockwiseDistanceToApproachAcrossBoardBoundary() {

        Board board = new Board();

        int distance = board.getDistanceToApproach(
                50,
                Colour.BLUE,
                Direction.CLOCKWISE);

        assertEquals(14, distance);
    }

    @Test
    void shouldCalculateCounterclockwiseDistanceToApproachAcrossBoardBoundary() {

        Board board = new Board();

        int distance = board.getDistanceToApproach(
                2,
                Colour.BLUE,
                Direction.COUNTERCLOCKWISE);

        assertEquals(42, distance);
    }

    @Test
    void shouldReturnZeroWhenAlreadyOnApproach() {

        Board board = new Board();

        int distance = board.getDistanceToApproach(
                25,
                Colour.RED,
                Direction.CLOCKWISE);

        assertEquals(0, distance);
    }

    @Test
    void shouldDetectMovementBeyondApproach() {

        Board board = new Board();

        boolean movesBeyond = board.movesBeyondApproach(
                22,
                4,
                Colour.RED,
                Direction.CLOCKWISE);

        assertTrue(movesBeyond);
    }

    @Test
    void shouldNotReportBeyondApproachWhenLandingExactlyOnApproach() {

        Board board = new Board();

        boolean movesBeyond = board.movesBeyondApproach(
                22,
                3,
                Colour.RED,
                Direction.CLOCKWISE);

        assertFalse(movesBeyond);
    }

    @Test
    void shouldNotReportBeyondApproachWhenMovementStopsBeforeApproach() {

        Board board = new Board();

        boolean movesBeyond = board.movesBeyondApproach(
                22,
                2,
                Colour.RED,
                Direction.CLOCKWISE);

        assertFalse(movesBeyond);
    }
}