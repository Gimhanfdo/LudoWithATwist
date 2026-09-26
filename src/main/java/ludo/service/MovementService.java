package ludo.service;

import ludo.domain.enums.PieceState;
import ludo.domain.model.GameState;
import ludo.domain.model.MovementResult;
import ludo.domain.model.Piece;

public class MovementService {

    private final MoveExecutor moveExecutor;
    private final CaptureService captureService;
    private final BlockService blockService;
    private final GameState gameState;

    public MovementService(
            MoveExecutor moveExecutor,
            CaptureService captureService,
            BlockService blockService,
            GameState gameState) {
        if (moveExecutor == null) {
            throw new IllegalArgumentException("Move executor cannot be null.");
        }

        if (captureService == null) {
            throw new IllegalArgumentException("Capture service cannot be null.");
        }

        if (blockService == null) {
            throw new IllegalArgumentException("Block service cannot be null.");
        }

        if (gameState == null) {
            throw new IllegalArgumentException("Game state cannot be null.");
        }

        this.moveExecutor = moveExecutor;
        this.captureService = captureService;
        this.blockService = blockService;
        this.gameState = gameState;
    }

    public MovementResult moveOnStandardPath(Piece piece, int distance) {
        
        int allowedDistance = blockService.getAllowedMovementDistance(piece, distance);

        if (allowedDistance == 0) {
            return MovementResult.NOT_MOVED;
        }

        boolean moved = moveExecutor.moveOnStandardPath(piece, allowedDistance);

        if (!moved) {
            return MovementResult.NOT_MOVED;
        }

        if (piece.getState() != PieceState.STANDARD_PATH) {
            return MovementResult.MOVED;
        }

        boolean captured = captureService.resolveCapture(piece, gameState);

        if (captured) {
            return MovementResult.CAPTURED;
        }

        return MovementResult.MOVED;
    }
}