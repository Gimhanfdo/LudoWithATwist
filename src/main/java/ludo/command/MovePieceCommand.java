package ludo.command;

import ludo.domain.enums.ActionType;
import ludo.domain.model.GameAction;
import ludo.domain.model.Piece;
import ludo.service.MovementCoordinator;

public class MovePieceCommand implements GameActionCommand {

    private final MovementCoordinator movementCoordinator;

    public MovePieceCommand(MovementCoordinator movementCoordinator) {
        if (movementCoordinator == null) {
            throw new IllegalArgumentException("Movement coordinator cannot be null.");
        }

        this.movementCoordinator = movementCoordinator;
    }

    @Override
    public void execute(GameAction action) {
        validateAction(action);

        Piece piece = action.getPieces().get(0);

        movementCoordinator.move(piece, action.getRoll());
    }

    private void validateAction(GameAction action) {
        if (action == null) {
            throw new IllegalArgumentException("Game action cannot be null.");
        }

        if (action.getType() != ActionType.MOVE_PIECE) {
            throw new IllegalArgumentException("Action must be MOVE_PIECE.");
        }

        if (action.getPieces().size() != 1) {
            throw new IllegalArgumentException("Move piece action must contain one piece.");
        }
    }
}