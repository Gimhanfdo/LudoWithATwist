package ludo.service;

import ludo.domain.enums.PieceState;
import ludo.domain.model.Player;

public class GameCompletionService {

    public boolean hasWon(Player player) {
        validatePlayer(player);

        return player.getPieces().stream().allMatch(piece -> piece.getState() == PieceState.HOME);
    }

    private void validatePlayer(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null.");
        }
    }
}