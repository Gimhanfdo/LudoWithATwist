package ludo.domain.model;

import ludo.domain.enums.ActionResult;
import ludo.domain.enums.Colour;
import ludo.domain.enums.Direction;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ActionExecutionResultTest {

    @Test
    void shouldCreateResultWithoutMovementDetails() {
        ActionExecutionResult executionResult = ActionExecutionResult.of(ActionResult.MOVED);

        assertEquals(ActionResult.MOVED, executionResult.getResult());
        assertFalse(executionResult.hasMovementOutcome());
        assertNull(executionResult.getMovementOutcome());
    }

    @Test
    void shouldCreateResultWithMovementDetails() {
        MovementOutcome movementOutcome = new MovementOutcome(MovementResult.MOVED, 4, 4, 10, 14, List.of(), List.of());
        ActionExecutionResult executionResult = ActionExecutionResult.withMovement(ActionResult.MOVED, movementOutcome);

        assertEquals(ActionResult.MOVED, executionResult.getResult());
        assertTrue(executionResult.hasMovementOutcome());
        assertSame(movementOutcome, executionResult.getMovementOutcome());
    }

    @Test
    void shouldStoreBlockMovementOutcome() {
        Piece firstPiece = new Piece(Colour.RED, 1);
        Piece secondPiece = new Piece(Colour.RED, 2);

        BlockMovementOutcome blockMovementOutcome = new BlockMovementOutcome(
                MovementResult.MOVED, 3, 3, 20, 23, Direction.CLOCKWISE,
                List.of(firstPiece, secondPiece), List.of());

        ActionExecutionResult result = ActionExecutionResult.withBlockMovement(ActionResult.MOVED,
                blockMovementOutcome);

        assertEquals(ActionResult.MOVED, result.getResult());
        assertTrue(result.hasBlockMovementOutcome());
        assertFalse(result.hasMovementOutcome());
        assertSame(blockMovementOutcome, result.getBlockMovementOutcome());
        assertNull(result.getMovementOutcome());
    }

    @Test
    void shouldRejectNullBlockMovementOutcome() {
        assertThrows(IllegalArgumentException.class,
                () -> ActionExecutionResult.withBlockMovement(ActionResult.MOVED, null));
    }

    @Test
    void shouldRejectNullActionResult() {
        assertThrows(IllegalArgumentException.class, () -> ActionExecutionResult.of(null));
    }

    @Test
    void shouldRejectNullMovementOutcome() {
        assertThrows(IllegalArgumentException.class,
                () -> ActionExecutionResult.withMovement(ActionResult.MOVED, null));
    }
}