package ludo.domain.model;

import ludo.domain.enums.ActionResult;

public class ActionExecutionResult {

    private final ActionResult result;
    private final MovementOutcome movementOutcome;

    public ActionExecutionResult(ActionResult result, MovementOutcome movementOutcome) {
        if (result == null) {
            throw new IllegalArgumentException("Action result cannot be null.");
        }

        this.result = result;
        this.movementOutcome = movementOutcome;
    }

    public static ActionExecutionResult of(ActionResult result) {
        return new ActionExecutionResult(result, null);
    }

    public static ActionExecutionResult withMovement(ActionResult result, MovementOutcome movementOutcome) {
        if (movementOutcome == null) {
            throw new IllegalArgumentException("Movement outcome cannot be null.");
        }

        return new ActionExecutionResult(result, movementOutcome);
    }

    public ActionResult getResult() {
        return result;
    }

    public MovementOutcome getMovementOutcome() {
        return movementOutcome;
    }

    public boolean hasMovementOutcome() {
        return movementOutcome != null;
    }
}