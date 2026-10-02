package ludo.service;

import java.util.List;

import ludo.domain.enums.Colour;
import ludo.domain.enums.PieceState;
import ludo.domain.model.GameState;
import ludo.domain.model.MovementResult;
import ludo.domain.model.Piece;
import ludo.domain.model.MovementOutcome;

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

    public MovementResult moveOnHomeStraight(Piece piece, int distance) {
        if (piece == null) {
            throw new IllegalArgumentException("Piece cannot be null.");
        }

        if (distance <= 0) {
            throw new IllegalArgumentException("Distance must be positive.");
        }

        if (piece.getState() != PieceState.HOME_STRAIGHT) {
            return MovementResult.NOT_MOVED;
        }

        boolean moved = moveExecutor.moveOnHomeStraight(piece, distance);

        return moved ? MovementResult.MOVED : MovementResult.NOT_MOVED;
    }

    public MovementResult moveBlock(int position, Colour colour, int diceValue) {

        List<Piece> movingBlock = blockService.getBlockAt(position, colour);

        if (movingBlock.isEmpty()) {
            return MovementResult.NOT_MOVED;
        }

        boolean moved = blockService.moveBlock(position, colour, diceValue);

        if (!moved) {
            return MovementResult.NOT_MOVED;
        }

        boolean captured = captureService.resolveBlockCapture(movingBlock, gameState);

        return captured ? MovementResult.CAPTURED : MovementResult.MOVED;
    }

    public MovementOutcome moveOnStandardPathDetailed(Piece piece, int distance) {
        if (piece == null) {
            throw new IllegalArgumentException("Piece cannot be null.");
        }

        if (distance <= 0) {
            throw new IllegalArgumentException("Distance must be positive.");
        }

        Integer fromPosition = piece.getPosition();
        List<Piece> blockingPieces = blockService.getFirstOpponentBlockInPath(piece, distance);
        int allowedDistance = blockService.getAllowedMovementDistance(piece, distance);

        if (allowedDistance == 0) {
            return new MovementOutcome(MovementResult.NOT_MOVED, distance, 0, fromPosition, fromPosition,
                    blockingPieces, List.of());
        }

        boolean moved = moveExecutor.moveOnStandardPath(piece, allowedDistance);

        if (!moved) {
            return new MovementOutcome(MovementResult.NOT_MOVED, distance, 0, fromPosition, fromPosition,
                    blockingPieces, List.of());
        }

        Integer toPosition = piece.getPosition();

        if (piece.getState() != PieceState.STANDARD_PATH) {
            return new MovementOutcome(MovementResult.MOVED, distance, allowedDistance, fromPosition, toPosition,
                    blockingPieces, List.of());
        }

        List<Piece> capturedPieces = captureService.getCapturablePieces(piece, gameState);
        boolean captured = captureService.resolveCaptureForReporting(piece, gameState);

        return new MovementOutcome(captured ? MovementResult.CAPTURED : MovementResult.MOVED, distance, allowedDistance,
                fromPosition, toPosition, blockingPieces, captured ? capturedPieces : List.of());
    }
}