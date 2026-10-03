package ludo.service;

import ludo.domain.enums.PieceState;
import ludo.domain.model.MovementResult;
import ludo.domain.model.MysteryTeleportOutcome;
import ludo.domain.model.Piece;

import java.util.List;
import ludo.domain.model.MovementOutcome;

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

        MovementResult result = moveByState(piece, movementDistance);

        if (result != MovementResult.NOT_MOVED && piece.getState() == PieceState.STANDARD_PATH) {
            mysteryLandingService.resolveLanding(piece);
        }

        return result;
    }

    private MovementResult moveByState(Piece piece, int movementDistance) {
        return switch (piece.getState()) {
            case STANDARD_PATH -> movementService.moveOnStandardPath(piece, movementDistance);
            case HOME_STRAIGHT -> movementService.moveOnHomeStraight(piece, movementDistance);
            default -> MovementResult.NOT_MOVED;
        };
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

    public MovementOutcome moveDetailed(Piece piece, int roll) {
        validatePiece(piece);
        validateRoll(roll);

        if (!pieceEffectService.canMove(piece)) {
            return notMovedOutcome(piece, roll);
        }

        int movementDistance = pieceEffectService.calculateMovement(piece, roll);

        if (movementDistance <= 0) {
            return notMovedOutcome(piece, roll);
        }

        MovementOutcome outcome = moveDetailedByState(piece, movementDistance);

        if (outcome.getResult() == MovementResult.NOT_MOVED || piece.getState() != PieceState.STANDARD_PATH) {
            return outcome;
        }

        MysteryTeleportOutcome mysteryOutcome = mysteryLandingService.resolveLanding(piece);

        if (mysteryOutcome == null) {
            return outcome;
        }

        return outcome.withMysteryTeleport(mysteryOutcome);
    }

    private MovementOutcome moveDetailedByState(Piece piece, int movementDistance) {
        return switch (piece.getState()) {
            case STANDARD_PATH -> movementService.moveOnStandardPathDetailed(piece, movementDistance);
            case HOME_STRAIGHT -> moveHomeStraightDetailed(piece, movementDistance);
            default -> notMovedOutcome(piece, movementDistance);
        };
    }

    private MovementOutcome moveHomeStraightDetailed(Piece piece, int movementDistance) {
        Integer fromPosition = piece.getPosition();
        MovementResult result = movementService.moveOnHomeStraight(piece, movementDistance);

        if (result == MovementResult.NOT_MOVED) {
            return new MovementOutcome(MovementResult.NOT_MOVED, movementDistance, 0, fromPosition, fromPosition,
                    List.of(), List.of());
        }

        return new MovementOutcome(result, movementDistance, movementDistance, fromPosition, piece.getPosition(),
                List.of(), List.of());
    }

    private MovementOutcome notMovedOutcome(Piece piece, int requestedDistance) {
        return new MovementOutcome(MovementResult.NOT_MOVED, requestedDistance, 0, piece.getPosition(),
                piece.getPosition(), List.of(), List.of());
    }
}