package ludo.strategy.effect;

import ludo.domain.enums.PieceEffect;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnergisedMovementStrategyTest {

    private EnergisedMovementStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new EnergisedMovementStrategy();
    }

    @Test
    void shouldHandleEnergisedEffect() {
        assertEquals(PieceEffect.ENERGISED, strategy.getEffect());
    }

    @Test
    void shouldDoubleMovement() {
        assertEquals(8, strategy.apply(4));
    }

    @Test
    void shouldRejectNonPositiveRoll() {
        assertThrows(IllegalArgumentException.class, () -> strategy.apply(0));
    }
}