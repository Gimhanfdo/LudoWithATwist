package ludo.domain;

import ludo.domain.enums.Colour;
import ludo.domain.enums.Direction;
import ludo.domain.model.Piece;
import ludo.domain.model.Player;
import ludo.domain.model.GameState;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameStateTest {

    private Player redPlayer;
    private Player bluePlayer;
    private GameState gameState;

    @BeforeEach
    void setUp() {

        redPlayer = new Player(Colour.RED);
        bluePlayer = new Player(Colour.BLUE);

        gameState = new GameState(List.of(redPlayer, bluePlayer));
    }

    @Test
    void shouldFindPieceAtStandardPosition() {

        Piece redPiece = redPlayer.getPieces().get(0);

        redPiece.enterBoard(26, Direction.CLOCKWISE);

        redPiece.moveTo(20);

        List<Piece> pieces = gameState.getPiecesAtStandardPosition(20);

        assertEquals(1, pieces.size());

        assertSame(redPiece, pieces.get(0));
    }

    @Test
    void shouldReturnEmptyListWhenPositionIsUnoccupied() {

        List<Piece> pieces = gameState.getPiecesAtStandardPosition(20);

        assertTrue(pieces.isEmpty());
    }

    @Test
    void shouldFindMultiplePiecesAtSameStandardPosition() {

        Piece redPieceOne = redPlayer.getPieces().get(0);

        Piece redPieceTwo = redPlayer.getPieces().get(1);

        redPieceOne.enterBoard(26, Direction.CLOCKWISE);

        redPieceTwo.enterBoard(26, Direction.COUNTERCLOCKWISE);

        redPieceOne.moveTo(20);
        redPieceTwo.moveTo(20);

        List<Piece> pieces = gameState.getPiecesAtStandardPosition(20);

        assertEquals(2, pieces.size());

        assertTrue(pieces.contains(redPieceOne));

        assertTrue(pieces.contains(redPieceTwo));
    }

    @Test
    void shouldFindPiecesOfDifferentColoursAtSamePosition() {

        Piece redPiece = redPlayer.getPieces().get(0);

        Piece bluePiece = bluePlayer.getPieces().get(0);

        redPiece.enterBoard(26, Direction.CLOCKWISE);

        bluePiece.enterBoard(13, Direction.CLOCKWISE);

        redPiece.moveTo(20);
        bluePiece.moveTo(20);

        List<Piece> pieces = gameState.getPiecesAtStandardPosition(20);

        assertEquals(2, pieces.size());

        assertTrue(pieces.contains(redPiece));
        assertTrue(pieces.contains(bluePiece));
    }

    @Test
    void shouldIgnorePiecesInBase() {

        List<Piece> pieces = gameState.getPiecesAtStandardPosition(0);

        assertTrue(pieces.isEmpty());
    }

    @Test
    void shouldIgnorePieceInHomeStraight() {

        Piece redPiece = redPlayer.getPieces().get(0);

        redPiece.enterBoard(26, Direction.CLOCKWISE);

        redPiece.enterHomeStraight(2);

        List<Piece> pieces = gameState.getPiecesAtStandardPosition(2);

        assertTrue(pieces.isEmpty());
    }

    @Test
    void shouldReturnUnmodifiableOccupancyList() {

        Piece redPiece = redPlayer.getPieces().get(0);

        redPiece.enterBoard(26, Direction.CLOCKWISE);

        redPiece.moveTo(20);

        List<Piece> pieces = gameState.getPiecesAtStandardPosition(20);

        assertThrows(UnsupportedOperationException.class,
                () -> pieces.add(new Piece(Colour.GREEN, 1)));
    }

    @Test
    void shouldRejectNullPlayerInList() {

        List<Player> players = new java.util.ArrayList<>();

        players.add(new Player(Colour.RED));

        players.add(null);

        assertThrows(IllegalArgumentException.class,
                () -> new GameState(players));
    }
}