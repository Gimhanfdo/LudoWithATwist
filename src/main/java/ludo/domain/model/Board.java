package ludo.domain.model;

import ludo.domain.enums.Colour;
import ludo.domain.enums.Direction;

import java.util.Map;

public class Board {

    public static final int STANDARD_PATH_SIZE = 52;
    public static final int HOME_STRAIGHT_SIZE = 5;

    private static final Map<Colour, Integer> START_POSITIONS = Map.of(
            Colour.YELLOW, 0,
            Colour.BLUE, 13,
            Colour.RED, 26,
            Colour.GREEN, 39);

    private static final Map<Colour, Integer> APPROACH_POSITIONS = Map.of(
            Colour.YELLOW, 51,
            Colour.BLUE, 12,
            Colour.RED, 25,
            Colour.GREEN, 38);

    public int getStartPosition(Colour colour) {
        validateColour(colour);
        return START_POSITIONS.get(colour);
    }

    public int getApproachPosition(Colour colour) {
        validateColour(colour);
        return APPROACH_POSITIONS.get(colour);
    }

    public boolean isValidStandardPosition(int position) {
        return position >= 0 && position < STANDARD_PATH_SIZE;
    }

    private void validateColour(Colour colour) {
        if (colour == null) {
            throw new IllegalArgumentException(
                    "Colour cannot be null.");
        }
    }

    public int getDistanceToApproach(
            int currentPosition,
            Colour colour,
            Direction direction) {

        if (!isValidStandardPosition(currentPosition)) {
            throw new IllegalArgumentException(
                    "Current position must be on the standard path.");
        }

        validateColour(colour);

        if (direction == null) {
            throw new IllegalArgumentException("Direction cannot be null.");
        }

        int approachPosition = getApproachPosition(colour);

        if (direction == Direction.CLOCKWISE) {

            return Math.floorMod(
                    approachPosition - currentPosition,
                    STANDARD_PATH_SIZE);
        }

        return Math.floorMod(
                currentPosition - approachPosition,
                STANDARD_PATH_SIZE);
    }

    public boolean movesBeyondApproach(
            int currentPosition,
            int movementDistance,
            Colour colour,
            Direction direction) {

        if (movementDistance <= 0) {
            throw new IllegalArgumentException(
                    "Movement distance must be greater than zero.");
        }

        int distanceToApproach = getDistanceToApproach(
                currentPosition,
                colour,
                direction);

        return movementDistance > distanceToApproach;
    }
}