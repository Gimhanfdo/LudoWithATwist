package ludo.service;

import ludo.domain.model.GameState;
import ludo.domain.model.MysteryCell;
import ludo.domain.model.MysteryCellUpdate;
import ludo.domain.model.Player;
import ludo.domain.enums.Colour;
import ludo.domain.enums.Direction;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MysteryCellServiceTest {

    private MysteryCell mysteryCell;
    private MysteryPositionSelector positionSelector;
    private MysteryCellService mysteryCellService;
    private GameState gameState;
    private Player redPlayer;

    @BeforeEach
    void setUp() {
        redPlayer = new Player(Colour.RED);

        gameState = new GameState(List.of(
                redPlayer,
                new Player(Colour.GREEN),
                new Player(Colour.YELLOW),
                new Player(Colour.BLUE)));

        mysteryCell = new MysteryCell();
        positionSelector = mock(MysteryPositionSelector.class);
        mysteryCellService = new MysteryCellService(mysteryCell, positionSelector, gameState);
    }

    private void placeRedPieceOnStandardPath() {
        redPlayer.getPieces().get(0).enterBoard(26, Direction.CLOCKWISE);
    }

    @Test
    void shouldInitiallyHaveZeroCompletedRounds() {
        assertEquals(0, mysteryCellService.getCompletedRounds());
        assertFalse(mysteryCell.isActive());
    }

    @Test
    void shouldNotActivateMysteryCellAfterFirstRound() {

        placeRedPieceOnStandardPath();
        mysteryCellService.completeRound();

        assertEquals(1, mysteryCellService.getCompletedRounds());
        assertFalse(mysteryCell.isActive());
        verifyNoInteractions(positionSelector);
    }

    @Test
    void shouldActivateMysteryCellAfterSecondRound() {

        placeRedPieceOnStandardPath();

        when(positionSelector.selectPosition()).thenReturn(25);

        mysteryCellService.completeRound();

        assertFalse(mysteryCell.isActive());

        mysteryCellService.completeRound();

        assertTrue(mysteryCell.isActive());
        assertEquals(25, mysteryCell.getPosition());
        assertEquals(2, mysteryCellService.getCompletedRounds());
    }

    @Test
    void shouldSelectPositionOnlyWhenMysteryCellIsActivated() {

        placeRedPieceOnStandardPath();

        when(positionSelector.selectPosition()).thenReturn(30);

        mysteryCellService.completeRound();

        verifyNoInteractions(positionSelector);

        mysteryCellService.completeRound();

        verify(positionSelector, times(1)).selectPosition();
        assertEquals(30, mysteryCell.getPosition());
    }

    @Test
    void shouldNotReactivateMysteryCellAfterThirdRound() {

        placeRedPieceOnStandardPath();

        when(positionSelector.selectPosition()).thenReturn(20);

        mysteryCellService.completeRound();
        mysteryCellService.completeRound();

        assertEquals(20, mysteryCell.getPosition());

        mysteryCellService.completeRound();

        assertEquals(20, mysteryCell.getPosition());
        verify(positionSelector, times(1)).selectPosition();
    }

    @Test
    void shouldStartWithZeroActiveRoundsWhenFirstActivated() {

        placeRedPieceOnStandardPath();

        when(positionSelector.selectPosition()).thenReturn(20);

        mysteryCellService.completeRound();
        mysteryCellService.completeRound();

        assertTrue(mysteryCell.isActive());
        assertEquals(20, mysteryCell.getPosition());
        assertEquals(0, mysteryCell.getRoundsActive());
    }

    @Test
    void shouldTrackRoundsAfterMysteryCellActivation() {

        placeRedPieceOnStandardPath();

        when(positionSelector.selectPosition()).thenReturn(20);

        mysteryCellService.completeRound();
        mysteryCellService.completeRound();
        mysteryCellService.completeRound();

        assertEquals(1, mysteryCell.getRoundsActive());

        mysteryCellService.completeRound();

        assertEquals(2, mysteryCell.getRoundsActive());
    }

    @Test
    void shouldNotRelocateBeforeFourActiveRounds() {

        placeRedPieceOnStandardPath();

        when(positionSelector.selectPosition()).thenReturn(20);
        when(positionSelector.selectPosition(20)).thenReturn(30);

        // Round 1
        mysteryCellService.completeRound();

        // Round 2
        mysteryCellService.completeRound();

        // Round 3
        mysteryCellService.completeRound();

        // Round 4
        mysteryCellService.completeRound();

        // Round 5
        mysteryCellService.completeRound();

        assertEquals(20, mysteryCell.getPosition());
        assertEquals(3, mysteryCell.getRoundsActive());
        verify(positionSelector, times(1)).selectPosition();
    }

    @Test
    void shouldRelocateAfterFourActiveRounds() {

        placeRedPieceOnStandardPath();

        when(positionSelector.selectPosition()).thenReturn(20);
        when(positionSelector.selectPosition(20)).thenReturn(30);

        // Round 1
        mysteryCellService.completeRound();

        // Round 2
        mysteryCellService.completeRound();

        // Rounds 3, 4, 5 and 6
        mysteryCellService.completeRound();
        mysteryCellService.completeRound();
        mysteryCellService.completeRound();
        mysteryCellService.completeRound();

        assertEquals(30, mysteryCell.getPosition());
        assertEquals(0, mysteryCell.getRoundsActive());

        verify(positionSelector, times(1)).selectPosition();
        verify(positionSelector, times(1)).selectPosition(20);
    }

    @Test
    void shouldRestartActiveRoundCountAfterRelocation() {

        placeRedPieceOnStandardPath();

        when(positionSelector.selectPosition()).thenReturn(20);
        when(positionSelector.selectPosition(20)).thenReturn(30);

        mysteryCellService.completeRound();
        mysteryCellService.completeRound();

        mysteryCellService.completeRound();
        mysteryCellService.completeRound();
        mysteryCellService.completeRound();
        mysteryCellService.completeRound();

        assertEquals(30, mysteryCell.getPosition());
        assertEquals(0, mysteryCell.getRoundsActive());

        // First round at new location
        mysteryCellService.completeRound();

        assertEquals(30, mysteryCell.getPosition());
        assertEquals(1, mysteryCell.getRoundsActive());
    }

    @Test
    void shouldRelocateEveryFourActiveRounds() {

        placeRedPieceOnStandardPath();

        when(positionSelector.selectPosition()).thenReturn(10);
        when(positionSelector.selectPosition(10)).thenReturn(20);
        when(positionSelector.selectPosition(20)).thenReturn(30);

        // Initial two rounds
        mysteryCellService.completeRound();
        mysteryCellService.completeRound();

        assertEquals(10, mysteryCell.getPosition());

        // First four active rounds
        for (int i = 0; i < 4; i++) {
            mysteryCellService.completeRound();
        }

        assertEquals(20, mysteryCell.getPosition());

        // Second four active rounds
        for (int i = 0; i < 4; i++) {
            mysteryCellService.completeRound();
        }

        assertEquals(30, mysteryCell.getPosition());
        verify(positionSelector, times(1)).selectPosition();
        verify(positionSelector, times(1)).selectPosition(10);
        verify(positionSelector, times(1)).selectPosition(20);
    }

    @Test
    void shouldReturnNoChangeWhenNoPieceIsOnStandardPath() {
        MysteryCellUpdate update = mysteryCellService.completeRound();

        assertEquals(MysteryCellUpdate.Type.NONE, update.getType());
        assertFalse(update.hasChanged());
        assertNull(update.getPosition());
    }

    @Test
    void shouldReturnNoChangeBeforeMysteryCellSpawns() {
        placeRedPieceOnStandardPath();

        MysteryCellUpdate update = mysteryCellService.completeRound();

        assertEquals(MysteryCellUpdate.Type.NONE, update.getType());
        assertFalse(update.hasChanged());
    }

    @Test
    void shouldReturnSpawnedUpdateWhenMysteryCellActivates() {
        placeRedPieceOnStandardPath();
        when(positionSelector.selectPosition()).thenReturn(20);

        mysteryCellService.completeRound();

        MysteryCellUpdate update = mysteryCellService.completeRound();

        assertEquals(MysteryCellUpdate.Type.SPAWNED, update.getType());
        assertEquals(20, update.getPosition());
        assertTrue(update.hasChanged());
    }

    @Test
    void shouldReturnRelocatedUpdateAfterFourActiveRounds() {
        placeRedPieceOnStandardPath();

        when(positionSelector.selectPosition()).thenReturn(20);
        when(positionSelector.selectPosition(20)).thenReturn(30);

        mysteryCellService.completeRound();
        mysteryCellService.completeRound();

        mysteryCellService.completeRound();
        mysteryCellService.completeRound();
        mysteryCellService.completeRound();

        MysteryCellUpdate update = mysteryCellService.completeRound();

        assertEquals(MysteryCellUpdate.Type.RELOCATED, update.getType());
        assertEquals(30, update.getPosition());

        assertTrue(update.hasChanged());
    }

    @Test
    void shouldRejectNullMysteryCell() {
        assertThrows(IllegalArgumentException.class,
                () -> new MysteryCellService(null, positionSelector, gameState));
    }

    @Test
    void shouldRejectNullPositionSelector() {
        assertThrows(IllegalArgumentException.class,
                () -> new MysteryCellService(mysteryCell, null, gameState));
    }

    @Test
    void shouldRejectNullGameState() {
        assertThrows(IllegalArgumentException.class,
                () -> new MysteryCellService(mysteryCell, positionSelector, null));
    }

    @Test
    void shouldExcludeCurrentPositionWhenRelocating() {

        placeRedPieceOnStandardPath();

        when(positionSelector.selectPosition()).thenReturn(20);
        when(positionSelector.selectPosition(20)).thenReturn(30);

        mysteryCellService.completeRound();
        mysteryCellService.completeRound();

        for (int i = 0; i < 4; i++) {
            mysteryCellService.completeRound();
        }

        assertEquals(30, mysteryCell.getPosition());
        verify(positionSelector).selectPosition(20);
    }

    @Test
    void shouldNotCountRoundsBeforePieceEntersStandardPath() {
        mysteryCellService.completeRound();
        mysteryCellService.completeRound();

        assertEquals(0, mysteryCellService.getCompletedRounds());
        assertFalse(mysteryCell.isActive());
        verifyNoInteractions(positionSelector);
    }

    @Test
    void shouldBeginCountingAfterPieceEntersStandardPath() {
        when(positionSelector.selectPosition()).thenReturn(20);

        // No piece on standard path
        mysteryCellService.completeRound();

        assertEquals(0, mysteryCellService.getCompletedRounds());

        placeRedPieceOnStandardPath();

        // First relevant round
        mysteryCellService.completeRound();

        assertEquals(1, mysteryCellService.getCompletedRounds());
        assertFalse(mysteryCell.isActive());

        // Second relevant round
        mysteryCellService.completeRound();

        assertEquals(2, mysteryCellService.getCompletedRounds());
        assertTrue(mysteryCell.isActive());
        assertEquals(20, mysteryCell.getPosition());
    }
}