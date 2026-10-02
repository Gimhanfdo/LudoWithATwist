package ludo.service;

import ludo.domain.enums.Colour;
import ludo.domain.enums.Direction;
import ludo.domain.enums.PieceState;
import ludo.domain.model.GameState;
import ludo.domain.model.MovementOutcome;
import ludo.domain.model.Piece;
import ludo.domain.model.MovementResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.List;

class MovementServiceTest {

    private MoveExecutor moveExecutor;
    private CaptureService captureService;
    private GameState gameState;
    private MovementService movementService;
    private BlockService blockService;

    @BeforeEach
    void setUp() {

        moveExecutor = mock(MoveExecutor.class);
        captureService = mock(CaptureService.class);
        gameState = mock(GameState.class);
        blockService = mock(BlockService.class);

        movementService = new MovementService(moveExecutor, captureService, blockService, gameState);
    }

    @Test
    void shouldResolveCaptureAfterSuccessfulStandardPathMovement() {

        Piece piece = new Piece(Colour.RED, 1);

        piece.enterBoard(26, Direction.CLOCKWISE);

        when(blockService.getAllowedMovementDistance(piece, 4))
                .thenReturn(4);

        when(
                moveExecutor.moveOnStandardPath(piece, 4))
                .thenAnswer(invocation -> {
                    piece.moveTo(30);
                    return true;
                });

        when(captureService.resolveCapture(piece, gameState))
                .thenReturn(false);

        MovementResult result = movementService.moveOnStandardPath(piece, 4);

        assertEquals(MovementResult.MOVED, result);

        verify(captureService).resolveCapture(piece, gameState);
    }

    @Test
    void shouldNotResolveCaptureWhenMovementFails() {

        Piece piece = new Piece(Colour.RED, 1);
        piece.enterBoard(26, Direction.CLOCKWISE);

        when(moveExecutor.moveOnStandardPath(piece, 4)).thenReturn(false);

        MovementResult result = movementService.moveOnStandardPath(piece, 4);

        assertEquals(MovementResult.NOT_MOVED, result);
        verifyNoInteractions(captureService);
    }

    @Test
    void shouldNotResolveCaptureAfterEnteringHomeStraight() {

        Piece piece = new Piece(Colour.RED, 1);
        piece.enterBoard(26, Direction.CLOCKWISE);
        piece.recordCapture();

        when(blockService.getAllowedMovementDistance(piece, 5))
                .thenReturn(5);

        when(moveExecutor.moveOnStandardPath(piece, 5)).thenAnswer(invocation -> {
            piece.enterHomeStraight(1);
            return true;
        });

        MovementResult result = movementService.moveOnStandardPath(piece, 5);

        assertEquals(MovementResult.MOVED, result);
        verifyNoInteractions(captureService);
    }

    @Test
    void shouldReturnCapturedWhenMovementCapturesOpponent() {

        Piece attacker = new Piece(Colour.RED, 1);
        attacker.enterBoard(26, Direction.CLOCKWISE);

        when(blockService.getAllowedMovementDistance(attacker, 4))
                .thenReturn(4);

        when(moveExecutor.moveOnStandardPath(attacker, 4)).thenAnswer(invocation -> {
            attacker.moveTo(30);
            return true;
        });

        when(captureService.resolveCapture(attacker, gameState)).thenReturn(true);

        MovementResult result = movementService.moveOnStandardPath(attacker, 4);

        assertEquals(MovementResult.CAPTURED, result);
        verify(captureService).resolveCapture(attacker, gameState);
    }

    @Test
    void shouldUseRestrictedDistanceWhenOpponentBlockIsInPath() {

        Piece piece = new Piece(Colour.RED, 1);
        piece.enterBoard(26, Direction.CLOCKWISE);
        piece.moveTo(10);

        when(blockService.getAllowedMovementDistance(piece, 6)).thenReturn(3);

        when(moveExecutor.moveOnStandardPath(piece, 3)).thenAnswer(invocation -> {
            piece.moveTo(13);
            return true;
        });

        when(captureService.resolveCapture(piece, gameState)).thenReturn(false);

        MovementResult result = movementService.moveOnStandardPath(piece, 6);

        assertEquals(MovementResult.MOVED, result);
        assertEquals(13, piece.getPosition());

        verify(moveExecutor).moveOnStandardPath(piece, 3);
        verify(moveExecutor, never()).moveOnStandardPath(piece, 6);
    }

    @Test
    void shouldNotMoveWhenOpponentBlockIsImmediatelyAhead() {

        Piece piece = new Piece(Colour.RED, 1);
        piece.enterBoard(26, Direction.CLOCKWISE);
        piece.moveTo(10);

        when(blockService.getAllowedMovementDistance(piece, 6)).thenReturn(0);

        MovementResult result = movementService.moveOnStandardPath(piece, 6);

        assertEquals(MovementResult.NOT_MOVED, result);
        verifyNoInteractions(moveExecutor);
        verifyNoInteractions(captureService);
    }

    @Test
    void shouldUseFullDistanceWhenNoOpponentBlockInterferes() {

        Piece piece = new Piece(Colour.RED, 1);
        piece.enterBoard(26, Direction.CLOCKWISE);
        piece.moveTo(10);

        when(blockService.getAllowedMovementDistance(piece, 6)).thenReturn(6);

        when(moveExecutor.moveOnStandardPath(piece, 6)).thenAnswer(invocation -> {
            piece.moveTo(16);
            return true;
        });

        when(captureService.resolveCapture(piece, gameState)).thenReturn(false);

        MovementResult result = movementService.moveOnStandardPath(piece, 6);

        assertEquals(MovementResult.MOVED, result);
        assertEquals(16, piece.getPosition());

        verify(moveExecutor).moveOnStandardPath(piece, 6);
    }

    @Test
    void shouldResolveCaptureAfterMovementIsShortenedByBlock() {

        Piece piece = new Piece(Colour.RED, 1);
        piece.enterBoard(26, Direction.CLOCKWISE);
        piece.moveTo(10);

        when(blockService.getAllowedMovementDistance(piece, 6)).thenReturn(3);

        when(moveExecutor.moveOnStandardPath(piece, 3)).thenAnswer(invocation -> {
            piece.moveTo(13);
            return true;
        });

        when(captureService.resolveCapture(piece, gameState)).thenReturn(true);

        MovementResult result = movementService.moveOnStandardPath(piece, 6);

        assertEquals(MovementResult.CAPTURED, result);
        verify(captureService).resolveCapture(piece, gameState);
    }

    @Test
    void shouldMovePieceThroughHomeStraight() {
        Piece piece = new Piece(Colour.RED, 1);
        piece.enterBoard(26, Direction.CLOCKWISE);
        piece.recordCapture();
        piece.enterHomeStraight(0);

        when(moveExecutor.moveOnHomeStraight(piece, 3)).thenReturn(true);

        MovementResult result = movementService.moveOnHomeStraight(piece, 3);

        assertEquals(MovementResult.MOVED, result);
        verify(moveExecutor).moveOnHomeStraight(piece, 3);
        verifyNoInteractions(captureService);
    }

    @Test
    void shouldReturnNotMovedWhenHomeStraightMovementFails() {
        Piece piece = new Piece(Colour.RED, 1);
        piece.enterBoard(26, Direction.CLOCKWISE);
        piece.recordCapture();
        piece.enterHomeStraight(0);

        when(moveExecutor.moveOnHomeStraight(piece, 5)).thenReturn(false);

        MovementResult result = movementService.moveOnHomeStraight(piece, 5);

        assertEquals(MovementResult.NOT_MOVED, result);
    }

    @Test
    void shouldRejectNullMoveExecutor() {
        assertThrows(IllegalArgumentException.class,
                () -> new MovementService(null, captureService, blockService, gameState));
    }

    @Test
    void shouldRejectNullCaptureService() {
        assertThrows(IllegalArgumentException.class,
                () -> new MovementService(moveExecutor, null, blockService, gameState));
    }

    @Test
    void shouldRejectNullBlockService() {
        assertThrows(IllegalArgumentException.class,
                () -> new MovementService(moveExecutor, captureService, null, gameState));
    }

    @Test
    void shouldRejectNullGameState() {
        assertThrows(IllegalArgumentException.class,
                () -> new MovementService(moveExecutor, captureService, blockService, null));
    }

    @Test
    void shouldReturnCapturedWhenMovedBlockCapturesOpponentBlock() {

        Piece firstPiece = new Piece(Colour.RED, 1);
        Piece secondPiece = new Piece(Colour.RED, 2);

        firstPiece.enterBoard(26, Direction.CLOCKWISE);
        secondPiece.enterBoard(26, Direction.CLOCKWISE);

        firstPiece.moveTo(20);
        secondPiece.moveTo(20);

        List<Piece> block = List.of(firstPiece, secondPiece);

        when(blockService.getBlockAt(20, Colour.RED)).thenReturn(block);
        when(blockService.moveBlock(20, Colour.RED, 6)).thenReturn(true);
        when(captureService.resolveBlockCapture(block, gameState)).thenReturn(true);

        MovementResult result = movementService.moveBlock(20, Colour.RED, 6);

        assertEquals(MovementResult.CAPTURED, result);
    }

    @Test
    void shouldReturnMovedWhenBlockMovesWithoutCapture() {

        Piece firstPiece = new Piece(Colour.RED, 1);
        Piece secondPiece = new Piece(Colour.RED, 2);
        List<Piece> block = List.of(firstPiece, secondPiece);

        when(blockService.getBlockAt(20, Colour.RED)).thenReturn(block);
        when(blockService.moveBlock(20, Colour.RED, 6)).thenReturn(true);
        when(captureService.resolveBlockCapture(block, gameState)).thenReturn(false);

        MovementResult result = movementService.moveBlock(20, Colour.RED, 6);

        assertEquals(MovementResult.MOVED, result);
    }

    @Test
    void shouldReturnNotMovedWhenBlockCannotMove() {

        Piece firstPiece = new Piece(Colour.RED, 1);
        Piece secondPiece = new Piece(Colour.RED, 2);
        List<Piece> block = List.of(firstPiece, secondPiece);

        when(blockService.getBlockAt(20, Colour.RED)).thenReturn(block);
        when(blockService.moveBlock(20, Colour.RED, 6)).thenReturn(false);

        MovementResult result = movementService.moveBlock(20, Colour.RED, 6);

        assertEquals(MovementResult.NOT_MOVED, result);
        verify(captureService, never()).resolveBlockCapture(anyList(), eq(gameState));
    }

    @Test
    void shouldReturnDetailedOutcomeForNormalMovement() {
        Piece piece = new Piece(Colour.RED, 1);
        piece.enterBoard(10, Direction.CLOCKWISE);

        when(blockService.getFirstOpponentBlockInPath(piece, 4)).thenReturn(List.of());
        when(blockService.getAllowedMovementDistance(piece, 4)).thenReturn(4);
        when(moveExecutor.moveOnStandardPath(piece, 4)).thenAnswer(invocation -> {
            piece.moveTo(14);
            return true;
        });
        when(captureService.getCapturablePieces(piece, gameState)).thenReturn(List.of());
        when(captureService.resolveCapture(piece, gameState)).thenReturn(false);

        MovementOutcome outcome = movementService.moveOnStandardPathDetailed(piece, 4);

        assertEquals(MovementResult.MOVED, outcome.getResult());
        assertEquals(4, outcome.getRequestedDistance());
        assertEquals(4, outcome.getActualDistance());
        assertEquals(10, outcome.getFromPosition());
        assertEquals(14, outcome.getToPosition());
        assertFalse(outcome.wasBlocked());
        assertFalse(outcome.captured());
    }

    @Test
    void shouldReturnDetailedOutcomeWhenMovementIsShortenedByBlock() {
        Piece piece = new Piece(Colour.RED, 1);
        piece.enterBoard(10, Direction.CLOCKWISE);
        Piece blueOne = new Piece(Colour.BLUE, 1);
        Piece blueTwo = new Piece(Colour.BLUE, 2);
        blueOne.enterBoard(14, Direction.CLOCKWISE);
        blueTwo.enterBoard(14, Direction.CLOCKWISE);
        List<Piece> blockingPieces = List.of(blueOne, blueTwo);

        when(blockService.getFirstOpponentBlockInPath(piece, 6)).thenReturn(blockingPieces);
        when(blockService.getAllowedMovementDistance(piece, 6)).thenReturn(3);
        when(moveExecutor.moveOnStandardPath(piece, 3)).thenAnswer(invocation -> {
            piece.moveTo(13);
            return true;
        });
        when(captureService.getCapturablePieces(piece, gameState)).thenReturn(List.of());
        when(captureService.resolveCapture(piece, gameState)).thenReturn(false);

        MovementOutcome outcome = movementService.moveOnStandardPathDetailed(piece, 6);

        assertEquals(MovementResult.MOVED, outcome.getResult());
        assertEquals(6, outcome.getRequestedDistance());
        assertEquals(3, outcome.getActualDistance());
        assertEquals(10, outcome.getFromPosition());
        assertEquals(13, outcome.getToPosition());
        assertTrue(outcome.wasBlocked());
        assertTrue(outcome.wasShortened());
        assertEquals(blockingPieces, outcome.getBlockingPieces());
        verify(moveExecutor).moveOnStandardPath(piece, 3);
        verify(moveExecutor, never()).moveOnStandardPath(piece, 6);
    }

    @Test
    void shouldReturnDetailedOutcomeWhenBlockIsImmediatelyAhead() {
        Piece piece = new Piece(Colour.RED, 1);
        piece.enterBoard(10, Direction.CLOCKWISE);
        Piece blueOne = new Piece(Colour.BLUE, 1);
        Piece blueTwo = new Piece(Colour.BLUE, 2);
        blueOne.enterBoard(11, Direction.CLOCKWISE);
        blueTwo.enterBoard(11, Direction.CLOCKWISE);
        List<Piece> blockingPieces = List.of(blueOne, blueTwo);

        when(blockService.getFirstOpponentBlockInPath(piece, 4)).thenReturn(blockingPieces);
        when(blockService.getAllowedMovementDistance(piece, 4)).thenReturn(0);

        MovementOutcome outcome = movementService.moveOnStandardPathDetailed(piece, 4);

        assertEquals(MovementResult.NOT_MOVED, outcome.getResult());
        assertEquals(0, outcome.getActualDistance());
        assertEquals(10, outcome.getFromPosition());
        assertEquals(10, outcome.getToPosition());
        assertTrue(outcome.wasBlocked());
        assertTrue(outcome.wasCompletelyBlocked());
        assertEquals(blockingPieces, outcome.getBlockingPieces());
        verifyNoInteractions(moveExecutor);
        verifyNoInteractions(captureService);
    }

    @Test
    void shouldReturnCapturedPieceInDetailedOutcome() {
        Piece attacker = new Piece(Colour.RED, 1);
        Piece opponent = new Piece(Colour.BLUE, 1);
        attacker.enterBoard(10, Direction.CLOCKWISE);
        opponent.enterBoard(14, Direction.CLOCKWISE);

        when(blockService.getFirstOpponentBlockInPath(attacker, 4)).thenReturn(List.of());
        when(blockService.getAllowedMovementDistance(attacker, 4)).thenReturn(4);
        when(moveExecutor.moveOnStandardPath(attacker, 4)).thenAnswer(invocation -> {
            attacker.moveTo(14);
            return true;
        });
        when(captureService.getCapturablePieces(attacker, gameState)).thenReturn(List.of(opponent));
        when(captureService.resolveCaptureForReporting(attacker, gameState)).thenAnswer(invocation -> {
            opponent.reset();
            attacker.recordCapture();
            return true;
        });

        MovementOutcome outcome = movementService.moveOnStandardPathDetailed(attacker, 4);

        assertEquals(MovementResult.CAPTURED, outcome.getResult());
        assertEquals(List.of(opponent), outcome.getCapturedPieces());
        assertTrue(outcome.captured());
        assertEquals(PieceState.BASE, opponent.getState());
    }
}