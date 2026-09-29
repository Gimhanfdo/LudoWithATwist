package ludo.observer;

import ludo.service.MysteryCellService;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MysteryCellRoundObserverTest {

    @Test
    void shouldCompleteMysteryCellRoundWhenNotified() {
        MysteryCellService mysteryCellService = mock(MysteryCellService.class);
        RoundObserver observer = new MysteryCellRoundObserver(mysteryCellService);

        observer.onRoundCompleted();

        verify(mysteryCellService).completeRound();
    }

    @Test
    void shouldRejectNullMysteryCellService() {
        assertThrows(IllegalArgumentException.class, () -> new MysteryCellRoundObserver(null));
    }
}