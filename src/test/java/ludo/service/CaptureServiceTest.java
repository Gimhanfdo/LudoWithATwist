package ludo.service;

import ludo.domain.enums.Colour;
import ludo.domain.enums.Direction;
import ludo.domain.enums.PieceState;
import ludo.domain.model.Piece;
import ludo.domain.model.GameState;
import ludo.domain.model.Player;

import java.util.List;
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

    @Test
    void shouldResolveCaptureFromGameState() {

        Player redPlayer = new Player(Colour.RED);
        Player bluePlayer = new Player(Colour.BLUE);

        GameState gameState = new GameState(List.of(redPlayer, bluePlayer));
        Piece attacker = redPlayer.getPieces().get(0);
        Piece opponent = bluePlayer.getPieces().get(0);

        attacker.enterBoard(26, Direction.CLOCKWISE);
        opponent.enterBoard(13, Direction.CLOCKWISE);

        attacker.moveTo(20);
        opponent.moveTo(20);

        boolean captured = captureService.resolveCapture(attacker, gameState);

        assertTrue(captured);
        assertEquals(PieceState.BASE, opponent.getState());
        assertNull(opponent.getPosition());
        assertEquals(1, attacker.getCaptureCount());
    }

    @Test
    void shouldNotCaptureWhenLandingPositionHasNoOpponent() {

        Player redPlayer = new Player(Colour.RED);
        Player bluePlayer = new Player(Colour.BLUE);

        GameState gameState = new GameState(List.of(redPlayer, bluePlayer));

        Piece attacker = redPlayer.getPieces().get(0);
        Piece opponent = bluePlayer.getPieces().get(0);

        attacker.enterBoard(26, Direction.CLOCKWISE);
        opponent.enterBoard(13, Direction.CLOCKWISE);

        attacker.moveTo(20);
        opponent.moveTo(21);

        boolean captured = captureService.resolveCapture(attacker, gameState);

        assertFalse(captured);
        assertEquals(PieceState.STANDARD_PATH, opponent.getState());
        assertEquals(21, opponent.getPosition());
        assertEquals(0, attacker.getCaptureCount());
    }

    @Test
    void shouldNotCaptureSameColourOccupant() {

        Player redPlayer = new Player(Colour.RED);
        GameState gameState = new GameState(List.of(redPlayer));

        Piece firstPiece = redPlayer.getPieces().get(0);
        Piece secondPiece = redPlayer.getPieces().get(1);

        firstPiece.enterBoard(26, Direction.CLOCKWISE);
        secondPiece.enterBoard(26, Direction.COUNTERCLOCKWISE);

        firstPiece.moveTo(20);
        secondPiece.moveTo(20);

        boolean captured = captureService.resolveCapture(firstPiece, gameState);

        assertFalse(captured);
        assertEquals(PieceState.STANDARD_PATH, secondPiece.getState());
        assertEquals(20, secondPiece.getPosition());
        assertEquals(0, firstPiece.getCaptureCount());
    }

    @Test
    void shouldIgnoreAttackerWhenResolvingCapture() {

        Player redPlayer = new Player(Colour.RED);

        GameState gameState = new GameState(List.of(redPlayer));

        Piece attacker = redPlayer.getPieces().get(0);

        attacker.enterBoard(26, Direction.CLOCKWISE);
        attacker.moveTo(20);

        boolean captured = captureService.resolveCapture(attacker, gameState);

        assertFalse(captured);
        assertEquals(0, attacker.getCaptureCount());

        assertEquals(PieceState.STANDARD_PATH, attacker.getState());

        assertEquals(20, attacker.getPosition());
    }

    @Test
    void shouldNotResolveCaptureWhenAttackerIsNotOnStandardPath() {

        Player redPlayer = new Player(Colour.RED);
        Player bluePlayer = new Player(Colour.BLUE);

        GameState gameState = new GameState(List.of(redPlayer, bluePlayer));

        Piece attacker = redPlayer.getPieces().get(0);
        Piece opponent = bluePlayer.getPieces().get(0);

        attacker.enterBoard(26, Direction.CLOCKWISE);
        attacker.enterHomeStraight(2);
        opponent.enterBoard(13, Direction.CLOCKWISE);

        opponent.moveTo(2);

        boolean captured = captureService.resolveCapture(attacker, gameState);

        assertFalse(captured);
        assertEquals(PieceState.STANDARD_PATH, opponent.getState());
        assertEquals(2, opponent.getPosition());
    }

    @Test
    void shouldRejectNullGameStateWhenResolvingCapture() {

        Piece attacker = new Piece(Colour.RED, 1);

        assertThrows(IllegalArgumentException.class,
                () -> captureService.resolveCapture(attacker, null));
    }

    @Test
    void shouldCaptureEqualSizedOpponentBlock() {

        Piece redOne = new Piece(Colour.RED, 1);
        Piece redTwo = new Piece(Colour.RED, 2);
        Piece blueOne = new Piece(Colour.BLUE, 1);
        Piece blueTwo = new Piece(Colour.BLUE, 2);

        redOne.enterBoard(26, Direction.CLOCKWISE);
        redTwo.enterBoard(26, Direction.COUNTERCLOCKWISE);
        blueOne.enterBoard(13, Direction.CLOCKWISE);
        blueTwo.enterBoard(13, Direction.COUNTERCLOCKWISE);

        redOne.moveTo(20);
        redTwo.moveTo(20);
        blueOne.moveTo(20);
        blueTwo.moveTo(20);

        boolean captured = captureService.captureBlock(
                List.of(redOne, redTwo),
                List.of(blueOne, blueTwo));

        assertTrue(captured);
        assertEquals(PieceState.BASE, blueOne.getState());
        assertEquals(PieceState.BASE, blueTwo.getState());
        assertEquals(1, redOne.getCaptureCount());
        assertEquals(1, redTwo.getCaptureCount());
    }

    @Test
    void shouldResetCapturedBlockPiecesCompletely() {

        Piece redOne = new Piece(Colour.RED, 1);
        Piece redTwo = new Piece(Colour.RED, 2);
        Piece blueOne = new Piece(Colour.BLUE, 1);
        Piece blueTwo = new Piece(Colour.BLUE, 2);

        redOne.enterBoard(26, Direction.CLOCKWISE);
        redTwo.enterBoard(26, Direction.CLOCKWISE);
        blueOne.enterBoard(13, Direction.COUNTERCLOCKWISE);
        blueTwo.enterBoard(13, Direction.COUNTERCLOCKWISE);

        redOne.moveTo(20);
        redTwo.moveTo(20);
        blueOne.moveTo(20);
        blueTwo.moveTo(20);

        blueOne.recordCapture();
        blueOne.recordApproachPass();

        captureService.captureBlock(
                List.of(redOne, redTwo),
                List.of(blueOne, blueTwo));

        assertEquals(PieceState.BASE, blueOne.getState());
        assertNull(blueOne.getPosition());
        assertNull(blueOne.getDirection());
        assertEquals(0, blueOne.getCaptureCount());
        assertEquals(0, blueOne.getApproachPassCount());
    }

    @Test
    void shouldNotCaptureDifferentSizedBlock() {

        Piece redOne = new Piece(Colour.RED, 1);
        Piece redTwo = new Piece(Colour.RED, 2);
        Piece blueOne = new Piece(Colour.BLUE, 1);
        Piece blueTwo = new Piece(Colour.BLUE, 2);
        Piece blueThree = new Piece(Colour.BLUE, 3);

        redOne.enterBoard(26, Direction.CLOCKWISE);
        redTwo.enterBoard(26, Direction.CLOCKWISE);
        blueOne.enterBoard(13, Direction.CLOCKWISE);
        blueTwo.enterBoard(13, Direction.CLOCKWISE);
        blueThree.enterBoard(13, Direction.CLOCKWISE);

        redOne.moveTo(20);
        redTwo.moveTo(20);
        blueOne.moveTo(20);
        blueTwo.moveTo(20);
        blueThree.moveTo(20);

        boolean captured = captureService.captureBlock(
                List.of(redOne, redTwo),
                List.of(blueOne, blueTwo, blueThree));

        assertFalse(captured);
        assertEquals(PieceState.STANDARD_PATH, blueOne.getState());
        assertEquals(PieceState.STANDARD_PATH, blueTwo.getState());
        assertEquals(PieceState.STANDARD_PATH, blueThree.getState());
        assertEquals(0, redOne.getCaptureCount());
        assertEquals(0, redTwo.getCaptureCount());
    }

    @Test
    void shouldNotCaptureSameColourBlock() {

        Piece redOne = new Piece(Colour.RED, 1);
        Piece redTwo = new Piece(Colour.RED, 2);
        Piece redThree = new Piece(Colour.RED, 3);
        Piece redFour = new Piece(Colour.RED, 4);

        redOne.enterBoard(26, Direction.CLOCKWISE);
        redTwo.enterBoard(26, Direction.CLOCKWISE);
        redThree.enterBoard(26, Direction.COUNTERCLOCKWISE);
        redFour.enterBoard(26, Direction.COUNTERCLOCKWISE);

        redOne.moveTo(20);
        redTwo.moveTo(20);
        redThree.moveTo(20);
        redFour.moveTo(20);

        boolean captured = captureService.captureBlock(
                List.of(redOne, redTwo),
                List.of(redThree, redFour));

        assertFalse(captured);
    }

    @Test
    void shouldNotCaptureBlockAtDifferentPosition() {

        Piece redOne = new Piece(Colour.RED, 1);
        Piece redTwo = new Piece(Colour.RED, 2);
        Piece blueOne = new Piece(Colour.BLUE, 1);
        Piece blueTwo = new Piece(Colour.BLUE, 2);

        redOne.enterBoard(26, Direction.CLOCKWISE);
        redTwo.enterBoard(26, Direction.CLOCKWISE);
        blueOne.enterBoard(13, Direction.CLOCKWISE);
        blueTwo.enterBoard(13, Direction.CLOCKWISE);

        redOne.moveTo(20);
        redTwo.moveTo(20);
        blueOne.moveTo(21);
        blueTwo.moveTo(21);

        boolean captured = captureService.captureBlock(
                List.of(redOne, redTwo),
                List.of(blueOne, blueTwo));

        assertFalse(captured);
    }

    @Test
    void shouldCaptureEqualThreePieceOpponentBlock() {

        Piece redOne = new Piece(Colour.RED, 1);
        Piece redTwo = new Piece(Colour.RED, 2);
        Piece redThree = new Piece(Colour.RED, 3);

        Piece blueOne = new Piece(Colour.BLUE, 1);
        Piece blueTwo = new Piece(Colour.BLUE, 2);
        Piece blueThree = new Piece(Colour.BLUE, 3);

        List<Piece> attackers = List.of(redOne, redTwo, redThree);
        List<Piece> defenders = List.of(blueOne, blueTwo, blueThree);

        for (Piece attacker : attackers) {
            attacker.enterBoard(26, Direction.CLOCKWISE);
            attacker.moveTo(20);
        }

        for (Piece defender : defenders) {
            defender.enterBoard(13, Direction.CLOCKWISE);
            defender.moveTo(20);
        }

        boolean captured = captureService.captureBlock(attackers, defenders);

        assertTrue(captured);
        assertTrue(defenders.stream()
                .allMatch(piece -> piece.getState() == PieceState.BASE));
        assertTrue(attackers.stream()
                .allMatch(piece -> piece.getCaptureCount() == 1));
    }

    @Test
    void shouldRejectNullAttackingBlock() {

        Piece blueOne = new Piece(Colour.BLUE, 1);
        Piece blueTwo = new Piece(Colour.BLUE, 2);

        assertThrows(IllegalArgumentException.class,
                () -> captureService.captureBlock(null, List.of(blueOne, blueTwo)));
    }

    @Test
    void shouldRejectGroupThatIsNotABlock() {

        Piece redPiece = new Piece(Colour.RED, 1);
        Piece blueOne = new Piece(Colour.BLUE, 1);
        Piece blueTwo = new Piece(Colour.BLUE, 2);

        assertThrows(IllegalArgumentException.class,
                () -> captureService.captureBlock(List.of(redPiece), List.of(blueOne, blueTwo)));
    }

    @Test
    void shouldNotAllowSinglePieceToPartiallyCaptureOpponentBlock() {

        Player redPlayer = new Player(Colour.RED);
        Player bluePlayer = new Player(Colour.BLUE);

        GameState gameState = new GameState(List.of(redPlayer, bluePlayer));

        Piece attacker = redPlayer.getPieces().get(0);
        Piece blueOne = bluePlayer.getPieces().get(0);
        Piece blueTwo = bluePlayer.getPieces().get(1);

        attacker.enterBoard(26, Direction.CLOCKWISE);
        blueOne.enterBoard(13, Direction.CLOCKWISE);
        blueTwo.enterBoard(13, Direction.COUNTERCLOCKWISE);

        attacker.moveTo(20);
        blueOne.moveTo(20);
        blueTwo.moveTo(20);

        boolean captured = captureService.resolveCapture(attacker, gameState);

        assertFalse(captured);
        assertEquals(PieceState.STANDARD_PATH, blueOne.getState());
        assertEquals(PieceState.STANDARD_PATH, blueTwo.getState());
        assertEquals(0, attacker.getCaptureCount());
    }

    @Test
    void shouldResolveEqualSizedBlockCaptureFromGameState() {

        Player redPlayer = new Player(Colour.RED);
        Player bluePlayer = new Player(Colour.BLUE);

        GameState gameState = new GameState(List.of(redPlayer, bluePlayer));

        Piece redOne = redPlayer.getPieces().get(0);
        Piece redTwo = redPlayer.getPieces().get(1);
        Piece blueOne = bluePlayer.getPieces().get(0);
        Piece blueTwo = bluePlayer.getPieces().get(1);

        redOne.enterBoard(26, Direction.CLOCKWISE);
        redTwo.enterBoard(26, Direction.COUNTERCLOCKWISE);
        blueOne.enterBoard(13, Direction.CLOCKWISE);
        blueTwo.enterBoard(13, Direction.COUNTERCLOCKWISE);

        redOne.moveTo(20);
        redTwo.moveTo(20);
        blueOne.moveTo(20);
        blueTwo.moveTo(20);

        boolean captured = captureService.resolveBlockCapture(List.of(redOne, redTwo), gameState);

        assertTrue(captured);
        assertEquals(PieceState.BASE, blueOne.getState());
        assertEquals(PieceState.BASE, blueTwo.getState());
        assertEquals(1, redOne.getCaptureCount());
        assertEquals(1, redTwo.getCaptureCount());
    }

    @Test
    void shouldNotResolveBlockCaptureWhenDefendingBlockHasDifferentSize() {

        Player redPlayer = new Player(Colour.RED);
        Player bluePlayer = new Player(Colour.BLUE);

        GameState gameState = new GameState(List.of(redPlayer, bluePlayer));

        Piece redOne = redPlayer.getPieces().get(0);
        Piece redTwo = redPlayer.getPieces().get(1);
        Piece blueOne = bluePlayer.getPieces().get(0);
        Piece blueTwo = bluePlayer.getPieces().get(1);
        Piece blueThree = bluePlayer.getPieces().get(2);

        redOne.enterBoard(26, Direction.CLOCKWISE);
        redTwo.enterBoard(26, Direction.CLOCKWISE);
        blueOne.enterBoard(13, Direction.CLOCKWISE);
        blueTwo.enterBoard(13, Direction.CLOCKWISE);
        blueThree.enterBoard(13, Direction.CLOCKWISE);

        redOne.moveTo(20);
        redTwo.moveTo(20);
        blueOne.moveTo(20);
        blueTwo.moveTo(20);
        blueThree.moveTo(20);

        boolean captured = captureService.resolveBlockCapture(List.of(redOne, redTwo), gameState);

        assertFalse(captured);
        assertEquals(PieceState.STANDARD_PATH, blueOne.getState());
        assertEquals(PieceState.STANDARD_PATH, blueTwo.getState());
        assertEquals(PieceState.STANDARD_PATH, blueThree.getState());
        assertEquals(0, redOne.getCaptureCount());
        assertEquals(0, redTwo.getCaptureCount());
    }

    @Test
    void shouldNotResolveCaptureWhenAttackersDoNotFormBlock() {

        Player redPlayer = new Player(Colour.RED);
        Player bluePlayer = new Player(Colour.BLUE);

        GameState gameState = new GameState(List.of(redPlayer, bluePlayer));

        Piece redOne = redPlayer.getPieces().get(0);
        Piece redTwo = redPlayer.getPieces().get(1);

        redOne.enterBoard(26, Direction.CLOCKWISE);
        redTwo.enterBoard(26, Direction.CLOCKWISE);

        redOne.moveTo(20);
        redTwo.moveTo(21);

        boolean captured = captureService.resolveBlockCapture(List.of(redOne, redTwo), gameState);

        assertFalse(captured);
    }

    @Test
    void shouldReturnCapturableOpponentWithoutCapturingIt() {
        Player redPlayer = new Player(Colour.RED);
        Player bluePlayer = new Player(Colour.BLUE);

        GameState gameState = new GameState(List.of(redPlayer, bluePlayer));

        Piece attacker = redPlayer.getPieces().get(0);
        Piece opponent = bluePlayer.getPieces().get(0);
        attacker.enterBoard(20, Direction.CLOCKWISE);
        opponent.enterBoard(20, Direction.CLOCKWISE);

        List<Piece> capturablePieces = captureService.getCapturablePieces(attacker, gameState);

        assertEquals(List.of(opponent), capturablePieces);
        assertEquals(PieceState.STANDARD_PATH, opponent.getState());
        assertEquals(20, opponent.getPosition());
    }

    @Test
    void shouldReturnCapturableOpponentBlockWithoutCapturingIt() {
        Player redPlayer = new Player(Colour.RED);
        Player bluePlayer = new Player(Colour.BLUE);
        GameState gameState = new GameState(List.of(redPlayer, bluePlayer));

        Piece redOne = redPlayer.getPieces().get(0);
        Piece redTwo = redPlayer.getPieces().get(1);
        Piece blueOne = bluePlayer.getPieces().get(0);
        Piece blueTwo = bluePlayer.getPieces().get(1);

        redOne.enterBoard(26, Direction.CLOCKWISE);
        redTwo.enterBoard(26, Direction.CLOCKWISE);
        blueOne.enterBoard(13, Direction.CLOCKWISE);
        blueTwo.enterBoard(13, Direction.CLOCKWISE);

        redOne.moveTo(20);
        redTwo.moveTo(20);
        blueOne.moveTo(20);
        blueTwo.moveTo(20);

        List<Piece> capturablePieces = captureService.getCapturableBlockPieces(List.of(redOne, redTwo), gameState);

        assertEquals(List.of(blueOne, blueTwo), capturablePieces);
        assertEquals(PieceState.STANDARD_PATH, blueOne.getState());
        assertEquals(PieceState.STANDARD_PATH, blueTwo.getState());
        assertEquals(20, blueOne.getPosition());
        assertEquals(20, blueTwo.getPosition());
    }

    @Test
    void shouldReturnNoCapturableBlockWhenSizesDiffer() {
        Player redPlayer = new Player(Colour.RED);
        Player bluePlayer = new Player(Colour.BLUE);
        GameState gameState = new GameState(List.of(redPlayer, bluePlayer));

        Piece redOne = redPlayer.getPieces().get(0);
        Piece redTwo = redPlayer.getPieces().get(1);
        Piece blueOne = bluePlayer.getPieces().get(0);
        Piece blueTwo = bluePlayer.getPieces().get(1);
        Piece blueThree = bluePlayer.getPieces().get(2);

        redOne.enterBoard(26, Direction.CLOCKWISE);
        redTwo.enterBoard(26, Direction.CLOCKWISE);
        blueOne.enterBoard(13, Direction.CLOCKWISE);
        blueTwo.enterBoard(13, Direction.CLOCKWISE);
        blueThree.enterBoard(13, Direction.CLOCKWISE);

        redOne.moveTo(20);
        redTwo.moveTo(20);
        blueOne.moveTo(20);
        blueTwo.moveTo(20);
        blueThree.moveTo(20);

        List<Piece> capturablePieces = captureService.getCapturableBlockPieces(List.of(redOne, redTwo), gameState);

        assertTrue(capturablePieces.isEmpty());
    }
}