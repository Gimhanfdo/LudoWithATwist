package ludo.service;

import ludo.domain.enums.Colour;
import ludo.domain.model.GameState;
import ludo.domain.model.Piece;
import ludo.domain.model.Board;
import ludo.domain.enums.Direction;
import ludo.domain.enums.PieceState;
import ludo.random.MovementDistributor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlockService {

    private static final int MINIMUM_BLOCK_SIZE = 2;
    private static final int FORCED_BREAK_MOVEMENT = 6;

    private final GameState gameState;
    private final Board board;
    private final MovementDistributor movementDistributor;

    public BlockService(GameState gameState, Board board, MovementDistributor movementDistributor) {
        if (gameState == null) {
            throw new IllegalArgumentException("Game state cannot be null.");
        }

        if (board == null) {
            throw new IllegalArgumentException("Board cannot be null.");
        }

        if (movementDistributor == null) {
            throw new IllegalArgumentException("Movement distributor cannot be null.");
        }

        this.board = board;
        this.movementDistributor = movementDistributor;
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

            if (!getOpponentBlockAt(position, piece.getColour()).isEmpty()) {
                return step - 1;
            }
        }

        return requestedDistance;
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

        int allowedDistance = getAllowedBlockMovementDistance(position, colour, movementDistance);

        if (allowedDistance == 0) {
            return false;
        }

        int destination = calculatePosition(position, allowedDistance, blockDirection);

        for (Piece piece : block) {
            piece.moveTo(destination);
        }

        return true;
    }

    public boolean breakBlockAfterThreeSixes(int position, Colour colour, Piece pieceToRemain) {
        List<Piece> block = getBlockAt(position, colour);

        if (block.isEmpty()) {
            return false;
        }

        if (!block.contains(pieceToRemain)) {
            throw new IllegalArgumentException("Remaining piece must belong to the block.");
        }

        List<Piece> piecesToMove = getPiecesToMove(block, pieceToRemain);
        List<Integer> distribution = movementDistributor.distribute(FORCED_BREAK_MOVEMENT, piecesToMove.size());

        movePiecesInOriginalDirections(piecesToMove, distribution);

        return true;
    }

    private List<Piece> getPiecesToMove(List<Piece> block, Piece pieceToRemain) {
        List<Piece> piecesToMove = new ArrayList<>(block);
        piecesToMove.remove(pieceToRemain);
        return piecesToMove;
    }

    private void movePiecesInOriginalDirections(List<Piece> pieces, List<Integer> distribution) {
        for (int i = 0; i < pieces.size(); i++) {
            moveInOriginalDirection(pieces.get(i), distribution.get(i));
        }
    }

    private void moveInOriginalDirection(Piece piece, int distance) {
        int destination = calculatePosition(piece.getPosition(), distance, piece.getDirection());
        piece.moveTo(destination);
    }

    public int getAllowedBlockMovementDistance(int position, Colour colour, int requestedDistance) {

        List<Piece> block = getBlockAt(position, colour);

        if (block.isEmpty()) {
            return 0;
        }

        if (requestedDistance <= 0) {
            throw new IllegalArgumentException("Requested distance must be greater than zero.");
        }

        Direction direction = getBlockMovementDirection(position, colour);
        int blockSize = block.size();

        for (int step = 1; step <= requestedDistance; step++) {
            int nextPosition = calculatePosition(position, step, direction);
            List<Piece> opponentBlock = getOpponentBlockAt(nextPosition, colour);

            if (opponentBlock.isEmpty()) {
                continue;
            }

            boolean isDestination = step == requestedDistance;
            boolean equalSize = opponentBlock.size() == blockSize;

            if (isDestination && equalSize) {
                return requestedDistance;
            }

            return step - 1;
        }

        return requestedDistance;
    }

    private List<Piece> getOpponentBlockAt(int position, Colour movingColour) {

        for (Colour colour : Colour.values()) {
            if (colour == movingColour) {
                continue;
            }

            List<Piece> block = getBlockAt(position, colour);

            if (!block.isEmpty()) {
                return block;
            }
        }

        return Collections.emptyList();
    }

    public List<Piece> getFirstOpponentBlockInPath(Piece piece, int requestedDistance) {
        validateMovementRequest(piece, requestedDistance);

        for (int step = 1; step <= requestedDistance; step++) {
            int position = calculatePosition(piece.getPosition(), step, piece.getDirection());
            List<Piece> opponentBlock = getOpponentBlockAt(position, piece.getColour());

            if (!opponentBlock.isEmpty()) {
                return opponentBlock;
            }
        }

        return Collections.emptyList();
    }
}