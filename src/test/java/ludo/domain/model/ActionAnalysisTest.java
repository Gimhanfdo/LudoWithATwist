package ludo.domain.model;

import ludo.domain.enums.ActionType;
import ludo.domain.enums.Colour;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ActionAnalysisTest {

    @Test
    void shouldCreateActionAnalysis() {
        Piece piece = new Piece(Colour.RED, 1);
        Piece opponent = new Piece(Colour.BLUE, 1);
        GameAction action = new GameAction(ActionType.ENTER_BOARD, List.of(piece), 6);
        ActionAnalysis analysis = new ActionAnalysis(action, opponent, false, 10, true, false);

        assertSame(action, analysis.getAction());
        assertTrue(analysis.capturesOpponent());
        assertSame(opponent, analysis.getCapturedPiece());
        assertFalse(analysis.createsBlock());
        assertEquals(10, analysis.getDistanceToHome());
    }

    @Test
    void shouldReportNoCaptureWhenCapturedPieceIsNull() {
        Piece piece = new Piece(Colour.RED, 1);
        GameAction action = new GameAction(ActionType.ENTER_BOARD, List.of(piece), 6);
        ActionAnalysis analysis = new ActionAnalysis(action, null, false, 0, false, false);

        assertFalse(analysis.capturesOpponent());
        assertNull(analysis.getCapturedPiece());
    }

    @Test
    void shouldRejectNullAction() {
        assertThrows(IllegalArgumentException.class, () -> new ActionAnalysis(null, null, false, 0, false, false));
    }

    @Test
    void shouldRejectNegativeDistanceToHome() {
        Piece piece = new Piece(Colour.RED, 1);
        GameAction action = new GameAction(ActionType.ENTER_BOARD, List.of(piece), 6);
        assertThrows(IllegalArgumentException.class, () -> new ActionAnalysis(action, null, false, -1, true, false));
    }
}