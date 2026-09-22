package ludo.domain;

import ludo.domain.enums.Colour;
import ludo.domain.model.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void shouldCreatePlayerWithFourPieces() {

        Player player = new Player(Colour.GREEN);

        assertEquals(
                4,
                player.getPieces().size()
        );
    }

    @Test
    void shouldCreatePiecesWithPlayerColour() {

        Player player = new Player(Colour.BLUE);

        assertTrue(
                player.getPieces()
                        .stream()
                        .allMatch(piece ->
                                piece.getColour() == Colour.BLUE)
        );
    }

    @Test
    void shouldCreatePiecesNumberedOneToFour() {

        Player player = new Player(Colour.YELLOW);

        assertEquals(
                "Y1",
                player.getPieces().get(0).getName()
        );

        assertEquals(
                "Y2",
                player.getPieces().get(1).getName()
        );

        assertEquals(
                "Y3",
                player.getPieces().get(2).getName()
        );

        assertEquals(
                "Y4",
                player.getPieces().get(3).getName()
        );
    }

    @Test
    void shouldRejectNullPlayerColour() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new Player(null)
        );
    }
}