package ludo.command;

import ludo.domain.enums.ActionResult;
import ludo.domain.enums.ActionType;
import ludo.domain.enums.Colour;
import ludo.domain.model.GameAction;
import ludo.domain.model.Piece;
import ludo.service.MoveExecutor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EnterBoardCommandTest {

    private MoveExecutor moveExecutor;
    private EnterBoardCommand command;

    @BeforeEach
    void setUp() {
        moveExecutor = mock(MoveExecutor.class);
        command = new EnterBoardCommand(moveExecutor);
    }

    @Test
    void shouldExecuteEnterBoardAction() {
        Piece piece = new Piece(Colour.RED, 1);
        GameAction action = new GameAction(ActionType.ENTER_BOARD, List.of(piece), 6);

        when(moveExecutor.moveFromBase(piece, 6)).thenReturn(true);

        ActionResult result = command.execute(action);

        assertEquals(ActionResult.MOVED, result);
        verify(moveExecutor).moveFromBase(piece, 6);
    }

    @Test
    void shouldReturnNotMovedWhenPieceCannotEnterBoard() {
        Piece piece = new Piece(Colour.RED, 1);
        GameAction action = new GameAction(ActionType.ENTER_BOARD, List.of(piece), 6);

        when(moveExecutor.moveFromBase(piece, 6)).thenReturn(false);

        ActionResult result = command.execute(action);

        assertEquals(ActionResult.NOT_MOVED, result);
    }

    @Test
    void shouldRejectWrongActionType() {
        Piece piece = new Piece(Colour.RED, 1);
        GameAction action = new GameAction(ActionType.MOVE_PIECE, List.of(piece), 6);

        assertThrows(IllegalArgumentException.class, () -> command.execute(action));
        verifyNoInteractions(moveExecutor);
    }

    @Test
    void shouldRejectNullAction() {
        assertThrows(IllegalArgumentException.class, () -> command.execute(null));
        verifyNoInteractions(moveExecutor);
    }

    @Test
    void shouldRejectNullMoveExecutor() {
        assertThrows(IllegalArgumentException.class, () -> new EnterBoardCommand(null));
    }
}