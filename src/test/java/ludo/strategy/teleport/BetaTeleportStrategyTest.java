package ludo.strategy.teleport;

import ludo.domain.enums.Colour;
import ludo.domain.enums.Direction;
import ludo.domain.enums.PieceEffect;
import ludo.domain.enums.PieceState;
import ludo.domain.enums.TeleportDestination;
import ludo.domain.model.Board;
import ludo.domain.model.Piece;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BetaTeleportStrategyTest {

    private BetaTeleportStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new BetaTeleportStrategy();
    }

    @Test
    void shouldHandleBetaDestination() {
        assertEquals(TeleportDestination.BETA, strategy.getDestination());
    }

    @Test
    void shouldTeleportPieceToBeta() {
        Piece piece = new Piece(Colour.BLUE, 1);

        piece.enterBoard(13, Direction.CLOCKWISE);
        piece.moveTo(20);

        strategy.teleport(piece);

        assertEquals(PieceState.STANDARD_PATH, piece.getState());
        assertEquals(Board.BETA_POSITION, piece.getPosition());
    }

    @Test
    void shouldApplyBriefingForFourRounds() {
        Piece piece = new Piece(Colour.BLUE, 1);

        piece.enterBoard(13, Direction.CLOCKWISE);

        strategy.teleport(piece);

        assertEquals(PieceEffect.BRIEFING, piece.getEffect());
        assertEquals(4, piece.getEffectRoundsRemaining());
    }

    @Test
    void shouldPreserveDirection() {
        Piece piece = new Piece(Colour.BLUE, 1);

        piece.enterBoard(13, Direction.COUNTERCLOCKWISE);

        strategy.teleport(piece);

        assertEquals(Direction.COUNTERCLOCKWISE, piece.getDirection());
    }

    @Test
    void shouldPreserveCaptureInformation() {
        Piece piece = new Piece(Colour.BLUE, 1);

        piece.enterBoard(13, Direction.CLOCKWISE);
        piece.recordCapture();

        strategy.teleport(piece);

        assertTrue(piece.hasCaptured());
    }

    @Test
    void shouldRejectNullPiece() {
        assertThrows(IllegalArgumentException.class, () -> strategy.teleport(null));
    }
}