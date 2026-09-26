package ludo.random;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomMovementDistributor implements MovementDistributor {

    private final Random random;

    public RandomMovementDistributor() {
        this(new Random());
    }

    RandomMovementDistributor(Random random) {
        if (random == null) {
            throw new IllegalArgumentException("Random cannot be null.");
        }

        this.random = random;
    }

    @Override
    public List<Integer> distribute(int totalMovement, int numberOfPieces) {
        validateDistribution(totalMovement, numberOfPieces);

        List<Integer> distribution = new ArrayList<>();
        int remainingMovement = totalMovement;

        for (int i = 0; i < numberOfPieces - 1; i++) {
            int remainingPieces = numberOfPieces - i - 1;
            int maximumMovement = remainingMovement - remainingPieces;
            int movement = random.nextInt(maximumMovement) + 1;

            distribution.add(movement);
            remainingMovement -= movement;
        }

        distribution.add(remainingMovement);

        return distribution;
    }

    private void validateDistribution(int totalMovement, int numberOfPieces) {
        if (totalMovement <= 0) {
            throw new IllegalArgumentException("Total movement must be greater than zero.");
        }

        if (numberOfPieces <= 0) {
            throw new IllegalArgumentException("Number of pieces must be greater than zero.");
        }

        if (numberOfPieces > totalMovement) {
            throw new IllegalArgumentException("Each piece must receive at least one movement unit.");
        }
    }
}