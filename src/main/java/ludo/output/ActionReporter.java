package ludo.output;

import ludo.domain.enums.ActionResult;
import ludo.domain.enums.ActionType;
import ludo.domain.enums.Direction;
import ludo.domain.enums.PieceState;
import ludo.domain.model.ActionExecutionResult;
import ludo.domain.model.GameAction;
import ludo.domain.model.GameState;
import ludo.domain.model.MovementOutcome;
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

        if (executionResult.getResult() == ActionResult.NOT_MOVED) {
            return;
        }

        switch (action.getType()) {
            case ENTER_BOARD -> reportEnteredBoard(action);
            case MOVE_PIECE -> reportPieceMovement(action, executionResult, previousDirection);
            case MOVE_BLOCK -> {
                // Block reporting will be added in 10M.4.
            }
        }
    }

    private void reportEnteredBoard(GameAction action) {
        Piece piece = action.getPieces().get(0);

        gameOutput.showPieceEnteredBoard(piece, countPiecesOnBoard(), countPiecesInBase());
    }

    private void reportPieceMovement(GameAction action, ActionExecutionResult executionResult, Direction previousDirection) {
        if (!executionResult.hasMovementOutcome() || previousDirection == null) {
            return;
        }

        MovementOutcome movementOutcome = executionResult.getMovementOutcome();

        if (movementOutcome.getFromPosition() == null || movementOutcome.getToPosition() == null) {
            return;
        }

        Piece piece = action.getPieces().get(0);

        if (piece.getState() != PieceState.STANDARD_PATH) {
            return;
        }

        gameOutput.showPieceMoved(piece, movementOutcome.getFromPosition(), movementOutcome.getToPosition(), movementOutcome.getActualDistance(), previousDirection);
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