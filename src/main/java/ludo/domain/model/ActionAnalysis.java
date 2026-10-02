package ludo.domain.model;

public class ActionAnalysis {

    private final GameAction action;
    private final Piece capturedPiece;
    private final boolean createsBlock;
    private final int distanceToHome;

    public ActionAnalysis(GameAction action, Piece capturedPiece, boolean createsBlock, int distanceToHome) {
        if (action == null) {
            throw new IllegalArgumentException("Game action cannot be null.");
        }

        if (distanceToHome < 0) {
            throw new IllegalArgumentException("Distance to home cannot be negative.");
        }

        this.action = action;
        this.capturedPiece = capturedPiece;
        this.createsBlock = createsBlock;
        this.distanceToHome = distanceToHome;
    }

    public GameAction getAction() {
        return action;
    }

    public boolean capturesOpponent() {
        return capturedPiece != null;
    }

    public Piece getCapturedPiece() {
        return capturedPiece;
    }

    public boolean createsBlock() {
        return createsBlock;
    }

    public int getDistanceToHome() {
        return distanceToHome;
    }
}