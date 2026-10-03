package ludo.command;

import ludo.domain.enums.ActionResult;
import ludo.domain.enums.ActionType;
import ludo.domain.enums.Colour;
import ludo.domain.model.ActionExecutionResult;
import ludo.domain.model.GameAction;
import ludo.domain.model.MovementOutcome;
import ludo.domain.model.MovementResult;
import ludo.domain.model.Piece;
import ludo.service.MovementCoordinator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MovePieceCommandTest {

    private MovementCoordinator movementCoordinator;
    private MovePieceCommand command;

    @BeforeEach
    void setUp() {
        movementCoordinator = mock(MovementCoordinator.class);
        command = new MovePieceCommand(movementCoordinator);
    }

    @Test
    void shouldReturnMovedWhenPieceMoves() {
        Piece piece = new Piece(Colour.YELLOW, 1);
        GameAction action = new GameAction(ActionType.MOVE_PIECE, List.of(piece), 4);
        MovementOutcome movementOutcome = new MovementOutcome(MovementResult.MOVED, 4, 4, 10, 14, List.of(), List.of());

        when(movementCoordinator.moveDetailed(piece, 4)).thenReturn(movementOutcome);

        ActionExecutionResult result = command.execute(action);

        assertEquals(ActionResult.MOVED, result.getResult());
        assertSame(movementOutcome, result.getMovementOutcome());
        assertTrue(result.hasMovementOutcome());
    }

    @Test
    void shouldReturnCapturedWhenMovementCapturesOpponent() {
        Piece piece = new Piece(Colour.YELLOW, 1);
        Piece opponent = new Piece(Colour.BLUE, 1);
        GameAction action = new GameAction(ActionType.MOVE_PIECE, List.of(piece), 4);
        MovementOutcome movementOutcome = new MovementOutcome(MovementResult.CAPTURED, 4, 4, 10, 14, List.of(), List.of(opponent));

        when(movementCoordinator.moveDetailed(piece, 4)).thenReturn(movementOutcome);

        ActionExecutionResult result = command.execute(action);

        assertEquals(ActionResult.CAPTURED, result.getResult());
        assertSame(movementOutcome, result.getMovementOutcome());
        assertTrue(result.hasMovementOutcome());
    }

    @Test
    void shouldReturnNotMovedWhenMovementFails() {
        Piece piece = new Piece(Colour.YELLOW, 1);
        GameAction action = new GameAction(ActionType.MOVE_PIECE, List.of(piece), 4);
        MovementOutcome movementOutcome = new MovementOutcome(MovementResult.NOT_MOVED, 4, 0, 10, 10, List.of(), List.of());

        when(movementCoordinator.moveDetailed(piece, 4)).thenReturn(movementOutcome);

        ActionExecutionResult result = command.execute(action);

        assertEquals(ActionResult.NOT_MOVED, result.getResult());
        assertSame(movementOutcome, result.getMovementOutcome());
        assertTrue(result.hasMovementOutcome());
    }

    @Test
    void shouldRejectWrongActionType() {
        Piece piece = new Piece(Colour.YELLOW, 1);
        GameAction action = new GameAction(ActionType.ENTER_BOARD, List.of(piece), 6);

        assertThrows(IllegalArgumentException.class, () -> command.execute(action));
        verifyNoInteractions(movementCoordinator);
    }

    @Test
    void shouldRejectNullAction() {
        assertThrows(IllegalArgumentException.class, () -> command.execute(null));
        verifyNoInteractions(movementCoordinator);
    }

    @Test
    void shouldRejectNullMovementCoordinator() {
        assertThrows(IllegalArgumentException.class, () -> new MovePieceCommand(null));
    }
}