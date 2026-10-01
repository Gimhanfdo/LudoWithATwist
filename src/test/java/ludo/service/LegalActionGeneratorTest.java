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
import static org.mockito.Mockito.*;

class LegalActionGeneratorTest {

    private PieceEffectService pieceEffectService;
    private LegalActionGenerator generator;
    private Player player;

    @BeforeEach
    void setUp() {
        pieceEffectService = mock(PieceEffectService.class);
        generator = new LegalActionGenerator(pieceEffectService);
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
        assertThrows(IllegalArgumentException.class, () -> new LegalActionGenerator(null));
    }
}