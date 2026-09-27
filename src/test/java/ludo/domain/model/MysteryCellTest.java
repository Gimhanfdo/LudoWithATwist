package ludo.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MysteryCellTest {

    private MysteryCell mysteryCell;

    @BeforeEach
    void setUp() {
        mysteryCell = new MysteryCell();
    }

    @Test
    void shouldInitiallyBeInactive() {

        assertFalse(mysteryCell.isActive());
        assertNull(mysteryCell.getPosition());
        assertEquals(0, mysteryCell.getRoundsActive());
    }

    @Test
    void shouldActivateOnStandardPathPosition() {

        mysteryCell.activate(20);

        assertTrue(mysteryCell.isActive());
        assertEquals(20, mysteryCell.getPosition());
        assertEquals(0, mysteryCell.getRoundsActive());
    }

    @Test
    void shouldTrackCompletedRoundsWhileActive() {

        mysteryCell.activate(20);

        mysteryCell.completeRound();
        mysteryCell.completeRound();

        assertEquals(2, mysteryCell.getRoundsActive());
    }

    @Test
    void shouldNotTrackRoundsWhileInactive() {

        mysteryCell.completeRound();

        assertEquals(0, mysteryCell.getRoundsActive());
    }

    @Test
    void shouldDeactivateMysteryCell() {
        
        mysteryCell.activate(20);
        mysteryCell.completeRound();

        mysteryCell.deactivate();

        assertFalse(mysteryCell.isActive());
        assertNull(mysteryCell.getPosition());
        assertEquals(0, mysteryCell.getRoundsActive());
    }

    @Test
    void shouldRejectNegativePosition() {
        assertThrows(IllegalArgumentException.class, () -> mysteryCell.activate(-1));
    }

    @Test
    void shouldRejectPositionOutsideStandardPath() {
        assertThrows(IllegalArgumentException.class,
                () -> mysteryCell.activate(Board.STANDARD_PATH_SIZE));
    }
}