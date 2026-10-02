package ludo.service;

import ludo.domain.model.Player;
import ludo.output.GameOutput;
import ludo.random.Dice;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FirstPlayerSelector {

    private final Dice dice;
    private final GameOutput gameOutput;

    public FirstPlayerSelector(Dice dice, GameOutput gameOutput) {
        if (dice == null) {
            throw new IllegalArgumentException("Dice cannot be null.");
        }

        if (gameOutput == null) {
            throw new IllegalArgumentException("Game output cannot be null.");
        }

        this.dice = dice;
        this.gameOutput = gameOutput;
    }

    public List<Player> determineOrder(List<Player> players) {
        validatePlayers(players);

        List<Player> candidates = new ArrayList<>(players);

        while (candidates.size() > 1) {
            candidates = findHighestRollers(candidates);
        }

        Player firstPlayer = candidates.get(0);
        List<Player> orderedPlayers = rotateFromFirstPlayer(players, firstPlayer);

        gameOutput.showFirstPlayer(firstPlayer);
        gameOutput.showRoundOrder(orderedPlayers);

        return List.copyOf(orderedPlayers);
    }

    private List<Player> findHighestRollers(List<Player> players) {
        Map<Player, Integer> rolls = new LinkedHashMap<>();
        int highestRoll = 0;

        for (Player player : players) {
            int roll = dice.roll();

            rolls.put(player, roll);
            gameOutput.showInitialRoll(player, roll);
            highestRoll = Math.max(highestRoll, roll);
        }

        int finalHighestRoll = highestRoll;

        return rolls.entrySet().stream()
                .filter(entry -> entry.getValue() == finalHighestRoll)
                .map(Map.Entry::getKey)
                .toList();
    }

    private List<Player> rotateFromFirstPlayer(List<Player> players, Player firstPlayer) {
        int firstIndex = players.indexOf(firstPlayer);
        List<Player> orderedPlayers = new ArrayList<>();

        for (int offset = 0; offset < players.size(); offset++) {
            int index = (firstIndex + offset) % players.size();
            orderedPlayers.add(players.get(index));
        }

        return orderedPlayers;
    }

    private void validatePlayers(List<Player> players) {
        if (players == null || players.isEmpty()) {
            throw new IllegalArgumentException("Players cannot be null or empty.");
        }

        for (Player player : players) {
            if (player == null) {
                throw new IllegalArgumentException("Players cannot contain null.");
            }
        }
    }
}