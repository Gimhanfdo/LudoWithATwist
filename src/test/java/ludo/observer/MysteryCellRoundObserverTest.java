package ludo.observer;

import ludo.domain.model.MysteryCellUpdate;
import ludo.output.GameOutput;
import ludo.service.MysteryCellService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MysteryCellRoundObserverTest {

    private MysteryCellService mysteryCellService;
    private GameOutput gameOutput;
    private MysteryCellRoundObserver observer;

    @BeforeEach
    void setUp() {
        mysteryCellService = mock(MysteryCellService.class);
        gameOutput = mock(GameOutput.class);
        observer = new MysteryCellRoundObserver(mysteryCellService, gameOutput);
    }

    @Test
    void shouldNotReportWhenMysteryCellDoesNotChange() {
        when(mysteryCellService.completeRound()).thenReturn(MysteryCellUpdate.none());

        observer.onRoundCompleted();

        verify(mysteryCellService).completeRound();
        verifyNoInteractions(gameOutput);
    }

    @Test
    void shouldReportMysteryCellSpawn() {
        when(mysteryCellService.completeRound()).thenReturn(MysteryCellUpdate.spawned(20));

        observer.onRoundCompleted();

        verify(mysteryCellService).completeRound();
        verify(gameOutput).showMysteryCellSpawned(20);
    }

    @Test
    void shouldReportMysteryCellRelocation() {
        when(mysteryCellService.completeRound()).thenReturn(MysteryCellUpdate.relocated(30));

        observer.onRoundCompleted();

        verify(mysteryCellService).completeRound();
        verify(gameOutput).showMysteryCellSpawned(30);
    }

    @Test
    void shouldRejectNullMysteryCellService() {
        assertThrows(IllegalArgumentException.class,
                () -> new MysteryCellRoundObserver(null, gameOutput));
    }

    @Test
    void shouldRejectNullGameOutput() {
        assertThrows(IllegalArgumentException.class,
                () -> new MysteryCellRoundObserver(mysteryCellService, null));
    }
}