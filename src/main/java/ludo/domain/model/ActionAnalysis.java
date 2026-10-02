package ludo.domain.model;

public class ActionAnalysis {

    private final GameAction action;
    private final Piece capturedPiece;
    private final boolean createsBlock;
    private final int distanceToHome;
    private final boolean progressKnown;
    private final boolean landsOnMystery;

    public ActionAnalysis(GameAction action, Piece capturedPiece, boolean createsBlock, int distanceToHome,
            boolean progressKnown, boolean landsOnMystery) {
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
        this.progressKnown = progressKnown;
        this.landsOnMystery = landsOnMystery;
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

    public boolean isProgressKnown() {
        return progressKnown;
    }

    public boolean landsOnMystery() {
        return landsOnMystery;
    }
}