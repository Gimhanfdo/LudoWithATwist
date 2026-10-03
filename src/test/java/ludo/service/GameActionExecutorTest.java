package ludo.service;

import ludo.command.GameActionCommand;
import ludo.domain.enums.ActionResult;
import ludo.domain.enums.ActionType;
import ludo.domain.enums.Colour;
import ludo.domain.enums.Direction;
import ludo.domain.model.ActionExecutionResult;
import ludo.domain.model.GameAction;
import ludo.domain.model.MovementOutcome;
import ludo.domain.model.MovementResult;
import ludo.domain.model.Piece;
import ludo.factory.GameActionCommandFactory;
import ludo.output.ActionReporter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameActionExecutorTest {

    private GameActionCommandFactory commandFactory;
    private GameActionCommand command;
    private ActionReporter actionReporter;
    private GameActionExecutor executor;

    @BeforeEach
    void setUp() {
        commandFactory = mock(GameActionCommandFactory.class);
        command = mock(GameActionCommand.class);
        actionReporter = mock(ActionReporter.class);
        executor = new GameActionExecutor(commandFactory, actionReporter);
    }

    @Test
    void shouldExecuteCommandAndReturnResult() {
        Piece piece = new Piece(Colour.RED, 1);
        piece.enterBoard(26, Direction.CLOCKWISE);
        GameAction action = new GameAction(ActionType.MOVE_PIECE, List.of(piece), 4);
        MovementOutcome movementOutcome = new MovementOutcome(MovementResult.CAPTURED, 4, 4, 26, 30, List.of(), List.of());
        ActionExecutionResult executionResult = ActionExecutionResult.withMovement(ActionResult.CAPTURED, movementOutcome);

        when(commandFactory.getCommand(ActionType.MOVE_PIECE)).thenReturn(command);
        when(command.execute(action)).thenReturn(executionResult);

        ActionResult result = executor.execute(action);

        assertEquals(ActionResult.CAPTURED, result);
        verify(commandFactory).getCommand(ActionType.MOVE_PIECE);
        verify(command).execute(action);
    }

    @Test
    void shouldReportExecutedAction() {
        Piece piece = new Piece(Colour.RED, 1);
        piece.enterBoard(26, Direction.CLOCKWISE);
        GameAction action = new GameAction(ActionType.MOVE_PIECE, List.of(piece), 4);
        MovementOutcome movementOutcome = new MovementOutcome(MovementResult.MOVED, 4, 4, 26, 30, List.of(), List.of());
        ActionExecutionResult executionResult = ActionExecutionResult.withMovement(ActionResult.MOVED, movementOutcome);

        when(commandFactory.getCommand(ActionType.MOVE_PIECE)).thenReturn(command);
        when(command.execute(action)).thenReturn(executionResult);

        executor.execute(action);

        verify(actionReporter).report(action, executionResult, Direction.CLOCKWISE);
    }

    @Test
    void shouldReportNullPreviousDirectionForEnterBoard() {
        Piece piece = new Piece(Colour.RED, 1);
        GameAction action = new GameAction(ActionType.ENTER_BOARD, List.of(piece), 6);
        ActionExecutionResult executionResult = ActionExecutionResult.of(ActionResult.MOVED);

        when(commandFactory.getCommand(ActionType.ENTER_BOARD)).thenReturn(command);
        when(command.execute(action)).thenReturn(executionResult);

        executor.execute(action);

        verify(actionReporter).report(action, executionResult, null);
    }

    @Test
    void shouldRejectNullAction() {
        assertThrows(IllegalArgumentException.class, () -> executor.execute(null));
        verifyNoInteractions(commandFactory, actionReporter);
    }

    @Test
    void shouldRejectNullCommandFactory() {
        assertThrows(IllegalArgumentException.class, () -> new GameActionExecutor(null, actionReporter));
    }

    @Test
    void shouldRejectNullActionReporter() {
        assertThrows(IllegalArgumentException.class, () -> new GameActionExecutor(commandFactory, null));
    }
}