package ludo.strategy.teleport;

import ludo.domain.enums.Colour;
import ludo.domain.enums.Direction;
import ludo.domain.enums.PieceEffect;
import ludo.domain.enums.PieceState;
import ludo.domain.enums.TeleportDestination;
import ludo.domain.model.Board;
import ludo.domain.model.Piece;
import ludo.random.AlphaEffectSelector;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AlphaTeleportStrategyTest {

    private AlphaEffectSelector effectSelector;
    private AlphaTeleportStrategy strategy;

    @BeforeEach
    void setUp() {
        effectSelector = mock(AlphaEffectSelector.class);
        strategy = new AlphaTeleportStrategy(effectSelector);
    }

    @Test
    void shouldHandleAlphaDestination() {
        assertEquals(TeleportDestination.ALPHA, strategy.getDestination());
    }

    @Test
    void shouldTeleportPieceToAlphaAndApplyEnergisedEffect() {
        Piece piece = new Piece(Colour.RED, 1);

        piece.enterBoard(26, Direction.CLOCKWISE);
        piece.moveTo(20);

        when(effectSelector.selectEffect()).thenReturn(PieceEffect.ENERGISED);

        strategy.teleport(piece);

        assertEquals(PieceState.STANDARD_PATH, piece.getState());
        assertEquals(Board.ALPHA_POSITION, piece.getPosition());
        assertEquals(PieceEffect.ENERGISED, piece.getEffect());
        assertEquals(4, piece.getEffectRoundsRemaining());
    }

    @Test
    void shouldTeleportPieceToAlphaAndApplySickEffect() {
        Piece piece = new Piece(Colour.BLUE, 1);

        piece.enterBoard(13, Direction.COUNTERCLOCKWISE);

        when(effectSelector.selectEffect()).thenReturn(PieceEffect.SICK);

        strategy.teleport(piece);

        assertEquals(Board.ALPHA_POSITION, piece.getPosition());
        assertEquals(PieceEffect.SICK, piece.getEffect());
        assertEquals(4, piece.getEffectRoundsRemaining());
    }

    @Test
    void shouldPreserveDirectionWhenTeleportedToAlpha() {
        Piece piece = new Piece(Colour.RED, 1);

        piece.enterBoard(26, Direction.COUNTERCLOCKWISE);

        when(effectSelector.selectEffect()).thenReturn(PieceEffect.ENERGISED);

        strategy.teleport(piece);

        assertEquals(Direction.COUNTERCLOCKWISE, piece.getDirection());
    }

    @Test
    void shouldPreserveCaptureInformationWhenTeleportedToAlpha() {
        Piece piece = new Piece(Colour.RED, 1);

        piece.enterBoard(26, Direction.CLOCKWISE);
        piece.recordCapture();

        when(effectSelector.selectEffect()).thenReturn(PieceEffect.SICK);

        strategy.teleport(piece);

        assertTrue(piece.hasCaptured());
    }

    @Test
    void shouldNotTeleportWhenAlphaEffectIsInvalid() {
        Piece piece = new Piece(Colour.RED, 1);

        piece.enterBoard(26, Direction.CLOCKWISE);
        piece.moveTo(20);

        when(effectSelector.selectEffect()).thenReturn(PieceEffect.BRIEFING);

        assertThrows(IllegalStateException.class, () -> strategy.teleport(piece));

        assertEquals(20, piece.getPosition());
        assertEquals(PieceEffect.NONE, piece.getEffect());
    }

    @Test
    void shouldRejectNullPiece() {
        assertThrows(IllegalArgumentException.class, () -> strategy.teleport(null));

        verifyNoInteractions(effectSelector);
    }

    @Test
    void shouldRejectNullEffectSelector() {
        assertThrows(IllegalArgumentException.class, () -> new AlphaTeleportStrategy(null));
    }
}