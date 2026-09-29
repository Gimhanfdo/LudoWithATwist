package ludo.service;

import ludo.domain.enums.Colour;
import ludo.domain.model.Piece;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BetaRollTrackerTest {

    private BetaRollTracker tracker;

    @BeforeEach
    void setUp() {
        tracker = new BetaRollTracker();
    }

    @Test
    void shouldIncreaseCountWhenThreeIsRolled() {
        Piece piece = new Piece(Colour.RED, 1);
        tracker.recordRoll(piece, 3);

        assertEquals(1, tracker.getConsecutiveThreeCount(piece));
    }

    @Test
    void shouldTriggerAfterThreeConsecutiveThrees() {
        Piece piece = new Piece(Colour.RED, 1);

        assertFalse(tracker.recordRoll(piece, 3));
        assertFalse(tracker.recordRoll(piece, 3));
        assertTrue(tracker.recordRoll(piece, 3));
    }

    @Test
    void shouldResetSequenceWhenDifferentValueIsRolled() {
        Piece piece = new Piece(Colour.RED, 1);
        
        tracker.recordRoll(piece, 3);
        tracker.recordRoll(piece, 3);
        tracker.recordRoll(piece, 5);

        assertEquals(0, tracker.getConsecutiveThreeCount(piece));
    }

    @Test
    void shouldTrackPiecesIndependently() {
        Piece firstPiece = new Piece(Colour.RED, 1);
        Piece secondPiece = new Piece(Colour.RED, 2);

        tracker.recordRoll(firstPiece, 3);
        tracker.recordRoll(firstPiece, 3);
        tracker.recordRoll(secondPiece, 3);

        assertEquals(2, tracker.getConsecutiveThreeCount(firstPiece));
        assertEquals(1, tracker.getConsecutiveThreeCount(secondPiece));
    }

    @Test
    void shouldResetCountAfterTriggering() {
        Piece piece = new Piece(Colour.RED, 1);
        tracker.recordRoll(piece, 3);
        tracker.recordRoll(piece, 3);
        tracker.recordRoll(piece, 3);

        assertEquals(0, tracker.getConsecutiveThreeCount(piece));
    }

    @Test
    void shouldRejectInvalidRoll() {
        assertThrows(IllegalArgumentException.class, () -> tracker.recordRoll(new Piece(Colour.RED, 1), 7));
    }

    @Test
    void shouldRejectNullColour() {
        assertThrows(IllegalArgumentException.class, () -> tracker.recordRoll(null, 3));
    }
}