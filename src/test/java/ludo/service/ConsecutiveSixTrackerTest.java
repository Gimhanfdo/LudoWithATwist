package ludo.service;

import ludo.domain.enums.Colour;
import ludo.domain.model.Player;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConsecutiveSixTrackerTest {

    private ConsecutiveSixTracker tracker;
    private Player player;

    @BeforeEach
    void setUp() {
        tracker = new ConsecutiveSixTracker();
        player = new Player(Colour.RED);
    }

    @Test
    void shouldRecordFirstSix() {
        boolean thirdSix = tracker.recordRoll(player, 6);

        assertFalse(thirdSix);
        assertEquals(1, tracker.getConsecutiveSixCount(player));
    }

    @Test
    void shouldRecordSecondConsecutiveSix() {
        tracker.recordRoll(player, 6);

        boolean thirdSix = tracker.recordRoll(player, 6);

        assertFalse(thirdSix);
        assertEquals(2, tracker.getConsecutiveSixCount(player));
    }

    @Test
    void shouldIdentifyThirdConsecutiveSix() {
        tracker.recordRoll(player, 6);
        tracker.recordRoll(player, 6);

        boolean thirdSix = tracker.recordRoll(player, 6);

        assertTrue(thirdSix);
        assertEquals(0, tracker.getConsecutiveSixCount(player));
    }

    @Test
    void shouldResetSequenceWhenNonSixIsRolled() {
        tracker.recordRoll(player, 6);
        tracker.recordRoll(player, 6);
        tracker.recordRoll(player, 4);

        assertEquals(0, tracker.getConsecutiveSixCount(player));
    }

    @Test
    void shouldTrackPlayersIndependently() {
        Player bluePlayer = new Player(Colour.BLUE);

        tracker.recordRoll(player, 6);
        tracker.recordRoll(player, 6);
        tracker.recordRoll(bluePlayer, 6);

        assertEquals(2, tracker.getConsecutiveSixCount(player));
        assertEquals(1, tracker.getConsecutiveSixCount(bluePlayer));
    }

    @Test
    void shouldRejectNullPlayer() {
        assertThrows(IllegalArgumentException.class, () -> tracker.recordRoll(null, 6));
    }

    @Test
    void shouldRejectInvalidRoll() {
        assertThrows(IllegalArgumentException.class, () -> tracker.recordRoll(player, 7));
    }
}