package ludo.observer;

import ludo.domain.enums.Colour;
import ludo.domain.model.GameState;
import ludo.domain.model.MysteryCell;
import ludo.domain.model.Player;
import ludo.output.GameOutput;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

class RoundStateObserverTest {

    private Player red;
    private Player green;
    private GameState gameState;
    private MysteryCell mysteryCell;
    private GameOutput gameOutput;
    private RoundStateObserver observer;

    @BeforeEach
    void setUp() {
        red = new Player(Colour.RED);
        green = new Player(Colour.GREEN);

        gameState = new GameState(List.of(red, green));
        mysteryCell = new MysteryCell();
        gameOutput = mock(GameOutput.class);

        observer = new RoundStateObserver(gameState, mysteryCell, gameOutput);
    }

    @Test
    void shouldReportRoundStateWhenRoundCompletes() {
        var order = inOrder(gameOutput);

        observer.onRoundCompleted();

        order.verify(gameOutput).showPlayerPieceCount(red);
        order.verify(gameOutput).showPlayerPieceCount(green);
        order.verify(gameOutput).showPieceLocations(red);
        order.verify(gameOutput).showPieceLocations(green);
        order.verify(gameOutput).showMysteryCellStatus(mysteryCell);
    }

    @Test
    void shouldRejectNullGameState() {
        assertThrows(IllegalArgumentException.class,
                () -> new RoundStateObserver(null, mysteryCell, gameOutput));
    }

    @Test
    void shouldRejectNullMysteryCell() {
        assertThrows(IllegalArgumentException.class,
                () -> new RoundStateObserver(gameState, null, gameOutput));
    }

    @Test
    void shouldRejectNullGameOutput() {
        assertThrows(IllegalArgumentException.class,
                () -> new RoundStateObserver(gameState, mysteryCell, null));
    }
}