package ludo.engine;

import ludo.domain.enums.Colour;
import ludo.domain.enums.Direction;
import ludo.domain.model.Board;
import ludo.domain.model.GameState;
import ludo.domain.model.Piece;
import ludo.domain.model.Player;
import ludo.observer.RoundNotifier;
import ludo.output.GameOutput;
import ludo.service.FirstPlayerSelector;
import ludo.service.GameCompletionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameEngineIntegrationTest {

    private TurnManager turnManager;
    private RoundNotifier roundNotifier;
    private Player red;
    private Player green;
    private Player yellow;
    private Player blue;
    private Board board;
    private GameEngine gameEngine;
    private FirstPlayerSelector firstPlayerSelector;
    private GameOutput gameOutput;

    @BeforeEach
    void setUp() {
        turnManager = mock(TurnManager.class);
        roundNotifier = mock(RoundNotifier.class);
        red = new Player(Colour.RED);
        green = new Player(Colour.GREEN);
        yellow = new Player(Colour.YELLOW);
        blue = new Player(Colour.BLUE);
        board = new Board();

        List<Player> players = List.of(red, green, yellow, blue);
        GameState gameState = new GameState(players);
        firstPlayerSelector = mock(FirstPlayerSelector.class);
        gameOutput = mock(GameOutput.class);

        when(firstPlayerSelector.determineOrder(gameState.getPlayers())).thenReturn(players);

        GameCompletionService gameCompletionService = new GameCompletionService();
        RoundManager roundManager = new RoundManager(turnManager, roundNotifier, gameCompletionService);
        gameEngine = new GameEngine(gameState, roundManager, firstPlayerSelector, gameOutput);
    }

    @Test
    void shouldStopGameWhenFirstPlayerWins() {
        makePlayerWinDuringTurn(red);

        Player winner = gameEngine.play();

        assertSame(red, winner);
        verify(turnManager).takeTurn(red);
        verify(turnManager, never()).takeTurn(green);
        verify(turnManager, never()).takeTurn(yellow);
        verify(turnManager, never()).takeTurn(blue);
        verify(roundNotifier, never()).notifyRoundCompleted();
        verify(gameOutput).showWinner(red);
    }

    @Test
    void shouldStopGameWhenLaterPlayerWins() {
        makePlayerWinDuringTurn(green);

        Player winner = gameEngine.play();

        assertSame(green, winner);
        verify(turnManager).takeTurn(red);
        verify(turnManager).takeTurn(green);
        verify(turnManager, never()).takeTurn(yellow);
        verify(turnManager, never()).takeTurn(blue);
        verify(roundNotifier, never()).notifyRoundCompleted();
        verify(gameOutput).showWinner(green);
    }

    private void makePlayerWinDuringTurn(Player player) {
        doAnswer(invocation -> {
            moveAllPiecesHome(player);
            return null;
        }).when(turnManager).takeTurn(player);
    }

    private void moveAllPiecesHome(Player player) {
        for (Piece piece : player.getPieces()) {
            piece.enterBoard(board.getStartPosition(player.getColour()), Direction.CLOCKWISE);
            piece.enterHomeStraight(Board.HOME_STRAIGHT_SIZE - 1);
            piece.reachHome();
        }
    }
}