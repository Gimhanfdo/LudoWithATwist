package ludo.strategy.player;

import ludo.domain.enums.ActionType;
import ludo.domain.enums.Colour;
import ludo.domain.enums.Direction;
import ludo.domain.model.ActionAnalysis;
import ludo.domain.model.GameAction;
import ludo.domain.model.Piece;
import ludo.domain.model.Player;
import ludo.service.ActionAnalyzer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BlueStrategyTest {

    private ActionAnalyzer actionAnalyzer;
    private BlueStrategy strategy;
    private Player bluePlayer;

    @BeforeEach
    void setUp() {
        actionAnalyzer = mock(ActionAnalyzer.class);
        strategy = new BlueStrategy(actionAnalyzer);
        bluePlayer = new Player(Colour.BLUE);
    }

    @Test
    void shouldMovePiecesCyclically() {
        Piece b1 = bluePlayer.getPieces().get(0);
        Piece b2 = bluePlayer.getPieces().get(1);

        b1.enterBoard(13, Direction.CLOCKWISE);
        b2.enterBoard(13, Direction.CLOCKWISE);

        GameAction b1Action = new GameAction(ActionType.MOVE_PIECE, List.of(b1), 3);
        GameAction b2Action = new GameAction(ActionType.MOVE_PIECE, List.of(b2), 3);

        when(actionAnalyzer.analyze(b1Action)).thenReturn(new ActionAnalysis(b1Action, null, false, 10, true, false));
        when(actionAnalyzer.analyze(b2Action)).thenReturn(new ActionAnalysis(b2Action, null, false, 10, true, false));

        List<GameAction> actions = List.of(b2Action, b1Action);

        GameAction firstChoice = strategy.chooseAction(bluePlayer, actions);
        GameAction secondChoice = strategy.chooseAction(bluePlayer, actions);

        assertSame(b1Action, firstChoice);
        assertSame(b2Action, secondChoice);
    }

    @Test
    void shouldSkipPieceWithoutLegalAction() {
        Piece b2 = bluePlayer.getPieces().get(1);

        b2.enterBoard(13, Direction.CLOCKWISE);

        GameAction b2Action = new GameAction(ActionType.MOVE_PIECE, List.of(b2), 4);

        when(actionAnalyzer.analyze(b2Action)).thenReturn(new ActionAnalysis(b2Action, null, false, 10, true, false));

        GameAction chosen = strategy.chooseAction(bluePlayer, List.of(b2Action));

        assertSame(b2Action, chosen);
    }

    @Test
    void shouldReturnNullWhenNoLegalActionsExist() {
        GameAction chosen = strategy.chooseAction(bluePlayer, List.of());

        assertNull(chosen);
        verifyNoInteractions(actionAnalyzer);
    }

    @Test
    void shouldRejectNullPlayer() {
        assertThrows(IllegalArgumentException.class, () -> strategy.chooseAction(null, List.of()));
    }

    @Test
    void shouldRejectNullActions() {
        assertThrows(IllegalArgumentException.class, () -> strategy.chooseAction(bluePlayer, null));
    }

    @Test
    void shouldRejectNullActionWithinList() {
        List<GameAction> actions = new ArrayList<>();
        actions.add(null);

        assertThrows(IllegalArgumentException.class, () -> strategy.chooseAction(bluePlayer, actions));
    }

    @Test
    void shouldRejectNullActionAnalyzer() {
        assertThrows(IllegalArgumentException.class, () -> new BlueStrategy(null));
    }
}