package ludo.factory;

import ludo.domain.enums.Colour;
import ludo.strategy.player.PlayerStrategy;

import java.util.EnumMap;
import java.util.Map;

public class PlayerStrategyFactory {

    private final Map<Colour, PlayerStrategy> strategies;

    public PlayerStrategyFactory(PlayerStrategy redStrategy, PlayerStrategy greenStrategy,
                                 PlayerStrategy yellowStrategy, PlayerStrategy blueStrategy) {
                                    
        validateStrategy(redStrategy, "Red");
        validateStrategy(greenStrategy, "Green");
        validateStrategy(yellowStrategy, "Yellow");
        validateStrategy(blueStrategy, "Blue");

        strategies = new EnumMap<>(Colour.class);

        strategies.put(Colour.RED, redStrategy);
        strategies.put(Colour.GREEN, greenStrategy);
        strategies.put(Colour.YELLOW, yellowStrategy);
        strategies.put(Colour.BLUE, blueStrategy);
    }

    public PlayerStrategy getStrategy(Colour colour) {
        if (colour == null) {
            throw new IllegalArgumentException("Colour cannot be null.");
        }

        return strategies.get(colour);
    }

    private void validateStrategy(PlayerStrategy strategy, String colour) {
        if (strategy == null) {
            throw new IllegalArgumentException(colour + " strategy cannot be null.");
        }
    }
}