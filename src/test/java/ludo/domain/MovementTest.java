package ludo.domain;

import ludo.domain.enums.Colour;
import ludo.domain.model.Movement;
import ludo.domain.model.Piece;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MovementTest {

    @Test
    void shouldCreateMoveWithCorrectDetails() {

        Piece piece = new Piece(Colour.RED, 1);

        Movement movement = new Movement(
                piece,
                26,
                32,
                6
        );

        assertSame(piece, movement.getPiece());
        assertEquals(26, movement.getFromPosition());
        assertEquals(32, movement.getToPosition());
        assertEquals(6, movement.getDistance());
    }

    @Test
    void shouldRejectNullPiece() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new Movement(null, 0, 6, 6)
        );
    }

    @Test
    void shouldRejectZeroDistance() {

        Piece piece = new Piece(Colour.RED, 1);

        assertThrows(
                IllegalArgumentException.class,
                () -> new Movement(piece, 26, 26, 0)
        );
    }

    @Test
    void shouldRejectNegativeDistance() {

        Piece piece = new Piece(Colour.RED, 1);

        assertThrows(
                IllegalArgumentException.class,
                () -> new Movement(piece, 26, 25, -1)
        );
    }
}