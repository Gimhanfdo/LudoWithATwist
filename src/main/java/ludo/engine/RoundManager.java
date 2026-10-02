package ludo.engine;

import ludo.domain.model.Player;
import ludo.observer.RoundNotifier;

import java.util.List;

public class RoundManager {

    private final TurnManager turnManager;
    private final RoundNotifier roundNotifier;

    public RoundManager(TurnManager turnManager, RoundNotifier roundNotifier) {
        if (turnManager == null) {
            throw new IllegalArgumentException("Turn manager cannot be null.");
        }

        if (roundNotifier == null) {
            throw new IllegalArgumentException("Round notifier cannot be null.");
        }

        this.turnManager = turnManager;
        this.roundNotifier = roundNotifier;
    }

    public void playRound(List<Player> players) {
        validatePlayers(players);

        for (Player player : players) {
            turnManager.takeTurn(player);
        }

        roundNotifier.notifyRoundCompleted();
    }

    private void validatePlayers(List<Player> players) {
        if (players == null) {
            throw new IllegalArgumentException("Players cannot be null.");
        }

        if (players.isEmpty()) {
            throw new IllegalArgumentException("Players cannot be empty.");
        }

        for (Player player : players) {
            if (player == null) {
                throw new IllegalArgumentException("Player cannot be null.");
            }
        }
    }
}