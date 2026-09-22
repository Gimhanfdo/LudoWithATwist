package ludo.domain.model;

import ludo.domain.enums.Colour;

import java.util.Map;

public class Board {

    public static final int STANDARD_PATH_SIZE = 52;
    public static final int HOME_STRAIGHT_SIZE = 5;

    private static final Map<Colour, Integer> START_POSITIONS = Map.of(
            Colour.YELLOW, 0,
            Colour.BLUE, 13,
            Colour.RED, 26,
            Colour.GREEN, 39
    );

    private static final Map<Colour, Integer> APPROACH_POSITIONS = Map.of(
            Colour.YELLOW, 51,
            Colour.BLUE, 12,
            Colour.RED, 25,
            Colour.GREEN, 38
    );

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
                    "Colour cannot be null."
            );
        }
    }
}