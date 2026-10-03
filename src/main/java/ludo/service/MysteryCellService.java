package ludo.service;

import ludo.domain.model.GameState;
import ludo.domain.model.MysteryCell;
import ludo.domain.model.MysteryCellUpdate;
import ludo.domain.enums.PieceState;

public class MysteryCellService {

    private static final int INITIAL_SPAWN_ROUND = 2;
    private static final int ACTIVE_ROUNDS_BEFORE_RELOCATION = 4;

    private final MysteryCell mysteryCell;
    private final MysteryPositionSelector positionSelector;
    private final GameState gameState;

    private int completedRounds;

    public MysteryCellService(MysteryCell mysteryCell, MysteryPositionSelector positionSelector, GameState gameState) {
        if (mysteryCell == null) {
            throw new IllegalArgumentException("Mystery Cell cannot be null.");
        }

        if (positionSelector == null) {
            throw new IllegalArgumentException("Position selector cannot be null.");
        }

        if (gameState == null) {
            throw new IllegalArgumentException("Game state cannot be null.");
        }

        this.mysteryCell = mysteryCell;
        this.positionSelector = positionSelector;
        this.gameState = gameState;
        this.completedRounds = 0;
    }

    public MysteryCellUpdate completeRound() {
        if (!hasPieceOnStandardPath() && !mysteryCell.isActive()) {
            return MysteryCellUpdate.none();
        }

        completedRounds++;

        if (shouldActivateMysteryCell()) {
            return activateMysteryCell();
        }

        if (mysteryCell.isActive()) {
            return updateActiveMysteryCell();
        }

        return MysteryCellUpdate.none();
    }

    private boolean hasPieceOnStandardPath() {
        return gameState.getPlayers().stream()
                .flatMap(player -> player.getPieces().stream())
                .anyMatch(piece -> piece.getState() == PieceState.STANDARD_PATH);
    }

    private MysteryCellUpdate updateActiveMysteryCell() {
        mysteryCell.completeRound();

        if (shouldRelocateMysteryCell()) {
            return relocateMysteryCell();
        }

        return MysteryCellUpdate.none();
    }

    private boolean shouldRelocateMysteryCell() {
        return mysteryCell.getRoundsActive() == ACTIVE_ROUNDS_BEFORE_RELOCATION;
    }

    private MysteryCellUpdate relocateMysteryCell() {
        int currentPosition = mysteryCell.getPosition();
        int newPosition = positionSelector.selectPosition(currentPosition);

        mysteryCell.activate(newPosition);

        return MysteryCellUpdate.relocated(newPosition);
    }

    public int getCompletedRounds() {
        return completedRounds;
    }

    private boolean shouldActivateMysteryCell() {
        return completedRounds == INITIAL_SPAWN_ROUND && !mysteryCell.isActive();
    }

    private MysteryCellUpdate activateMysteryCell() {
        int position = positionSelector.selectPosition();
        mysteryCell.activate(position);

        return MysteryCellUpdate.spawned(position);
    }
}