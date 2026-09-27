package ludo.domain.model;

public class MysteryCell {

    private Integer position;
    private int roundsActive;

    public MysteryCell() {
        position = null;
        roundsActive = 0;
    }

    public boolean isActive() {
        return position != null;
    }

    public Integer getPosition() {
        return position;
    }

    public int getRoundsActive() {
        return roundsActive;
    }

    public void activate(int position) {
        validatePosition(position);
        this.position = position;
        this.roundsActive = 0;
    }

    public void completeRound() {
        if (!isActive()) {
            return;
        }

        roundsActive++;
    }

    public void deactivate() {
        position = null;
        roundsActive = 0;
    }

    private void validatePosition(int position) {
        if (position < 0 || position >= Board.STANDARD_PATH_SIZE) {
            throw new IllegalArgumentException("Mystery Cell position must be on the standard path.");
        }
    }
}