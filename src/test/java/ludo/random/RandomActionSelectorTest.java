package ludo.random;

import ludo.domain.enums.ActionType;
import ludo.domain.enums.Colour;
import ludo.domain.model.GameAction;
import ludo.domain.model.Piece;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RandomActionSelectorTest {

    @Test
    void shouldSelectActionUsingRandomIndex() {
        Random random = mock(Random.class);
        RandomActionSelector selector = new RandomActionSelector(random);
        Piece firstPiece = new Piece(Colour.BLUE, 1);
        Piece secondPiece = new Piece(Colour.BLUE, 2);

        GameAction firstAction = new GameAction(ActionType.MOVE_PIECE, List.of(firstPiece), 3);
        GameAction secondAction = new GameAction(ActionType.MOVE_PIECE, List.of(secondPiece), 3);

        when(random.nextInt(2)).thenReturn(1);

        GameAction selected = selector.select(List.of(firstAction, secondAction));

        assertSame(secondAction, selected);
        verify(random).nextInt(2);
    }

    @Test
    void shouldRejectNullActions() {
        RandomActionSelector selector = new RandomActionSelector();

        assertThrows(IllegalArgumentException.class, () -> selector.select(null));
    }

    @Test
    void shouldRejectEmptyActions() {
        RandomActionSelector selector = new RandomActionSelector();

        assertThrows(IllegalArgumentException.class, () -> selector.select(List.of()));
    }

    @Test
    void shouldRejectNullAction() {
        RandomActionSelector selector = new RandomActionSelector();
        List<GameAction> actions = new ArrayList<>();
        actions.add(null);

        assertThrows(IllegalArgumentException.class, () -> selector.select(actions));
    }

    @Test
    void shouldRejectNullRandom() {
        assertThrows(IllegalArgumentException.class, () -> new RandomActionSelector(null));
    }
}