package ludo.service;

import ludo.domain.enums.ActionType;
import ludo.domain.enums.Colour;
import ludo.domain.enums.Direction;
import ludo.domain.model.GameAction;
import ludo.domain.model.Piece;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class MoveValidatorTest {

    private PieceEffectService pieceEffectService;
    private BlockService blockService;
    private MoveValidator validator;

    @BeforeEach
    void setUp() {
        pieceEffectService = mock(PieceEffectService.class);
        blockService = mock(BlockService.class);
        validator = new MoveValidator(pieceEffectService, blockService);
    }

    private Piece createStandardPiece() {
        Piece piece = new Piece(Colour.RED, 1);

        piece.enterBoard(26, Direction.CLOCKWISE);

        return piece;
    }

    @Test
    void shouldAllowStandardPathMovement() {
        Piece piece = createStandardPiece();
        GameAction action = new GameAction(ActionType.MOVE_PIECE, List.of(piece), 4);

        when(pieceEffectService.canMove(piece)).thenReturn(true);
        when(pieceEffectService.calculateMovement(piece, 4)).thenReturn(4);
        when(blockService.getAllowedMovementDistance(piece, 4)).thenReturn(4);

        assertTrue(validator.isValid(action));
    }

    @Test
    void shouldRejectMovementWhenBlockPreventsAnyMovement() {
        Piece piece = createStandardPiece();
        GameAction action = new GameAction(ActionType.MOVE_PIECE, List.of(piece), 4);

        when(pieceEffectService.canMove(piece)).thenReturn(true);
        when(pieceEffectService.calculateMovement(piece, 4)).thenReturn(4);
        when(blockService.getAllowedMovementDistance(piece, 4)).thenReturn(0);

        assertFalse(validator.isValid(action));
    }

    @Test
    void shouldAllowMovementShortenedByOpponentBlock() {
        Piece piece = createStandardPiece();
        GameAction action = new GameAction(ActionType.MOVE_PIECE, List.of(piece), 6);

        when(pieceEffectService.canMove(piece)).thenReturn(true);
        when(pieceEffectService.calculateMovement(piece, 6)).thenReturn(6);
        when(blockService.getAllowedMovementDistance(piece, 6)).thenReturn(3);

        assertTrue(validator.isValid(action));
    }

    @Test
    void shouldRejectMovementWhenPieceCannotMove() {
        Piece piece = createStandardPiece();
        GameAction action = new GameAction(ActionType.MOVE_PIECE, List.of(piece), 4);

        when(pieceEffectService.canMove(piece)).thenReturn(false);

        assertFalse(validator.isValid(action));
        verify(pieceEffectService, never()).calculateMovement(any(), anyInt());
        verifyNoInteractions(blockService);
    }

    @Test
    void shouldRejectZeroEffectiveMovement() {
        Piece piece = createStandardPiece();
        GameAction action = new GameAction(ActionType.MOVE_PIECE, List.of(piece), 1);

        when(pieceEffectService.canMove(piece)).thenReturn(true);
        when(pieceEffectService.calculateMovement(piece, 1)).thenReturn(0);

        assertFalse(validator.isValid(action));
        verifyNoInteractions(blockService);
    }

    @Test
    void shouldAllowExactMovementToHome() {
        Piece piece = createStandardPiece();

        piece.enterHomeStraight(3);

        GameAction action = new GameAction(ActionType.MOVE_PIECE, List.of(piece), 2);

        when(pieceEffectService.canMove(piece)).thenReturn(true);
        when(pieceEffectService.calculateMovement(piece, 2)).thenReturn(2);

        assertTrue(validator.isValid(action));
        verifyNoInteractions(blockService);
    }

    @Test
    void shouldRejectMovementBeyondHome() {
        Piece piece = createStandardPiece();

        piece.enterHomeStraight(3);

        GameAction action = new GameAction(ActionType.MOVE_PIECE, List.of(piece), 3);

        when(pieceEffectService.canMove(piece)).thenReturn(true);
        when(pieceEffectService.calculateMovement(piece, 3)).thenReturn(3);

        assertFalse(validator.isValid(action));
        verifyNoInteractions(blockService);
    }

    @Test
    void shouldRejectMovePieceActionWithMultiplePieces() {
        Piece firstPiece = createStandardPiece();
        Piece secondPiece = new Piece(Colour.RED, 2);

        secondPiece.enterBoard(26, Direction.CLOCKWISE);

        GameAction action = new GameAction(ActionType.MOVE_PIECE, List.of(firstPiece, secondPiece), 4);

        assertFalse(validator.isValid(action));
    }

    @Test
    void shouldRejectMoveBlockActionWithSinglePiece() {
        Piece piece = createStandardPiece();
        GameAction action = new GameAction(ActionType.MOVE_BLOCK, List.of(piece), 4);

        assertFalse(validator.isValid(action));
    }

    @Test
    void shouldAllowMovableBlock() {
        Piece firstPiece = createStandardPiece();
        Piece secondPiece = new Piece(Colour.RED, 2);

        secondPiece.enterBoard(26, Direction.CLOCKWISE);

        GameAction action = new GameAction(ActionType.MOVE_BLOCK, List.of(firstPiece, secondPiece), 4);

        when(pieceEffectService.canMove(firstPiece)).thenReturn(true);
        when(pieceEffectService.canMove(secondPiece)).thenReturn(true);

        assertTrue(validator.isValid(action));
    }

    @Test
    void shouldRejectNullAction() {
        assertThrows(IllegalArgumentException.class, () -> validator.isValid(null));
    }

    @Test
    void shouldRejectNullPieceEffectService() {
        assertThrows(IllegalArgumentException.class, () -> new MoveValidator(null, blockService));
    }

    @Test
    void shouldRejectNullBlockService() {
        assertThrows(IllegalArgumentException.class, () -> new MoveValidator(pieceEffectService, null));
    }
}