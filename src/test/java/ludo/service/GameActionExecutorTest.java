package ludo.service;

import ludo.command.GameActionCommand;
import ludo.domain.enums.ActionResult;
import ludo.domain.enums.ActionType;
import ludo.domain.enums.Colour;
import ludo.domain.enums.Direction;
import ludo.domain.model.GameAction;
import ludo.domain.model.GameState;
import ludo.domain.model.Piece;
import ludo.domain.model.Player;
import ludo.factory.GameActionCommandFactory;
import ludo.output.GameOutput;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameActionExecutorTest {

    private GameActionCommandFactory commandFactory;
    private GameActionCommand command;
    private GameActionExecutor executor;
    private GameState gameState;
    private GameOutput gameOutput;
    private Player red;
    private Player green;
    private Player yellow;
    private Player blue;

    @BeforeEach
    void setUp() {
        commandFactory = mock(GameActionCommandFactory.class);
        command = mock(GameActionCommand.class);
        gameOutput = mock(GameOutput.class);
        red = new Player(Colour.RED);
        green = new Player(Colour.GREEN);
        yellow = new Player(Colour.YELLOW);
        blue = new Player(Colour.BLUE);
        gameState = new GameState(List.of(red, green, yellow, blue));
        executor = new GameActionExecutor(commandFactory, gameState, gameOutput);
    }

    @Test
    void shouldReturnCommandResult() {
        Piece piece = red.getPieces().get(0);
        GameAction action = new GameAction(ActionType.MOVE_PIECE, List.of(piece), 4);

        when(commandFactory.getCommand(ActionType.MOVE_PIECE)).thenReturn(command);
        when(command.execute(action)).thenReturn(ActionResult.CAPTURED);

        ActionResult result = executor.execute(action);

        assertEquals(ActionResult.CAPTURED, result);
        verify(command).execute(action);
    }

    @Test
    void shouldReportPieceEnteringBoard() {
        Piece piece = red.getPieces().get(0);
        GameAction action = new GameAction(ActionType.ENTER_BOARD, List.of(piece), 6);

        when(commandFactory.getCommand(ActionType.ENTER_BOARD)).thenReturn(command);
        when(command.execute(action)).thenAnswer(invocation -> {
            piece.enterBoard(26, Direction.CLOCKWISE);
            return ActionResult.MOVED;
        });

        ActionResult result = executor.execute(action);

        assertEquals(ActionResult.MOVED, result);
        verify(gameOutput).showPieceEnteredBoard(piece, 1, 15);
    }

    @Test
    void shouldNotReportEnterBoardWhenMovementFails() {
        Piece piece = red.getPieces().get(0);
        GameAction action = new GameAction(ActionType.ENTER_BOARD, List.of(piece), 6);

        when(commandFactory.getCommand(ActionType.ENTER_BOARD)).thenReturn(command);
        when(command.execute(action)).thenReturn(ActionResult.NOT_MOVED);

        ActionResult result = executor.execute(action);

        assertEquals(ActionResult.NOT_MOVED, result);
        verify(gameOutput, never()).showPieceEnteredBoard(any(Piece.class), anyInt(), anyInt());
    }

    @Test
    void shouldRejectNullAction() {
        assertThrows(IllegalArgumentException.class, () -> executor.execute(null));
        verifyNoInteractions(commandFactory);
    }

    @Test
    void shouldRejectNullCommandFactory() {
        assertThrows(IllegalArgumentException.class, () -> new GameActionExecutor(null, gameState, gameOutput));
    }

    @Test
    void shouldRejectNullGameState() {
        assertThrows(IllegalArgumentException.class, () -> new GameActionExecutor(commandFactory, null, gameOutput));
    }

    @Test
    void shouldRejectNullGameOutput() {
        assertThrows(IllegalArgumentException.class, () -> new GameActionExecutor(commandFactory, gameState, null));
    }
}