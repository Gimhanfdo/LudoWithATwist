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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GammaTeleportStrategyTest {

    private TeleportStrategy betaStrategy;
    private GammaTeleportStrategy strategy;

    @BeforeEach
    void setUp() {
        betaStrategy = mock(TeleportStrategy.class);

        when(betaStrategy.getDestination()).thenReturn(TeleportDestination.BETA);

        strategy = new GammaTeleportStrategy(betaStrategy);
    }

    @Test
    void shouldHandleGammaDestination() {
        assertEquals(TeleportDestination.GAMMA, strategy.getDestination());
    }

    @Test
    void shouldTeleportClockwisePieceToGammaAndReverseDirection() {
        Piece piece = new Piece(Colour.RED, 1);

        piece.enterBoard(26, Direction.CLOCKWISE);
        piece.moveTo(20);

        strategy.teleport(piece);

        assertEquals(PieceState.STANDARD_PATH, piece.getState());
        assertEquals(Board.GAMMA_POSITION, piece.getPosition());
        assertEquals(Direction.COUNTERCLOCKWISE, piece.getDirection());
    }

    @Test
    void shouldNotUseBetaStrategyForClockwisePiece() {
        Piece piece = new Piece(Colour.RED, 1);

        piece.enterBoard(26, Direction.CLOCKWISE);

        strategy.teleport(piece);

        verify(betaStrategy, never()).teleport(piece);
    }

    @Test
    void shouldDelegateCounterclockwisePieceToBeta() {
        Piece piece = new Piece(Colour.BLUE, 1);

        piece.enterBoard(13, Direction.COUNTERCLOCKWISE);

        strategy.teleport(piece);

        verify(betaStrategy).teleport(piece);
    }

    @Test
    void shouldApplyBetaBehaviourToCounterclockwisePiece() {
        GammaTeleportStrategy realStrategy = new GammaTeleportStrategy(new BetaTeleportStrategy());
        Piece piece = new Piece(Colour.BLUE, 1);

        piece.enterBoard(13, Direction.COUNTERCLOCKWISE);

        realStrategy.teleport(piece);

        assertEquals(Board.BETA_POSITION, piece.getPosition());
        assertEquals(PieceEffect.BRIEFING, piece.getEffect());
        assertEquals(4, piece.getEffectRoundsRemaining());
    }

    @Test
    void shouldPreserveCaptureInformationForClockwisePiece() {
        Piece piece = new Piece(Colour.RED, 1);

        piece.enterBoard(26, Direction.CLOCKWISE);
        piece.recordCapture();

        strategy.teleport(piece);

        assertTrue(piece.hasCaptured());
    }

    @Test
    void shouldRejectNullPiece() {
        assertThrows(IllegalArgumentException.class, () -> strategy.teleport(null));

        verify(betaStrategy, never()).teleport(any());
    }

    @Test
    void shouldRejectNullBetaStrategy() {
        assertThrows(IllegalArgumentException.class, () -> new GammaTeleportStrategy(null));
    }

    @Test
    void shouldRejectNonBetaStrategy() {
        TeleportStrategy alphaStrategy = mock(TeleportStrategy.class);

        when(alphaStrategy.getDestination()).thenReturn(TeleportDestination.ALPHA);

        assertThrows(IllegalArgumentException.class, () -> new GammaTeleportStrategy(alphaStrategy));
    }

    @Test
    void shouldRejectPieceWithoutDirection() {
        Piece piece = new Piece(Colour.RED, 1);

        piece.teleportToStandardPath(10);

        assertThrows(IllegalStateException.class, () -> strategy.teleport(piece));
    }
}