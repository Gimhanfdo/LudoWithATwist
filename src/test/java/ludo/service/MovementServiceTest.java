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

    @BeforeEach
    void setUp() {

        moveExecutor = mock(MoveExecutor.class);
        captureService = mock(CaptureService.class);
        gameState = mock(GameState.class);

        movementService = new MovementService(moveExecutor, captureService, gameState);
    }

    @Test
    void shouldResolveCaptureAfterSuccessfulStandardPathMovement() {

        Piece piece = new Piece(Colour.RED, 1);

        piece.enterBoard(26, Direction.CLOCKWISE);

        when(
                moveExecutor.moveOnStandardPath(piece, 4))
                .thenAnswer(invocation -> {
                    piece.moveTo(30);
                    return true;
                });

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
    void shouldRejectNullMoveExecutor() {
        assertThrows(IllegalArgumentException.class,
                () -> new MovementService(null, captureService, gameState));
    }

    @Test
    void shouldRejectNullCaptureService() {
        assertThrows(IllegalArgumentException.class,
                () -> new MovementService(moveExecutor, null, gameState));
    }

    @Test
    void shouldRejectNullGameState() {
        assertThrows(IllegalArgumentException.class,
                () -> new MovementService(moveExecutor, captureService, null));
    }
}