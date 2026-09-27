package ludo.random;

import java.util.Random;

public class RandomPositionGenerator implements PositionGenerator {

    private final Random random;

    public RandomPositionGenerator() {
        this(new Random());
    }

    RandomPositionGenerator(Random random) {
        if (random == null) {
            throw new IllegalArgumentException("Random cannot be null.");
        }

        this.random = random;
    }

    @Override
    public int nextPosition(int bound) {
        if (bound <= 0) {
            throw new IllegalArgumentException("Bound must be greater than zero.");
        }

        return random.nextInt(bound);
    }
}