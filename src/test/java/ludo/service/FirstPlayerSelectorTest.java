package ludo.service;

import ludo.domain.enums.Colour;
import ludo.domain.model.Player;
import ludo.output.GameOutput;
import ludo.random.Dice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class FirstPlayerSelectorTest {

    private Dice dice;
    private GameOutput gameOutput;
    private FirstPlayerSelector selector;
    private Player red;
    private Player green;
    private Player yellow;
    private Player blue;

    @BeforeEach
    void setUp() {
        dice = mock(Dice.class);
        gameOutput = mock(GameOutput.class);
        selector = new FirstPlayerSelector(dice, gameOutput);
        red = new Player(Colour.RED);
        green = new Player(Colour.GREEN);
        yellow = new Player(Colour.YELLOW);
        blue = new Player(Colour.BLUE);
    }

    @Test
    void shouldStartRoundOrderFromHighestRoller() {
        when(dice.roll()).thenReturn(2, 6, 3, 1);

        List<Player> order = selector.determineOrder(List.of(red, green, yellow, blue));

        assertEquals(List.of(green, yellow, blue, red), order);
        verify(gameOutput).showInitialRoll(red, 2);
        verify(gameOutput).showInitialRoll(green, 6);
        verify(gameOutput).showInitialRoll(yellow, 3);
        verify(gameOutput).showInitialRoll(blue, 1);
        verify(gameOutput).showFirstPlayer(green);
        verify(gameOutput).showRoundOrder(order);
    }

    @Test
    void shouldWrapRoundOrderAfterLastPlayer() {
        when(dice.roll()).thenReturn(2, 3, 4, 6);

        List<Player> order = selector.determineOrder(List.of(red, green, yellow, blue));

        assertEquals(List.of(blue, red, green, yellow), order);
    }

    @Test
    void shouldRerollOnlyTiedHighestPlayers() {
        when(dice.roll()).thenReturn(6, 2, 6, 1, 3, 5);

        List<Player> order = selector.determineOrder(List.of(red, green, yellow, blue));

        assertEquals(List.of(yellow, blue, red, green), order);
        verify(dice, times(6)).roll();
        verify(gameOutput).showInitialRoll(red, 6);
        verify(gameOutput).showInitialRoll(green, 2);
        verify(gameOutput).showInitialRoll(yellow, 6);
        verify(gameOutput).showInitialRoll(blue, 1);
        verify(gameOutput).showInitialRoll(red, 3);
        verify(gameOutput).showInitialRoll(yellow, 5);
        verify(gameOutput).showFirstPlayer(yellow);
    }

    @Test
    void shouldRejectNullPlayers() {
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> selector.determineOrder(null));
    }

    @Test
    void shouldRejectEmptyPlayers() {
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> selector.determineOrder(List.of()));
    }
}