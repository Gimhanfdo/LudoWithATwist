package ludo.random;

import ludo.domain.enums.TeleportDestination;

import org.junit.jupiter.api.Test;
import java.util.Random;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class RandomTeleportDestinationSelectorTest {

    @Test
    void shouldSelectValidTeleportDestination() {
        TeleportDestinationSelector selector = new RandomTeleportDestinationSelector();

        for (int i = 0; i < 100; i++) {
            TeleportDestination destination = selector.selectDestination();

            assertNotNull(destination);
        }
    }

    @Test
    void shouldRejectNullRandom() {
        assertThrows(IllegalArgumentException.class,
                () -> new RandomTeleportDestinationSelector(null));
    }

    @Test
    void shouldSelectDestinationUsingGeneratedIndex() {
        Random random = mock(Random.class);

        when(random.nextInt(TeleportDestination.values().length)).thenReturn(2);

        TeleportDestinationSelector selector = new RandomTeleportDestinationSelector(random);
        TeleportDestination destination = selector.selectDestination();

        assertEquals(TeleportDestination.GAMMA, destination);
    }
}