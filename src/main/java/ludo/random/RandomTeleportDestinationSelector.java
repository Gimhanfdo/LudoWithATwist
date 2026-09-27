package ludo.random;

import ludo.domain.enums.TeleportDestination;

import java.util.Random;

public class RandomTeleportDestinationSelector implements TeleportDestinationSelector {

    private final Random random;

    public RandomTeleportDestinationSelector() {
        this(new Random());
    }

    RandomTeleportDestinationSelector(Random random) {
        if (random == null) {
            throw new IllegalArgumentException("Random cannot be null.");
        }

        this.random = random;
    }

    @Override
    public TeleportDestination selectDestination() {
        TeleportDestination[] destinations = TeleportDestination.values();
        int selectedIndex = random.nextInt(destinations.length);

        return destinations[selectedIndex];
    }
}