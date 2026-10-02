package ludo.service;

import ludo.domain.model.Player;

import java.util.IdentityHashMap;
import java.util.Map;

public class ConsecutiveSixTracker {

    private static final int THIRD_CONSECUTIVE_SIX = 3;

    private final Map<Player, Integer> consecutiveSixes = new IdentityHashMap<>();

    public boolean recordRoll(Player player, int roll) {
        validatePlayer(player);
        validateRoll(roll);

        if (roll != 6) {
            reset(player);
            return false;
        }

        int count = consecutiveSixes.getOrDefault(player, 0) + 1;

        if (count == THIRD_CONSECUTIVE_SIX) {
            reset(player);
            return true;
        }

        consecutiveSixes.put(player, count);

        return false;
    }

    public int getConsecutiveSixCount(Player player) {
        validatePlayer(player);

        return consecutiveSixes.getOrDefault(player, 0);
    }

    public void reset(Player player) {
        validatePlayer(player);

        consecutiveSixes.remove(player);
    }

    private void validatePlayer(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null.");
        }
    }

    private void validateRoll(int roll) {
        if (roll < 1 || roll > 6) {
            throw new IllegalArgumentException("Roll must be between 1 and 6.");
        }
    }
}