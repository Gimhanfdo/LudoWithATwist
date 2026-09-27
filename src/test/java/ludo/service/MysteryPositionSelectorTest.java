package ludo.service;

import ludo.domain.enums.Colour;
import ludo.domain.enums.Direction;
import ludo.domain.model.GameState;
import ludo.domain.model.Player;
import ludo.domain.model.Board;
import ludo.random.PositionGenerator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MysteryPositionSelectorTest {

    private GameState gameState;
    private PositionGenerator positionGenerator;
    private MysteryPositionSelector selector;

    @BeforeEach
    void setUp() {
        gameState = new GameState(List.of(
                new Player(Colour.RED),
                new Player(Colour.GREEN),
                new Player(Colour.YELLOW),
                new Player(Colour.BLUE)));

        positionGenerator = mock(PositionGenerator.class);
        selector = new MysteryPositionSelector(gameState, positionGenerator);
    }

    @Test
    void shouldSelectRandomAvailablePosition() {
        when(positionGenerator.nextPosition(Board.STANDARD_PATH_SIZE)).thenReturn(20);

        int position = selector.selectPosition();

        assertEquals(20, position);
    }

    @Test
    void shouldExcludeOccupiedPositionFromSelection() {
        Player redPlayer = gameState.getPlayers().stream()
                .filter(player -> player.getColour() == Colour.RED)
                .findFirst()
                .orElseThrow();

        redPlayer.getPieces().get(0).enterBoard(26, Direction.CLOCKWISE);
        redPlayer.getPieces().get(0).moveTo(20);

        when(positionGenerator.nextPosition(51)).thenReturn(24);

        int position = selector.selectPosition();

        assertEquals(25, position);
        assertNotEquals(20, position);
    }

    @Test
    void shouldRejectPositionOccupiedByBlock() {
        Player redPlayer = gameState.getPlayers().stream()
                .filter(player -> player.getColour() == Colour.RED)
                .findFirst()
                .orElseThrow();

        redPlayer.getPieces().get(0).enterBoard(26, Direction.CLOCKWISE);
        redPlayer.getPieces().get(1).enterBoard(26, Direction.COUNTERCLOCKWISE);

        redPlayer.getPieces().get(0).moveTo(30);
        redPlayer.getPieces().get(1).moveTo(30);

        when(positionGenerator.nextPosition(51)).thenReturn(30);

        int position = selector.selectPosition();

        assertEquals(31, position);
        assertNotEquals(30, position);
    }

    @Test
    void shouldIgnorePiecesInBaseWhenSelectingPosition() {
        when(positionGenerator.nextPosition(Board.STANDARD_PATH_SIZE)).thenReturn(26);

        int position = selector.selectPosition();

        assertEquals(26, position);
    }

    @Test
    void shouldRejectNullGameState() {
        assertThrows(IllegalArgumentException.class,
                () -> new MysteryPositionSelector(null, positionGenerator));
    }

    @Test
    void shouldRejectNullPositionGenerator() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new MysteryPositionSelector(
                        gameState,
                        null));
    }
}