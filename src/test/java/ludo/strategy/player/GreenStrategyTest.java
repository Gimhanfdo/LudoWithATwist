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

class GreenStrategyTest {

    private ActionAnalyzer actionAnalyzer;
    private GreenStrategy strategy;
    private Player greenPlayer;

    @BeforeEach
    void setUp() {
        actionAnalyzer = mock(ActionAnalyzer.class);
        strategy = new GreenStrategy(actionAnalyzer);
        greenPlayer = new Player(Colour.GREEN);
    }

    @Test
    void shouldEnterPieceFromBaseOnSix() {
        Piece movingPiece = greenPlayer.getPieces().get(0);
        Piece basePiece = greenPlayer.getPieces().get(1);

        movingPiece.enterBoard(39, Direction.CLOCKWISE);

        GameAction moveAction = new GameAction(ActionType.MOVE_PIECE, List.of(movingPiece), 6);
        GameAction enterBoardAction = new GameAction(ActionType.ENTER_BOARD, List.of(basePiece), 6);

        when(actionAnalyzer.analyze(moveAction)).thenReturn(new ActionAnalysis(moveAction, null, false, 10));
        when(actionAnalyzer.analyze(enterBoardAction)).thenReturn(new ActionAnalysis(enterBoardAction, null, false, 0));

        GameAction chosen = strategy.chooseAction(greenPlayer, List.of(moveAction, enterBoardAction));

        assertSame(enterBoardAction, chosen);
    }

    @Test
    void shouldCreateBlockInsteadOfEnteringBoardOnSix() {
        Piece movingPiece = greenPlayer.getPieces().get(0);
        Piece basePiece = greenPlayer.getPieces().get(1);

        movingPiece.enterBoard(39, Direction.CLOCKWISE);

        GameAction blockMove = new GameAction(ActionType.MOVE_PIECE, List.of(movingPiece), 6);
        GameAction enterBoardAction = new GameAction(ActionType.ENTER_BOARD, List.of(basePiece), 6);

        when(actionAnalyzer.analyze(blockMove)).thenReturn(new ActionAnalysis(blockMove, null, true, 10));
        when(actionAnalyzer.analyze(enterBoardAction)).thenReturn(new ActionAnalysis(enterBoardAction, null, false, 0));

        GameAction chosen = strategy.chooseAction(greenPlayer, List.of(enterBoardAction, blockMove));

        assertSame(blockMove, chosen);
    }

    @Test
    void shouldPreferCreatingBlockOnOrdinaryRoll() {
        Piece firstPiece = greenPlayer.getPieces().get(0);
        Piece secondPiece = greenPlayer.getPieces().get(1);

        firstPiece.enterBoard(39, Direction.CLOCKWISE);
        secondPiece.enterBoard(39, Direction.CLOCKWISE);

        GameAction ordinaryMove = new GameAction(ActionType.MOVE_PIECE, List.of(firstPiece), 3);
        GameAction blockMove = new GameAction(ActionType.MOVE_PIECE, List.of(secondPiece), 3);

        when(actionAnalyzer.analyze(ordinaryMove)).thenReturn(new ActionAnalysis(ordinaryMove, null, false, 10));
        when(actionAnalyzer.analyze(blockMove)).thenReturn(new ActionAnalysis(blockMove, null, true, 12));

        GameAction chosen = strategy.chooseAction(greenPlayer, List.of(ordinaryMove, blockMove));

        assertSame(blockMove, chosen);
    }

    @Test
    void shouldPreferMovingExistingBlock() {
        Piece firstPiece = greenPlayer.getPieces().get(0);
        Piece secondPiece = greenPlayer.getPieces().get(1);

        firstPiece.enterBoard(39, Direction.CLOCKWISE);
        secondPiece.enterBoard(39, Direction.CLOCKWISE);

        GameAction ordinaryMove = new GameAction(ActionType.MOVE_PIECE, List.of(firstPiece), 4);
        GameAction blockMove = new GameAction(ActionType.MOVE_BLOCK, List.of(firstPiece, secondPiece), 4);

        when(actionAnalyzer.analyze(ordinaryMove)).thenReturn(new ActionAnalysis(ordinaryMove, null, false, 10));
        when(actionAnalyzer.analyze(blockMove)).thenReturn(new ActionAnalysis(blockMove, null, false, 0));

        GameAction chosen = strategy.chooseAction(greenPlayer, List.of(ordinaryMove, blockMove));

        assertSame(blockMove, chosen);
    }

    @Test
    void shouldMoveExistingBlockBeforeEnteringBoardOnSix() {
        Piece firstPiece = greenPlayer.getPieces().get(0);
        Piece secondPiece = greenPlayer.getPieces().get(1);
        Piece basePiece = greenPlayer.getPieces().get(2);

        firstPiece.enterBoard(39, Direction.CLOCKWISE);
        secondPiece.enterBoard(39, Direction.CLOCKWISE);

        GameAction enterBoardAction = new GameAction(ActionType.ENTER_BOARD, List.of(basePiece), 6);
        GameAction blockAction = new GameAction(ActionType.MOVE_BLOCK, List.of(firstPiece, secondPiece), 6);

        when(actionAnalyzer.analyze(enterBoardAction)).thenReturn(new ActionAnalysis(enterBoardAction, null, false, 0));
        when(actionAnalyzer.analyze(blockAction)).thenReturn(new ActionAnalysis(blockAction, null, false, 0));

        GameAction chosen = strategy.chooseAction(greenPlayer, List.of(enterBoardAction, blockAction));

        assertSame(blockAction, chosen);
    }

    @Test
    void shouldReturnNullWhenNoLegalActionsExist() {
        GameAction chosen = strategy.chooseAction(greenPlayer, List.of());

        assertNull(chosen);
        verifyNoInteractions(actionAnalyzer);
    }

    @Test
    void shouldRejectNullPlayer() {
        assertThrows(IllegalArgumentException.class, () -> strategy.chooseAction(null, List.of()));
    }

    @Test
    void shouldRejectNullActions() {
        assertThrows(IllegalArgumentException.class, () -> strategy.chooseAction(greenPlayer, null));
    }

    @Test
    void shouldRejectNullActionWithinList() {
        List<GameAction> actions = new ArrayList<>();
        actions.add(null);

        assertThrows(IllegalArgumentException.class, () -> strategy.chooseAction(greenPlayer, actions));
    }

    @Test
    void shouldRejectNullActionAnalyzer() {
        assertThrows(IllegalArgumentException.class, () -> new GreenStrategy(null));
    }
}