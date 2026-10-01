package ludo.service;

import ludo.domain.enums.ActionType;
import ludo.domain.enums.Colour;
import ludo.domain.enums.Direction;
import ludo.domain.model.GameAction;
import ludo.domain.model.Piece;
import ludo.domain.model.Player;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LegalActionGeneratorTest {

    private PieceEffectService pieceEffectService;
    private BlockService blockService;
    private LegalActionGenerator generator;
    private Player player;

    @BeforeEach
    void setUp() {
        pieceEffectService = mock(PieceEffectService.class);
        blockService = mock(BlockService.class);
        generator = new LegalActionGenerator(pieceEffectService, blockService);
        player = new Player(Colour.RED);
    }

    @Test
    void shouldGenerateNoActionsForBasePiecesWithoutSix() {
        List<GameAction> actions = generator.generateActions(player, 4);

        assertTrue(actions.isEmpty());
    }

    @Test
    void shouldGenerateEnterBoardActionsOnSix() {
        List<GameAction> actions = generator.generateActions(player, 6);

        assertEquals(4, actions.size());
        assertTrue(actions.stream().allMatch(action -> action.getType() == ActionType.ENTER_BOARD));
    }

    @Test
    void shouldGenerateMoveActionForStandardPathPiece() {
        Piece piece = player.getPieces().get(0);

        piece.enterBoard(26, Direction.CLOCKWISE);

        when(pieceEffectService.canMove(piece)).thenReturn(true);

        List<GameAction> actions = generator.generateActions(player, 4);

        assertEquals(1, actions.size());
        assertEquals(ActionType.MOVE_PIECE, actions.get(0).getType());
        assertEquals(List.of(piece), actions.get(0).getPieces());
    }

    @Test
    void shouldGenerateMoveAndEnterActionsOnSix() {
        Piece movingPiece = player.getPieces().get(0);

        movingPiece.enterBoard(26, Direction.CLOCKWISE);

        when(pieceEffectService.canMove(movingPiece)).thenReturn(true);

        List<GameAction> actions = generator.generateActions(player, 6);

        long moveActions = actions.stream()
                .filter(action -> action.getType() == ActionType.MOVE_PIECE)
                .count();

        long enterActions = actions.stream()
                .filter(action -> action.getType() == ActionType.ENTER_BOARD)
                .count();

        assertEquals(1, moveActions);
        assertEquals(3, enterActions);
    }

    @Test
    void shouldNotGenerateActionForRestrictedPiece() {
        Piece piece = player.getPieces().get(0);

        piece.enterBoard(26, Direction.CLOCKWISE);

        when(pieceEffectService.canMove(piece)).thenReturn(false);

        List<GameAction> actions = generator.generateActions(player, 4);

        assertTrue(actions.isEmpty());
    }

    @Test
    void shouldGenerateActionForHomeStraightPiece() {
        Piece piece = player.getPieces().get(0);

        piece.enterBoard(26, Direction.CLOCKWISE);
        piece.enterHomeStraight(1);

        when(pieceEffectService.canMove(piece)).thenReturn(true);

        List<GameAction> actions = generator.generateActions(player, 2);

        assertEquals(1, actions.size());
        assertEquals(ActionType.MOVE_PIECE, actions.get(0).getType());
    }

    @Test
    void shouldNotGenerateActionForPieceAlreadyHome() {
        Piece piece = player.getPieces().get(0);

        piece.enterBoard(26, Direction.CLOCKWISE);
        piece.enterHomeStraight(4);
        piece.reachHome();

        List<GameAction> actions = generator.generateActions(player, 4);

        assertTrue(actions.isEmpty());
        verifyNoInteractions(pieceEffectService);
    }

    @Test
    void shouldGenerateMoveBlockAction() {
        Piece firstPiece = player.getPieces().get(0);
        Piece secondPiece = player.getPieces().get(1);

        firstPiece.enterBoard(26, Direction.CLOCKWISE);
        secondPiece.enterBoard(26, Direction.CLOCKWISE);
        firstPiece.moveTo(20);
        secondPiece.moveTo(20);

        when(pieceEffectService.canMove(any(Piece.class))).thenReturn(true);
        when(blockService.hasBlockAt(20, Colour.RED)).thenReturn(true);
        when(blockService.getBlockAt(20, Colour.RED)).thenReturn(List.of(firstPiece, secondPiece));

        List<GameAction> actions = generator.generateActions(player, 4);

        List<GameAction> blockActions = actions.stream()
                .filter(action -> action.getType() == ActionType.MOVE_BLOCK)
                .toList();

        assertEquals(1, blockActions.size());
        assertEquals(List.of(firstPiece, secondPiece), blockActions.get(0).getPieces());
    }

    @Test
    void shouldGenerateBlockActionOnlyOnce() {
        Piece firstPiece = player.getPieces().get(0);
        Piece secondPiece = player.getPieces().get(1);

        firstPiece.enterBoard(26, Direction.CLOCKWISE);
        secondPiece.enterBoard(26, Direction.CLOCKWISE);
        firstPiece.moveTo(20);
        secondPiece.moveTo(20);

        when(pieceEffectService.canMove(any(Piece.class))).thenReturn(true);
        when(blockService.hasBlockAt(20, Colour.RED)).thenReturn(true);
        when(blockService.getBlockAt(20, Colour.RED)).thenReturn(List.of(firstPiece, secondPiece));

        long blockActions = generator.generateActions(player, 4).stream()
                .filter(action -> action.getType() == ActionType.MOVE_BLOCK)
                .count();

        assertEquals(1, blockActions);
    }

    @Test
    void shouldNotGenerateBlockActionForSinglePiece() {
        Piece piece = player.getPieces().get(0);

        piece.enterBoard(26, Direction.CLOCKWISE);
        piece.moveTo(20);

        when(pieceEffectService.canMove(piece)).thenReturn(true);
        when(blockService.hasBlockAt(20, Colour.RED)).thenReturn(false);

        List<GameAction> actions = generator.generateActions(player, 4);

        assertTrue(actions.stream().noneMatch(action -> action.getType() == ActionType.MOVE_BLOCK));
    }

    @Test
    void shouldGenerateBlockActionForMoreThanTwoPieces() {
        Piece firstPiece = player.getPieces().get(0);
        Piece secondPiece = player.getPieces().get(1);
        Piece thirdPiece = player.getPieces().get(2);

        for (Piece piece : List.of(firstPiece, secondPiece, thirdPiece)) {
            piece.enterBoard(26, Direction.CLOCKWISE);
            piece.moveTo(20);
        }

        when(pieceEffectService.canMove(any(Piece.class))).thenReturn(true);
        when(blockService.hasBlockAt(20, Colour.RED)).thenReturn(true);
        when(blockService.getBlockAt(20, Colour.RED)).thenReturn(List.of(firstPiece, secondPiece, thirdPiece));

        GameAction blockAction = generator.generateActions(player, 6).stream()
                .filter(action -> action.getType() == ActionType.MOVE_BLOCK)
                .findFirst()
                .orElseThrow();

        assertEquals(3, blockAction.getPieces().size());
    }

    @Test
    void shouldNotGenerateBlockActionWhenMemberCannotMove() {
        Piece firstPiece = player.getPieces().get(0);
        Piece secondPiece = player.getPieces().get(1);

        firstPiece.enterBoard(26, Direction.CLOCKWISE);
        secondPiece.enterBoard(26, Direction.CLOCKWISE);
        firstPiece.moveTo(20);
        secondPiece.moveTo(20);

        when(blockService.hasBlockAt(20, Colour.RED)).thenReturn(true);
        when(blockService.getBlockAt(20, Colour.RED)).thenReturn(List.of(firstPiece, secondPiece));
        when(pieceEffectService.canMove(firstPiece)).thenReturn(true);
        when(pieceEffectService.canMove(secondPiece)).thenReturn(false);

        List<GameAction> actions = generator.generateActions(player, 4);

        assertTrue(actions.stream().noneMatch(action -> action.getType() == ActionType.MOVE_BLOCK));
    }

    @Test
    void shouldReturnUnmodifiableActions() {
        List<GameAction> actions = generator.generateActions(player, 6);

        assertThrows(UnsupportedOperationException.class, () -> actions.add(actions.get(0)));
    }

    @Test
    void shouldRejectNullPlayer() {
        assertThrows(IllegalArgumentException.class, () -> generator.generateActions(null, 4));
    }

    @Test
    void shouldRejectInvalidRoll() {
        assertThrows(IllegalArgumentException.class, () -> generator.generateActions(player, 7));
    }

    @Test
    void shouldRejectNullPieceEffectService() {
        assertThrows(IllegalArgumentException.class, () -> new LegalActionGenerator(null, blockService));
    }

    @Test
    void shouldRejectNullBlockService() {
        assertThrows(IllegalArgumentException.class, () -> new LegalActionGenerator(pieceEffectService, null));
    }
}