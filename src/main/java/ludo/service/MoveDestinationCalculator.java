package ludo.service;

import ludo.domain.enums.Direction;
import ludo.domain.enums.PieceState;
import ludo.domain.model.Board;
import ludo.domain.model.Piece;

public class MoveDestinationCalculator {

    public int calculateStandardDestination(Piece piece, int movementDistance) {
        validatePiece(piece);
        validateDistance(movementDistance);

        if (piece.getState() != PieceState.STANDARD_PATH) {
            throw new IllegalArgumentException("Piece must be on the standard path.");
        }

        int signedDistance = piece.getDirection() == Direction.CLOCKWISE
                ? movementDistance
                : -movementDistance;

        return Math.floorMod(piece.getPosition() + signedDistance, Board.STANDARD_PATH_SIZE);
    }

    private void validatePiece(Piece piece) {
        if (piece == null) {
            throw new IllegalArgumentException("Piece cannot be null.");
        }
    }

    private void validateDistance(int movementDistance) {
        if (movementDistance <= 0) {
            throw new IllegalArgumentException("Movement distance must be positive.");
        }
    }
}