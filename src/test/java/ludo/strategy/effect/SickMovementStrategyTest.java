package ludo.strategy.effect;

import ludo.domain.enums.PieceEffect;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SickMovementStrategyTest {

    private SickMovementStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new SickMovementStrategy();
    }

    @Test
    void shouldHandleSickEffect() {
        assertEquals(PieceEffect.SICK, strategy.getEffect());
    }

    @Test
    void shouldHalveEvenMovement() {
        assertEquals(2, strategy.apply(4));
    }

    @Test
    void shouldHalveOddMovementUsingIntegerDivision() {
        assertEquals(2, strategy.apply(5));
    }

    @Test
    void shouldAllowMovementToBecomeZero() {
        assertEquals(0, strategy.apply(1));
    }

    @Test
    void shouldRejectNonPositiveRoll() {
        assertThrows(IllegalArgumentException.class, () -> strategy.apply(0));
    }
}