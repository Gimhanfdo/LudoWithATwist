package ludo.engine;

import ludo.domain.enums.ActionResult;
import ludo.domain.enums.ActionType;
import ludo.domain.enums.Colour;
import ludo.domain.model.GameAction;
import ludo.domain.model.Player;
import ludo.factory.PlayerStrategyFactory;
import ludo.random.Dice;
import ludo.service.GameActionExecutor;
import ludo.service.LegalActionGenerator;
import ludo.strategy.player.PlayerStrategy;
import ludo.service.ConsecutiveSixTracker;
import ludo.service.ForcedBlockBreakService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TurnManagerTest {

    private Dice dice;
    private LegalActionGenerator legalActionGenerator;
    private PlayerStrategyFactory strategyFactory;
    private GameActionExecutor actionExecutor;
    private ConsecutiveSixTracker consecutiveSixTracker;
    private ForcedBlockBreakService forcedBlockBreakService;
    private PlayerStrategy strategy;
    private TurnManager turnManager;
    private Player player;

    @BeforeEach
    void setUp() {
        dice = mock(Dice.class);
        legalActionGenerator = mock(LegalActionGenerator.class);
        strategyFactory = mock(PlayerStrategyFactory.class);
        actionExecutor = mock(GameActionExecutor.class);
        strategy = mock(PlayerStrategy.class);
        consecutiveSixTracker = mock(ConsecutiveSixTracker.class);
        forcedBlockBreakService = mock(ForcedBlockBreakService.class);
        turnManager = new TurnManager(dice, legalActionGenerator, strategyFactory, actionExecutor,
                consecutiveSixTracker, forcedBlockBreakService);
        player = new Player(Colour.RED);
    }

    @Test
    void shouldExecuteChosenAction() {
        GameAction action = new GameAction(ActionType.MOVE_PIECE, List.of(player.getPieces().get(0)), 4);
        List<GameAction> legalActions = List.of(action);

        when(dice.roll()).thenReturn(4);
        when(legalActionGenerator.generateActions(player, 4)).thenReturn(legalActions);
        when(strategyFactory.getStrategy(Colour.RED)).thenReturn(strategy);
        when(strategy.chooseAction(player, legalActions)).thenReturn(action);

        turnManager.takeTurn(player);

        verify(dice).roll();
        verify(legalActionGenerator).generateActions(player, 4);
        verify(strategyFactory).getStrategy(Colour.RED);
        verify(strategy).chooseAction(player, legalActions);
        verify(actionExecutor).execute(action);
    }

    @Test
    void shouldEndRollWhenNoLegalActionsExist() {
        when(dice.roll()).thenReturn(3);
        when(legalActionGenerator.generateActions(player, 3)).thenReturn(List.of());

        turnManager.takeTurn(player);

        verify(dice).roll();
        verify(legalActionGenerator).generateActions(player, 3);
        verifyNoInteractions(strategyFactory, strategy, actionExecutor);
    }

    @Test
    void shouldNotExecuteWhenStrategyChoosesNoAction() {
        GameAction action = new GameAction(ActionType.MOVE_PIECE, List.of(player.getPieces().get(0)), 2);
        List<GameAction> legalActions = List.of(action);

        when(dice.roll()).thenReturn(2);
        when(legalActionGenerator.generateActions(player, 2)).thenReturn(legalActions);
        when(strategyFactory.getStrategy(Colour.RED)).thenReturn(strategy);
        when(strategy.chooseAction(player, legalActions)).thenReturn(null);

        turnManager.takeTurn(player);

        verify(strategy).chooseAction(player, legalActions);
        verifyNoInteractions(actionExecutor);
    }

    @Test
    void shouldRejectNullPlayer() {
        assertThrows(IllegalArgumentException.class, () -> turnManager.takeTurn(null));
        verifyNoInteractions(dice, legalActionGenerator, strategyFactory, actionExecutor, consecutiveSixTracker);
    }

    @Test
    void shouldRejectNullDice() {
        assertThrows(IllegalArgumentException.class,
                () -> new TurnManager(null, legalActionGenerator, strategyFactory, actionExecutor,
                        consecutiveSixTracker, forcedBlockBreakService));
    }

    @Test
    void shouldRejectNullLegalActionGenerator() {
        assertThrows(IllegalArgumentException.class,
                () -> new TurnManager(dice, null, strategyFactory, actionExecutor, consecutiveSixTracker,
                        forcedBlockBreakService));
    }

    @Test
    void shouldRejectNullStrategyFactory() {
        assertThrows(IllegalArgumentException.class,
                () -> new TurnManager(dice, legalActionGenerator, null, actionExecutor, consecutiveSixTracker,
                        forcedBlockBreakService));
    }

    @Test
    void shouldRejectNullActionExecutor() {
        assertThrows(IllegalArgumentException.class,
                () -> new TurnManager(dice, legalActionGenerator, strategyFactory, null, consecutiveSixTracker,
                        forcedBlockBreakService));
    }

    @Test
    void shouldRejectNullConsecutiveSixTracker() {
        assertThrows(IllegalArgumentException.class,
                () -> new TurnManager(dice, legalActionGenerator, strategyFactory, actionExecutor, null,
                        forcedBlockBreakService));
    }

    @Test
    void shouldRejectNullForcedBlockBreakService() {
        assertThrows(IllegalArgumentException.class,
                () -> new TurnManager(dice, legalActionGenerator, strategyFactory, actionExecutor,
                        consecutiveSixTracker, null));
    }

    @Test
    void shouldIgnoreThirdConsecutiveSix() {
        when(dice.roll()).thenReturn(6);
        when(consecutiveSixTracker.recordRoll(player, 6)).thenReturn(true);

        turnManager.takeTurn(player);

        verify(dice).roll();
        verify(consecutiveSixTracker).recordRoll(player, 6);
        verifyNoInteractions(legalActionGenerator, strategyFactory, actionExecutor);
    }

    @Test
    void shouldContinueTurnWhenRollIsNotThirdConsecutiveSix() {
        when(dice.roll()).thenReturn(5);
        when(consecutiveSixTracker.recordRoll(player, 5)).thenReturn(false);
        when(legalActionGenerator.generateActions(player, 5)).thenReturn(List.of());

        turnManager.takeTurn(player);

        verify(consecutiveSixTracker).recordRoll(player, 5);
        verify(legalActionGenerator).generateActions(player, 5);
    }

    @Test
    void shouldGrantBonusRollAfterSix() {
        when(dice.roll()).thenReturn(6, 4);
        when(consecutiveSixTracker.recordRoll(player, 6)).thenReturn(false);
        when(consecutiveSixTracker.recordRoll(player, 4)).thenReturn(false);
        when(legalActionGenerator.generateActions(player, 6)).thenReturn(List.of());
        when(legalActionGenerator.generateActions(player, 4)).thenReturn(List.of());

        turnManager.takeTurn(player);

        verify(dice, times(2)).roll();
        verify(legalActionGenerator).generateActions(player, 6);
        verify(legalActionGenerator).generateActions(player, 4);
    }

    @Test
    void shouldNotGrantBonusRollAfterNonSix() {
        when(dice.roll()).thenReturn(5);
        when(consecutiveSixTracker.recordRoll(player, 5)).thenReturn(false);
        when(legalActionGenerator.generateActions(player, 5)).thenReturn(List.of());

        turnManager.takeTurn(player);

        verify(dice, times(1)).roll();
    }

    @Test
    void shouldContinueAfterTwoConsecutiveSixes() {
        when(dice.roll()).thenReturn(6, 6, 4);
        when(consecutiveSixTracker.recordRoll(player, 6)).thenReturn(false, false);
        when(consecutiveSixTracker.recordRoll(player, 4)).thenReturn(false);
        when(legalActionGenerator.generateActions(player, 6)).thenReturn(List.of());
        when(legalActionGenerator.generateActions(player, 4)).thenReturn(List.of());

        turnManager.takeTurn(player);

        verify(dice, times(3)).roll();
        verify(consecutiveSixTracker, times(2)).recordRoll(player, 6);
        verify(consecutiveSixTracker).recordRoll(player, 4);
    }

    @Test
    void shouldAttemptForcedBlockBreakOnThirdConsecutiveSix() {
        when(dice.roll()).thenReturn(6);
        when(consecutiveSixTracker.recordRoll(player, 6)).thenReturn(true);

        turnManager.takeTurn(player);

        verify(forcedBlockBreakService).breakBlock(player);
        verifyNoInteractions(legalActionGenerator, strategyFactory, actionExecutor);
    }

    @Test
    void shouldExecuteActionBeforeBonusRoll() {
        GameAction action = new GameAction(ActionType.ENTER_BOARD, List.of(player.getPieces().get(0)), 6);
        List<GameAction> legalActions = List.of(action);

        when(dice.roll()).thenReturn(6, 2);
        when(consecutiveSixTracker.recordRoll(player, 6)).thenReturn(false);
        when(consecutiveSixTracker.recordRoll(player, 2)).thenReturn(false);
        when(legalActionGenerator.generateActions(player, 6)).thenReturn(legalActions);
        when(legalActionGenerator.generateActions(player, 2)).thenReturn(List.of());
        when(strategyFactory.getStrategy(Colour.RED)).thenReturn(strategy);
        when(strategy.chooseAction(player, legalActions)).thenReturn(action);

        turnManager.takeTurn(player);

        verify(actionExecutor).execute(action);
        verify(dice, times(2)).roll();
    }

    @Test
    void shouldGrantBonusRollAfterCapture() {
        GameAction captureAction = new GameAction(ActionType.MOVE_PIECE, List.of(player.getPieces().get(0)), 4);
        List<GameAction> legalActions = List.of(captureAction);

        when(dice.roll()).thenReturn(4, 2);
        when(consecutiveSixTracker.recordRoll(player, 4)).thenReturn(false);
        when(consecutiveSixTracker.recordRoll(player, 2)).thenReturn(false);
        when(legalActionGenerator.generateActions(player, 4)).thenReturn(legalActions);
        when(legalActionGenerator.generateActions(player, 2)).thenReturn(List.of());
        when(strategyFactory.getStrategy(Colour.RED)).thenReturn(strategy);
        when(strategy.chooseAction(player, legalActions)).thenReturn(captureAction);
        when(actionExecutor.execute(captureAction)).thenReturn(ActionResult.CAPTURED);

        turnManager.takeTurn(player);

        verify(dice, times(2)).roll();
    }

    @Test
    void shouldNotGrantBonusRollAfterNormalMovement() {
        GameAction action = new GameAction(ActionType.MOVE_PIECE, List.of(player.getPieces().get(0)), 4);
        List<GameAction> legalActions = List.of(action);

        when(dice.roll()).thenReturn(4);
        when(consecutiveSixTracker.recordRoll(player, 4)).thenReturn(false);
        when(legalActionGenerator.generateActions(player, 4)).thenReturn(legalActions);
        when(strategyFactory.getStrategy(Colour.RED)).thenReturn(strategy);
        when(strategy.chooseAction(player, legalActions)).thenReturn(action);
        when(actionExecutor.execute(action)).thenReturn(ActionResult.MOVED);

        turnManager.takeTurn(player);

        verify(dice, times(1)).roll();
    }
}