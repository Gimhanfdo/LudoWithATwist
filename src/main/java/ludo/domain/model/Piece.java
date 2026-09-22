package ludo.domain.model;

import ludo.domain.enums.Colour;
import ludo.domain.enums.Direction;
import ludo.domain.enums.PieceEffect;
import ludo.domain.enums.PieceState;

public class Piece {

    private final Colour colour;
    private final int number;

    private PieceState state;
    private Direction direction;
    private PieceEffect effect;

    private Integer position;
    private int captureCount;
    private int approachPassCount;
    private int effectRoundsRemaining;

    public Piece(Colour colour, int number) {

        if (colour == null) {
            throw new IllegalArgumentException(
                    "Piece colour cannot be null.");
        }

        if (number < 1 || number > 4) {
            throw new IllegalArgumentException(
                    "Piece number must be between 1 and 4.");
        }

        this.colour = colour;
        this.number = number;

        reset();
    }

    public void reset() {
        state = PieceState.BASE;
        direction = null;
        effect = PieceEffect.NONE;

        position = null;

        captureCount = 0;
        approachPassCount = 0;
        effectRoundsRemaining = 0;
    }

    public void enterBoard(int startPosition, Direction direction) {

        if (state != PieceState.BASE) {
            throw new IllegalStateException(
                    "Only a piece in Base can enter the board.");
        }

        if (direction == null) {
            throw new IllegalArgumentException(
                    "Direction cannot be null.");
        }

        state = PieceState.STANDARD_PATH;
        position = startPosition;
        this.direction = direction;
    }

    public void moveTo(int newPosition) {

        if (state != PieceState.STANDARD_PATH) {
            throw new IllegalStateException(
                    "Only a piece on the standard path can move to a standard position.");
        }

        position = newPosition;
    }

    public void recordCapture() {
        captureCount++;
    }

    public boolean hasCaptured() {
        return captureCount > 0;
    }

    public void recordApproachPass() {
        approachPassCount++;
    }

    public boolean hasPassedApproachTwice() {
        return approachPassCount >= 2;
    }

    public Colour getColour() {
        return colour;
    }

    public int getNumber() {
        return number;
    }

    public String getName() {
        return colour.name().charAt(0) + String.valueOf(number);
    }

    public PieceState getState() {
        return state;
    }

    public Direction getDirection() {
        return direction;
    }

    public PieceEffect getEffect() {
        return effect;
    }

    public Integer getPosition() {
        return position;
    }

    public int getCaptureCount() {
        return captureCount;
    }

    public int getApproachPassCount() {
        return approachPassCount;
    }

    public int getEffectRoundsRemaining() {
        return effectRoundsRemaining;
    }
}