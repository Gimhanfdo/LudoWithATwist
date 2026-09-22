package ludo.random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RandomDiceTest {

    private RandomDice dice;

    @BeforeEach
    void setUp() {
        dice = new RandomDice();
    }

    @Test
    void shouldRollValueWithinValidRange() {

        int result = dice.roll();

        assertTrue(result >= 1 && result <= 6);
    }
}