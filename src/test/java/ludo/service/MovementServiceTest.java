package ludo.service;

import ludo.domain.enums.Colour;
import ludo.domain.enums.Direction;
import ludo.domain.model.GameState;
import ludo.domain.model.Piece;
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

        boolean moved = movementService.moveOnStandardPath(piece, 4);

        assertTrue(moved);

        verify(captureService).resolveCapture(piece, gameState);
    }

    @Test
    void shouldNotResolveCaptureWhenMovementFails() {

        Piece piece = new Piece(Colour.RED, 1);
        piece.enterBoard(26, Direction.CLOCKWISE);

        when(moveExecutor.moveOnStandardPath(piece, 4)).thenReturn(false);

        boolean moved = movementService.moveOnStandardPath(piece, 4);

        assertFalse(moved);
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

        boolean moved = movementService.moveOnStandardPath(piece, 5);

        assertTrue(moved);
        verifyNoInteractions(captureService);
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