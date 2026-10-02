package ludo.random;

import ludo.domain.model.GameAction;

import java.util.List;
import java.util.Random;

public class RandomActionSelector implements ActionSelector {

    private final Random random;

    public RandomActionSelector() {
        this(new Random());
    }

    RandomActionSelector(Random random) {
        if (random == null) {
            throw new IllegalArgumentException("Random cannot be null.");
        }

        this.random = random;
    }

    @Override
    public GameAction select(List<GameAction> actions) {
        validateActions(actions);

        return actions.get(random.nextInt(actions.size()));
    }

    private void validateActions(List<GameAction> actions) {
        if (actions == null || actions.isEmpty()) {
            throw new IllegalArgumentException("Actions cannot be null or empty.");
        }

        for (GameAction action : actions) {
            if (action == null) {
                throw new IllegalArgumentException("Actions cannot contain null.");
            }
        }
    }
}