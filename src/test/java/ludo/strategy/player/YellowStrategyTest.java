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

class YellowStrategyTest {

    private ActionAnalyzer actionAnalyzer;
    private YellowStrategy strategy;
    private Player yellowPlayer;

    @BeforeEach
    void setUp() {
        actionAnalyzer = mock(ActionAnalyzer.class);
        strategy = new YellowStrategy(actionAnalyzer);
        yellowPlayer = new Player(Colour.YELLOW);
    }

    @Test
    void shouldEnterPieceFromBaseOnSix() {
        Piece movingPiece = yellowPlayer.getPieces().get(0);
        Piece basePiece = yellowPlayer.getPieces().get(1);

        movingPiece.enterBoard(0, Direction.CLOCKWISE);

        GameAction moveAction = new GameAction(ActionType.MOVE_PIECE, List.of(movingPiece), 6);
        GameAction enterBoardAction = new GameAction(ActionType.ENTER_BOARD, List.of(basePiece), 6);

        when(actionAnalyzer.analyze(moveAction)).thenReturn(new ActionAnalysis(moveAction, null, false, 5, true));
        when(actionAnalyzer.analyze(enterBoardAction))
                .thenReturn(new ActionAnalysis(enterBoardAction, null, false, 0, false));

        GameAction chosen = strategy.chooseAction(yellowPlayer, List.of(moveAction, enterBoardAction));

        assertSame(enterBoardAction, chosen);
    }

    @Test
    void shouldPrioritizeEnteringBoardOverCaptureOnSix() {
        Piece movingPiece = yellowPlayer.getPieces().get(0);
        Piece basePiece = yellowPlayer.getPieces().get(1);
        Piece opponent = new Piece(Colour.RED, 1);

        movingPiece.enterBoard(0, Direction.CLOCKWISE);
        opponent.enterBoard(26, Direction.CLOCKWISE);
        opponent.moveTo(20);

        GameAction captureAction = new GameAction(ActionType.MOVE_PIECE, List.of(movingPiece), 6);
        GameAction enterBoardAction = new GameAction(ActionType.ENTER_BOARD, List.of(basePiece), 6);

        when(actionAnalyzer.analyze(captureAction))
                .thenReturn(new ActionAnalysis(captureAction, opponent, false, 5, true));
        when(actionAnalyzer.analyze(enterBoardAction))
                .thenReturn(new ActionAnalysis(enterBoardAction, null, false, 0, false));

        GameAction chosen = strategy.chooseAction(yellowPlayer, List.of(captureAction, enterBoardAction));

        assertSame(enterBoardAction, chosen);
    }

    @Test
    void shouldCaptureWhenPieceNeedsHomeEligibility() {
        Piece firstPiece = yellowPlayer.getPieces().get(0);
        Piece secondPiece = yellowPlayer.getPieces().get(1);
        Piece opponent = new Piece(Colour.BLUE, 1);

        firstPiece.enterBoard(0, Direction.CLOCKWISE);
        secondPiece.enterBoard(0, Direction.CLOCKWISE);
        opponent.enterBoard(13, Direction.CLOCKWISE);
        opponent.moveTo(20);

        GameAction progressAction = new GameAction(ActionType.MOVE_PIECE, List.of(firstPiece), 4);
        GameAction captureAction = new GameAction(ActionType.MOVE_PIECE, List.of(secondPiece), 4);

        when(actionAnalyzer.analyze(progressAction))
                .thenReturn(new ActionAnalysis(progressAction, null, false, 3, true));
        when(actionAnalyzer.analyze(captureAction))
                .thenReturn(new ActionAnalysis(captureAction, opponent, false, 10, true));

        GameAction chosen = strategy.chooseAction(yellowPlayer, List.of(progressAction, captureAction));

        assertSame(captureAction, chosen);
    }

    @Test
    void shouldPreferHomeProgressOverUnnecessaryCapture() {
        Piece capturePiece = yellowPlayer.getPieces().get(0);
        Piece progressPiece = yellowPlayer.getPieces().get(1);
        Piece opponent = new Piece(Colour.BLUE, 1);

        capturePiece.enterBoard(0, Direction.CLOCKWISE);
        progressPiece.enterBoard(0, Direction.CLOCKWISE);
        capturePiece.recordCapture();
        progressPiece.recordCapture();
        opponent.enterBoard(13, Direction.CLOCKWISE);
        opponent.moveTo(20);

        GameAction captureAction = new GameAction(ActionType.MOVE_PIECE, List.of(capturePiece), 3);
        GameAction progressAction = new GameAction(ActionType.MOVE_PIECE, List.of(progressPiece), 3);

        when(actionAnalyzer.analyze(captureAction))
                .thenReturn(new ActionAnalysis(captureAction, opponent, false, 10, true));
        when(actionAnalyzer.analyze(progressAction))
                .thenReturn(new ActionAnalysis(progressAction, null, false, 4, true));

        GameAction chosen = strategy.chooseAction(yellowPlayer, List.of(captureAction, progressAction));

        assertSame(progressAction, chosen);
    }

    @Test
    void shouldPreferPieceClosestToHome() {
        Piece firstPiece = yellowPlayer.getPieces().get(0);
        Piece secondPiece = yellowPlayer.getPieces().get(1);

        firstPiece.enterBoard(0, Direction.CLOCKWISE);
        secondPiece.enterBoard(0, Direction.CLOCKWISE);

        GameAction firstAction = new GameAction(ActionType.MOVE_PIECE, List.of(firstPiece), 3);
        GameAction secondAction = new GameAction(ActionType.MOVE_PIECE, List.of(secondPiece), 3);

        when(actionAnalyzer.analyze(firstAction)).thenReturn(new ActionAnalysis(firstAction, null, false, 12, true));
        when(actionAnalyzer.analyze(secondAction)).thenReturn(new ActionAnalysis(secondAction, null, false, 5, true));

        GameAction chosen = strategy.chooseAction(yellowPlayer, List.of(firstAction, secondAction));

        assertSame(secondAction, chosen);
    }

    @Test
    void shouldPreferActionThatReachesHome() {
        Piece firstPiece = yellowPlayer.getPieces().get(0);
        Piece secondPiece = yellowPlayer.getPieces().get(1);

        firstPiece.enterBoard(0, Direction.CLOCKWISE);
        secondPiece.enterBoard(0, Direction.CLOCKWISE);
        firstPiece.recordCapture();
        secondPiece.recordCapture();

        GameAction ordinaryAction = new GameAction(ActionType.MOVE_PIECE, List.of(firstPiece), 2);
        GameAction homeAction = new GameAction(ActionType.MOVE_PIECE, List.of(secondPiece), 2);

        when(actionAnalyzer.analyze(ordinaryAction))
                .thenReturn(new ActionAnalysis(ordinaryAction, null, false, 5, true));
        when(actionAnalyzer.analyze(homeAction)).thenReturn(new ActionAnalysis(homeAction, null, false, 0, true));

        GameAction chosen = strategy.chooseAction(yellowPlayer, List.of(ordinaryAction, homeAction));

        assertSame(homeAction, chosen);
    }

    @Test
    void shouldIgnoreUnknownProgressWhenKnownProgressExists() {
        Piece firstPiece = yellowPlayer.getPieces().get(0);
        Piece secondPiece = yellowPlayer.getPieces().get(1);

        firstPiece.enterBoard(0, Direction.CLOCKWISE);
        secondPiece.enterBoard(0, Direction.CLOCKWISE);

        GameAction unknownAction = new GameAction(ActionType.MOVE_PIECE, List.of(firstPiece), 2);
        GameAction knownAction = new GameAction(ActionType.MOVE_PIECE, List.of(secondPiece), 2);

        when(actionAnalyzer.analyze(unknownAction))
                .thenReturn(new ActionAnalysis(unknownAction, null, false, 0, false));
        when(actionAnalyzer.analyze(knownAction)).thenReturn(new ActionAnalysis(knownAction, null, false, 10, true));

        GameAction chosen = strategy.chooseAction(yellowPlayer, List.of(unknownAction, knownAction));

        assertSame(knownAction, chosen);
    }

    @Test
    void shouldReturnNullWhenNoLegalActionsExist() {
        GameAction chosen = strategy.chooseAction(yellowPlayer, List.of());

        assertNull(chosen);
        verifyNoInteractions(actionAnalyzer);
    }

    @Test
    void shouldRejectNullPlayer() {
        assertThrows(IllegalArgumentException.class, () -> strategy.chooseAction(null, List.of()));
    }

    @Test
    void shouldRejectNullActions() {
        assertThrows(IllegalArgumentException.class, () -> strategy.chooseAction(yellowPlayer, null));
    }

    @Test
    void shouldRejectNullActionWithinList() {
        List<GameAction> actions = new ArrayList<>();
        actions.add(null);

        assertThrows(IllegalArgumentException.class, () -> strategy.chooseAction(yellowPlayer, actions));
    }

    @Test
    void shouldRejectNullActionAnalyzer() {
        assertThrows(IllegalArgumentException.class, () -> new YellowStrategy(null));
    }
}