package ludo.service;

import ludo.domain.enums.TeleportDestination;
import ludo.domain.model.Piece;
import ludo.random.TeleportDestinationSelector;
import ludo.strategy.teleport.TeleportStrategy;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class MysteryTeleportService {

    private final TeleportDestinationSelector destinationSelector;
    private final Map<TeleportDestination, TeleportStrategy> strategies;

    public MysteryTeleportService(TeleportDestinationSelector destinationSelector, List<TeleportStrategy> strategies) {
        if (destinationSelector == null) {
            throw new IllegalArgumentException("Destination selector cannot be null.");
        }

        if (strategies == null) {
            throw new IllegalArgumentException("Teleport strategies cannot be null.");
        }

        this.destinationSelector = destinationSelector;
        this.strategies = createStrategyMap(strategies);
    }

    public TeleportDestination teleport(Piece piece) {
        if (piece == null) {
            throw new IllegalArgumentException("Piece cannot be null.");
        }

        TeleportDestination destination = destinationSelector.selectDestination();
        TeleportStrategy strategy = getStrategy(destination);

        strategy.teleport(piece);

        return destination;
    }

    private Map<TeleportDestination, TeleportStrategy> createStrategyMap(List<TeleportStrategy> strategies) {
        Map<TeleportDestination, TeleportStrategy> strategyMap = new EnumMap<>(TeleportDestination.class);

        for (TeleportStrategy strategy : strategies) {
            validateStrategy(strategy);

            TeleportDestination destination = strategy.getDestination();

            if (strategyMap.containsKey(destination)) {
                throw new IllegalArgumentException("Duplicate teleport strategy for " + destination + ".");
            }

            strategyMap.put(destination, strategy);
        }

        return strategyMap;
    }

    private void validateStrategy(TeleportStrategy strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("Teleport strategy cannot be null.");
        }

        if (strategy.getDestination() == null) {
            throw new IllegalArgumentException("Teleport destination cannot be null.");
        }
    }

    private TeleportStrategy getStrategy(TeleportDestination destination) {
        TeleportStrategy strategy = strategies.get(destination);

        if (strategy == null) {
            throw new IllegalStateException("No teleport strategy registered for " + destination + ".");
        }

        return strategy;
    }
}