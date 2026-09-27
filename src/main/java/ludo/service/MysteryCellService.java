package ludo.service;

import ludo.domain.model.MysteryCell;

public class MysteryCellService {

    private static final int INITIAL_SPAWN_ROUND = 2;
    private static final int ACTIVE_ROUNDS_BEFORE_RELOCATION = 4;

    private final MysteryCell mysteryCell;
    private final MysteryPositionSelector positionSelector;

    private int completedRounds;

    public MysteryCellService(MysteryCell mysteryCell, MysteryPositionSelector positionSelector) {
        if (mysteryCell == null) {
            throw new IllegalArgumentException("Mystery Cell cannot be null.");
        }

        if (positionSelector == null) {
            throw new IllegalArgumentException("Position selector cannot be null.");
        }

        this.mysteryCell = mysteryCell;
        this.positionSelector = positionSelector;
        this.completedRounds = 0;
    }

    public void completeRound() {
        completedRounds++;

        if (shouldActivateMysteryCell()) {
            activateMysteryCell();
            return;
        }

        if (mysteryCell.isActive()) {
            updateActiveMysteryCell();
        }
    }

    private void updateActiveMysteryCell() {

        mysteryCell.completeRound();

        if (shouldRelocateMysteryCell()) {
            relocateMysteryCell();
        }
    }

    private boolean shouldRelocateMysteryCell() {
        return mysteryCell.getRoundsActive() == ACTIVE_ROUNDS_BEFORE_RELOCATION;
    }

    private void relocateMysteryCell() {
        int currentPosition = mysteryCell.getPosition();
        int newPosition = positionSelector.selectPosition(currentPosition);

        mysteryCell.activate(newPosition);
    }

    public int getCompletedRounds() {
        return completedRounds;
    }

    private boolean shouldActivateMysteryCell() {
        return completedRounds == INITIAL_SPAWN_ROUND && !mysteryCell.isActive();
    }

    private void activateMysteryCell() {
        int position = positionSelector.selectPosition();
        mysteryCell.activate(position);
    }
}