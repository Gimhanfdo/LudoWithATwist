package ludo.strategy.player;

import ludo.domain.enums.ActionType;
import ludo.domain.enums.Colour;
import ludo.domain.enums.Direction;
import ludo.domain.model.ActionAnalysis;
import ludo.domain.model.Board;
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

class RedStrategyTest {

    private ActionAnalyzer actionAnalyzer;
    private Board board;
    private RedStrategy strategy;
    private Player redPlayer;

    @BeforeEach
    void setUp() {
        actionAnalyzer = mock(ActionAnalyzer.class);
        board = new Board();
        strategy = new RedStrategy(actionAnalyzer, board);
        redPlayer = new Player(Colour.RED);
    }

    @Test
    void shouldPrioritizeCapture() {
        Piece redPiece = redPlayer.getPieces().get(0);
        Piece opponent = new Piece(Colour.BLUE, 1);

        opponent.enterBoard(13, Direction.CLOCKWISE);
        opponent.moveTo(20);

        GameAction ordinaryAction = new GameAction(ActionType.MOVE_PIECE, List.of(redPiece), 2);
        GameAction captureAction = new GameAction(ActionType.MOVE_PIECE, List.of(redPiece), 4);

        when(actionAnalyzer.analyze(ordinaryAction)).thenReturn(new ActionAnalysis(ordinaryAction, null, false, 10));
        when(actionAnalyzer.analyze(captureAction)).thenReturn(new ActionAnalysis(captureAction, opponent, false, 8));

        GameAction chosen = strategy.chooseAction(redPlayer, List.of(ordinaryAction, captureAction));

        assertSame(captureAction, chosen);
    }

    @Test
    void shouldCaptureOpponentClosestToHome() {
        Piece redPiece = redPlayer.getPieces().get(0);
        Piece firstOpponent = new Piece(Colour.BLUE, 1);
        Piece secondOpponent = new Piece(Colour.GREEN, 1);

        firstOpponent.enterBoard(13, Direction.CLOCKWISE);
        firstOpponent.moveTo(15);
        secondOpponent.enterBoard(39, Direction.CLOCKWISE);
        secondOpponent.moveTo(35);

        GameAction firstCapture = new GameAction(ActionType.MOVE_PIECE, List.of(redPiece), 2);
        GameAction secondCapture = new GameAction(ActionType.MOVE_PIECE, List.of(redPiece), 4);

        when(actionAnalyzer.analyze(firstCapture))
                .thenReturn(new ActionAnalysis(firstCapture, firstOpponent, false, 0));
        when(actionAnalyzer.analyze(secondCapture))
                .thenReturn(new ActionAnalysis(secondCapture, secondOpponent, false, 0));

        int firstDistance = board.getDistanceToHome(firstOpponent.getPosition(), firstOpponent.getColour(),
                firstOpponent.getDirection());
        int secondDistance = board.getDistanceToHome(secondOpponent.getPosition(), secondOpponent.getColour(),
                secondOpponent.getDirection());

        GameAction expected = firstDistance < secondDistance ? firstCapture : secondCapture;
        GameAction chosen = strategy.chooseAction(redPlayer, List.of(firstCapture, secondCapture));

        assertSame(expected, chosen);
    }

    @Test
    void shouldAvoidCreatingBlockWhenAlternativeExists() {
        Piece redPiece = redPlayer.getPieces().get(0);

        GameAction blockAction = new GameAction(ActionType.MOVE_PIECE, List.of(redPiece), 2);
        GameAction ordinaryAction = new GameAction(ActionType.MOVE_PIECE, List.of(redPiece), 4);

        when(actionAnalyzer.analyze(blockAction)).thenReturn(new ActionAnalysis(blockAction, null, true, 10));
        when(actionAnalyzer.analyze(ordinaryAction)).thenReturn(new ActionAnalysis(ordinaryAction, null, false, 8));

        GameAction chosen = strategy.chooseAction(redPlayer, List.of(blockAction, ordinaryAction));

        assertSame(ordinaryAction, chosen);
    }

    @Test
    void shouldReturnNullWhenNoLegalActionsExist() {
        GameAction chosen = strategy.chooseAction(redPlayer, List.of());

        assertNull(chosen);
        verifyNoInteractions(actionAnalyzer);
    }

    @Test
    void shouldRejectNullPlayer() {
        assertThrows(IllegalArgumentException.class, () -> strategy.chooseAction(null, List.of()));
    }

    @Test
    void shouldRejectNullActions() {
        assertThrows(IllegalArgumentException.class, () -> strategy.chooseAction(redPlayer, null));
    }

    @Test
    void shouldRejectNullActionWithinList() {
        List<GameAction> actions = new ArrayList<>();
        actions.add(null);

        assertThrows(IllegalArgumentException.class, () -> strategy.chooseAction(redPlayer, actions));
    }

    @Test
    void shouldRejectNullActionAnalyzer() {
        assertThrows(IllegalArgumentException.class, () -> new RedStrategy(null, board));
    }

    @Test
    void shouldRejectNullBoard() {
        assertThrows(IllegalArgumentException.class, () -> new RedStrategy(actionAnalyzer, null));
    }
}