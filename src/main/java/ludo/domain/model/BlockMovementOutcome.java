package ludo.domain.model;

import ludo.domain.enums.Direction;

import java.util.List;

public class BlockMovementOutcome {

    private final MovementResult result;
    private final int requestedDistance;
    private final int actualDistance;
    private final int fromPosition;
    private final int toPosition;
    private final Direction direction;
    private final List<Piece> movingPieces;
    private final List<Piece> capturedPieces;

    public BlockMovementOutcome(MovementResult result, int requestedDistance, int actualDistance, int fromPosition, int toPosition, Direction direction, List<Piece> movingPieces, List<Piece> capturedPieces) {
        if (result == null) {
            throw new IllegalArgumentException("Movement result cannot be null.");
        }

        if (requestedDistance < 0) {
            throw new IllegalArgumentException("Requested distance cannot be negative.");
        }

        if (actualDistance < 0 || actualDistance > requestedDistance) {
            throw new IllegalArgumentException("Actual distance must be between zero and requested distance.");
        }

        if (movingPieces == null) {
            throw new IllegalArgumentException("Moving pieces cannot be null.");
        }

        if (capturedPieces == null) {
            throw new IllegalArgumentException("Captured pieces cannot be null.");
        }

        this.result = result;
        this.requestedDistance = requestedDistance;
        this.actualDistance = actualDistance;
        this.fromPosition = fromPosition;
        this.toPosition = toPosition;
        this.direction = direction;
        this.movingPieces = List.copyOf(movingPieces);
        this.capturedPieces = List.copyOf(capturedPieces);
    }

    public MovementResult getResult() {
        return result;
    }

    public int getRequestedDistance() {
        return requestedDistance;
    }

    public int getActualDistance() {
        return actualDistance;
    }

    public int getFromPosition() {
        return fromPosition;
    }

    public int getToPosition() {
        return toPosition;
    }

    public Direction getDirection() {
        return direction;
    }

    public List<Piece> getMovingPieces() {
        return movingPieces;
    }

    public List<Piece> getCapturedPieces() {
        return capturedPieces;
    }

    public boolean moved() {
        return actualDistance > 0;
    }

    public boolean captured() {
        return !capturedPieces.isEmpty();
    }

    public boolean wasShortened() {
        return actualDistance > 0 && actualDistance < requestedDistance;
    }
}