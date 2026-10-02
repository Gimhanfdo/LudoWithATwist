package ludo.service;

import ludo.command.GameActionCommand;
import ludo.domain.enums.ActionResult;
import ludo.domain.enums.ActionType;
import ludo.domain.enums.Direction;
import ludo.domain.enums.PieceState;
import ludo.domain.model.GameAction;
import ludo.domain.model.GameState;
import ludo.domain.model.Piece;
import ludo.factory.GameActionCommandFactory;
import ludo.output.GameOutput;

public class GameActionExecutor {

    private final GameActionCommandFactory commandFactory;
    private final GameState gameState;
    private final GameOutput gameOutput;

    public GameActionExecutor(GameActionCommandFactory commandFactory, GameState gameState, GameOutput gameOutput) {
        if (commandFactory == null) {
            throw new IllegalArgumentException("Command factory cannot be null.");
        }

        if (gameState == null) {
            throw new IllegalArgumentException("Game state cannot be null.");
        }

        if (gameOutput == null) {
            throw new IllegalArgumentException("Game output cannot be null.");
        }

        this.commandFactory = commandFactory;
        this.gameState = gameState;
        this.gameOutput = gameOutput;
    }

    public ActionResult execute(GameAction action) {
        if (action == null) {
            throw new IllegalArgumentException("Game action cannot be null.");
        }

        Integer previousPosition = null;
        Direction previousDirection = null;

        if (action.getType() == ActionType.MOVE_PIECE) {
            Piece piece = action.getPieces().get(0);
            previousPosition = piece.getPosition();
            previousDirection = piece.getDirection();
        }

        GameActionCommand command = commandFactory.getCommand(action.getType());
        ActionResult result = command.execute(action);

        reportAction(action, result, previousPosition, previousDirection);

        return result;
    }

    private void reportAction(GameAction action, ActionResult result, Integer previousPosition,
            Direction previousDirection) {
        if (result == ActionResult.NOT_MOVED) {
            return;
        }

        if (action.getType() == ActionType.ENTER_BOARD) {
            reportEnteredBoard(action);
            return;
        }

        if (action.getType() == ActionType.MOVE_PIECE) {
            reportPieceMovement(action, previousPosition, previousDirection);
        }
    }

    private void reportPieceMovement(GameAction action, Integer previousPosition, Direction previousDirection) {
        Piece piece = action.getPieces().get(0);

        if (previousPosition == null || previousDirection == null) {
            return;
        }

        if (piece.getState() != PieceState.STANDARD_PATH) {
            return;
        }

        gameOutput.showPieceMoved(piece, previousPosition, piece.getPosition(), action.getRoll(), previousDirection);
    }

    private void reportEnteredBoard(GameAction action) {
        Piece piece = action.getPieces().get(0);

        gameOutput.showPieceEnteredBoard(piece, countPiecesOnBoard(), countPiecesInBase());
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
}