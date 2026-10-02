package ludo.engine;

import ludo.domain.enums.Colour;
import ludo.domain.model.Player;
import ludo.observer.RoundNotifier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoundManagerTest {

    private TurnManager turnManager;
    private RoundNotifier roundNotifier;
    private RoundManager roundManager;

    @BeforeEach
    void setUp() {
        turnManager = mock(TurnManager.class);
        roundNotifier = mock(RoundNotifier.class);
        roundManager = new RoundManager(turnManager, roundNotifier);
    }

    @Test
    void shouldGiveEveryPlayerOneTurn() {
        Player red = new Player(Colour.RED);
        Player green = new Player(Colour.GREEN);
        Player yellow = new Player(Colour.YELLOW);
        Player blue = new Player(Colour.BLUE);

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

        roundManager.playRound(List.of(red, green, yellow, blue));

        verify(roundNotifier, times(1)).notifyRoundCompleted();
    }

    @Test
    void shouldNotifyObserversAfterAllTurnsComplete() {
        Player red = new Player(Colour.RED);
        Player green = new Player(Colour.GREEN);
        Player yellow = new Player(Colour.YELLOW);
        Player blue = new Player(Colour.BLUE);

        roundManager.playRound(List.of(red, green, yellow, blue));

        var inOrder = inOrder(turnManager, roundNotifier);

        inOrder.verify(turnManager).takeTurn(red);
        inOrder.verify(turnManager).takeTurn(green);
        inOrder.verify(turnManager).takeTurn(yellow);
        inOrder.verify(turnManager).takeTurn(blue);
        inOrder.verify(roundNotifier).notifyRoundCompleted();
    }

    @Test
    void shouldRejectNullTurnManager() {
        assertThrows(IllegalArgumentException.class, () -> new RoundManager(null, roundNotifier));
    }

    @Test
    void shouldRejectNullRoundNotifier() {
        assertThrows(IllegalArgumentException.class, () -> new RoundManager(turnManager, null));
    }

    @Test
    void shouldRejectNullPlayers() {
        assertThrows(IllegalArgumentException.class, () -> roundManager.playRound(null));
        verifyNoInteractions(turnManager, roundNotifier);
    }

    @Test
    void shouldRejectEmptyPlayers() {
        assertThrows(IllegalArgumentException.class, () -> roundManager.playRound(List.of()));
        verifyNoInteractions(turnManager, roundNotifier);
    }

    @Test
    void shouldRejectNullPlayer() {
        Player red = new Player(Colour.RED);
        List<Player> players = new java.util.ArrayList<>();

        players.add(red);
        players.add(null);

        assertThrows(IllegalArgumentException.class, () -> roundManager.playRound(players));
        verifyNoInteractions(turnManager, roundNotifier);
    }
}