package ludo.domain.model;

import ludo.domain.enums.ActionResult;

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
    void shouldRejectNullActionResult() {
        assertThrows(IllegalArgumentException.class, () -> ActionExecutionResult.of(null));
    }

    @Test
    void shouldRejectNullMovementOutcome() {
        assertThrows(IllegalArgumentException.class, () -> ActionExecutionResult.withMovement(ActionResult.MOVED, null));
    }
}