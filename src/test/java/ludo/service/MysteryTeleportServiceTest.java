package ludo.service;

import ludo.domain.enums.Colour;
import ludo.domain.enums.TeleportDestination;
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
                destinationSelector,
                List.of(alphaStrategy, betaStrategy));
    }

    @Test
    void shouldUseStrategyForSelectedDestination() {
        Piece piece = new Piece(Colour.RED, 1);

        when(destinationSelector.selectDestination()).thenReturn(TeleportDestination.ALPHA);

        TeleportDestination destination = teleportService.teleport(piece);

        assertEquals(TeleportDestination.ALPHA, destination);
        verify(alphaStrategy).teleport(piece);
        verify(betaStrategy, never()).teleport(piece);
    }

    @Test
    void shouldUseDifferentStrategyForDifferentDestination() {
        Piece piece = new Piece(Colour.BLUE, 1);

        when(destinationSelector.selectDestination()).thenReturn(TeleportDestination.BETA);

        TeleportDestination destination = teleportService.teleport(piece);

        assertEquals(TeleportDestination.BETA, destination);
        verify(betaStrategy).teleport(piece);
        verify(alphaStrategy, never()).teleport(piece);
    }

    @Test
    void shouldRejectDestinationWithoutRegisteredStrategy() {
        Piece piece = new Piece(Colour.GREEN, 1);

        when(destinationSelector.selectDestination()).thenReturn(TeleportDestination.GAMMA);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> teleportService.teleport(piece));

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
                () -> new MysteryTeleportService(
                        destinationSelector,
                        List.of(alphaStrategy, anotherAlphaStrategy)));
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
}