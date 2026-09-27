package ludo.service;

import ludo.domain.model.MysteryCell;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MysteryCellServiceTest {

    private MysteryCell mysteryCell;
    private MysteryPositionSelector positionSelector;
    private MysteryCellService mysteryCellService;

    @BeforeEach
    void setUp() {
        mysteryCell = new MysteryCell();
        positionSelector = mock(MysteryPositionSelector.class);
        mysteryCellService = new MysteryCellService(mysteryCell, positionSelector);
    }

    @Test
    void shouldInitiallyHaveZeroCompletedRounds() {
        assertEquals(0, mysteryCellService.getCompletedRounds());
        assertFalse(mysteryCell.isActive());
    }

    @Test
    void shouldNotActivateMysteryCellAfterFirstRound() {
        mysteryCellService.completeRound();

        assertEquals(1, mysteryCellService.getCompletedRounds());
        assertFalse(mysteryCell.isActive());
        verifyNoInteractions(positionSelector);
    }

    @Test
    void shouldActivateMysteryCellAfterSecondRound() {
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
        when(positionSelector.selectPosition()).thenReturn(30);

        mysteryCellService.completeRound();

        verifyNoInteractions(positionSelector);

        mysteryCellService.completeRound();

        verify(positionSelector, times(1)).selectPosition();
        assertEquals(30, mysteryCell.getPosition());
    }

    @Test
    void shouldNotReactivateMysteryCellAfterThirdRound() {
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
        when(positionSelector.selectPosition()).thenReturn(20);

        mysteryCellService.completeRound();
        mysteryCellService.completeRound();

        assertTrue(mysteryCell.isActive());
        assertEquals(20, mysteryCell.getPosition());
        assertEquals(0, mysteryCell.getRoundsActive());
    }

    @Test
    void shouldTrackRoundsAfterMysteryCellActivation() {
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
    void shouldRejectNullMysteryCell() {
        assertThrows(IllegalArgumentException.class,
                () -> new MysteryCellService(null, positionSelector));
    }

    @Test
    void shouldRejectNullPositionSelector() {
        assertThrows(IllegalArgumentException.class,
                () -> new MysteryCellService(mysteryCell, null));
    }

    @Test
    void shouldExcludeCurrentPositionWhenRelocating() {
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
}