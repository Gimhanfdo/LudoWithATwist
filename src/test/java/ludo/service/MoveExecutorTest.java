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

        @Test
        void shouldMoveClockwiseOnStandardPath() {

                Piece piece = new Piece(Colour.YELLOW, 1);

                piece.enterBoard(
                                0,
                                Direction.CLOCKWISE);

                boolean moved = moveExecutor.moveOnStandardPath(piece, 4);

                assertTrue(moved);
                assertEquals(4, piece.getPosition());
        }

        @Test
        void shouldMoveCounterclockwiseOnStandardPath() {

                Piece piece = new Piece(Colour.YELLOW, 1);

                piece.enterBoard(
                                0,
                                Direction.COUNTERCLOCKWISE);

                boolean moved = moveExecutor.moveOnStandardPath(piece, 4);

                assertTrue(moved);
                assertEquals(48, piece.getPosition());
        }

        @Test
        void shouldWrapAroundWhenMovingClockwisePastEndOfStandardPath() {

                Piece piece = new Piece(Colour.RED, 1);

                piece.enterBoard(
                                26,
                                Direction.CLOCKWISE);

                piece.moveTo(50);

                boolean moved = moveExecutor.moveOnStandardPath(piece, 4);

                assertTrue(moved);
                assertEquals(2, piece.getPosition());
        }

        @Test
        void shouldWrapAroundWhenMovingCounterclockwisePastStartOfStandardPath() {

                Piece piece = new Piece(Colour.BLUE, 1);

                piece.enterBoard(
                                13,
                                Direction.COUNTERCLOCKWISE);

                piece.moveTo(2);

                boolean moved = moveExecutor.moveOnStandardPath(piece, 4);

                assertTrue(moved);
                assertEquals(50, piece.getPosition());
        }

        @Test
        void shouldNotMoveBasePieceOnStandardPath() {

                Piece piece = new Piece(Colour.GREEN, 1);

                boolean moved = moveExecutor.moveOnStandardPath(piece, 4);

                assertFalse(moved);

                assertEquals(
                                PieceState.BASE,
                                piece.getState());

                assertNull(piece.getPosition());
        }

        @Test
        void shouldRejectNoMovementDistance() {

                Piece piece = new Piece(Colour.RED, 1);

                piece.enterBoard(
                                26,
                                Direction.CLOCKWISE);

                assertThrows(
                                IllegalArgumentException.class,
                                () -> moveExecutor.moveOnStandardPath(
                                                piece,
                                                0));
        }

        @Test
        void shouldRejectNegativeMovementDistance() {

                Piece piece = new Piece(Colour.RED, 1);

                piece.enterBoard(
                                26,
                                Direction.CLOCKWISE);

                assertThrows(
                                IllegalArgumentException.class,
                                () -> moveExecutor.moveOnStandardPath(
                                                piece,
                                                -1));
        }

        @Test
        void shouldEnterHomeStraightWhenClockwisePieceHasCaptured() {

                Piece piece = new Piece(Colour.RED, 1);

                piece.enterBoard(
                                26,
                                Direction.CLOCKWISE);

                piece.moveTo(22);
                piece.recordCapture();

                boolean moved = moveExecutor.moveOnStandardPath(
                                piece,
                                5);

                assertTrue(moved);

                assertEquals(
                                PieceState.HOME_STRAIGHT,
                                piece.getState());

                assertEquals(1, piece.getPosition());
        }

        @Test
        void shouldNotEnterHomeStraightWithoutCapture() {

                Piece piece = new Piece(Colour.RED, 1);

                piece.enterBoard(
                                26,
                                Direction.CLOCKWISE);

                piece.moveTo(22);

                boolean moved = moveExecutor.moveOnStandardPath(
                                piece,
                                5);

                assertTrue(moved);

                assertEquals(
                                PieceState.STANDARD_PATH,
                                piece.getState());

                assertEquals(27, piece.getPosition());
        }

        @Test
        void shouldContinueStandardPathOnFirstCounterclockwiseApproachPass() {

                Piece piece = new Piece(Colour.RED, 1);

                piece.enterBoard(
                                26,
                                Direction.COUNTERCLOCKWISE);

                piece.moveTo(28);
                piece.recordCapture();

                boolean moved = moveExecutor.moveOnStandardPath(
                                piece,
                                5);

                assertTrue(moved);

                assertEquals(
                                PieceState.STANDARD_PATH,
                                piece.getState());

                assertEquals(23, piece.getPosition());

                assertEquals(
                                1,
                                piece.getApproachPassCount());
        }

        @Test
        void shouldEnterHomeStraightOnEligibleCounterclockwiseApproachPass() {

                Piece piece = new Piece(Colour.RED, 1);

                piece.enterBoard(
                                26,
                                Direction.COUNTERCLOCKWISE);

                piece.moveTo(28);

                piece.recordCapture();

                piece.recordApproachPass();
                piece.recordApproachPass();

                boolean moved = moveExecutor.moveOnStandardPath(
                                piece,
                                5);

                assertTrue(moved);

                assertEquals(
                                PieceState.HOME_STRAIGHT,
                                piece.getState());

                assertEquals(1, piece.getPosition());
        }

        @Test
        void shouldEnterHomeStraightOnSecondCounterclockwiseApproachPass() {

                Piece piece = new Piece(Colour.RED, 1);

                piece.enterBoard(
                                26,
                                Direction.COUNTERCLOCKWISE);

                piece.moveTo(28);
                piece.recordCapture();

                piece.recordApproachPass();

                boolean moved = moveExecutor.moveOnStandardPath(
                                piece,
                                5);

                assertTrue(moved);

                assertEquals(
                                PieceState.HOME_STRAIGHT,
                                piece.getState());

                assertEquals(1, piece.getPosition());
        }

        @Test
        void shouldRemainOnStandardPathWhenLandingExactlyOnApproach() {

                Piece piece = new Piece(Colour.RED, 1);

                piece.enterBoard(
                                26,
                                Direction.CLOCKWISE);

                piece.moveTo(22);
                piece.recordCapture();

                boolean moved = moveExecutor.moveOnStandardPath(
                                piece,
                                3);

                assertTrue(moved);

                assertEquals(
                                PieceState.STANDARD_PATH,
                                piece.getState());

                assertEquals(25, piece.getPosition());
        }
}
