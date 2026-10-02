package ludo.observer;

import ludo.service.MysteryCellService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MysteryCellRoundObserverTest {

    private MysteryCellService mysteryCellService;
    private MysteryCellRoundObserver observer;

    @BeforeEach
    void setUp() {
        mysteryCellService = mock(MysteryCellService.class);
        observer = new MysteryCellRoundObserver(mysteryCellService);
    }

    @Test
    void shouldCompleteMysteryCellRound() {
        observer.onRoundCompleted();

        verify(mysteryCellService).completeRound();
    }

    @Test
    void shouldRejectNullMysteryCellService() {
        assertThrows(IllegalArgumentException.class, () -> new MysteryCellRoundObserver(null));
    }
}