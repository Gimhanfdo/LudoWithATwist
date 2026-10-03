package ludo.command;

import ludo.domain.enums.ActionResult;
import ludo.domain.enums.ActionType;
import ludo.domain.model.ActionExecutionResult;
import ludo.domain.model.GameAction;
import ludo.domain.model.Piece;
import ludo.service.MoveExecutor;

public class EnterBoardCommand implements GameActionCommand {

    private final MoveExecutor moveExecutor;

    public EnterBoardCommand(MoveExecutor moveExecutor) {
        if (moveExecutor == null) {
            throw new IllegalArgumentException("Move executor cannot be null.");
        }

        this.moveExecutor = moveExecutor;
    }

    @Override
    public ActionExecutionResult execute(GameAction action) {
        validateAction(action);

        Piece piece = action.getPieces().get(0);
        boolean moved = moveExecutor.moveFromBase(piece, action.getRoll());

        ActionResult result = moved ? ActionResult.MOVED : ActionResult.NOT_MOVED;

        return ActionExecutionResult.of(result);
    }

    private void validateAction(GameAction action) {
        if (action == null) {
            throw new IllegalArgumentException("Game action cannot be null.");
        }

        if (action.getType() != ActionType.ENTER_BOARD) {
            throw new IllegalArgumentException("Action must be ENTER_BOARD.");
        }

        if (action.getPieces().size() != 1) {
            throw new IllegalArgumentException("Enter board action must contain one piece.");
        }
    }
}