package ludo.domain.model;

import ludo.domain.enums.Colour;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MovementOutcomeTest {

    @Test
    void shouldRepresentNormalMovement() {
        MovementOutcome outcome = new MovementOutcome(MovementResult.MOVED, 6, 6, 10, 16, List.of(), List.of());

        assertEquals(MovementResult.MOVED, outcome.getResult());
        assertEquals(6, outcome.getRequestedDistance());
        assertEquals(6, outcome.getActualDistance());
        assertEquals(10, outcome.getFromPosition());
        assertEquals(16, outcome.getToPosition());
        assertFalse(outcome.wasBlocked());
        assertFalse(outcome.wasShortened());
        assertFalse(outcome.wasCompletelyBlocked());
        assertFalse(outcome.captured());
    }

    @Test
    void shouldRepresentMovementShortenedByBlock() {
        Piece blueOne = new Piece(Colour.BLUE, 1);
        Piece blueTwo = new Piece(Colour.BLUE, 2);
        MovementOutcome outcome = new MovementOutcome(MovementResult.MOVED, 6, 3, 10, 13, List.of(blueOne, blueTwo), List.of());

        assertTrue(outcome.wasBlocked());
        assertTrue(outcome.wasShortened());
        assertFalse(outcome.wasCompletelyBlocked());
    }

    @Test
    void shouldRepresentCompletelyBlockedMovement() {
        Piece blueOne = new Piece(Colour.BLUE, 1);
        Piece blueTwo = new Piece(Colour.BLUE, 2);
        MovementOutcome outcome = new MovementOutcome(MovementResult.NOT_MOVED, 6, 0, 10, 10, List.of(blueOne, blueTwo), List.of());

        assertTrue(outcome.wasBlocked());
        assertTrue(outcome.wasCompletelyBlocked());
        assertFalse(outcome.wasShortened());
    }

    @Test
    void shouldRepresentCapture() {
        Piece bluePiece = new Piece(Colour.BLUE, 1);
        MovementOutcome outcome = new MovementOutcome(MovementResult.CAPTURED, 4, 4, 10, 14, List.of(), List.of(bluePiece));

        assertTrue(outcome.captured());
        assertEquals(List.of(bluePiece), outcome.getCapturedPieces());
    }

    @Test
    void shouldDefensivelyCopyPieceLists() {
        Piece bluePiece = new Piece(Colour.BLUE, 1);
        List<Piece> capturedPieces = new java.util.ArrayList<>();
        capturedPieces.add(bluePiece);
        MovementOutcome outcome = new MovementOutcome(MovementResult.CAPTURED, 4, 4, 10, 14, List.of(), capturedPieces);

        capturedPieces.clear();

        assertEquals(1, outcome.getCapturedPieces().size());
        assertThrows(UnsupportedOperationException.class, () -> outcome.getCapturedPieces().clear());
    }

    @Test
    void shouldRejectNullMovementResult() {
        assertThrows(IllegalArgumentException.class, () -> new MovementOutcome(null, 4, 4, 10, 14, List.of(), List.of()));
    }

    @Test
    void shouldRejectNonPositiveRequestedDistance() {
        assertThrows(IllegalArgumentException.class, () -> new MovementOutcome(MovementResult.MOVED, 0, 0, 10, 10, List.of(), List.of()));
    }

    @Test
    void shouldRejectNegativeActualDistance() {
        assertThrows(IllegalArgumentException.class, () -> new MovementOutcome(MovementResult.MOVED, 4, -1, 10, 10, List.of(), List.of()));
    }

    @Test
    void shouldRejectActualDistanceGreaterThanRequestedDistance() {
        assertThrows(IllegalArgumentException.class, () -> new MovementOutcome(MovementResult.MOVED, 4, 5, 10, 15, List.of(), List.of()));
    }

    @Test
    void shouldRejectNullBlockingPieces() {
        assertThrows(IllegalArgumentException.class, () -> new MovementOutcome(MovementResult.MOVED, 4, 4, 10, 14, null, List.of()));
    }

    @Test
    void shouldRejectNullCapturedPieces() {
        assertThrows(IllegalArgumentException.class, () -> new MovementOutcome(MovementResult.MOVED, 4, 4, 10, 14, List.of(), null));
    }
}