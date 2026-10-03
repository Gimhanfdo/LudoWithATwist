package ludo.service;

import ludo.domain.enums.Colour;
import ludo.domain.enums.Direction;
import ludo.domain.model.MovementResult;
import ludo.domain.model.Piece;
import ludo.domain.model.MovementOutcome;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class MovementCoordinatorTest {

    private MovementService movementService;
    private PieceEffectService pieceEffectService;
    private MysteryLandingService mysteryLandingService;
    private MovementCoordinator coordinator;

    @BeforeEach
    void setUp() {
        movementService = mock(MovementService.class);
        pieceEffectService = mock(PieceEffectService.class);
        mysteryLandingService = mock(MysteryLandingService.class);

        coordinator = new MovementCoordinator(movementService, pieceEffectService, mysteryLandingService);
    }

    private Piece createPiece() {
        Piece piece = new Piece(Colour.RED, 1);

        piece.enterBoard(26, Direction.CLOCKWISE);

        return piece;
    }

    private Piece createHomeStraightPiece() {
        Piece piece = createPiece();
        piece.recordCapture();
        piece.enterHomeStraight(0);

        return piece;
    }

    @Test
    void shouldMoveUsingCalculatedMovementDistance() {
        Piece piece = createPiece();

        when(pieceEffectService.canMove(piece)).thenReturn(true);
        when(pieceEffectService.calculateMovement(piece, 4)).thenReturn(4);
        when(movementService.moveOnStandardPath(piece, 4)).thenReturn(MovementResult.MOVED);

        MovementResult result = coordinator.move(piece, 4);

        assertEquals(MovementResult.MOVED, result);
        verify(movementService).moveOnStandardPath(piece, 4);
    }

    @Test
    void shouldUseModifiedMovementDistance() {
        Piece piece = createPiece();

        when(pieceEffectService.canMove(piece)).thenReturn(true);
        when(pieceEffectService.calculateMovement(piece, 4)).thenReturn(8);
        when(movementService.moveOnStandardPath(piece, 8)).thenReturn(MovementResult.MOVED);

        coordinator.move(piece, 4);

        verify(movementService).moveOnStandardPath(piece, 8);
    }

    @Test
    void shouldNotMoveBriefingPiece() {
        Piece piece = createPiece();

        when(pieceEffectService.canMove(piece)).thenReturn(false);

        MovementResult result = coordinator.move(piece, 4);

        assertEquals(MovementResult.NOT_MOVED, result);
        verify(pieceEffectService, never()).calculateMovement(any(), anyInt());
        verifyNoInteractions(movementService, mysteryLandingService);
    }

    @Test
    void shouldNotMoveWhenEffectProducesZeroMovement() {
        Piece piece = createPiece();

        when(pieceEffectService.canMove(piece)).thenReturn(true);
        when(pieceEffectService.calculateMovement(piece, 1)).thenReturn(0);

        MovementResult result = coordinator.move(piece, 1);

        assertEquals(MovementResult.NOT_MOVED, result);
        verifyNoInteractions(movementService, mysteryLandingService);
    }

    @Test
    void shouldResolveMysteryLandingAfterSuccessfulMovement() {
        Piece piece = createPiece();

        when(pieceEffectService.canMove(piece)).thenReturn(true);
        when(pieceEffectService.calculateMovement(piece, 4)).thenReturn(4);
        when(movementService.moveOnStandardPath(piece, 4)).thenReturn(MovementResult.MOVED);

        coordinator.move(piece, 4);

        verify(mysteryLandingService).resolveLanding(piece);
    }

    @Test
    void shouldResolveMysteryLandingAfterCapture() {
        Piece piece = createPiece();

        when(pieceEffectService.canMove(piece)).thenReturn(true);
        when(pieceEffectService.calculateMovement(piece, 4)).thenReturn(4);
        when(movementService.moveOnStandardPath(piece, 4)).thenReturn(MovementResult.CAPTURED);

        MovementResult result = coordinator.move(piece, 4);

        assertEquals(MovementResult.CAPTURED, result);
        verify(mysteryLandingService).resolveLanding(piece);
    }

    @Test
    void shouldNotResolveMysteryLandingWhenMovementFails() {
        Piece piece = createPiece();

        when(pieceEffectService.canMove(piece)).thenReturn(true);
        when(pieceEffectService.calculateMovement(piece, 4)).thenReturn(4);
        when(movementService.moveOnStandardPath(piece, 4)).thenReturn(MovementResult.NOT_MOVED);

        coordinator.move(piece, 4);

        verify(mysteryLandingService, never()).resolveLanding(any());
    }

    @Test
    void shouldMoveHomeStraightPieceUsingHomeStraightMovement() {
        Piece piece = createHomeStraightPiece();

        when(pieceEffectService.canMove(piece)).thenReturn(true);
        when(pieceEffectService.calculateMovement(piece, 3)).thenReturn(3);
        when(movementService.moveOnHomeStraight(piece, 3)).thenReturn(MovementResult.MOVED);

        MovementResult result = coordinator.move(piece, 3);

        assertEquals(MovementResult.MOVED, result);
        verify(movementService).moveOnHomeStraight(piece, 3);
        verify(movementService, never()).moveOnStandardPath(any(), anyInt());
    }

    @Test
    void shouldUseModifiedMovementDistanceOnHomeStraight() {
        Piece piece = createHomeStraightPiece();

        when(pieceEffectService.canMove(piece)).thenReturn(true);
        when(pieceEffectService.calculateMovement(piece, 2)).thenReturn(4);
        when(movementService.moveOnHomeStraight(piece, 4)).thenReturn(MovementResult.MOVED);

        coordinator.move(piece, 2);

        verify(movementService).moveOnHomeStraight(piece, 4);
    }

    @Test
    void shouldNotResolveMysteryLandingAfterHomeStraightMovement() {
        Piece piece = createHomeStraightPiece();

        when(pieceEffectService.canMove(piece)).thenReturn(true);
        when(pieceEffectService.calculateMovement(piece, 3)).thenReturn(3);
        when(movementService.moveOnHomeStraight(piece, 3)).thenReturn(MovementResult.MOVED);

        coordinator.move(piece, 3);

        verify(mysteryLandingService, never()).resolveLanding(any());
    }

    @Test
    void shouldRejectNullPiece() {
        assertThrows(IllegalArgumentException.class, () -> coordinator.move(null, 4));
    }

    @Test
    void shouldRejectInvalidRoll() {
        Piece piece = createPiece();

        assertThrows(IllegalArgumentException.class, () -> coordinator.move(piece, 7));
    }

    @Test
    void shouldRejectNullMovementService() {
        assertThrows(IllegalArgumentException.class,
                () -> new MovementCoordinator(null, pieceEffectService, mysteryLandingService));
    }

    @Test
    void shouldRejectNullPieceEffectService() {
        assertThrows(IllegalArgumentException.class,
                () -> new MovementCoordinator(movementService, null, mysteryLandingService));
    }

    @Test
    void shouldRejectNullMysteryLandingService() {
        assertThrows(IllegalArgumentException.class,
                () -> new MovementCoordinator(movementService, pieceEffectService, null));
    }

    @Test
    void shouldReturnDetailedStandardPathOutcome() {
        Piece piece = createPiece();
        MovementOutcome expected = new MovementOutcome(MovementResult.MOVED, 4, 4, 26, 30, List.of(), List.of());

        when(pieceEffectService.canMove(piece)).thenReturn(true);
        when(pieceEffectService.calculateMovement(piece, 4)).thenReturn(4);
        when(movementService.moveOnStandardPathDetailed(piece, 4)).thenReturn(expected);

        MovementOutcome actual = coordinator.moveDetailed(piece, 4);

        assertSame(expected, actual);
        verify(movementService).moveOnStandardPathDetailed(piece, 4);
    }

    @Test
    void shouldUseModifiedDistanceInDetailedMovement() {
        Piece piece = createPiece();
        MovementOutcome expected = new MovementOutcome(MovementResult.MOVED, 8, 8, 26, 34, List.of(), List.of());

        when(pieceEffectService.canMove(piece)).thenReturn(true);
        when(pieceEffectService.calculateMovement(piece, 4)).thenReturn(8);
        when(movementService.moveOnStandardPathDetailed(piece, 8)).thenReturn(expected);

        MovementOutcome actual = coordinator.moveDetailed(piece, 4);

        assertEquals(8, actual.getRequestedDistance());
        verify(movementService).moveOnStandardPathDetailed(piece, 8);
        verify(movementService, never()).moveOnStandardPathDetailed(piece, 4);
    }

    @Test
    void shouldReturnDetailedNotMovedWhenEffectPreventsMovement() {
        Piece piece = createPiece();

        when(pieceEffectService.canMove(piece)).thenReturn(false);

        MovementOutcome outcome = coordinator.moveDetailed(piece, 4);

        assertEquals(MovementResult.NOT_MOVED, outcome.getResult());
        assertEquals(0, outcome.getActualDistance());
        assertEquals(26, outcome.getFromPosition());
        assertEquals(26, outcome.getToPosition());
        verify(pieceEffectService, never()).calculateMovement(any(), anyInt());
        verifyNoInteractions(movementService, mysteryLandingService);
    }

    @Test
    void shouldReturnDetailedNotMovedWhenEffectProducesZeroMovement() {
        Piece piece = createPiece();

        when(pieceEffectService.canMove(piece)).thenReturn(true);
        when(pieceEffectService.calculateMovement(piece, 1)).thenReturn(0);

        MovementOutcome outcome = coordinator.moveDetailed(piece, 1);

        assertEquals(MovementResult.NOT_MOVED, outcome.getResult());
        assertEquals(0, outcome.getActualDistance());
        assertEquals(26, outcome.getFromPosition());
        assertEquals(26, outcome.getToPosition());
        verifyNoInteractions(movementService, mysteryLandingService);
    }

    @Test
    void shouldResolveMysteryLandingAfterDetailedMovement() {
        Piece piece = createPiece();
        MovementOutcome expected = new MovementOutcome(MovementResult.MOVED, 4, 4, 26, 30, List.of(), List.of());

        when(pieceEffectService.canMove(piece)).thenReturn(true);
        when(pieceEffectService.calculateMovement(piece, 4)).thenReturn(4);
        when(movementService.moveOnStandardPathDetailed(piece, 4)).thenReturn(expected);

        coordinator.moveDetailed(piece, 4);

        verify(mysteryLandingService).resolveLanding(piece);
    }
}