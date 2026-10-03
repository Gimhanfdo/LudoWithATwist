package ludo.command;

import ludo.domain.enums.ActionResult;
import ludo.domain.enums.ActionType;
import ludo.domain.enums.Colour;
import ludo.domain.enums.Direction;
import ludo.domain.model.ActionExecutionResult;
import ludo.domain.model.BlockMovementOutcome;
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

        List<Piece> block = List.of(firstPiece, secondPiece);
        GameAction action = new GameAction(ActionType.MOVE_BLOCK, block, 4);

        BlockMovementOutcome movementOutcome = new BlockMovementOutcome(
                MovementResult.MOVED, 2, 2, 20, 22, Direction.CLOCKWISE, block, List.of());

        when(movementService.moveBlockDetailed(20, Colour.GREEN, 4)).thenReturn(movementOutcome);

        ActionExecutionResult result = command.execute(action);

        assertEquals(ActionResult.MOVED, result.getResult());
        assertTrue(result.hasBlockMovementOutcome());
        assertFalse(result.hasMovementOutcome());
        assertSame(movementOutcome, result.getBlockMovementOutcome());

        verify(movementService).moveBlockDetailed(20, Colour.GREEN, 4);
    }

    @Test
    void shouldReturnCapturedWhenBlockCapturesOpponentBlock() {
        Piece firstPiece = new Piece(Colour.GREEN, 1);
        Piece secondPiece = new Piece(Colour.GREEN, 2);
        Piece blueOne = new Piece(Colour.BLUE, 1);
        Piece blueTwo = new Piece(Colour.BLUE, 2);

        firstPiece.enterBoard(39, Direction.CLOCKWISE);
        secondPiece.enterBoard(39, Direction.CLOCKWISE);
        firstPiece.moveTo(20);
        secondPiece.moveTo(20);

        List<Piece> block = List.of(firstPiece, secondPiece);
        List<Piece> capturedPieces = List.of(blueOne, blueTwo);
        GameAction action = new GameAction(ActionType.MOVE_BLOCK, block, 6);

        BlockMovementOutcome movementOutcome = new BlockMovementOutcome(
                MovementResult.CAPTURED, 3, 3, 20, 23, Direction.CLOCKWISE, block, capturedPieces);

        when(movementService.moveBlockDetailed(20, Colour.GREEN, 6)).thenReturn(movementOutcome);

        ActionExecutionResult result = command.execute(action);

        assertEquals(ActionResult.CAPTURED, result.getResult());
        assertTrue(result.hasBlockMovementOutcome());
        assertEquals(capturedPieces, result.getBlockMovementOutcome().getCapturedPieces());
    }

    @Test
    void shouldRejectActionWithOnePiece() {
        Piece piece = new Piece(Colour.GREEN, 1);
        GameAction action = new GameAction(ActionType.MOVE_BLOCK, List.of(piece), 4);

        assertThrows(IllegalArgumentException.class, () -> command.execute(action));
        verifyNoInteractions(movementService);
    }

    @Test
    void shouldReturnDetailedOutcomeWhenBlockDoesNotMove() {
        Piece firstPiece = new Piece(Colour.GREEN, 1);
        Piece secondPiece = new Piece(Colour.GREEN, 2);

        firstPiece.enterBoard(39, Direction.CLOCKWISE);
        secondPiece.enterBoard(39, Direction.CLOCKWISE);
        firstPiece.moveTo(20);
        secondPiece.moveTo(20);

        List<Piece> block = List.of(firstPiece, secondPiece);
        GameAction action = new GameAction(ActionType.MOVE_BLOCK, block, 1);

        BlockMovementOutcome movementOutcome = new BlockMovementOutcome(
                MovementResult.NOT_MOVED, 0, 0, 20, 20, Direction.CLOCKWISE, block, List.of());

        when(movementService.moveBlockDetailed(20, Colour.GREEN, 1)).thenReturn(movementOutcome);

        ActionExecutionResult result = command.execute(action);

        assertEquals(ActionResult.NOT_MOVED, result.getResult());
        assertTrue(result.hasBlockMovementOutcome());
        assertFalse(result.getBlockMovementOutcome().moved());
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