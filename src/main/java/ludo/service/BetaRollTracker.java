package ludo.service;

import ludo.domain.model.Piece;

import java.util.IdentityHashMap;
import java.util.Map;

public class BetaRollTracker {

    private static final int REQUIRED_CONSECUTIVE_THREES = 3;

    private final Map<Piece, Integer> consecutiveThreeCounts = new IdentityHashMap<>();

    public boolean recordRoll(Piece piece, int roll) {
        validatePiece(piece);
        validateRoll(roll);

        if (roll != 3) {
            reset(piece);
            return false;
        }

        int newCount = consecutiveThreeCounts.getOrDefault(piece, 0) + 1;

        if (newCount == REQUIRED_CONSECUTIVE_THREES) {
            reset(piece);
            return true;
        }

        consecutiveThreeCounts.put(piece, newCount);

        return false;
    }

    public int getConsecutiveThreeCount(Piece piece) {
        validatePiece(piece);

        return consecutiveThreeCounts.getOrDefault(piece, 0);
    }

    public void reset(Piece piece) {
        validatePiece(piece);

        consecutiveThreeCounts.remove(piece);
    }

    private void validatePiece(Piece piece) {
        if (piece == null) {
            throw new IllegalArgumentException("Piece cannot be null.");
        }
    }

    private void validateRoll(int roll) {
        if (roll < 1 || roll > 6) {
            throw new IllegalArgumentException("Roll must be between 1 and 6.");
        }
    }
}