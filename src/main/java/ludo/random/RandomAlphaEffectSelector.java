package ludo.random;

import ludo.domain.enums.PieceEffect;

import java.util.Random;

public class RandomAlphaEffectSelector implements AlphaEffectSelector {

    private final Random random;

    public RandomAlphaEffectSelector() {
        this(new Random());
    }

    RandomAlphaEffectSelector(Random random) {
        if (random == null) {
            throw new IllegalArgumentException("Random cannot be null.");
        }

        this.random = random;
    }

    @Override
    public PieceEffect selectEffect() {
        return random.nextBoolean()
                ? PieceEffect.ENERGISED
                : PieceEffect.SICK;
    }
}