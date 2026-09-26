package ludo.service;

import ludo.domain.enums.Colour;
import ludo.domain.model.GameState;
import ludo.domain.model.Piece;

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

    public boolean hasBlockAt(int position, Colour colour) {
        return !getBlockAt(position, colour).isEmpty();
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
}