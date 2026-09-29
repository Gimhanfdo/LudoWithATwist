package ludo.observer;

import java.util.ArrayList;
import java.util.List;

public class RoundNotifier {

    private final List<RoundObserver> observers;

    public RoundNotifier(List<RoundObserver> observers) {
        if (observers == null) {
            throw new IllegalArgumentException("Observers cannot be null.");
        }

        validateObservers(observers);

        this.observers = new ArrayList<>(observers);
    }

    public void notifyRoundCompleted() {
        for (RoundObserver observer : observers) {
            observer.onRoundCompleted();
        }
    }

    private void validateObservers(List<RoundObserver> observers) {
        for (RoundObserver observer : observers) {
            if (observer == null) {
                throw new IllegalArgumentException("Observer cannot be null.");
            }
        }
    }
}