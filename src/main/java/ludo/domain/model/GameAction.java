package ludo.domain.model;

import ludo.domain.enums.ActionType;

import java.util.List;

public class GameAction {

    private final ActionType type;
    private final List<Piece> pieces;
    private final int roll;

    public GameAction(ActionType type, List<Piece> pieces, int roll) {
        validateType(type);
        validatePieces(pieces);
        validateRoll(roll);

        this.type = type;
        this.pieces = List.copyOf(pieces);
        this.roll = roll;
    }

    public ActionType getType() {
        return type;
    }

    public List<Piece> getPieces() {
        return pieces;
    }

    public int getRoll() {
        return roll;
    }

    private void validateType(ActionType type) {
        if (type == null) {
            throw new IllegalArgumentException("Action type cannot be null.");
        }
    }

    private void validatePieces(List<Piece> pieces) {
        if (pieces == null || pieces.isEmpty()) {
            throw new IllegalArgumentException("Action must contain at least one piece.");
        }

        for (Piece piece : pieces) {
            if (piece == null) {
                throw new IllegalArgumentException("Action cannot contain null pieces.");
            }
        }
    }

    private void validateRoll(int roll) {
        if (roll < 1 || roll > 6) {
            throw new IllegalArgumentException("Roll must be between 1 and 6.");
        }
    }
}