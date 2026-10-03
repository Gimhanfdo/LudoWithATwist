package ludo.command;

import ludo.domain.enums.ActionResult;
import ludo.domain.enums.ActionType;
import ludo.domain.enums.Colour;
import ludo.domain.enums.Direction;
import ludo.domain.model.ActionExecutionResult;
import ludo.domain.model.GameAction;
import ludo.domain.model.MovementResult;
import ludo.domain.model.Piece;
import ludo.service.MovementService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MoveBlockCommandTest {

    private MovementService movementService;
    private MoveBlockCommand command;

    @BeforeEach
    void setUp() {
        movementService = mock(MovementService.class);
        command = new MoveBlockCommand(movementService);
    }

    @Test
    void shouldExecuteBlockMovement() {
        Piece firstPiece = new Piece(Colour.GREEN, 1);
        Piece secondPiece = new Piece(Colour.GREEN, 2);
        firstPiece.enterBoard(39, Direction.CLOCKWISE);
        secondPiece.enterBoard(39, Direction.CLOCKWISE);
        firstPiece.moveTo(20);
        secondPiece.moveTo(20);
        GameAction action = new GameAction(ActionType.MOVE_BLOCK, List.of(firstPiece, secondPiece), 4);

        when(movementService.moveBlock(20, Colour.GREEN, 4)).thenReturn(MovementResult.MOVED);

        ActionExecutionResult result = command.execute(action);

        assertEquals(ActionResult.MOVED, result.getResult());
        assertFalse(result.hasMovementOutcome());
        verify(movementService).moveBlock(20, Colour.GREEN, 4);
    }

    @Test
    void shouldReturnCapturedWhenBlockCapturesOpponentBlock() {
        Piece firstPiece = new Piece(Colour.GREEN, 1);
        Piece secondPiece = new Piece(Colour.GREEN, 2);
        firstPiece.enterBoard(39, Direction.CLOCKWISE);
        secondPiece.enterBoard(39, Direction.CLOCKWISE);
        firstPiece.moveTo(20);
        secondPiece.moveTo(20);
        GameAction action = new GameAction(ActionType.MOVE_BLOCK, List.of(firstPiece, secondPiece), 4);

        when(movementService.moveBlock(20, Colour.GREEN, 4)).thenReturn(MovementResult.CAPTURED);

        ActionExecutionResult result = command.execute(action);

        assertEquals(ActionResult.CAPTURED, result.getResult());
        assertFalse(result.hasMovementOutcome());
    }

    @Test
    void shouldRejectActionWithOnePiece() {
        Piece piece = new Piece(Colour.GREEN, 1);
        GameAction action = new GameAction(ActionType.MOVE_BLOCK, List.of(piece), 4);

        assertThrows(IllegalArgumentException.class, () -> command.execute(action));
        verifyNoInteractions(movementService);
    }

    @Test
    void shouldRejectWrongActionType() {
        Piece piece = new Piece(Colour.GREEN, 1);
        GameAction action = new GameAction(ActionType.MOVE_PIECE, List.of(piece), 4);

        assertThrows(IllegalArgumentException.class, () -> command.execute(action));
        verifyNoInteractions(movementService);
    }

    @Test
    void shouldRejectNullAction() {
        assertThrows(IllegalArgumentException.class, () -> command.execute(null));
        verifyNoInteractions(movementService);
    }

    @Test
    void shouldRejectNullMovementService() {
        assertThrows(IllegalArgumentException.class, () -> new MoveBlockCommand(null));
    }
}