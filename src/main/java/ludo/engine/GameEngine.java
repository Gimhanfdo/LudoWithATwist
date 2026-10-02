package ludo.engine;

import ludo.domain.model.GameState;
import ludo.domain.model.Player;

public class GameEngine {

    private final GameState gameState;
    private final RoundManager roundManager;

    public GameEngine(GameState gameState, RoundManager roundManager) {
        if (gameState == null) {
            throw new IllegalArgumentException("Game state cannot be null.");
        }

        if (roundManager == null) {
            throw new IllegalArgumentException("Round manager cannot be null.");
        }

        this.gameState = gameState;
        this.roundManager = roundManager;
    }

    public Player play() {
        Player winner = null;

        while (winner == null) {
            winner = roundManager.playRound(gameState.getPlayers());
        }

        return winner;
    }
}