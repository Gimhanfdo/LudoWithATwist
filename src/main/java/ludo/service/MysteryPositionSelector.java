package ludo.service;

import java.util.ArrayList;
import java.util.List;

import ludo.domain.model.Board;
import ludo.domain.model.GameState;
import ludo.random.PositionGenerator;

public class MysteryPositionSelector {

    private final GameState gameState;
    private final PositionGenerator positionGenerator;

    public MysteryPositionSelector(GameState gameState, PositionGenerator positionGenerator) {
        if (gameState == null) {
            throw new IllegalArgumentException("Game state cannot be null.");
        }

        if (positionGenerator == null) {
            throw new IllegalArgumentException("Position generator cannot be null.");
        }

        this.gameState = gameState;
        this.positionGenerator = positionGenerator;
    }

    public int selectPosition() {
        return selectPosition(null);
    }

    public int selectPosition(Integer excludedPosition) {
        List<Integer> availablePositions = getAvailablePositions(excludedPosition);
        int selectedIndex = positionGenerator.nextPosition(availablePositions.size());

        return availablePositions.get(selectedIndex);
    }

    private List<Integer> getAvailablePositions(Integer excludedPosition) {
        List<Integer> availablePositions = new ArrayList<>();

        for (int position = 0; position < Board.STANDARD_PATH_SIZE; position++) {
            if (isAvailable(position, excludedPosition)) {
                availablePositions.add(position);
            }
        }

        return availablePositions;
    }

    private boolean isAvailable(int position, Integer excludedPosition) {
        return !isOccupied(position) && !isExcluded(position, excludedPosition);
    }

    private boolean isExcluded(int position, Integer excludedPosition) {
        return excludedPosition != null && position == excludedPosition;
    }

    private boolean isOccupied(int position) {
        return !gameState.getPiecesAtStandardPosition(position).isEmpty();
    }
}