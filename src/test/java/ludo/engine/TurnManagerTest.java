package ludo.engine;

import ludo.domain.enums.ActionType;
import ludo.domain.enums.Colour;
import ludo.domain.model.GameAction;
import ludo.domain.model.Player;
import ludo.factory.PlayerStrategyFactory;
import ludo.random.Dice;
import ludo.service.GameActionExecutor;
import ludo.service.LegalActionGenerator;
import ludo.strategy.player.PlayerStrategy;

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

        turnManager = new TurnManager(dice, legalActionGenerator, strategyFactory, actionExecutor);
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
        verifyNoInteractions(dice, legalActionGenerator, strategyFactory, actionExecutor);
    }

    @Test
    void shouldRejectNullDice() {
        assertThrows(IllegalArgumentException.class,
                () -> new TurnManager(null, legalActionGenerator, strategyFactory, actionExecutor));
    }

    @Test
    void shouldRejectNullLegalActionGenerator() {
        assertThrows(IllegalArgumentException.class,
                () -> new TurnManager(dice, null, strategyFactory, actionExecutor));
    }

    @Test
    void shouldRejectNullStrategyFactory() {
        assertThrows(IllegalArgumentException.class,
                () -> new TurnManager(dice, legalActionGenerator, null, actionExecutor));
    }

    @Test
    void shouldRejectNullActionExecutor() {
        assertThrows(IllegalArgumentException.class,
                () -> new TurnManager(dice, legalActionGenerator, strategyFactory, null));
    }
}