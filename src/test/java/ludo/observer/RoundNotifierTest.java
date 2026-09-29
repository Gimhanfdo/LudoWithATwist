package ludo.observer;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoundNotifierTest {

    @Test
    void shouldNotifyAllObserversWhenRoundCompletes() {
        RoundObserver firstObserver = mock(RoundObserver.class);
        RoundObserver secondObserver = mock(RoundObserver.class);

        RoundNotifier notifier = new RoundNotifier(
                List.of(firstObserver, secondObserver)
        );

        notifier.notifyRoundCompleted();

        verify(firstObserver).onRoundCompleted();
        verify(secondObserver).onRoundCompleted();
    }

    @Test
    void shouldRejectNullObserverList() {
        assertThrows(IllegalArgumentException.class, () -> new RoundNotifier(null));
    }

    @Test
    void shouldRejectNullObserver() {
        List<RoundObserver> observers = new ArrayList<>();

        observers.add(mock(RoundObserver.class));
        observers.add(null);

        assertThrows(IllegalArgumentException.class, () -> new RoundNotifier(observers));
    }
}