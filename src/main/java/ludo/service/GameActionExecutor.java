package ludo.service;

import ludo.command.GameActionCommand;
import ludo.domain.enums.ActionResult;
import ludo.domain.enums.ActionType;
import ludo.domain.enums.Direction;
import ludo.domain.model.ActionExecutionResult;
import ludo.domain.model.GameAction;
import ludo.domain.model.Piece;
import ludo.factory.GameActionCommandFactory;
import ludo.output.ActionReporter;

public class GameActionExecutor {

    private final GameActionCommandFactory commandFactory;
    private final ActionReporter actionReporter;

    public GameActionExecutor(GameActionCommandFactory commandFactory, ActionReporter actionReporter) {
        if (commandFactory == null) {
            throw new IllegalArgumentException("Command factory cannot be null.");
        }

        if (actionReporter == null) {
            throw new IllegalArgumentException("Action reporter cannot be null.");
        }

        this.commandFactory = commandFactory;
        this.actionReporter = actionReporter;
    }

    public ActionResult execute(GameAction action) {
        validateAction(action);

        Direction previousDirection = getPreviousDirection(action);
        GameActionCommand command = commandFactory.getCommand(action.getType());
        ActionExecutionResult executionResult = command.execute(action);

        actionReporter.report(action, executionResult, previousDirection);

        return executionResult.getResult();
    }

    private Direction getPreviousDirection(GameAction action) {
        if (action.getType() != ActionType.MOVE_PIECE) {
            return null;
        }

        Piece piece = action.getPieces().get(0);

        return piece.getDirection();
    }

    private void validateAction(GameAction action) {
        if (action == null) {
            throw new IllegalArgumentException("Game action cannot be null.");
        }
    }
}