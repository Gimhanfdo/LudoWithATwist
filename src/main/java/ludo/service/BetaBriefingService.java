package ludo.service;

import java.util.ArrayList;
import java.util.List;

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

    public List<Piece> recordRoll(Player player, int roll) {
        validatePlayer(player);

        List<Piece> returnedPieces = new ArrayList<>();

        for (Piece piece : player.getPieces()) {
            if (piece.getEffect() != PieceEffect.BRIEFING) {
                rollTracker.reset(piece);
                continue;
            }

            if (rollTracker.recordRoll(piece, roll)) {
                piece.reset();
                returnedPieces.add(piece);
            }
        }

        return List.copyOf(returnedPieces);
    }

    private void validatePlayer(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null.");
        }
    }
}