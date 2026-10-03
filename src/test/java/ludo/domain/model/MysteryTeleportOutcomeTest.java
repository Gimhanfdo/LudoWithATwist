package ludo.domain.model;

import ludo.domain.enums.Direction;
import ludo.domain.enums.PieceEffect;
import ludo.domain.enums.PieceState;
import ludo.domain.enums.TeleportDestination;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MysteryTeleportOutcomeTest {

    @Test
    void shouldReportDirectionChange() {
        MysteryTeleportOutcome outcome = new MysteryTeleportOutcome(
                TeleportDestination.GAMMA, PieceState.STANDARD_PATH, Board.GAMMA_POSITION,
                Direction.CLOCKWISE, Direction.COUNTERCLOCKWISE, PieceEffect.NONE);

        assertTrue(outcome.directionChanged());
    }

    @Test
    void shouldNotReportDirectionChangeWhenDirectionRemainsSame() {
        MysteryTeleportOutcome outcome = new MysteryTeleportOutcome(
                TeleportDestination.ALPHA, PieceState.STANDARD_PATH, Board.ALPHA_POSITION,
                Direction.CLOCKWISE, Direction.CLOCKWISE, PieceEffect.ENERGISED);

        assertFalse(outcome.directionChanged());
    }

    @Test
    void shouldAllowNullFinalPositionForBaseDestination() {
        MysteryTeleportOutcome outcome = new MysteryTeleportOutcome(
                TeleportDestination.BASE, PieceState.BASE, null,
                Direction.CLOCKWISE, null, PieceEffect.NONE);

        assertNull(outcome.getFinalPosition());
        assertEquals(PieceState.BASE, outcome.getFinalState());
        assertEquals(PieceEffect.NONE, outcome.getFinalEffect());
    }

    @Test
    void shouldRejectNullSelectedDestination() {
        assertThrows(IllegalArgumentException.class, () -> new MysteryTeleportOutcome(
                null, PieceState.STANDARD_PATH, Board.ALPHA_POSITION,
                Direction.CLOCKWISE, Direction.CLOCKWISE, PieceEffect.NONE));
    }

    @Test
    void shouldRejectNullFinalState() {
        assertThrows(IllegalArgumentException.class, () -> new MysteryTeleportOutcome(
                TeleportDestination.ALPHA, null, Board.ALPHA_POSITION,
                Direction.CLOCKWISE, Direction.CLOCKWISE, PieceEffect.NONE));
    }

    @Test
    void shouldRejectNullFinalEffect() {
        assertThrows(IllegalArgumentException.class, () -> new MysteryTeleportOutcome(
                TeleportDestination.ALPHA, PieceState.STANDARD_PATH, Board.ALPHA_POSITION,
                Direction.CLOCKWISE, Direction.CLOCKWISE, null));
    }
}