package ludo.service;

import ludo.domain.enums.Direction;
import ludo.domain.enums.PieceState;
import ludo.domain.model.Board;
import ludo.domain.model.Piece;
import ludo.random.Coin;

public class MoveExecutor {

    private static final int REQUIRED_ROLL_TO_LEAVE_BASE = 6;

    private final Board board;
    private final Coin coin;

    public MoveExecutor(Board board, Coin coin) {

        if (board == null) {
            throw new IllegalArgumentException(
                    "Board cannot be null."
            );
        }

        if (coin == null) {
            throw new IllegalArgumentException(
                    "Coin cannot be null."
            );
        }

        this.board = board;
        this.coin = coin;
    }

    public boolean moveFromBase(Piece piece, int diceValue) {

        if (piece == null) {
            throw new IllegalArgumentException(
                    "Piece cannot be null."
            );
        }

        if (piece.getState() != PieceState.BASE) {
            return false;
        }

        if (diceValue != REQUIRED_ROLL_TO_LEAVE_BASE) {
            return false;
        }

        int startPosition =
                board.getStartPosition(piece.getColour());

        Direction direction =
                coin.toss()
                        ? Direction.CLOCKWISE
                        : Direction.COUNTERCLOCKWISE;

        piece.enterBoard(startPosition, direction);

        return true;
    }

    public boolean moveOnStandardPath(Piece piece, int distance) {

        if (piece == null) {
            throw new IllegalArgumentException(
                    "Piece cannot be null.");
        }

        if (piece.getState() != PieceState.STANDARD_PATH) {
            return false;
        }

        if (distance <= 0) {
            throw new IllegalArgumentException(
                    "Movement distance must be greater than zero.");
        }

        int currentPosition = piece.getPosition();

        int newPosition;

        if (piece.getDirection() == Direction.CLOCKWISE) {

            newPosition = (currentPosition + distance)
                    % Board.STANDARD_PATH_SIZE;

        } else {

            newPosition = Math.floorMod(
                    currentPosition - distance,
                    Board.STANDARD_PATH_SIZE);
        }

        piece.moveTo(newPosition);

        return true;
    }
}