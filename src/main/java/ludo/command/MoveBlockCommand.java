package ludo.command;

import ludo.domain.enums.ActionType;
import ludo.domain.model.GameAction;
import ludo.domain.model.Piece;
import ludo.service.MovementService;

public class MoveBlockCommand implements GameActionCommand {

    private final MovementService movementService;

    public MoveBlockCommand(MovementService movementService) {
        if (movementService == null) {
            throw new IllegalArgumentException("Movement service cannot be null.");
        }

        this.movementService = movementService;
    }

    @Override
    public void execute(GameAction action) {
        validateAction(action);

        Piece representative = action.getPieces().get(0);

        movementService.moveBlock(representative.getPosition(), representative.getColour(), action.getRoll());
    }

    private void validateAction(GameAction action) {
        if (action == null) {
            throw new IllegalArgumentException("Game action cannot be null.");
        }

        if (action.getType() != ActionType.MOVE_BLOCK) {
            throw new IllegalArgumentException("Action must be MOVE_BLOCK.");
        }

        if (action.getPieces().size() < 2) {
            throw new IllegalArgumentException("Move block action must contain at least two pieces.");
        }
    }
}