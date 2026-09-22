package ludo.domain;

import ludo.domain.enums.Colour;
import ludo.domain.enums.Direction;
import ludo.domain.enums.PieceEffect;
import ludo.domain.enums.PieceState;
import ludo.domain.model.Piece;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PieceTest {

        @Test
        void shouldCreatePieceInBase() {

                Piece piece = new Piece(Colour.RED, 1);

                assertEquals(Colour.RED, piece.getColour());
                assertEquals(1, piece.getNumber());
                assertEquals("R1", piece.getName());

                assertEquals(PieceState.BASE, piece.getState());
                assertNull(piece.getPosition());
                assertNull(piece.getDirection());

                assertEquals(PieceEffect.NONE, piece.getEffect());

                assertEquals(0, piece.getCaptureCount());
                assertEquals(0, piece.getApproachPassCount());
                assertEquals(0, piece.getEffectRoundsRemaining());
        }

        @Test
        void shouldRejectPieceNumberBelowOne() {

                assertThrows(
                                IllegalArgumentException.class,
                                () -> new Piece(Colour.RED, 0));
        }

        @Test
        void shouldRejectPieceNumberAboveFour() {

                assertThrows(
                                IllegalArgumentException.class,
                                () -> new Piece(Colour.RED, 5));
        }

        @Test
        void shouldRejectNullColour() {

                assertThrows(
                                IllegalArgumentException.class,
                                () -> new Piece(null, 1));
        }

        @Test
        void shouldEnterBoardClockwise() {

                Piece piece = new Piece(Colour.RED, 1);

                piece.enterBoard(26, Direction.CLOCKWISE);

                assertEquals(
                                PieceState.STANDARD_PATH,
                                piece.getState());

                assertEquals(26, piece.getPosition());

                assertEquals(
                                Direction.CLOCKWISE,
                                piece.getDirection());
        }

        @Test
        void shouldEnterBoardCounterclockwise() {

                Piece piece = new Piece(Colour.BLUE, 1);

                piece.enterBoard(
                                13,
                                Direction.COUNTERCLOCKWISE);

                assertEquals(
                                PieceState.STANDARD_PATH,
                                piece.getState());

                assertEquals(13, piece.getPosition());

                assertEquals(
                                Direction.COUNTERCLOCKWISE,
                                piece.getDirection());
        }

        @Test
        void shouldRejectEnteringBoardWithoutDirection() {

                Piece piece = new Piece(Colour.YELLOW, 1);

                assertThrows(
                                IllegalArgumentException.class,
                                () -> piece.enterBoard(0, null));
        }

        @Test
        void shouldRejectEnteringBoardWhenAlreadyOnBoard() {

                Piece piece = new Piece(Colour.GREEN, 1);

                piece.enterBoard(
                                39,
                                Direction.CLOCKWISE);

                assertThrows(
                                IllegalStateException.class,
                                () -> piece.enterBoard(
                                                39,
                                                Direction.COUNTERCLOCKWISE));
        }

        @Test
        void shouldMovePieceToNewStandardPosition() {

                Piece piece = new Piece(Colour.RED, 1);

                piece.enterBoard(
                                26,
                                Direction.CLOCKWISE);

                piece.moveTo(30);

                assertEquals(30, piece.getPosition());
        }

        @Test
        void shouldRejectStandardMovementWhenPieceIsInBase() {

                Piece piece = new Piece(Colour.RED, 1);

                assertThrows(
                                IllegalStateException.class,
                                () -> piece.moveTo(30));
        }

        @Test
        void shouldStartWithZeroCaptures() {

                Piece piece = new Piece(Colour.RED, 1);

                assertEquals(0, piece.getCaptureCount());
        }

        @Test
        void shouldRecordCapture() {

                Piece piece = new Piece(Colour.RED, 1);

                piece.recordCapture();

                assertEquals(1, piece.getCaptureCount());
        }

        @Test
        void shouldRecordMultipleCaptures() {

                Piece piece = new Piece(Colour.RED, 1);

                piece.recordCapture();
                piece.recordCapture();

                assertEquals(2, piece.getCaptureCount());
        }

        @Test
        void shouldResetCaptureCountWhenPieceIsReset() {

                Piece piece = new Piece(Colour.RED, 1);

                piece.recordCapture();
                piece.recordCapture();

                piece.reset();

                assertEquals(0, piece.getCaptureCount());
                assertEquals(PieceState.BASE, piece.getState());
        }

        @Test
        void shouldReportNoCaptureInitially() {

                Piece piece = new Piece(Colour.RED, 1);

                assertFalse(piece.hasCaptured());
        }

        @Test
        void shouldReportCaptureAfterRecordingCapture() {

                Piece piece = new Piece(Colour.RED, 1);

                piece.recordCapture();

                assertTrue(piece.hasCaptured());
        }

        @Test
        void shouldStartWithZeroApproachPasses() {

                Piece piece = new Piece(Colour.RED, 1);

                assertEquals(
                                0,
                                piece.getApproachPassCount());
        }

        @Test
        void shouldRecordApproachPass() {

                Piece piece = new Piece(Colour.RED, 1);

                piece.recordApproachPass();

                assertEquals(
                                1,
                                piece.getApproachPassCount());
        }

        @Test
        void shouldRecordMultipleApproachPasses() {

                Piece piece = new Piece(Colour.RED, 1);

                piece.recordApproachPass();
                piece.recordApproachPass();

                assertEquals(
                                2,
                                piece.getApproachPassCount());
        }

        @Test
        void shouldReportApproachNotPassedTwiceAfterOnePass() {

                Piece piece = new Piece(Colour.RED, 1);

                piece.recordApproachPass();

                assertFalse(
                                piece.hasPassedApproachTwice());
        }

        @Test
        void shouldReportApproachPassedTwiceAfterTwoPasses() {

                Piece piece = new Piece(Colour.RED, 1);

                piece.recordApproachPass();
                piece.recordApproachPass();

                assertTrue(
                                piece.hasPassedApproachTwice());
        }

        @Test
        void shouldResetApproachPassCountWhenPieceIsReset() {

                Piece piece = new Piece(Colour.RED, 1);

                piece.recordApproachPass();
                piece.recordApproachPass();

                piece.reset();

                assertEquals(
                                0,
                                piece.getApproachPassCount());

                assertFalse(
                                piece.hasPassedApproachTwice());
        }

        @Test
        void shouldEnterHomeStraight() {

                Piece piece = new Piece(Colour.RED, 1);

                piece.enterBoard(
                                26,
                                Direction.CLOCKWISE);

                piece.enterHomeStraight(0);

                assertEquals(
                                PieceState.HOME_STRAIGHT,
                                piece.getState());

                assertEquals(0, piece.getPosition());
        }

        @Test
        void shouldRejectEnteringHomeStraightFromBase() {

                Piece piece = new Piece(Colour.RED, 1);

                assertThrows(
                                IllegalStateException.class,
                                () -> piece.enterHomeStraight(0));
        }

        @Test
        void shouldRejectInvalidHomeStraightPosition() {

                Piece piece = new Piece(Colour.RED, 1);

                piece.enterBoard(
                                26,
                                Direction.CLOCKWISE);

                assertThrows(
                                IllegalArgumentException.class,
                                () -> piece.enterHomeStraight(5));
        }
}