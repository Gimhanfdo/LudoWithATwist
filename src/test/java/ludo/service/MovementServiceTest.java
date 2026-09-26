package ludo.service;

import ludo.domain.enums.Colour;
import ludo.domain.enums.Direction;
import ludo.domain.model.GameState;
import ludo.domain.model.Piece;
import ludo.domain.model.MovementResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
}