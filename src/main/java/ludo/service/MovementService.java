package ludo.service;

import ludo.domain.enums.PieceState;
import ludo.domain.model.GameState;
import ludo.domain.model.MovementResult;
import ludo.domain.model.Piece;

public class MovementService {

    private final MoveExecutor moveExecutor;
    private final CaptureService captureService;
    private final GameState gameState;

    public MovementService(
            MoveExecutor moveExecutor,
            CaptureService captureService,
            GameState gameState) {
        if (moveExecutor == null) {
            throw new IllegalArgumentException("Move executor cannot be null.");
        }

        if (captureService == null) {
            throw new IllegalArgumentException("Capture service cannot be null.");
        }

        if (gameState == null) {
            throw new IllegalArgumentException("Game state cannot be null.");
        }

        this.moveExecutor = moveExecutor;
        this.captureService = captureService;
        this.gameState = gameState;
    }

    public MovementResult moveOnStandardPath(Piece piece, int distance) {

        boolean moved = moveExecutor.moveOnStandardPath(piece, distance);

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