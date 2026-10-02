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
        GameAction action = new GameAction(ActionType.ENTER_BOARD, List.of(piece), 6);
        ActionAnalysis analysis = new ActionAnalysis(action, true, false);

        assertSame(action, analysis.getAction());
        assertTrue(analysis.capturesOpponent());
        assertFalse(analysis.createsBlock());
    }

    @Test
    void shouldRejectNullAction() {
        assertThrows(IllegalArgumentException.class, () -> new ActionAnalysis(null, false, false));
    }
}