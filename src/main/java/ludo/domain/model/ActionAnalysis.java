package ludo.domain.model;

public class ActionAnalysis {

    private final GameAction action;
    private final boolean capturesOpponent;
    private final boolean createsBlock;

    public ActionAnalysis(GameAction action, boolean capturesOpponent, boolean createsBlock) {
        if (action == null) {
            throw new IllegalArgumentException("Game action cannot be null.");
        }

        this.action = action;
        this.capturesOpponent = capturesOpponent;
        this.createsBlock = createsBlock;
    }

    public GameAction getAction() {
        return action;
    }

    public boolean capturesOpponent() {
        return capturesOpponent;
    }

    public boolean createsBlock() {
        return createsBlock;
    }
}