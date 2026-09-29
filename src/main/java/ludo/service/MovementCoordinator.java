package ludo.service;

import ludo.domain.model.Piece;
import ludo.domain.model.MovementResult;

public class MovementCoordinator {

    private final MovementService movementService;
    private final PieceEffectService pieceEffectService;
    private final MysteryLandingService mysteryLandingService;

    public MovementCoordinator(MovementService movementService, PieceEffectService pieceEffectService,
                               MysteryLandingService mysteryLandingService) {
        if (movementService == null) {
            throw new IllegalArgumentException("Movement service cannot be null.");
        }

        if (pieceEffectService == null) {
            throw new IllegalArgumentException("Piece effect service cannot be null.");
        }

        if (mysteryLandingService == null) {
            throw new IllegalArgumentException("Mystery landing service cannot be null.");
        }

        this.movementService = movementService;
        this.pieceEffectService = pieceEffectService;
        this.mysteryLandingService = mysteryLandingService;
    }

    public MovementResult move(Piece piece, int roll) {
        validatePiece(piece);
        validateRoll(roll);

        if (!pieceEffectService.canMove(piece)) {
            return MovementResult.NOT_MOVED;
        }

        int movementDistance = pieceEffectService.calculateMovement(piece, roll);

        if (movementDistance <= 0) {
            return MovementResult.NOT_MOVED;
        }

        MovementResult result = movementService.moveOnStandardPath(piece, movementDistance);

        if (result != MovementResult.NOT_MOVED) {
            mysteryLandingService.resolveLanding(piece);
        }

        return result;
    }

    private void validatePiece(Piece piece) {
        if (piece == null) {
            throw new IllegalArgumentException("Piece cannot be null.");
        }
    }

    private void validateRoll(int roll) {
        if (roll < 1 || roll > 6) {
            throw new IllegalArgumentException("Roll must be between 1 and 6.");
        }
    }
}