package ludo.engine;

import ludo.domain.enums.Colour;
import ludo.domain.enums.Direction;
import ludo.domain.model.Board;
import ludo.domain.model.GameState;
import ludo.domain.model.Piece;
import ludo.domain.model.Player;
import ludo.observer.RoundNotifier;
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

    @BeforeEach
    void setUp() {
        turnManager = mock(TurnManager.class);
        roundNotifier = mock(RoundNotifier.class);
        red = new Player(Colour.RED);
        green = new Player(Colour.GREEN);
        yellow = new Player(Colour.YELLOW);
        blue = new Player(Colour.BLUE);
        board = new Board();

        GameState gameState = new GameState(List.of(red, green, yellow, blue));
        GameCompletionService gameCompletionService = new GameCompletionService();
        RoundManager roundManager = new RoundManager(turnManager, roundNotifier, gameCompletionService);
        gameEngine = new GameEngine(gameState, roundManager);
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