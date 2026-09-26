package ludo.service;

import ludo.domain.enums.Colour;
import ludo.domain.model.GameState;
import ludo.domain.model.Piece;
import ludo.domain.model.Board;
import ludo.domain.enums.Direction;
import ludo.domain.enums.PieceState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlockService {

    private static final int MINIMUM_BLOCK_SIZE = 2;

    private final GameState gameState;

    public BlockService(GameState gameState) {
        if (gameState == null) {
            throw new IllegalArgumentException("Game state cannot be null.");
        }

        this.gameState = gameState;
    }

    public List<Piece> getBlockAt(int position, Colour colour) {
        if (colour == null) {
            throw new IllegalArgumentException("Colour cannot be null.");
        }

        List<Piece> sameColourPieces = getPiecesOfColourAt(position, colour);

        if (sameColourPieces.size() < MINIMUM_BLOCK_SIZE) {
            return Collections.emptyList();
        }

        return Collections.unmodifiableList(sameColourPieces);
    }

    private List<Piece> getPiecesOfColourAt(int position, Colour colour) {
        List<Piece> sameColourPieces = new ArrayList<>();

        for (Piece piece : gameState.getPiecesAtStandardPosition(position)) {
            if (piece.getColour() == colour) {
                sameColourPieces.add(piece);
            }
        }

        return sameColourPieces;
    }

    public boolean hasBlockAt(int position, Colour colour) {
        return !getBlockAt(position, colour).isEmpty();
    }

    public int getAllowedMovementDistance(Piece piece, int requestedDistance) {
        validateMovementRequest(piece, requestedDistance);

        for (int step = 1; step <= requestedDistance; step++) {
            int position = calculatePosition(piece.getPosition(), step, piece.getDirection());

            if (hasOpponentBlockAt(position, piece.getColour())) {
                return step - 1;
            }
        }

        return requestedDistance;
    }

    private boolean hasOpponentBlockAt(int position, Colour movingColour) {
        for (Colour colour : Colour.values()) {
            if (colour == movingColour) {
                continue;
            }

            if (hasBlockAt(position, colour)) {
                return true;
            }
        }

        return false;
    }

    private int calculatePosition(int startPosition, int distance, Direction direction) {
        if (direction == Direction.CLOCKWISE) {
            return Math.floorMod(startPosition + distance, Board.STANDARD_PATH_SIZE);
        }

        return Math.floorMod(startPosition - distance, Board.STANDARD_PATH_SIZE);
    }

    private void validateMovementRequest(Piece piece, int requestedDistance) {
        if (piece == null) {
            throw new IllegalArgumentException("Piece cannot be null.");
        }

        if (piece.getState() != PieceState.STANDARD_PATH) {
            throw new IllegalArgumentException("Piece must be on the standard path.");
        }

        if (requestedDistance <= 0) {
            throw new IllegalArgumentException("Movement distance must be greater than zero.");
        }
    }
}