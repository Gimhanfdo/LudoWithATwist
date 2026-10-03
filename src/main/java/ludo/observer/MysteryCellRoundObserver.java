package ludo.observer;

import ludo.domain.model.MysteryCellUpdate;
import ludo.output.GameOutput;
import ludo.service.MysteryCellService;

public class MysteryCellRoundObserver implements RoundObserver {

    private final MysteryCellService mysteryCellService;
    private final GameOutput gameOutput;

    public MysteryCellRoundObserver(MysteryCellService mysteryCellService, GameOutput gameOutput) {
        if (mysteryCellService == null) {
            throw new IllegalArgumentException("Mystery cell service cannot be null.");
        }

        if (gameOutput == null) {
            throw new IllegalArgumentException("Game output cannot be null.");
        }

        this.mysteryCellService = mysteryCellService;
        this.gameOutput = gameOutput;
    }

    @Override
    public void onRoundCompleted() {
        MysteryCellUpdate update = mysteryCellService.completeRound();

        if (!update.hasChanged()) {
            return;
        }

        gameOutput.showMysteryCellSpawned(update.getPosition());
    }
}