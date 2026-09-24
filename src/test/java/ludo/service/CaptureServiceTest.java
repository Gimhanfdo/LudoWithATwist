package ludo.service;

import ludo.domain.enums.Colour;
import ludo.domain.enums.Direction;
import ludo.domain.enums.PieceState;
import ludo.domain.model.Piece;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CaptureServiceTest {

    private CaptureService captureService;

    @BeforeEach
    void setUp() {
        captureService = new CaptureService();
    }

    @Test
    void shouldAllowCaptureWhenOpponentsOccupySamePosition() {

        Piece attacker = new Piece(Colour.RED, 1);

        Piece opponent = new Piece(Colour.BLUE, 1);

        attacker.enterBoard(26, Direction.CLOCKWISE);

        opponent.enterBoard(13, Direction.CLOCKWISE);

        attacker.moveTo(20);
        opponent.moveTo(20);

        assertTrue(captureService.canCapture(attacker, opponent));
    }

    @Test
    void shouldNotCaptureOpponentOnDifferentPosition() {

        Piece attacker = new Piece(Colour.RED, 1);

        Piece opponent = new Piece(Colour.BLUE, 1);

        attacker.enterBoard(26, Direction.CLOCKWISE);

        opponent.enterBoard(13, Direction.CLOCKWISE);

        attacker.moveTo(20);
        opponent.moveTo(21);

        assertFalse(captureService.canCapture(attacker, opponent));
    }

    @Test
    void shouldNotCapturePieceOfSameColour() {

        Piece attacker = new Piece(Colour.RED, 1);

        Piece sameColourPiece = new Piece(Colour.RED, 2);

        attacker.enterBoard(26, Direction.CLOCKWISE);

        sameColourPiece.enterBoard(26, Direction.CLOCKWISE);

        attacker.moveTo(20);
        sameColourPiece.moveTo(20);

        assertFalse(captureService.canCapture(attacker, sameColourPiece));
    }

    @Test
    void shouldCaptureOpponentAndReturnItToBase() {

        Piece attacker = new Piece(Colour.RED, 1);

        Piece opponent = new Piece(Colour.BLUE, 1);

        attacker.enterBoard(26, Direction.CLOCKWISE);

        opponent.enterBoard(13, Direction.COUNTERCLOCKWISE);

        attacker.moveTo(20);
        opponent.moveTo(20);

        boolean captured = captureService.capture(attacker, opponent);

        assertTrue(captured);

        assertEquals(PieceState.BASE, opponent.getState());

        assertNull(opponent.getPosition());

        assertEquals(1, attacker.getCaptureCount());
    }

    @Test
    void shouldResetCapturedPieceInformation() {

        Piece attacker = new Piece(Colour.RED, 1);

        Piece opponent = new Piece(Colour.BLUE, 1);

        attacker.enterBoard(26, Direction.CLOCKWISE);

        opponent.enterBoard(13, Direction.COUNTERCLOCKWISE);

        attacker.moveTo(20);
        opponent.moveTo(20);

        opponent.recordCapture();
        opponent.recordApproachPass();

        captureService.capture(attacker, opponent);

        assertEquals(PieceState.BASE, opponent.getState());

        assertNull(opponent.getPosition());

        assertNull(opponent.getDirection());

        assertEquals(0, opponent.getCaptureCount());

        assertEquals(0, opponent.getApproachPassCount());
    }

    @Test
    void shouldNotModifyPiecesWhenCaptureIsInvalid() {

        Piece attacker = new Piece(Colour.RED, 1);

        Piece opponent = new Piece(Colour.BLUE, 1);

        attacker.enterBoard(26, Direction.CLOCKWISE);

        opponent.enterBoard(13, Direction.CLOCKWISE);

        attacker.moveTo(20);
        opponent.moveTo(21);

        boolean captured = captureService.capture(attacker, opponent);

        assertFalse(captured);

        assertEquals(20, attacker.getPosition());

        assertEquals(21, opponent.getPosition());

        assertEquals(0, attacker.getCaptureCount());

        assertEquals(PieceState.STANDARD_PATH, opponent.getState());
    }

    @Test
    void shouldRejectNullAttacker() {

        Piece opponent = new Piece(Colour.BLUE, 1);

        assertThrows(IllegalArgumentException.class,
                () -> captureService.canCapture(null, opponent));
    }

    @Test
    void shouldRejectNullOpponent() {

        Piece attacker = new Piece(Colour.RED, 1);

        assertThrows(IllegalArgumentException.class,
                () -> captureService.canCapture(attacker, null));
    }
}