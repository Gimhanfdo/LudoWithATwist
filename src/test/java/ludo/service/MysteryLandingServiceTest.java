package ludo.service;

import ludo.domain.enums.Colour;
import ludo.domain.enums.Direction;
import ludo.domain.enums.PieceEffect;
import ludo.domain.enums.PieceState;
import ludo.domain.enums.TeleportDestination;
import ludo.domain.model.Board;
import ludo.domain.model.MysteryCell;
import ludo.domain.model.MysteryTeleportOutcome;
import ludo.domain.model.Piece;
import ludo.random.TeleportDestinationSelector;
import ludo.strategy.teleport.BetaTeleportStrategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MysteryLandingServiceTest {

    private MysteryCell mysteryCell;
    private MysteryTeleportService teleportService;
    private MysteryLandingService service;

    @BeforeEach
    void setUp() {
        mysteryCell = new MysteryCell();
        teleportService = mock(MysteryTeleportService.class);
        service = new MysteryLandingService(mysteryCell, teleportService);
    }

    @Test
    void shouldTeleportPieceWhenLandingOnMysteryCell() {
        mysteryCell.activate(20);

        Piece piece = new Piece(Colour.RED, 1);
        piece.enterBoard(26, Direction.CLOCKWISE);
        piece.moveTo(20);

        MysteryTeleportOutcome expected = new MysteryTeleportOutcome(
                TeleportDestination.ALPHA, PieceState.STANDARD_PATH, Board.ALPHA_POSITION,
                Direction.CLOCKWISE, Direction.CLOCKWISE, PieceEffect.ENERGISED);

        when(teleportService.teleport(piece)).thenReturn(expected);

        MysteryTeleportOutcome outcome = service.resolveLanding(piece);

        assertSame(expected, outcome);

        verify(teleportService).teleport(piece);
    }

    @Test
    void shouldNotTeleportWhenPieceDoesNotLandOnMysteryCell() {
        mysteryCell.activate(20);

        Piece piece = new Piece(Colour.RED, 1);
        piece.enterBoard(26, Direction.CLOCKWISE);
        piece.moveTo(22);

        MysteryTeleportOutcome outcome = service.resolveLanding(piece);

        assertNull(outcome);

        verify(teleportService, never()).teleport(any());
    }

    @Test
    void shouldNotTeleportWhenMysteryCellIsInactive() {
        Piece piece = new Piece(Colour.RED, 1);
        piece.enterBoard(26, Direction.CLOCKWISE);
        piece.moveTo(20);

        MysteryTeleportOutcome outcome = service.resolveLanding(piece);

        assertNull(outcome);

        verify(teleportService, never()).teleport(any());
    }

    @Test
    void shouldNotTeleportPieceInHomeStraight() {
        mysteryCell.activate(0);

        Piece piece = new Piece(Colour.YELLOW, 1);
        piece.enterBoard(0, Direction.CLOCKWISE);
        piece.recordCapture();
        piece.enterHomeStraight(0);

        MysteryTeleportOutcome outcome = service.resolveLanding(piece);

        assertNull(outcome);

        verify(teleportService, never()).teleport(any());
    }

    @Test
    void shouldRejectNullPiece() {
        assertThrows(IllegalArgumentException.class, () -> service.resolveLanding(null));

        verify(teleportService, never()).teleport(any());
    }

    @Test
    void shouldRejectNullMysteryCell() {
        assertThrows(IllegalArgumentException.class,
                () -> new MysteryLandingService(null, teleportService));
    }

    @Test
    void shouldRejectNullTeleportService() {
        assertThrows(IllegalArgumentException.class,
                () -> new MysteryLandingService(mysteryCell, null));
    }

    @Test
    void shouldApplySelectedTeleportStrategyAfterMysteryLanding() {
        mysteryCell.activate(20);

        TeleportDestinationSelector selector = mock(TeleportDestinationSelector.class);
        when(selector.selectDestination()).thenReturn(TeleportDestination.BETA);

        MysteryTeleportService realTeleportService = new MysteryTeleportService(
                selector, List.of(new BetaTeleportStrategy()));

        MysteryLandingService realService = new MysteryLandingService(mysteryCell, realTeleportService);

        Piece piece = new Piece(Colour.RED, 1);
        piece.enterBoard(26, Direction.CLOCKWISE);
        piece.moveTo(20);

        MysteryTeleportOutcome outcome = realService.resolveLanding(piece);

        assertNotNull(outcome);
        assertEquals(TeleportDestination.BETA, outcome.getSelectedDestination());
        assertEquals(PieceState.STANDARD_PATH, outcome.getFinalState());
        assertEquals(Board.BETA_POSITION, outcome.getFinalPosition());
        assertEquals(PieceEffect.BRIEFING, outcome.getFinalEffect());
        assertEquals(Board.BETA_POSITION, piece.getPosition());
        assertEquals(PieceEffect.BRIEFING, piece.getEffect());
        assertEquals(4, piece.getEffectRoundsRemaining());
    }

    @Test
    void shouldNotApplyBetaEffectWhenNormallyLandingOnBetaPosition() {
        mysteryCell.activate(10);

        Piece piece = new Piece(Colour.BLUE, 1);
        piece.enterBoard(13, Direction.CLOCKWISE);
        piece.moveTo(Board.BETA_POSITION);

        MysteryTeleportOutcome outcome = service.resolveLanding(piece);

        assertNull(outcome);
        assertEquals(PieceEffect.NONE, piece.getEffect());

        verify(teleportService, never()).teleport(any());
    }
}