package ludo.random;

import java.util.Random;

public class RandomCoin implements Coin {

    private final Random random;

    public RandomCoin() {
        this.random = new Random();
    }

    @Override
    public boolean toss() {
        return random.nextBoolean();
    }
}