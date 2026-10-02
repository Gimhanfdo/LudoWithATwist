package ludo.engine;

import ludo.domain.enums.Colour;
import ludo.domain.model.Player;
import ludo.observer.RoundNotifier;
import ludo.service.GameCompletionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RoundManagerTest {

    private TurnManager turnManager;
    private RoundNotifier roundNotifier;
    private GameCompletionService gameCompletionService;
    private RoundManager roundManager;

    @BeforeEach
    void setUp() {
        turnManager = mock(TurnManager.class);
        roundNotifier = mock(RoundNotifier.class);
        gameCompletionService = mock(GameCompletionService.class);
        roundManager = new RoundManager(turnManager, roundNotifier, gameCompletionService);
    }

    @Test
    void shouldGiveEveryPlayerOneTurn() {
        Player red = new Player(Colour.RED);
        Player green = new Player(Colour.GREEN);
        Player yellow = new Player(Colour.YELLOW);
        Player blue = new Player(Colour.BLUE);

        when(gameCompletionService.hasWon(any(Player.class))).thenReturn(false);

        roundManager.playRound(List.of(red, green, yellow, blue));

        verify(turnManager).takeTurn(red);
        verify(turnManager).takeTurn(green);
        verify(turnManager).takeTurn(yellow);
        verify(turnManager).takeTurn(blue);
    }

    @Test
    void shouldNotifyRoundCompletionOnce() {
        Player red = new Player(Colour.RED);
        Player green = new Player(Colour.GREEN);
        Player yellow = new Player(Colour.YELLOW);
        Player blue = new Player(Colour.BLUE);

        when(gameCompletionService.hasWon(any(Player.class))).thenReturn(false);

        roundManager.playRound(List.of(red, green, yellow, blue));

        verify(roundNotifier, times(1)).notifyRoundCompleted();
    }

    @Test
    void shouldNotifyObserversAfterAllTurnsComplete() {
        Player red = new Player(Colour.RED);
        Player green = new Player(Colour.GREEN);
        Player yellow = new Player(Colour.YELLOW);
        Player blue = new Player(Colour.BLUE);

        when(gameCompletionService.hasWon(any(Player.class))).thenReturn(false);

        roundManager.playRound(List.of(red, green, yellow, blue));

        var inOrder = inOrder(turnManager, roundNotifier);

        inOrder.verify(turnManager).takeTurn(red);
        inOrder.verify(turnManager).takeTurn(green);
        inOrder.verify(turnManager).takeTurn(yellow);
        inOrder.verify(turnManager).takeTurn(blue);
        inOrder.verify(roundNotifier).notifyRoundCompleted();
    }

    @Test
    void shouldReturnWinnerImmediatelyAfterWinningTurn() {
        Player red = new Player(Colour.RED);
        Player green = new Player(Colour.GREEN);
        Player yellow = new Player(Colour.YELLOW);
        Player blue = new Player(Colour.BLUE);

        when(gameCompletionService.hasWon(red)).thenReturn(true);

        Player winner = roundManager.playRound(List.of(red, green, yellow, blue));

        assertSame(red, winner);
        verify(turnManager).takeTurn(red);
        verify(turnManager, never()).takeTurn(green);
        verify(turnManager, never()).takeTurn(yellow);
        verify(turnManager, never()).takeTurn(blue);
        verify(roundNotifier, never()).notifyRoundCompleted();
    }

    @Test
    void shouldStopWhenLaterPlayerWins() {
        Player red = new Player(Colour.RED);
        Player green = new Player(Colour.GREEN);
        Player yellow = new Player(Colour.YELLOW);
        Player blue = new Player(Colour.BLUE);

        when(gameCompletionService.hasWon(red)).thenReturn(false);
        when(gameCompletionService.hasWon(green)).thenReturn(true);

        Player winner = roundManager.playRound(List.of(red, green, yellow, blue));

        assertSame(green, winner);
        verify(turnManager).takeTurn(red);
        verify(turnManager).takeTurn(green);
        verify(turnManager, never()).takeTurn(yellow);
        verify(turnManager, never()).takeTurn(blue);
        verify(roundNotifier, never()).notifyRoundCompleted();
    }

    @Test
    void shouldReturnNullWhenRoundCompletesWithoutWinner() {
        Player red = new Player(Colour.RED);
        Player green = new Player(Colour.GREEN);
        Player yellow = new Player(Colour.YELLOW);
        Player blue = new Player(Colour.BLUE);

        when(gameCompletionService.hasWon(any(Player.class))).thenReturn(false);

        Player winner = roundManager.playRound(List.of(red, green, yellow, blue));

        assertNull(winner);
        verify(roundNotifier).notifyRoundCompleted();
    }

    @Test
    void shouldRejectNullTurnManager() {
        assertThrows(IllegalArgumentException.class, () -> new RoundManager(null, roundNotifier, gameCompletionService));
    }

    @Test
    void shouldRejectNullRoundNotifier() {
        assertThrows(IllegalArgumentException.class, () -> new RoundManager(turnManager, null, gameCompletionService));
    }

    @Test
    void shouldRejectNullGameCompletionService() {
        assertThrows(IllegalArgumentException.class, () -> new RoundManager(turnManager, roundNotifier, null));
    }

    @Test
    void shouldRejectNullPlayers() {
        assertThrows(IllegalArgumentException.class, () -> roundManager.playRound(null));
        verifyNoInteractions(turnManager, roundNotifier, gameCompletionService);
    }

    @Test
    void shouldRejectEmptyPlayers() {
        assertThrows(IllegalArgumentException.class, () -> roundManager.playRound(List.of()));
        verifyNoInteractions(turnManager, roundNotifier, gameCompletionService);
    }

    @Test
    void shouldRejectNullPlayer() {
        Player red = new Player(Colour.RED);
        List<Player> players = new ArrayList<>();

        players.add(red);
        players.add(null);

        assertThrows(IllegalArgumentException.class, () -> roundManager.playRound(players));
        verifyNoInteractions(turnManager, roundNotifier, gameCompletionService);
    }
}