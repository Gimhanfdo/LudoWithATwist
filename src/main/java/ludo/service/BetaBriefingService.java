package ludo.service;

import ludo.domain.enums.PieceEffect;
import ludo.domain.model.Piece;
import ludo.domain.model.Player;

public class BetaBriefingService {

    private final BetaRollTracker rollTracker;

    public BetaBriefingService(BetaRollTracker rollTracker) {
        if (rollTracker == null) {
            throw new IllegalArgumentException("Roll tracker cannot be null.");
        }

        this.rollTracker = rollTracker;
    }

    public boolean recordRoll(Player player, int roll) {
        validatePlayer(player);

        boolean pieceReturnedToBase = false;

        for (Piece piece : player.getPieces()) {
            if (piece.getEffect() != PieceEffect.BRIEFING) {
                rollTracker.reset(piece);
                continue;
            }

            if (rollTracker.recordRoll(piece, roll)) {
                piece.reset();
                pieceReturnedToBase = true;
            }
        }

        return pieceReturnedToBase;
    }

    private boolean hasBriefingPiece(Player player) {
        return player.getPieces()
                .stream()
                .anyMatch(piece -> piece.getEffect() == PieceEffect.BRIEFING);
    }

    private void returnBriefingPiecesToBase(Player player) {
        for (Piece piece : player.getPieces()) {
            if (piece.getEffect() == PieceEffect.BRIEFING) {
                piece.reset();
            }
        }
    }

    private void validatePlayer(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null.");
        }
    }
}