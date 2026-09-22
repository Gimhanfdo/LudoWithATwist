package ludo.random;

import java.util.Random;

public class RandomDice implements Dice {

    private static final int NUMBER_OF_SIDES = 6;

    private final Random random;

    public RandomDice() {
        this.random = new Random();
    }

    @Override
    public int roll() {
        return random.nextInt(NUMBER_OF_SIDES) + 1;
    }
}