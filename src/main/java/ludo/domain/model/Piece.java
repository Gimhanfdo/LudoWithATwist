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

    public void applyEffect(PieceEffect effect, int rounds) {

        if (effect == null) {
            throw new IllegalArgumentException("Piece effect cannot be null.");
        }

        if (effect == PieceEffect.NONE) {
            throw new IllegalArgumentException("Cannot apply NONE as an active effect.");
        }

        if (rounds <= 0) {
            throw new IllegalArgumentException("Effect rounds must be positive.");
        }

        this.effect = effect;
        this.effectRoundsRemaining = rounds;
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

    public void enterHomeStraight(int homeStraightPosition) {

        if (state != PieceState.STANDARD_PATH) {
            throw new IllegalStateException(
                    "Only a piece on the standard path can enter the Home Straight.");
        }

        if (homeStraightPosition < 0
                || homeStraightPosition >= Board.HOME_STRAIGHT_SIZE) {

            throw new IllegalArgumentException(
                    "Invalid Home Straight position.");
        }

        state = PieceState.HOME_STRAIGHT;
        position = homeStraightPosition;
    }

    public void reachHome() {

        if (state != PieceState.HOME_STRAIGHT) {
            throw new IllegalStateException(
                    "Only a piece in the Home Straight can reach Home.");
        }

        state = PieceState.HOME;
        position = null;
    }

    public void moveWithinHomeStraight(int newPosition) {

        if (state != PieceState.HOME_STRAIGHT) {
            throw new IllegalStateException("Only a piece in the Home Straight can move within it.");
        }

        if (newPosition < 0 || newPosition >= Board.HOME_STRAIGHT_SIZE) {

            throw new IllegalArgumentException("Invalid Home Straight position.");
        }

        position = newPosition;
    }

    public void teleportToStandardPath(int newPosition) {
        if (newPosition < 0 || newPosition >= Board.STANDARD_PATH_SIZE) {
            throw new IllegalArgumentException("Teleport position must be on the standard path.");
        }

        state = PieceState.STANDARD_PATH;
        position = newPosition;
    }

    public void completeEffectRound() {

        if (effect == PieceEffect.NONE) {
            return;
        }

        effectRoundsRemaining--;

        if (effectRoundsRemaining == 0) {
            clearEffect();
        }
    }

    public void clearEffect() {
        effect = PieceEffect.NONE;
        effectRoundsRemaining = 0;
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