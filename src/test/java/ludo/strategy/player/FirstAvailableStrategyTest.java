package ludo.strategy.player;

import ludo.domain.enums.ActionType;
import ludo.domain.enums.Colour;
import ludo.domain.model.GameAction;
import ludo.domain.model.Piece;
import ludo.domain.model.Player;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FirstAvailableStrategyTest {

    private PlayerStrategy strategy;
    private Player player;

    @BeforeEach
    void setUp() {

        strategy =
                new FirstAvailableStrategy();

        player =
                new Player(
                        Colour.RED
                );
    }

    @Test
    void shouldChooseFirstLegalAction() {

        Piece firstPiece =
                player.getPieces().get(0);

        Piece secondPiece =
                player.getPieces().get(1);

        GameAction firstAction =
                new GameAction(
                        ActionType.MOVE_PIECE,
                        List.of(firstPiece),
                        4
                );

        GameAction secondAction =
                new GameAction(
                        ActionType.MOVE_PIECE,
                        List.of(secondPiece),
                        4
                );

        GameAction selected =
                strategy.chooseAction(
                        player,
                        List.of(
                                firstAction,
                                secondAction
                        )
                );

        assertSame(
                firstAction,
                selected
        );
    }

    @Test
    void shouldReturnNullWhenNoLegalActionsExist() {

        assertNull(
                strategy.chooseAction(
                        player,
                        List.of()
                )
        );
    }

    @Test
    void shouldRejectNullPlayer() {

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        strategy.chooseAction(
                                null,
                                List.of()
                        )
        );
    }

    @Test
    void shouldRejectNullLegalActions() {

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        strategy.chooseAction(
                                player,
                                null
                        )
        );
    }

    @Test
    void shouldRejectNullAction() {

        List<GameAction> actions =
                new ArrayList<>();

        actions.add(null);

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        strategy.chooseAction(
                                player,
                                actions
                        )
        );
    }
}