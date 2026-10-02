package ludo.service;

import ludo.domain.enums.Colour;
import ludo.domain.enums.Direction;
import ludo.domain.model.Piece;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MoveDestinationCalculatorTest {

    private MoveDestinationCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new MoveDestinationCalculator();
    }

    @Test
    void shouldCalculateClockwiseDestination() {
        Piece piece = new Piece(Colour.RED, 1);

        piece.enterBoard(26, Direction.CLOCKWISE);
        piece.moveTo(20);

        assertEquals(24, calculator.calculateStandardDestination(piece, 4));
    }

    @Test
    void shouldCalculateCounterclockwiseDestination() {
        Piece piece = new Piece(Colour.RED, 1);

        piece.enterBoard(26, Direction.COUNTERCLOCKWISE);
        piece.moveTo(20);

        assertEquals(16, calculator.calculateStandardDestination(piece, 4));
    }

    @Test
    void shouldWrapClockwiseAroundBoard() {
        Piece piece = new Piece(Colour.RED, 1);

        piece.enterBoard(26, Direction.CLOCKWISE);
        piece.moveTo(50);

        assertEquals(2, calculator.calculateStandardDestination(piece, 4));
    }

    @Test
    void shouldWrapCounterclockwiseAroundBoard() {
        Piece piece = new Piece(Colour.RED, 1);

        piece.enterBoard(26, Direction.COUNTERCLOCKWISE);
        piece.moveTo(1);

        assertEquals(49, calculator.calculateStandardDestination(piece, 4));
    }

    @Test
    void shouldRejectNullPiece() {
        assertThrows(IllegalArgumentException.class, () -> calculator.calculateStandardDestination(null, 4));
    }

    @Test
    void shouldRejectNonPositiveDistance() {
        Piece piece = new Piece(Colour.RED, 1);

        piece.enterBoard(26, Direction.CLOCKWISE);

        assertThrows(IllegalArgumentException.class, () -> calculator.calculateStandardDestination(piece, 0));
    }
}