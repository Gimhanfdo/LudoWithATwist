package ludo.factory;

import ludo.domain.enums.Colour;
import ludo.strategy.player.PlayerStrategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlayerStrategyFactoryTest {

    private PlayerStrategy redStrategy;
    private PlayerStrategy greenStrategy;
    private PlayerStrategy yellowStrategy;
    private PlayerStrategy blueStrategy;
    private PlayerStrategyFactory factory;

    @BeforeEach
    void setUp() {
        redStrategy = mock(PlayerStrategy.class);
        greenStrategy = mock(PlayerStrategy.class);
        yellowStrategy = mock(PlayerStrategy.class);
        blueStrategy = mock(PlayerStrategy.class);

        factory = new PlayerStrategyFactory(redStrategy, greenStrategy, yellowStrategy, blueStrategy);
    }

    @Test
    void shouldReturnRedStrategy() {
        assertSame(redStrategy, factory.getStrategy(Colour.RED));
    }

    @Test
    void shouldReturnGreenStrategy() {
        assertSame(greenStrategy, factory.getStrategy(Colour.GREEN));
    }

    @Test
    void shouldReturnYellowStrategy() {
        assertSame(yellowStrategy, factory.getStrategy(Colour.YELLOW));
    }

    @Test
    void shouldReturnBlueStrategy() {
        assertSame(blueStrategy, factory.getStrategy(Colour.BLUE));
    }

    @Test
    void shouldRejectNullColour() {
        assertThrows(IllegalArgumentException.class, () -> factory.getStrategy(null));
    }

    @Test
    void shouldRejectNullRedStrategy() {
        assertThrows(IllegalArgumentException.class,
                () -> new PlayerStrategyFactory(null, greenStrategy, yellowStrategy, blueStrategy));
    }

    @Test
    void shouldRejectNullGreenStrategy() {
        assertThrows(IllegalArgumentException.class,
                () -> new PlayerStrategyFactory(redStrategy, null, yellowStrategy, blueStrategy));
    }

    @Test
    void shouldRejectNullYellowStrategy() {
        assertThrows(IllegalArgumentException.class,
                () -> new PlayerStrategyFactory(redStrategy, greenStrategy, null, blueStrategy));
    }

    @Test
    void shouldRejectNullBlueStrategy() {
        assertThrows(IllegalArgumentException.class,
                () -> new PlayerStrategyFactory(redStrategy, greenStrategy, yellowStrategy, null));
    }
}