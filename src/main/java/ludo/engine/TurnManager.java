package ludo.engine;

import ludo.domain.enums.ActionResult;
import ludo.domain.model.GameAction;
import ludo.domain.model.Player;
import ludo.factory.PlayerStrategyFactory;
import ludo.random.Dice;
import ludo.service.ConsecutiveSixTracker;
import ludo.service.GameActionExecutor;
import ludo.service.LegalActionGenerator;
import ludo.strategy.player.PlayerStrategy;

import java.util.List;

public class TurnManager {

    private final Dice dice;
    private final LegalActionGenerator legalActionGenerator;
    private final PlayerStrategyFactory strategyFactory;
    private final GameActionExecutor actionExecutor;
    private final ConsecutiveSixTracker consecutiveSixTracker;

    public TurnManager(Dice dice, LegalActionGenerator legalActionGenerator, PlayerStrategyFactory strategyFactory,
            GameActionExecutor actionExecutor, ConsecutiveSixTracker consecutiveSixTracker) {
        validateDependencies(dice, legalActionGenerator, strategyFactory, actionExecutor, consecutiveSixTracker);

        this.dice = dice;
        this.legalActionGenerator = legalActionGenerator;
        this.strategyFactory = strategyFactory;
        this.actionExecutor = actionExecutor;
        this.consecutiveSixTracker = consecutiveSixTracker;
    }

    public void takeTurn(Player player) {
        validatePlayer(player);

        boolean bonusRoll;

        do {
            bonusRoll = executeRoll(player);
        } while (bonusRoll);
    }

    private boolean executeRoll(Player player) {
        int roll = dice.roll();
        boolean thirdConsecutiveSix = consecutiveSixTracker.recordRoll(player, roll);

        if (thirdConsecutiveSix) {
            return false;
        }

        ActionResult result = executeAction(player, roll);

        return roll == 6 || result == ActionResult.CAPTURED;
    }

    private ActionResult executeAction(Player player, int roll) {
        List<GameAction> legalActions = legalActionGenerator.generateActions(player, roll);

        if (legalActions.isEmpty()) {
            return ActionResult.NOT_MOVED;
        }

        PlayerStrategy strategy = strategyFactory.getStrategy(player.getColour());
        GameAction chosenAction = strategy.chooseAction(player, legalActions);

        if (chosenAction == null) {
            return ActionResult.NOT_MOVED;
        }

        return actionExecutor.execute(chosenAction);
    }

    private void validatePlayer(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null.");
        }
    }

    private void validateDependencies(Dice dice, LegalActionGenerator legalActionGenerator,
            PlayerStrategyFactory strategyFactory, GameActionExecutor actionExecutor,
            ConsecutiveSixTracker consecutiveSixTracker) {
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

        if (consecutiveSixTracker == null) {
            throw new IllegalArgumentException("Consecutive six tracker cannot be null.");
        }
    }
}