package ludo.service;

import ludo.domain.enums.Colour;
import ludo.domain.enums.Direction;
import ludo.domain.enums.PieceEffect;
import ludo.domain.enums.PieceState;
import ludo.domain.enums.TeleportDestination;
import ludo.domain.model.MysteryTeleportOutcome;
import ludo.domain.model.Piece;
import ludo.random.TeleportDestinationSelector;
import ludo.strategy.teleport.TeleportStrategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MysteryTeleportServiceTest {

    private TeleportDestinationSelector destinationSelector;
    private TeleportStrategy alphaStrategy;
    private TeleportStrategy betaStrategy;
    private MysteryTeleportService teleportService;

    @BeforeEach
    void setUp() {
        destinationSelector = mock(TeleportDestinationSelector.class);
        alphaStrategy = mock(TeleportStrategy.class);
        betaStrategy = mock(TeleportStrategy.class);

        when(alphaStrategy.getDestination()).thenReturn(TeleportDestination.ALPHA);
        when(betaStrategy.getDestination()).thenReturn(TeleportDestination.BETA);

        teleportService = new MysteryTeleportService(
                destinationSelector, List.of(alphaStrategy, betaStrategy));
    }

    @Test
    void shouldUseStrategyForSelectedDestination() {
        Piece piece = createPiece(Colour.RED);

        when(destinationSelector.selectDestination()).thenReturn(TeleportDestination.ALPHA);

        MysteryTeleportOutcome outcome = teleportService.teleport(piece);

        assertEquals(TeleportDestination.ALPHA, outcome.getSelectedDestination());
        assertEquals(PieceState.STANDARD_PATH, outcome.getFinalState());
        assertEquals(26, outcome.getFinalPosition());
        assertEquals(Direction.CLOCKWISE, outcome.getPreviousDirection());
        assertEquals(Direction.CLOCKWISE, outcome.getFinalDirection());
        assertEquals(PieceEffect.NONE, outcome.getFinalEffect());

        verify(alphaStrategy).teleport(piece);
        verify(betaStrategy, never()).teleport(piece);
    }

    @Test
    void shouldUseDifferentStrategyForDifferentDestination() {
        Piece piece = createPiece(Colour.BLUE);

        when(destinationSelector.selectDestination()).thenReturn(TeleportDestination.BETA);

        MysteryTeleportOutcome outcome = teleportService.teleport(piece);

        assertEquals(TeleportDestination.BETA, outcome.getSelectedDestination());

        verify(betaStrategy).teleport(piece);
        verify(alphaStrategy, never()).teleport(piece);
    }

    @Test
    void shouldCaptureStateAfterStrategyExecution() {
        Piece piece = createPiece(Colour.RED);

        when(destinationSelector.selectDestination()).thenReturn(TeleportDestination.ALPHA);
        doAnswer(invocation -> {
            piece.teleportToStandardPath(8);
            piece.applyEffect(PieceEffect.ENERGISED, 4);
            return null;
        }).when(alphaStrategy).teleport(piece);

        MysteryTeleportOutcome outcome = teleportService.teleport(piece);

        assertEquals(TeleportDestination.ALPHA, outcome.getSelectedDestination());
        assertEquals(PieceState.STANDARD_PATH, outcome.getFinalState());
        assertEquals(8, outcome.getFinalPosition());
        assertEquals(Direction.CLOCKWISE, outcome.getPreviousDirection());
        assertEquals(Direction.CLOCKWISE, outcome.getFinalDirection());
        assertEquals(PieceEffect.ENERGISED, outcome.getFinalEffect());
    }

    @Test
    void shouldRejectDestinationWithoutRegisteredStrategy() {
        Piece piece = createPiece(Colour.GREEN);

        when(destinationSelector.selectDestination()).thenReturn(TeleportDestination.GAMMA);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class, () -> teleportService.teleport(piece));

        assertEquals("No teleport strategy registered for GAMMA.", exception.getMessage());
    }

    @Test
    void shouldRejectNullPiece() {
        assertThrows(IllegalArgumentException.class, () -> teleportService.teleport(null));

        verifyNoInteractions(destinationSelector);
    }

    @Test
    void shouldRejectDuplicateDestinationStrategies() {
        TeleportStrategy anotherAlphaStrategy = mock(TeleportStrategy.class);

        when(anotherAlphaStrategy.getDestination()).thenReturn(TeleportDestination.ALPHA);

        assertThrows(IllegalArgumentException.class,
                () -> new MysteryTeleportService(destinationSelector, List.of(alphaStrategy, anotherAlphaStrategy)));
    }

    @Test
    void shouldRejectNullDestinationSelector() {
        assertThrows(IllegalArgumentException.class,
                () -> new MysteryTeleportService(null, List.of(alphaStrategy)));
    }

    @Test
    void shouldRejectNullStrategyList() {
        assertThrows(IllegalArgumentException.class,
                () -> new MysteryTeleportService(destinationSelector, null));
    }

    @Test
    void shouldRejectNullStrategy() {
        List<TeleportStrategy> strategies = new java.util.ArrayList<>();

        strategies.add(alphaStrategy);
        strategies.add(null);

        assertThrows(IllegalArgumentException.class,
                () -> new MysteryTeleportService(destinationSelector, strategies));
    }

    @Test
    void shouldRejectStrategyWithNullDestination() {
        TeleportStrategy invalidStrategy = mock(TeleportStrategy.class);

        when(invalidStrategy.getDestination()).thenReturn(null);

        assertThrows(IllegalArgumentException.class,
                () -> new MysteryTeleportService(destinationSelector, List.of(invalidStrategy)));
    }

    private Piece createPiece(Colour colour) {
        Piece piece = new Piece(colour, 1);

        int startPosition = switch (colour) {
            case YELLOW -> 0;
            case BLUE -> 13;
            case RED -> 26;
            case GREEN -> 39;
        };

        piece.enterBoard(startPosition, Direction.CLOCKWISE);

        return piece;
    }
}