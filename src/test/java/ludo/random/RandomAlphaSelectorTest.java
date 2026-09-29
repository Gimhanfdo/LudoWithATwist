package ludo.random;

import ludo.domain.enums.PieceEffect;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RandomAlphaEffectSelectorTest {

    @Test
    void shouldSelectEnergisedWhenRandomReturnsTrue() {
        Random random = mock(Random.class);

        when(random.nextBoolean()).thenReturn(true);

        AlphaEffectSelector selector = new RandomAlphaEffectSelector(random);

        assertEquals(PieceEffect.ENERGISED, selector.selectEffect());
    }

    @Test
    void shouldSelectSickWhenRandomReturnsFalse() {
        Random random = mock(Random.class);

        when(random.nextBoolean()).thenReturn(false);

        AlphaEffectSelector selector = new RandomAlphaEffectSelector(random);

        assertEquals(PieceEffect.SICK, selector.selectEffect());
    }

    @Test
    void shouldRejectNullRandom() {
        assertThrows(IllegalArgumentException.class, () -> new RandomAlphaEffectSelector(null));
    }
}