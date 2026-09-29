package ludo.strategy.teleport;

import ludo.domain.enums.Colour;
import ludo.domain.enums.Direction;
import ludo.domain.enums.PieceEffect;
import ludo.domain.enums.PieceState;
import ludo.domain.enums.TeleportDestination;
import ludo.domain.model.Piece;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BaseTeleportStrategyTest {

    private BaseTeleportStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new BaseTeleportStrategy();
    }

    @Test
    void shouldHandleBaseDestination() {
        assertEquals(TeleportDestination.BASE, strategy.getDestination());
    }

    @Test
    void shouldTeleportPieceToBase() {
        Piece piece = new Piece(Colour.RED, 1);

        piece.enterBoard(26, Direction.CLOCKWISE);
        piece.recordCapture();
        piece.recordApproachPass();

        strategy.teleport(piece);

        assertEquals(PieceState.BASE, piece.getState());
        assertNull(piece.getPosition());
        assertNull(piece.getDirection());
        assertEquals(0, piece.getCaptureCount());
        assertEquals(0, piece.getApproachPassCount());
        assertEquals(PieceEffect.NONE, piece.getEffect());
    }

    @Test
    void shouldRejectNullPiece() {
        assertThrows(IllegalArgumentException.class, () -> strategy.teleport(null));
    }
}