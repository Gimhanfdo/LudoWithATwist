package ludo.observer;

import ludo.service.MysteryCellService;

public class MysteryCellRoundObserver implements RoundObserver {

    private final MysteryCellService mysteryCellService;

    public MysteryCellRoundObserver(MysteryCellService mysteryCellService) {
        if (mysteryCellService == null) {
            throw new IllegalArgumentException("Mystery cell service cannot be null.");
        }

        this.mysteryCellService = mysteryCellService;
    }

    @Override
    public void onRoundCompleted() {
        mysteryCellService.completeRound();
    }
}