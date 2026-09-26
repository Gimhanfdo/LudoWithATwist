package ludo.service;

import ludo.domain.enums.PieceState;
import ludo.domain.model.GameState;
import ludo.domain.model.Piece;

public class MovementService {

    private final MoveExecutor moveExecutor;
    private final CaptureService captureService;
    private final GameState gameState;

    public MovementService(
            MoveExecutor moveExecutor,
            CaptureService captureService,
            GameState gameState
    ) {
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

    public boolean moveOnStandardPath(Piece piece, int distance) {
        boolean moved = moveExecutor.moveOnStandardPath(piece, distance);

        if (!moved) {
            return false;
        }

        if (piece.getState() == PieceState.STANDARD_PATH) {

            captureService.resolveCapture(piece, gameState);
        }

        return true;
    }
}