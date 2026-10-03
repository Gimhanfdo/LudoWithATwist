package ludo.service;

import ludo.domain.enums.PieceState;
import ludo.domain.model.Board;
import ludo.domain.model.GameAction;
import ludo.domain.model.Piece;

public class MoveValidator {

    private final PieceEffectService pieceEffectService;
    private final BlockService blockService;

    public MoveValidator(PieceEffectService pieceEffectService, BlockService blockService) {
        if (pieceEffectService == null) {
            throw new IllegalArgumentException("Piece effect service cannot be null.");
        }

        if (blockService == null) {
            throw new IllegalArgumentException("Block service cannot be null.");
        }

        this.pieceEffectService = pieceEffectService;
        this.blockService = blockService;
    }

    public boolean isValid(GameAction action) {
        if (action == null) {
            throw new IllegalArgumentException("Game action cannot be null.");
        }

        return switch (action.getType()) {
            case ENTER_BOARD -> true;
            case MOVE_PIECE -> isValidPieceMove(action);
            case MOVE_BLOCK -> isValidBlockMove(action);
        };
    }

    private boolean isValidPieceMove(GameAction action) {
        if (action.getPieces().size() != 1) {
            return false;
        }

        Piece piece = action.getPieces().get(0);

        if (!pieceEffectService.canMove(piece)) {
            return false;
        }

        int movementDistance = pieceEffectService.calculateMovement(piece, action.getRoll());

        if (movementDistance <= 0) {
            return false;
        }

        if (piece.getState() == PieceState.HOME_STRAIGHT) {
            return canMoveWithinHomeStraight(piece, movementDistance);
        }

        if (piece.getState() != PieceState.STANDARD_PATH) {
            return false;
        }

        return blockService.getAllowedMovementDistance(piece, movementDistance) > 0;
    }

    private boolean canMoveWithinHomeStraight(Piece piece, int movementDistance) {
        int currentPosition = piece.getPosition();
        int homePosition = Board.HOME_STRAIGHT_SIZE;

        return currentPosition + movementDistance <= homePosition;
    }

    private boolean isValidBlockMove(GameAction action) {
        if (action.getPieces().size() < 2) {
            return false;
        }

        for (Piece piece : action.getPieces()) {
            if (piece.getState() != PieceState.STANDARD_PATH) {
                return false;
            }

            if (!pieceEffectService.canMove(piece)) {
                return false;
            }
        }

        return true;
    }
}