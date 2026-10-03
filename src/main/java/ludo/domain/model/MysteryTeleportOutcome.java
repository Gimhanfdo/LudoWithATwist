package ludo.domain.model;

import ludo.domain.enums.Direction;
import ludo.domain.enums.PieceEffect;
import ludo.domain.enums.PieceState;
import ludo.domain.enums.TeleportDestination;

public class MysteryTeleportOutcome {

    private final TeleportDestination selectedDestination;
    private final PieceState finalState;
    private final Integer finalPosition;
    private final Direction previousDirection;
    private final Direction finalDirection;
    private final PieceEffect finalEffect;

    public MysteryTeleportOutcome(TeleportDestination selectedDestination, PieceState finalState, Integer finalPosition,
                                  Direction previousDirection, Direction finalDirection, PieceEffect finalEffect) {
        if (selectedDestination == null) {
            throw new IllegalArgumentException("Selected destination cannot be null.");
        }

        if (finalState == null) {
            throw new IllegalArgumentException("Final piece state cannot be null.");
        }

        if (finalEffect == null) {
            throw new IllegalArgumentException("Final piece effect cannot be null.");
        }

        this.selectedDestination = selectedDestination;
        this.finalState = finalState;
        this.finalPosition = finalPosition;
        this.previousDirection = previousDirection;
        this.finalDirection = finalDirection;
        this.finalEffect = finalEffect;
    }

    public TeleportDestination getSelectedDestination() {
        return selectedDestination;
    }

    public PieceState getFinalState() {
        return finalState;
    }

    public Integer getFinalPosition() {
        return finalPosition;
    }

    public Direction getPreviousDirection() {
        return previousDirection;
    }

    public Direction getFinalDirection() {
        return finalDirection;
    }

    public PieceEffect getFinalEffect() {
        return finalEffect;
    }

    public boolean directionChanged() {
        return previousDirection != null && finalDirection != null && previousDirection != finalDirection;
    }
}