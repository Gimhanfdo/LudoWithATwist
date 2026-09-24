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
                    "Board cannot be null.");
        }

        if (coin == null) {
            throw new IllegalArgumentException(
                    "Coin cannot be null.");
        }

        this.board = board;
        this.coin = coin;
    }

    public boolean moveFromBase(Piece piece, int diceValue) {

        if (piece == null) {
            throw new IllegalArgumentException(
                    "Piece cannot be null.");
        }

        if (piece.getState() != PieceState.BASE) {
            return false;
        }

        if (diceValue != REQUIRED_ROLL_TO_LEAVE_BASE) {
            return false;
        }

        int startPosition = board.getStartPosition(piece.getColour());

        Direction direction = coin.toss()
                ? Direction.CLOCKWISE
                : Direction.COUNTERCLOCKWISE;

        piece.enterBoard(startPosition, direction);

        return true;
    }

    private void validateMovement(Piece piece, int distance) {
        if (piece == null) {
            throw new IllegalArgumentException(
                    "Piece cannot be null.");
        }

        if (distance <= 0) {
            throw new IllegalArgumentException(
                    "Movement distance must be greater than zero.");
        }
    }

    private boolean movesBeyondApproach(Piece piece, int distance) {
        return board.movesBeyondApproach(
                piece.getPosition(),
                distance,
                piece.getColour(),
                piece.getDirection());
    }

    // private boolean enterHomeStraight(Piece piece, int distance) {

    //     int distanceToApproach = board.getDistanceToApproach(
    //             piece.getPosition(),
    //             piece.getColour(),
    //             piece.getDirection());

    //     int remainingDistance = distance - distanceToApproach;

    //     int homeStraightPosition = remainingDistance - 1;

    //     if (homeStraightPosition < Board.HOME_STRAIGHT_SIZE) {

    //         piece.enterHomeStraight(homeStraightPosition);

    //         return true;
    //     }

    //     if (homeStraightPosition == Board.HOME_STRAIGHT_SIZE) {

    //         piece.enterHomeStraight(Board.HOME_STRAIGHT_SIZE - 1);

    //         piece.reachHome();

    //         return true;
    //     }

    //     return false;
    // }

    private void moveAlongStandardPath(Piece piece, int distance) {
        int newPosition = calculateStandardPathPosition(piece, distance);

        piece.moveTo(newPosition);
    }

    private int calculateStandardPathPosition(Piece piece, int distance) {
        int currentPosition = piece.getPosition();

        if (piece.getDirection() == Direction.CLOCKWISE) {
            return Math.floorMod(
                    currentPosition + distance,
                    Board.STANDARD_PATH_SIZE);
        }

        return Math.floorMod(
                currentPosition - distance,
                Board.STANDARD_PATH_SIZE);
    }

    public boolean moveOnStandardPath(Piece piece, int distance) {
        validateMovement(piece, distance);

        if (piece.getState() != PieceState.STANDARD_PATH) {
            return false;
        }

        if (movesBeyondApproach(piece, distance)) {

            if (canEnterHomeStraight(piece)) {
                return moveBeyondApproach(piece, distance);
            }

            piece.recordApproachPass();
        }

        moveAlongStandardPath(piece, distance);

        return true;
    }

    private boolean moveBeyondApproach(Piece piece, int distance) {

        int distanceToApproach = board.getDistanceToApproach(
                piece.getPosition(),
                piece.getColour(),
                piece.getDirection());

        int remainingDistance = distance - distanceToApproach;

        int homeStraightPosition = remainingDistance - 1;

        if (homeStraightPosition < Board.HOME_STRAIGHT_SIZE) {

            piece.enterHomeStraight(
                    homeStraightPosition);

            return true;
        }

        if (homeStraightPosition == Board.HOME_STRAIGHT_SIZE) {

            piece.enterHomeStraight(
                    Board.HOME_STRAIGHT_SIZE - 1);

            piece.reachHome();

            return true;
        }

        return false;
    }

    private boolean canEnterHomeStraight(Piece piece) {

        if (!piece.hasCaptured()) {
            return false;
        }

        if (piece.getDirection() == Direction.CLOCKWISE) {

            return true;
        }

        return piece.getApproachPassCount() >= 1;
    }

    public boolean moveOnHomeStraight(Piece piece, int distance) {

        validateMovement(piece, distance);

        if (piece.getState() != PieceState.HOME_STRAIGHT) {
            return false;
        }

        int currentPosition = piece.getPosition();

        int targetPosition = currentPosition + distance;

        if (targetPosition < Board.HOME_STRAIGHT_SIZE) {

            piece.moveWithinHomeStraight(
                    targetPosition);

            return true;
        }

        if (targetPosition == Board.HOME_STRAIGHT_SIZE) {

            piece.reachHome();

            return true;
        }

        return false;
    }
}