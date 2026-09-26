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
    private final Board board;

    public BlockService(GameState gameState, Board board) {
        if (gameState == null) {
            throw new IllegalArgumentException("Game state cannot be null.");
        }

        if (board == null) {
            throw new IllegalArgumentException("Board cannot be null.");
        }

        this.board = board;

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

    public Direction getBlockMovementDirection(int position, Colour colour) {
        List<Piece> block = getBlockAt(position, colour);

        if (block.isEmpty()) {
            throw new IllegalArgumentException("No block exists at the specified position.");
        }

        Piece furthestFromHome = block.get(0);
        int longestDistance = getDistanceToHome(furthestFromHome);

        for (int i = 1; i < block.size(); i++) {
            Piece piece = block.get(i);
            int distance = getDistanceToHome(piece);

            if (distance > longestDistance) {
                furthestFromHome = piece;
                longestDistance = distance;
            }
        }

        return furthestFromHome.getDirection();
    }

    private int getDistanceToHome(Piece piece) {
        return board.getDistanceToHome(piece.getPosition(), piece.getColour(), piece.getDirection());
    }

    public int getBlockMovementDistance(int diceValue, int blockSize) {
        if (diceValue <= 0) {
            throw new IllegalArgumentException("Dice value must be greater than zero.");
        }

        if (blockSize < MINIMUM_BLOCK_SIZE) {
            throw new IllegalArgumentException("Block must contain at least two pieces.");
        }

        return diceValue / blockSize;
    }

    public boolean moveBlock(int position, Colour colour, int diceValue) {
        List<Piece> block = getBlockAt(position, colour);

        if (block.isEmpty()) {
            return false;
        }

        Direction blockDirection = getBlockMovementDirection(position, colour);
        int movementDistance = getBlockMovementDistance(diceValue, block.size());

        if (movementDistance == 0) {
            return false;
        }

        int destination = calculatePosition(position, movementDistance, blockDirection);

        for (Piece piece : block) {
            piece.moveTo(destination);
        }

        return true;
    }
}