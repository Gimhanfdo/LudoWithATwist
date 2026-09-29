package ludo.observer;

import ludo.domain.model.GameState;
import ludo.domain.model.Piece;
import ludo.domain.model.Player;
import ludo.service.PieceEffectService;

public class PieceEffectRoundObserver implements RoundObserver {

    private final GameState gameState;
    private final PieceEffectService pieceEffectService;

    public PieceEffectRoundObserver(GameState gameState, PieceEffectService pieceEffectService) {
        if (gameState == null) {
            throw new IllegalArgumentException("Game state cannot be null.");
        }

        if (pieceEffectService == null) {
            throw new IllegalArgumentException("Piece effect service cannot be null.");
        }

        this.gameState = gameState;
        this.pieceEffectService = pieceEffectService;
    }

    @Override
    public void onRoundCompleted() {
        for (Player player : gameState.getPlayers()) {
            for (Piece piece : player.getPieces()) {
                pieceEffectService.completeRound(piece);
            }
        }
    }
}