package ludo.domain.model;

import ludo.domain.enums.ActionType;
import ludo.domain.enums.Colour;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameActionTest {

    @Test
    void shouldCreateAction() {
        Piece piece = new Piece(Colour.RED, 1);
        GameAction action = new GameAction(ActionType.MOVE_PIECE, List.of(piece), 4);

        assertEquals(ActionType.MOVE_PIECE, action.getType());
        assertEquals(List.of(piece), action.getPieces());
        assertEquals(4, action.getRoll());
    }

    @Test
    void shouldSupportMultiplePieces() {
        Piece firstPiece = new Piece(Colour.RED, 1);
        Piece secondPiece = new Piece(Colour.RED, 2);
        GameAction action = new GameAction(ActionType.MOVE_BLOCK, List.of(firstPiece, secondPiece), 6);

        assertEquals(2, action.getPieces().size());
    }

    @Test
    void shouldDefensivelyCopyPieces() {
        Piece piece = new Piece(Colour.RED, 1);
        List<Piece> pieces = new ArrayList<>();

        pieces.add(piece);

        GameAction action = new GameAction(ActionType.MOVE_PIECE, pieces, 4);

        pieces.clear();

        assertEquals(1, action.getPieces().size());
    }

    @Test
    void shouldReturnUnmodifiablePieces() {
        Piece piece = new Piece(Colour.RED, 1);
        GameAction action = new GameAction(ActionType.MOVE_PIECE, List.of(piece), 4);

        assertThrows(UnsupportedOperationException.class,
                () -> action.getPieces().add(new Piece(Colour.RED, 2)));
    }

    @Test
    void shouldRejectNullType() {
        Piece piece = new Piece(Colour.RED, 1);

        assertThrows(IllegalArgumentException.class,
                () -> new GameAction(null, List.of(piece), 4));
    }

    @Test
    void shouldRejectEmptyPieces() {
        assertThrows(IllegalArgumentException.class,
                () -> new GameAction(ActionType.MOVE_PIECE, List.of(), 4));
    }

    @Test
    void shouldRejectNullPiece() {
        List<Piece> pieces = new ArrayList<>();

        pieces.add(null);

        assertThrows(IllegalArgumentException.class,
                () -> new GameAction(ActionType.MOVE_PIECE, pieces, 4));
    }

    @Test
    void shouldRejectInvalidRoll() {
        Piece piece = new Piece(Colour.RED, 1);

        assertThrows(IllegalArgumentException.class,
                () -> new GameAction(ActionType.MOVE_PIECE, List.of(piece), 7));
    }
}