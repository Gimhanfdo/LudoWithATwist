package ludo.engine;

import ludo.domain.enums.Colour;
import ludo.domain.model.GameState;
import ludo.domain.model.Player;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameEngineTest {

    private RoundManager roundManager;
    private Player red;
    private Player green;
    private Player yellow;
    private Player blue;
    private GameState gameState;
    private GameEngine gameEngine;

    @BeforeEach
    void setUp() {
        roundManager = mock(RoundManager.class);
        red = new Player(Colour.RED);
        green = new Player(Colour.GREEN);
        yellow = new Player(Colour.YELLOW);
        blue = new Player(Colour.BLUE);
        gameState = new GameState(List.of(red, green, yellow, blue));
        gameEngine = new GameEngine(gameState, roundManager);
    }

    @Test
    void shouldReturnWinner() {
        when(roundManager.playRound(gameState.getPlayers())).thenReturn(red);

        Player winner = gameEngine.play();

        assertSame(red, winner);
    }

    @Test
    void shouldContinuePlayingUntilWinnerExists() {
        when(roundManager.playRound(gameState.getPlayers())).thenReturn(null, null, green);

        Player winner = gameEngine.play();

        assertSame(green, winner);
        verify(roundManager, times(3)).playRound(gameState.getPlayers());
    }

    @Test
    void shouldStopPlayingAfterWinnerIsFound() {
        when(roundManager.playRound(gameState.getPlayers())).thenReturn(yellow);

        Player winner = gameEngine.play();

        assertSame(yellow, winner);
        verify(roundManager, times(1)).playRound(gameState.getPlayers());
    }

    @Test
    void shouldRejectNullGameState() {
        assertThrows(IllegalArgumentException.class, () -> new GameEngine(null, roundManager));
    }

    @Test
    void shouldRejectNullRoundManager() {
        assertThrows(IllegalArgumentException.class, () -> new GameEngine(gameState, null));
    }
}