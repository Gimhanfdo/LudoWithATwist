package ludo.domain.model;

import ludo.domain.enums.ActionResult;

public class ActionExecutionResult {

    private final ActionResult result;
    private final MovementOutcome movementOutcome;
    private final BlockMovementOutcome blockMovementOutcome;

    private ActionExecutionResult(ActionResult result, MovementOutcome movementOutcome, BlockMovementOutcome blockMovementOutcome) {
        if (result == null) {
            throw new IllegalArgumentException("Action result cannot be null.");
        }

        if (movementOutcome != null && blockMovementOutcome != null) {
            throw new IllegalArgumentException("Execution result cannot contain multiple movement outcomes.");
        }

        this.result = result;
        this.movementOutcome = movementOutcome;
        this.blockMovementOutcome = blockMovementOutcome;
    }

    public static ActionExecutionResult of(ActionResult result) {
        return new ActionExecutionResult(result, null, null);
    }

    public static ActionExecutionResult withMovement(ActionResult result, MovementOutcome movementOutcome) {
        if (movementOutcome == null) {
            throw new IllegalArgumentException("Movement outcome cannot be null.");
        }

        return new ActionExecutionResult(result, movementOutcome, null);
    }

    public static ActionExecutionResult withBlockMovement(ActionResult result, BlockMovementOutcome blockMovementOutcome) {
        if (blockMovementOutcome == null) {
            throw new IllegalArgumentException("Block movement outcome cannot be null.");
        }

        return new ActionExecutionResult(result, null, blockMovementOutcome);
    }

    public ActionResult getResult() {
        return result;
    }

    public MovementOutcome getMovementOutcome() {
        return movementOutcome;
    }

    public BlockMovementOutcome getBlockMovementOutcome() {
        return blockMovementOutcome;
    }

    public boolean hasMovementOutcome() {
        return movementOutcome != null;
    }

    public boolean hasBlockMovementOutcome() {
        return blockMovementOutcome != null;
    }
}