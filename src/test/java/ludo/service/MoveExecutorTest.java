package ludo.service;

import ludo.domain.enums.Colour;
import ludo.domain.enums.Direction;
import ludo.domain.enums.PieceState;
import ludo.domain.model.Board;
import ludo.domain.model.Piece;
import ludo.random.Coin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MoveExecutorTest {

    private Board board;
    private Coin coin;
    private MoveExecutor moveExecutor;

    @BeforeEach
    void setUp() {
        board = new Board();
        coin = mock(Coin.class);

        moveExecutor = new MoveExecutor(
                board,
                coin);
    }

    @Test
    void shouldMovePieceFromBaseToStartPositionOnSixWithHeads() {

        Piece piece = new Piece(Colour.RED, 1);

        when(coin.toss()).thenReturn(true);

        boolean moved = moveExecutor.moveFromBase(piece, 6);

        assertTrue(moved);

        assertEquals(
                PieceState.STANDARD_PATH,
                piece.getState());

        assertEquals(26, piece.getPosition());

        assertEquals(
                Direction.CLOCKWISE,
                piece.getDirection());

        verify(coin).toss();
    }

    @Test
    void shouldMovePieceFromBaseToStartPositionOnSixWithTails() {

        Piece piece = new Piece(Colour.BLUE, 1);

        when(coin.toss()).thenReturn(false);

        boolean moved = moveExecutor.moveFromBase(piece, 6);

        assertTrue(moved);

        assertEquals(
                PieceState.STANDARD_PATH,
                piece.getState());

        assertEquals(13, piece.getPosition());

        assertEquals(
                Direction.COUNTERCLOCKWISE,
                piece.getDirection());

        verify(coin).toss();
    }

    @Test
    void shouldNotMovePieceFromBaseWithoutSix() {

        Piece piece = new Piece(Colour.YELLOW, 1);

        boolean moved = moveExecutor.moveFromBase(piece, 5);

        assertFalse(moved);

        assertEquals(
                PieceState.BASE,
                piece.getState());

        assertNull(piece.getPosition());
        assertNull(piece.getDirection());

        verifyNoInteractions(coin);
    }

    @Test
    void shouldNotMovePieceFromBaseWhenPieceIsAlreadyOnBoard() {

        Piece piece = new Piece(Colour.GREEN, 1);

        piece.enterBoard(
                39,
                Direction.CLOCKWISE);

        boolean moved = moveExecutor.moveFromBase(piece, 6);

        assertFalse(moved);

        assertEquals(39, piece.getPosition());

        assertEquals(
                Direction.CLOCKWISE,
                piece.getDirection());

        verifyNoInteractions(coin);
    }

    @Test
    void shouldRejectNullBoard() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new MoveExecutor(null, coin));
    }

    @Test
    void shouldRejectNullCoin() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new MoveExecutor(board, null));
    }

    @Test
    void shouldRejectNullPiece() {

        assertThrows(
                IllegalArgumentException.class,
                () -> moveExecutor.moveFromBase(null, 6));
    }

    public boolean moveOnStandardPath(Piece piece, int distance) {

        if (piece == null) {
            throw new IllegalArgumentException(
                    "Piece cannot be null.");
        }

        if (piece.getState() != PieceState.STANDARD_PATH) {
            return false;
        }

        if (distance <= 0) {
            throw new IllegalArgumentException(
                    "Movement distance must be greater than zero.");
        }

        int currentPosition = piece.getPosition();

        int newPosition;

        if (piece.getDirection() == Direction.CLOCKWISE) {

            newPosition = (currentPosition + distance)
                    % Board.STANDARD_PATH_SIZE;

        } else {

            newPosition = Math.floorMod(
                    currentPosition - distance,
                    Board.STANDARD_PATH_SIZE);
        }

        piece.moveTo(newPosition);

        return true;
    }
}
