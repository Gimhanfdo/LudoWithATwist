package ludo.output;

import ludo.domain.enums.ActionResult;
import ludo.domain.enums.ActionType;
import ludo.domain.enums.Direction;
import ludo.domain.enums.PieceState;
import ludo.domain.model.ActionExecutionResult;
import ludo.domain.model.GameAction;
import ludo.domain.model.GameState;
import ludo.domain.model.MovementOutcome;
import ludo.domain.model.MovementResult;
import ludo.domain.model.Piece;

public class ActionReporter {

    private final GameState gameState;
    private final GameOutput gameOutput;

    public ActionReporter(GameState gameState, GameOutput gameOutput) {
        if (gameState == null) {
            throw new IllegalArgumentException("Game state cannot be null.");
        }

        if (gameOutput == null) {
            throw new IllegalArgumentException("Game output cannot be null.");
        }

        this.gameState = gameState;
        this.gameOutput = gameOutput;
    }

    public void report(GameAction action, ActionExecutionResult executionResult, Direction previousDirection) {
        validateArguments(action, executionResult);

        if (action.getType() == ActionType.MOVE_PIECE && executionResult.hasMovementOutcome()) {
            reportPieceMovement(action, executionResult, previousDirection);
            return;
        }

        if (executionResult.getResult() == ActionResult.NOT_MOVED) {
            return;
        }

        if (action.getType() == ActionType.ENTER_BOARD) {
            reportEnteredBoard(action);
        }
    }

    private void reportEnteredBoard(GameAction action) {
        Piece piece = action.getPieces().get(0);

        gameOutput.showPieceEnteredBoard(piece, countPiecesOnBoard(), countPiecesInBase());
    }

    private void reportPieceMovement(GameAction action, ActionExecutionResult executionResult,
            Direction previousDirection) {
        MovementOutcome movementOutcome = executionResult.getMovementOutcome();
        Piece piece = action.getPieces().get(0);

        if (movementOutcome.wasBlocked()) {
            reportBlockedMovement(piece, movementOutcome);
            return;
        }

        if (movementOutcome.captured()) {
            reportCapture(piece, movementOutcome);
            return;
        }

        reportStandardMovement(piece, movementOutcome, previousDirection);
    }

    private void reportCapture(Piece attacker, MovementOutcome movementOutcome) {
        if (movementOutcome.getToPosition() == null || movementOutcome.getCapturedPieces().isEmpty()) {
            return;
        }

        for (Piece capturedPiece : movementOutcome.getCapturedPieces()) {
            gameOutput.showPieceCaptured(attacker, capturedPiece, movementOutcome.getToPosition(), countPiecesOnBoard(),
                    countPiecesInBase());
        }
    }

    private void reportStandardMovement(Piece piece, MovementOutcome movementOutcome, Direction previousDirection) {
        if (movementOutcome.getResult() == MovementResult.NOT_MOVED) {
            return;
        }

        if (previousDirection == null || movementOutcome.getFromPosition() == null
                || movementOutcome.getToPosition() == null) {
            return;
        }

        if (piece.getState() != PieceState.STANDARD_PATH) {
            return;
        }

        gameOutput.showPieceMoved(piece, movementOutcome.getFromPosition(), movementOutcome.getToPosition(),
                movementOutcome.getActualDistance(), previousDirection);
    }

    private void reportBlockedMovement(Piece piece, MovementOutcome movementOutcome) {
        if (movementOutcome.getFromPosition() == null || movementOutcome.getBlockingPieces().isEmpty()) {
            return;
        }

        Piece blockingPiece = movementOutcome.getBlockingPieces().get(0);
        Integer blockedPosition = blockingPiece.getPosition();

        if (blockedPosition == null) {
            return;
        }

        gameOutput.showPieceBlocked(piece, movementOutcome.getFromPosition(), blockedPosition, blockingPiece);

        if (movementOutcome.wasCompletelyBlocked()) {
            gameOutput.showBlockedPieceNotMoved(piece);
            return;
        }

        if (movementOutcome.wasShortened() && movementOutcome.getToPosition() != null) {
            gameOutput.showBlockedPieceMoved(piece, movementOutcome.getToPosition());
        }
    }

    private int countPiecesOnBoard() {
        return (int) gameState.getPlayers().stream()
                .flatMap(player -> player.getPieces().stream())
                .filter(piece -> piece.getState() != PieceState.BASE && piece.getState() != PieceState.HOME)
                .count();
    }

    private int countPiecesInBase() {
        return (int) gameState.getPlayers().stream()
                .flatMap(player -> player.getPieces().stream())
                .filter(piece -> piece.getState() == PieceState.BASE)
                .count();
    }

    private void validateArguments(GameAction action, ActionExecutionResult executionResult) {
        if (action == null) {
            throw new IllegalArgumentException("Game action cannot be null.");
        }

        if (executionResult == null) {
            throw new IllegalArgumentException("Execution result cannot be null.");
        }
    }
}