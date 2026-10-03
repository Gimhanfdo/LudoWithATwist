package ludo.domain.model;

import java.util.List;

public class MovementOutcome {

    private final MovementResult result;
    private final int requestedDistance;
    private final int actualDistance;
    private final Integer fromPosition;
    private final Integer toPosition;
    private final List<Piece> blockingPieces;
    private final List<Piece> capturedPieces;
    private final MysteryTeleportOutcome mysteryTeleportOutcome;

    public MovementOutcome(MovementResult result, int requestedDistance, int actualDistance, Integer fromPosition,
                           Integer toPosition, List<Piece> blockingPieces, List<Piece> capturedPieces) {
        this(result, requestedDistance, actualDistance, fromPosition, toPosition, blockingPieces, capturedPieces, null);
    }

    private MovementOutcome(MovementResult result, int requestedDistance, int actualDistance, Integer fromPosition,
                            Integer toPosition, List<Piece> blockingPieces, List<Piece> capturedPieces,
                            MysteryTeleportOutcome mysteryTeleportOutcome) {
        if (result == null) {
            throw new IllegalArgumentException("Movement result cannot be null.");
        }

        if (requestedDistance <= 0) {
            throw new IllegalArgumentException("Requested distance must be positive.");
        }

        if (actualDistance < 0) {
            throw new IllegalArgumentException("Actual distance cannot be negative.");
        }

        if (actualDistance > requestedDistance) {
            throw new IllegalArgumentException("Actual distance cannot exceed requested distance.");
        }

        if (blockingPieces == null) {
            throw new IllegalArgumentException("Blocking pieces cannot be null.");
        }

        if (capturedPieces == null) {
            throw new IllegalArgumentException("Captured pieces cannot be null.");
        }

        this.result = result;
        this.requestedDistance = requestedDistance;
        this.actualDistance = actualDistance;
        this.fromPosition = fromPosition;
        this.toPosition = toPosition;
        this.blockingPieces = List.copyOf(blockingPieces);
        this.capturedPieces = List.copyOf(capturedPieces);
        this.mysteryTeleportOutcome = mysteryTeleportOutcome;
    }

    public MovementOutcome withMysteryTeleport(MysteryTeleportOutcome mysteryTeleportOutcome) {
        if (mysteryTeleportOutcome == null) {
            throw new IllegalArgumentException("Mystery teleport outcome cannot be null.");
        }

        return new MovementOutcome(result, requestedDistance, actualDistance, fromPosition, toPosition,
                blockingPieces, capturedPieces, mysteryTeleportOutcome);
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

    public Integer getFromPosition() {
        return fromPosition;
    }

    public Integer getToPosition() {
        return toPosition;
    }

    public List<Piece> getBlockingPieces() {
        return blockingPieces;
    }

    public List<Piece> getCapturedPieces() {
        return capturedPieces;
    }

    public MysteryTeleportOutcome getMysteryTeleportOutcome() {
        return mysteryTeleportOutcome;
    }

    public boolean hasMysteryTeleport() {
        return mysteryTeleportOutcome != null;
    }

    public boolean wasBlocked() {
        return !blockingPieces.isEmpty();
    }

    public boolean wasShortened() {
        return actualDistance > 0 && actualDistance < requestedDistance;
    }

    public boolean wasCompletelyBlocked() {
        return wasBlocked() && actualDistance == 0;
    }

    public boolean captured() {
        return !capturedPieces.isEmpty();
    }
}