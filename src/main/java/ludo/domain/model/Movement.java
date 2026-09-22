package ludo.domain.model;

public class Movement {

    private final Piece piece;
    private final int fromPosition;
    private final int toPosition;
    private final int distance;

    public Movement(
            Piece piece,
            int fromPosition,
            int toPosition,
            int distance
    ) {
        if (piece == null) {
            throw new IllegalArgumentException(
                    "Piece cannot be null."
            );
        }

        if (distance <= 0) {
            throw new IllegalArgumentException(
                    "Move distance must be greater than zero."
            );
        }

        this.piece = piece;
        this.fromPosition = fromPosition;
        this.toPosition = toPosition;
        this.distance = distance;
    }

    public Piece getPiece() {
        return piece;
    }

    public int getFromPosition() {
        return fromPosition;
    }

    public int getToPosition() {
        return toPosition;
    }

    public int getDistance() {
        return distance;
    }
}