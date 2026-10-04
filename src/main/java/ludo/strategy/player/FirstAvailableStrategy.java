package ludo.strategy.player;

import ludo.domain.model.GameAction;
import ludo.domain.model.Player;

import java.util.List;

public class FirstAvailableStrategy implements PlayerStrategy {

    @Override
    public GameAction chooseAction(
            Player player,
            List<GameAction> legalActions
    ) {
        validatePlayer(player);
        validateActions(legalActions);

        if (legalActions.isEmpty()) {
            return null;
        }

        return legalActions.get(0);
    }

    private void validatePlayer(Player player) {
        if (player == null) {
            throw new IllegalArgumentException(
                    "Player cannot be null."
            );
        }
    }

    private void validateActions(List<GameAction> legalActions) {
        if (legalActions == null) {
            throw new IllegalArgumentException(
                    "Legal actions cannot be null."
            );
        }

        for (GameAction action : legalActions) {
            if (action == null) {
                throw new IllegalArgumentException(
                        "Legal actions cannot contain null."
                );
            }
        }
    }
}