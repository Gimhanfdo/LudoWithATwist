package ludo.domain.model;

import ludo.domain.enums.Colour;
import ludo.domain.enums.Direction;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BlockMovementOutcomeTest {

    @Test
    void shouldStoreBlockMovementDetails() {
        Piece firstPiece = new Piece(Colour.RED, 1);
        Piece secondPiece = new Piece(Colour.RED, 2);

        BlockMovementOutcome outcome = new BlockMovementOutcome(MovementResult.MOVED, 3, 3, 20, 23, Direction.CLOCKWISE, List.of(firstPiece, secondPiece), List.of());

        assertEquals(MovementResult.MOVED, outcome.getResult());
        assertEquals(3, outcome.getRequestedDistance());
        assertEquals(3, outcome.getActualDistance());
        assertEquals(20, outcome.getFromPosition());
        assertEquals(23, outcome.getToPosition());
        assertEquals(Direction.CLOCKWISE, outcome.getDirection());
        assertEquals(List.of(firstPiece, secondPiece), outcome.getMovingPieces());
        assertTrue(outcome.moved());
        assertFalse(outcome.captured());
        assertFalse(outcome.wasShortened());
    }

    @Test
    void shouldStoreCapturedPieces() {
        Piece firstAttacker = new Piece(Colour.RED, 1);
        Piece secondAttacker = new Piece(Colour.RED, 2);
        Piece firstOpponent = new Piece(Colour.BLUE, 1);
        Piece secondOpponent = new Piece(Colour.BLUE, 2);

        BlockMovementOutcome outcome = new BlockMovementOutcome(MovementResult.CAPTURED, 3, 3, 20, 23, Direction.CLOCKWISE,
                List.of(firstAttacker, secondAttacker), List.of(firstOpponent, secondOpponent));

        assertTrue(outcome.moved());
        assertTrue(outcome.captured());
        assertEquals(List.of(firstOpponent, secondOpponent), outcome.getCapturedPieces());
    }

    @Test
    void shouldIdentifyShortenedMovement() {
        Piece firstPiece = new Piece(Colour.RED, 1);
        Piece secondPiece = new Piece(Colour.RED, 2);

        BlockMovementOutcome outcome = new BlockMovementOutcome(MovementResult.MOVED, 3, 2, 20, 22, Direction.CLOCKWISE, List.of(firstPiece, secondPiece), List.of());

        assertTrue(outcome.moved());
        assertTrue(outcome.wasShortened());
    }

    @Test
    void shouldDefensivelyCopyPieceLists() {
        Piece firstPiece = new Piece(Colour.RED, 1);
        Piece secondPiece = new Piece(Colour.RED, 2);

        BlockMovementOutcome outcome = new BlockMovementOutcome(MovementResult.MOVED, 3, 3, 20, 23, Direction.CLOCKWISE, List.of(firstPiece, secondPiece), List.of());

        assertThrows(UnsupportedOperationException.class, () -> outcome.getMovingPieces().add(new Piece(Colour.RED, 3)));
    }

    @Test
    void shouldRejectNullResult() {
        assertThrows(IllegalArgumentException.class, () -> new BlockMovementOutcome(null, 3, 3, 20, 23, Direction.CLOCKWISE, List.of(), List.of()));
    }

    @Test
    void shouldRejectInvalidActualDistance() {
        assertThrows(IllegalArgumentException.class, () -> new BlockMovementOutcome(MovementResult.MOVED, 3, 4, 20, 24, Direction.CLOCKWISE, List.of(), List.of()));
    }

    @Test
    void shouldRejectNullMovingPieces() {
        assertThrows(IllegalArgumentException.class, () -> new BlockMovementOutcome(MovementResult.MOVED, 3, 3, 20, 23, Direction.CLOCKWISE, null, List.of()));
    }

    @Test
    void shouldRejectNullCapturedPieces() {
        assertThrows(IllegalArgumentException.class, () -> new BlockMovementOutcome(MovementResult.MOVED, 3, 3, 20, 23, Direction.CLOCKWISE, List.of(), null));
    }
}