package ludo.engine;

import ludo.domain.model.GameAction;
import ludo.domain.model.Player;
import ludo.factory.PlayerStrategyFactory;
import ludo.random.Dice;
import ludo.service.GameActionExecutor;
import ludo.service.LegalActionGenerator;
import ludo.strategy.player.PlayerStrategy;

import java.util.List;

public class TurnManager {

    private final Dice dice;
    private final LegalActionGenerator legalActionGenerator;
    private final PlayerStrategyFactory strategyFactory;
    private final GameActionExecutor actionExecutor;

    public TurnManager(Dice dice, LegalActionGenerator legalActionGenerator, PlayerStrategyFactory strategyFactory,
                       GameActionExecutor actionExecutor) {
        validateDependencies(dice, legalActionGenerator, strategyFactory, actionExecutor);

        this.dice = dice;
        this.legalActionGenerator = legalActionGenerator;
        this.strategyFactory = strategyFactory;
        this.actionExecutor = actionExecutor;
    }

    public void takeTurn(Player player) {
        validatePlayer(player);

        int roll = dice.roll();
        List<GameAction> legalActions = legalActionGenerator.generateActions(player, roll);

        if (legalActions.isEmpty()) {
            return;
        }

        PlayerStrategy strategy = strategyFactory.getStrategy(player.getColour());
        GameAction chosenAction = strategy.chooseAction(player, legalActions);

        if (chosenAction != null) {
            actionExecutor.execute(chosenAction);
        }
    }

    private void validatePlayer(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null.");
        }
    }

    private void validateDependencies(Dice dice, LegalActionGenerator legalActionGenerator,
                                      PlayerStrategyFactory strategyFactory, GameActionExecutor actionExecutor) {
        if (dice == null) {
            throw new IllegalArgumentException("Dice cannot be null.");
        }

        if (legalActionGenerator == null) {
            throw new IllegalArgumentException("Legal action generator cannot be null.");
        }

        if (strategyFactory == null) {
            throw new IllegalArgumentException("Strategy factory cannot be null.");
        }

        if (actionExecutor == null) {
            throw new IllegalArgumentException("Action executor cannot be null.");
        }
    }
}