package ludo.engine;

import ludo.domain.model.GameState;
import ludo.domain.model.Player;
import ludo.output.GameOutput;
import ludo.service.FirstPlayerSelector;

import java.util.List;

public class GameEngine {

    private final GameState gameState;
    private final RoundManager roundManager;
    private final FirstPlayerSelector firstPlayerSelector;
    private final GameOutput gameOutput;

    public GameEngine(GameState gameState, RoundManager roundManager, FirstPlayerSelector firstPlayerSelector,
                      GameOutput gameOutput) {
        if (gameState == null) {
            throw new IllegalArgumentException("Game state cannot be null.");
        }

        if (roundManager == null) {
            throw new IllegalArgumentException("Round manager cannot be null.");
        }

        if (firstPlayerSelector == null) {
            throw new IllegalArgumentException("First player selector cannot be null.");
        }

        if (gameOutput == null) {
            throw new IllegalArgumentException("Game output cannot be null.");
        }

        this.gameState = gameState;
        this.roundManager = roundManager;
        this.firstPlayerSelector = firstPlayerSelector;
        this.gameOutput = gameOutput;
    }

    public Player play() {
        showPlayers();

        List<Player> roundOrder = firstPlayerSelector.determineOrder(gameState.getPlayers());
        Player winner = null;

        while (winner == null) {
            winner = roundManager.playRound(roundOrder);
        }

        gameOutput.showWinner(winner);

        return winner;
    }

    private void showPlayers() {
        for (Player player : gameState.getPlayers()) {
            gameOutput.showPlayerPieces(player);
        }
    }
}