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

        when(actionAnalyzer.analyze(moveAction)).thenReturn(new ActionAnalysis(moveAction, null, false, 10, true));
        when(actionAnalyzer.analyze(enterBoardAction))
                .thenReturn(new ActionAnalysis(enterBoardAction, null, false, 0, false));

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

        when(actionAnalyzer.analyze(blockMove)).thenReturn(new ActionAnalysis(blockMove, null, true, 10, true));
        when(actionAnalyzer.analyze(enterBoardAction))
                .thenReturn(new ActionAnalysis(enterBoardAction, null, false, 0, false));

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

        when(actionAnalyzer.analyze(ordinaryMove)).thenReturn(new ActionAnalysis(ordinaryMove, null, false, 10, true));
        when(actionAnalyzer.analyze(blockMove)).thenReturn(new ActionAnalysis(blockMove, null, true, 12, true));

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

        when(actionAnalyzer.analyze(ordinaryMove)).thenReturn(new ActionAnalysis(ordinaryMove, null, false, 10, true));
        when(actionAnalyzer.analyze(blockMove)).thenReturn(new ActionAnalysis(blockMove, null, false, 0, false));

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

        when(actionAnalyzer.analyze(enterBoardAction))
                .thenReturn(new ActionAnalysis(enterBoardAction, null, false, 0, false));
        when(actionAnalyzer.analyze(blockAction)).thenReturn(new ActionAnalysis(blockAction, null, false, 0, false));

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

    @Test
    void shouldCaptureWithPieceThatNeedsHomeEligibility() {
        Piece firstPiece = greenPlayer.getPieces().get(0);
        Piece secondPiece = greenPlayer.getPieces().get(1);
        Piece opponent = new Piece(Colour.RED, 1);

        firstPiece.enterBoard(39, Direction.CLOCKWISE);
        secondPiece.enterBoard(39, Direction.CLOCKWISE);
        opponent.enterBoard(26, Direction.CLOCKWISE);
        opponent.moveTo(20);

        GameAction ordinaryAction = new GameAction(ActionType.MOVE_PIECE, List.of(firstPiece), 3);
        GameAction captureAction = new GameAction(ActionType.MOVE_PIECE, List.of(secondPiece), 3);

        when(actionAnalyzer.analyze(ordinaryAction))
                .thenReturn(new ActionAnalysis(ordinaryAction, null, false, 5, true));
        when(actionAnalyzer.analyze(captureAction))
                .thenReturn(new ActionAnalysis(captureAction, opponent, false, 10, true));

        GameAction chosen = strategy.chooseAction(greenPlayer, List.of(ordinaryAction, captureAction));

        assertSame(captureAction, chosen);
    }

    @Test
    void shouldPreferHomeProgressOverUnnecessaryCapture() {
        Piece capturePiece = greenPlayer.getPieces().get(0);
        Piece progressingPiece = greenPlayer.getPieces().get(1);
        Piece opponent = new Piece(Colour.RED, 1);

        capturePiece.enterBoard(39, Direction.CLOCKWISE);
        progressingPiece.enterBoard(39, Direction.CLOCKWISE);
        capturePiece.recordCapture();
        progressingPiece.recordCapture();
        opponent.enterBoard(26, Direction.CLOCKWISE);
        opponent.moveTo(20);

        GameAction captureAction = new GameAction(ActionType.MOVE_PIECE, List.of(capturePiece), 3);
        GameAction progressAction = new GameAction(ActionType.MOVE_PIECE, List.of(progressingPiece), 3);

        when(actionAnalyzer.analyze(captureAction))
                .thenReturn(new ActionAnalysis(captureAction, opponent, false, 12, true));
        when(actionAnalyzer.analyze(progressAction))
                .thenReturn(new ActionAnalysis(progressAction, null, false, 5, true));

        GameAction chosen = strategy.chooseAction(greenPlayer, List.of(captureAction, progressAction));

        assertSame(progressAction, chosen);
    }

    @Test
    void shouldPreferHomeEligiblePieceClosestToHome() {
        Piece firstPiece = greenPlayer.getPieces().get(0);
        Piece secondPiece = greenPlayer.getPieces().get(1);

        firstPiece.enterBoard(39, Direction.CLOCKWISE);
        secondPiece.enterBoard(39, Direction.CLOCKWISE);
        firstPiece.recordCapture();
        secondPiece.recordCapture();

        GameAction firstAction = new GameAction(ActionType.MOVE_PIECE, List.of(firstPiece), 4);
        GameAction secondAction = new GameAction(ActionType.MOVE_PIECE, List.of(secondPiece), 4);

        when(actionAnalyzer.analyze(firstAction)).thenReturn(new ActionAnalysis(firstAction, null, false, 14, true));
        when(actionAnalyzer.analyze(secondAction)).thenReturn(new ActionAnalysis(secondAction, null, false, 6, true));

        GameAction chosen = strategy.chooseAction(greenPlayer, List.of(firstAction, secondAction));

        assertSame(secondAction, chosen);
    }

    @Test
    void shouldPreferClosestPieceToHomeAsFallback() {
        Piece firstPiece = greenPlayer.getPieces().get(0);
        Piece secondPiece = greenPlayer.getPieces().get(1);

        firstPiece.enterBoard(39, Direction.CLOCKWISE);
        secondPiece.enterBoard(39, Direction.CLOCKWISE);

        GameAction firstAction = new GameAction(ActionType.MOVE_PIECE, List.of(firstPiece), 2);
        GameAction secondAction = new GameAction(ActionType.MOVE_PIECE, List.of(secondPiece), 2);

        when(actionAnalyzer.analyze(firstAction)).thenReturn(new ActionAnalysis(firstAction, null, false, 15, true));
        when(actionAnalyzer.analyze(secondAction)).thenReturn(new ActionAnalysis(secondAction, null, false, 9, true));

        GameAction chosen = strategy.chooseAction(greenPlayer, List.of(firstAction, secondAction));

        assertSame(secondAction, chosen);
    }

    @Test
    void shouldPrioritizeBlockOverCapture() {
        Piece blockPiece = greenPlayer.getPieces().get(0);
        Piece capturePiece = greenPlayer.getPieces().get(1);
        Piece opponent = new Piece(Colour.RED, 1);

        blockPiece.enterBoard(39, Direction.CLOCKWISE);
        capturePiece.enterBoard(39, Direction.CLOCKWISE);
        opponent.enterBoard(26, Direction.CLOCKWISE);
        opponent.moveTo(20);

        GameAction captureAction = new GameAction(ActionType.MOVE_PIECE, List.of(capturePiece), 4);
        GameAction blockAction = new GameAction(ActionType.MOVE_PIECE, List.of(blockPiece), 4);

        when(actionAnalyzer.analyze(captureAction))
                .thenReturn(new ActionAnalysis(captureAction, opponent, false, 5, true));
        when(actionAnalyzer.analyze(blockAction)).thenReturn(new ActionAnalysis(blockAction, null, true, 12, true));

        GameAction chosen = strategy.chooseAction(greenPlayer, List.of(captureAction, blockAction));

        assertSame(blockAction, chosen);
    }

    @Test
    void shouldPreferActionThatReachesHome() {
        Piece firstPiece = greenPlayer.getPieces().get(0);
        Piece secondPiece = greenPlayer.getPieces().get(1);

        firstPiece.enterBoard(39, Direction.CLOCKWISE);
        secondPiece.enterBoard(39, Direction.CLOCKWISE);
        firstPiece.recordCapture();
        secondPiece.recordCapture();

        GameAction ordinaryAction = new GameAction(ActionType.MOVE_PIECE, List.of(firstPiece), 2);
        GameAction homeAction = new GameAction(ActionType.MOVE_PIECE, List.of(secondPiece), 2);

        when(actionAnalyzer.analyze(ordinaryAction))
                .thenReturn(new ActionAnalysis(ordinaryAction, null, false, 5, true));
        when(actionAnalyzer.analyze(homeAction)).thenReturn(new ActionAnalysis(homeAction, null, false, 0, true));

        GameAction chosen = strategy.chooseAction(greenPlayer, List.of(ordinaryAction, homeAction));

        assertSame(homeAction, chosen);
    }
}