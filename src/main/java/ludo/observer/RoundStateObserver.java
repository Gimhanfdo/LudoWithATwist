package ludo.observer;

import ludo.domain.model.GameState;
import ludo.domain.model.MysteryCell;
import ludo.domain.model.Player;
import ludo.output.GameOutput;

public class RoundStateObserver implements RoundObserver {

    private final GameState gameState;
    private final MysteryCell mysteryCell;
    private final GameOutput gameOutput;

    public RoundStateObserver(GameState gameState, MysteryCell mysteryCell, GameOutput gameOutput) {
        if (gameState == null) {
            throw new IllegalArgumentException("Game state cannot be null.");
        }

        if (mysteryCell == null) {
            throw new IllegalArgumentException("Mystery Cell cannot be null.");
        }

        if (gameOutput == null) {
            throw new IllegalArgumentException("Game output cannot be null.");
        }

        this.gameState = gameState;
        this.mysteryCell = mysteryCell;
        this.gameOutput = gameOutput;
    }

    @Override
    public void onRoundCompleted() {
        showPlayerPieceCounts();
        showPlayerPieceLocations();
        gameOutput.showMysteryCellStatus(mysteryCell);
    }

    private void showPlayerPieceCounts() {
        for (Player player : gameState.getPlayers()) {
            gameOutput.showPlayerPieceCount(player);
        }
    }

    private void showPlayerPieceLocations() {
        for (Player player : gameState.getPlayers()) {
            gameOutput.showPieceLocations(player);
        }
    }
}