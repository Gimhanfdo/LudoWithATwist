package ludo.observer;

import org.junit.jupiter.api.Test;

import ludo.domain.enums.Colour;
import ludo.domain.model.GameState;
import ludo.domain.model.Piece;
import ludo.domain.model.Player;
import ludo.domain.model.MysteryCellUpdate;
import ludo.service.MysteryCellService;
import ludo.service.PieceEffectService;
import ludo.output.GameOutput;

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
                List.of(firstObserver, secondObserver));

        notifier.notifyRoundCompleted();

        verify(firstObserver).onRoundCompleted();
        verify(secondObserver).onRoundCompleted();
    }

    @Test
    void shouldNotNotifyObserversDuringConstruction() {
        RoundObserver observer = mock(RoundObserver.class);

        new RoundNotifier(List.of(observer));

        verifyNoInteractions(observer);
    }

    @Test
    void shouldDefensivelyCopyObserverList() {
        RoundObserver firstObserver = mock(RoundObserver.class);
        RoundObserver secondObserver = mock(RoundObserver.class);
        List<RoundObserver> observers = new ArrayList<>();

        observers.add(firstObserver);

        RoundNotifier notifier = new RoundNotifier(observers);

        observers.add(secondObserver);

        notifier.notifyRoundCompleted();

        verify(firstObserver).onRoundCompleted();
        verifyNoInteractions(secondObserver);
    }

    @Test
    void shouldNotifyConcreteRoundObservers() {
        MysteryCellService mysteryCellService = mock(MysteryCellService.class);
        PieceEffectService pieceEffectService = mock(PieceEffectService.class);
        GameOutput gameOutput = mock(GameOutput.class);

        when(mysteryCellService.completeRound()).thenReturn(MysteryCellUpdate.none());

        Player red = new Player(Colour.RED);
        GameState gameState = new GameState(List.of(red));

        RoundObserver mysteryObserver = new MysteryCellRoundObserver(mysteryCellService, gameOutput);
        RoundObserver effectObserver = new PieceEffectRoundObserver(gameState, pieceEffectService);
        RoundNotifier notifier = new RoundNotifier(List.of(mysteryObserver, effectObserver));

        notifier.notifyRoundCompleted();

        verify(mysteryCellService).completeRound();

        for (Piece piece : red.getPieces()) {
            verify(pieceEffectService).completeRound(piece);
        }
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