package ludo.engine;

import ludo.domain.enums.Colour;
import ludo.domain.model.GameState;
import ludo.domain.model.Player;
import ludo.output.GameOutput;
import ludo.service.FirstPlayerSelector;

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
    private GameOutput gameOutput;
    private FirstPlayerSelector firstPlayerSelector;
    private List<Player> players;

    @BeforeEach
    void setUp() {
        roundManager = mock(RoundManager.class);
        red = new Player(Colour.RED);
        green = new Player(Colour.GREEN);
        yellow = new Player(Colour.YELLOW);
        blue = new Player(Colour.BLUE);
        players = List.of(red, green, yellow, blue);
        gameState = new GameState(players);
        gameOutput = mock(GameOutput.class);
        firstPlayerSelector = mock(FirstPlayerSelector.class);
        gameEngine = new GameEngine(gameState, roundManager, firstPlayerSelector, gameOutput);

        when(firstPlayerSelector.determineOrder(gameState.getPlayers())).thenReturn(players);
    }

    @Test
    void shouldReturnWinner() {
        when(roundManager.playRound(players)).thenReturn(red);

        Player winner = gameEngine.play();

        assertSame(red, winner);
    }

    @Test
    void shouldContinuePlayingUntilWinnerExists() {
        when(roundManager.playRound(players)).thenReturn(null, null, green);

        Player winner = gameEngine.play();

        assertSame(green, winner);
        verify(roundManager, times(3)).playRound(players);
    }

    @Test
    void shouldStopPlayingAfterWinnerIsFound() {
        when(roundManager.playRound(players)).thenReturn(yellow);

        Player winner = gameEngine.play();

        assertSame(yellow, winner);
        verify(roundManager).playRound(players);
    }

    @Test
    void shouldShowPlayerPiecesBeforeGameBegins() {
        when(roundManager.playRound(players)).thenReturn(red);

        gameEngine.play();

        verify(gameOutput).showPlayerPieces(red);
        verify(gameOutput).showPlayerPieces(green);
        verify(gameOutput).showPlayerPieces(yellow);
        verify(gameOutput).showPlayerPieces(blue);
    }

    @Test
    void shouldUseSelectedPlayerOrderForRounds() {
        List<Player> selectedOrder = List.of(yellow, blue, red, green);

        when(firstPlayerSelector.determineOrder(gameState.getPlayers())).thenReturn(selectedOrder);
        when(roundManager.playRound(selectedOrder)).thenReturn(yellow);

        gameEngine.play();

        verify(roundManager).playRound(selectedOrder);
    }

    @Test
    void shouldShowWinnerWhenGameEnds() {
        when(roundManager.playRound(players)).thenReturn(blue);

        gameEngine.play();

        verify(gameOutput).showWinner(blue);
    }

    @Test
    void shouldRejectNullGameState() {
        assertThrows(IllegalArgumentException.class, () -> new GameEngine(null, roundManager, firstPlayerSelector, gameOutput));
    }

    @Test
    void shouldRejectNullRoundManager() {
        assertThrows(IllegalArgumentException.class, () -> new GameEngine(gameState, null, firstPlayerSelector, gameOutput));
    }

    @Test
    void shouldRejectNullFirstPlayerSelector() {
        assertThrows(IllegalArgumentException.class, () -> new GameEngine(gameState, roundManager, null, gameOutput));
    }

    @Test
    void shouldRejectNullGameOutput() {
        assertThrows(IllegalArgumentException.class, () -> new GameEngine(gameState, roundManager, firstPlayerSelector, null));
    }
}