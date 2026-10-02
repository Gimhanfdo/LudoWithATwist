package ludo.service;

import ludo.domain.enums.ActionType;
import ludo.domain.enums.Colour;
import ludo.domain.enums.Direction;
import ludo.domain.model.ActionAnalysis;
import ludo.domain.model.GameAction;
import ludo.domain.model.GameState;
import ludo.domain.model.MysteryCell;
import ludo.domain.model.Piece;
import ludo.domain.model.Player;
import ludo.domain.model.Board;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ActionAnalyzerTest {

    private Player redPlayer;
    private Player bluePlayer;
    private GameState gameState;
    private Board board;
    private MysteryCell mysteryCell;

    private PieceEffectService pieceEffectService;
    private BlockService blockService;
    private MoveDestinationCalculator destinationCalculator;

    private ActionAnalyzer analyzer;

    @BeforeEach
    void setUp() {
        redPlayer = new Player(Colour.RED);
        bluePlayer = new Player(Colour.BLUE);
        gameState = new GameState(List.of(redPlayer, bluePlayer));
        board = new Board();
        mysteryCell = new MysteryCell();

        pieceEffectService = mock(PieceEffectService.class);
        blockService = mock(BlockService.class);
        destinationCalculator = new MoveDestinationCalculator();

        analyzer = new ActionAnalyzer(gameState, pieceEffectService, blockService, destinationCalculator, board,
                mysteryCell);
    }

    @Test
    void shouldIdentifyCaptureOpportunity() {
        Piece attacker = redPlayer.getPieces().get(0);
        Piece opponent = bluePlayer.getPieces().get(0);

        attacker.enterBoard(26, Direction.CLOCKWISE);
        attacker.moveTo(18);
        opponent.enterBoard(13, Direction.CLOCKWISE);
        opponent.moveTo(20);

        GameAction action = new GameAction(ActionType.MOVE_PIECE, List.of(attacker), 2);

        when(pieceEffectService.calculateMovement(attacker, 2)).thenReturn(2);
        when(blockService.getAllowedMovementDistance(attacker, 2)).thenReturn(2);

        ActionAnalysis analysis = analyzer.analyze(action);

        assertTrue(analysis.capturesOpponent());
        assertFalse(analysis.createsBlock());
    }

    @Test
    void shouldIdentifyBlockCreation() {
        Piece movingPiece = redPlayer.getPieces().get(0);
        Piece friendlyPiece = redPlayer.getPieces().get(1);

        movingPiece.enterBoard(26, Direction.CLOCKWISE);
        movingPiece.moveTo(18);
        friendlyPiece.enterBoard(26, Direction.CLOCKWISE);
        friendlyPiece.moveTo(20);

        GameAction action = new GameAction(ActionType.MOVE_PIECE, List.of(movingPiece), 2);

        when(pieceEffectService.calculateMovement(movingPiece, 2)).thenReturn(2);
        when(blockService.getAllowedMovementDistance(movingPiece, 2)).thenReturn(2);

        ActionAnalysis analysis = analyzer.analyze(action);

        assertFalse(analysis.capturesOpponent());
        assertTrue(analysis.createsBlock());
    }

    @Test
    void shouldIdentifyOrdinaryMovement() {
        Piece piece = redPlayer.getPieces().get(0);

        piece.enterBoard(26, Direction.CLOCKWISE);
        piece.moveTo(18);

        GameAction action = new GameAction(ActionType.MOVE_PIECE, List.of(piece), 2);

        when(pieceEffectService.calculateMovement(piece, 2)).thenReturn(2);
        when(blockService.getAllowedMovementDistance(piece, 2)).thenReturn(2);

        ActionAnalysis analysis = analyzer.analyze(action);

        assertFalse(analysis.capturesOpponent());
        assertFalse(analysis.createsBlock());
    }

    @Test
    void shouldNotIdentifyOpponentBlockAsIndividualCapture() {
        Piece attacker = redPlayer.getPieces().get(0);
        Piece firstOpponent = bluePlayer.getPieces().get(0);
        Piece secondOpponent = bluePlayer.getPieces().get(1);

        attacker.enterBoard(26, Direction.CLOCKWISE);
        attacker.moveTo(18);
        firstOpponent.enterBoard(13, Direction.CLOCKWISE);
        secondOpponent.enterBoard(13, Direction.CLOCKWISE);
        firstOpponent.moveTo(20);
        secondOpponent.moveTo(20);

        GameAction action = new GameAction(ActionType.MOVE_PIECE, List.of(attacker), 2);

        when(pieceEffectService.calculateMovement(attacker, 2)).thenReturn(2);
        when(blockService.getAllowedMovementDistance(attacker, 2)).thenReturn(2);

        ActionAnalysis analysis = analyzer.analyze(action);

        assertFalse(analysis.capturesOpponent());
    }

    @Test
    void shouldReturnNeutralAnalysisForEnterBoardAction() {
        Piece piece = redPlayer.getPieces().get(0);
        GameAction action = new GameAction(ActionType.ENTER_BOARD, List.of(piece), 6);

        ActionAnalysis analysis = analyzer.analyze(action);

        assertFalse(analysis.capturesOpponent());
        assertFalse(analysis.createsBlock());
        verifyNoInteractions(pieceEffectService, blockService);
    }

    @Test
    void shouldCalculateRemainingDistanceToHome() {
        Piece piece = redPlayer.getPieces().get(0);

        piece.enterBoard(26, Direction.CLOCKWISE);
        piece.moveTo(20);

        GameAction action = new GameAction(ActionType.MOVE_PIECE, List.of(piece), 2);

        when(pieceEffectService.calculateMovement(piece, 2)).thenReturn(2);
        when(blockService.getAllowedMovementDistance(piece, 2)).thenReturn(2);

        ActionAnalysis analysis = analyzer.analyze(action);

        int expectedDistance = board.getDistanceToHome(22, Colour.RED, Direction.CLOCKWISE);

        assertEquals(expectedDistance, analysis.getDistanceToHome());
    }

    @Test
    void shouldUseEffectAdjustedMovementForProgress() {
        Piece piece = redPlayer.getPieces().get(0);

        piece.enterBoard(26, Direction.CLOCKWISE);
        piece.moveTo(18);

        GameAction action = new GameAction(ActionType.MOVE_PIECE, List.of(piece), 2);

        when(pieceEffectService.calculateMovement(piece, 2)).thenReturn(4);
        when(blockService.getAllowedMovementDistance(piece, 4)).thenReturn(4);

        ActionAnalysis analysis = analyzer.analyze(action);

        int expectedDistance = board.getDistanceToHome(22, Colour.RED, Direction.CLOCKWISE);

        assertEquals(expectedDistance, analysis.getDistanceToHome());
    }

    @Test
    void shouldUseBlockRestrictedMovementForProgress() {
        Piece piece = redPlayer.getPieces().get(0);

        piece.enterBoard(26, Direction.CLOCKWISE);
        piece.moveTo(18);

        GameAction action = new GameAction(ActionType.MOVE_PIECE, List.of(piece), 6);

        when(pieceEffectService.calculateMovement(piece, 6)).thenReturn(6);
        when(blockService.getAllowedMovementDistance(piece, 6)).thenReturn(3);

        ActionAnalysis analysis = analyzer.analyze(action);

        int expectedDistance = board.getDistanceToHome(21, Colour.RED, Direction.CLOCKWISE);

        assertEquals(expectedDistance, analysis.getDistanceToHome());
    }

    @Test
    void shouldReturnNeutralAnalysisWhenMovementPassesApproach() {
        Piece piece = redPlayer.getPieces().get(0);

        piece.enterBoard(26, Direction.CLOCKWISE);
        piece.moveTo(24);

        GameAction action = new GameAction(ActionType.MOVE_PIECE, List.of(piece), 4);

        when(pieceEffectService.calculateMovement(piece, 4)).thenReturn(4);
        when(blockService.getAllowedMovementDistance(piece, 4)).thenReturn(4);

        ActionAnalysis analysis = analyzer.analyze(action);

        assertFalse(analysis.capturesOpponent());
        assertFalse(analysis.createsBlock());
        assertEquals(0, analysis.getDistanceToHome());
    }

    @Test
    void shouldIdentifyCapturedOpponent() {
        Piece attacker = redPlayer.getPieces().get(0);
        Piece opponent = bluePlayer.getPieces().get(0);

        attacker.enterBoard(26, Direction.CLOCKWISE);
        attacker.moveTo(18);
        opponent.enterBoard(13, Direction.CLOCKWISE);
        opponent.moveTo(20);

        GameAction action = new GameAction(ActionType.MOVE_PIECE, List.of(attacker), 2);

        when(pieceEffectService.calculateMovement(attacker, 2)).thenReturn(2);
        when(blockService.getAllowedMovementDistance(attacker, 2)).thenReturn(2);

        ActionAnalysis analysis = analyzer.analyze(action);

        assertTrue(analysis.capturesOpponent());
        assertSame(opponent, analysis.getCapturedPiece());
    }

    @Test
    void shouldAnalyzeHomeStraightProgress() {
        Piece piece = redPlayer.getPieces().get(0);

        piece.enterBoard(26, Direction.CLOCKWISE);
        piece.enterHomeStraight(1);

        GameAction action = new GameAction(ActionType.MOVE_PIECE, List.of(piece), 2);

        when(pieceEffectService.calculateMovement(piece, 2)).thenReturn(2);

        ActionAnalysis analysis = analyzer.analyze(action);

        assertTrue(analysis.isProgressKnown());
        assertEquals(2, analysis.getDistanceToHome());
        assertFalse(analysis.capturesOpponent());
        assertFalse(analysis.createsBlock());
        verifyNoInteractions(blockService);
    }

    @Test
    void shouldRecognizeExactMoveToHome() {
        Piece piece = redPlayer.getPieces().get(0);

        piece.enterBoard(26, Direction.CLOCKWISE);
        piece.enterHomeStraight(3);

        GameAction action = new GameAction(ActionType.MOVE_PIECE, List.of(piece), 2);

        when(pieceEffectService.calculateMovement(piece, 2)).thenReturn(2);

        ActionAnalysis analysis = analyzer.analyze(action);

        assertTrue(analysis.isProgressKnown());
        assertEquals(0, analysis.getDistanceToHome());
    }

    @Test
    void shouldReturnUnknownProgressForHomeOvershoot() {
        Piece piece = redPlayer.getPieces().get(0);

        piece.enterBoard(26, Direction.CLOCKWISE);
        piece.enterHomeStraight(4);

        GameAction action = new GameAction(ActionType.MOVE_PIECE, List.of(piece), 2);

        when(pieceEffectService.calculateMovement(piece, 2)).thenReturn(2);

        ActionAnalysis analysis = analyzer.analyze(action);

        assertFalse(analysis.isProgressKnown());
    }

    @Test
    void shouldIdentifyLandingOnMysteryCell() {
        Piece piece = bluePlayer.getPieces().get(0);

        piece.enterBoard(13, Direction.COUNTERCLOCKWISE);
        piece.moveTo(20);
        mysteryCell.activate(17);

        GameAction action = new GameAction(ActionType.MOVE_PIECE, List.of(piece), 3);

        when(pieceEffectService.calculateMovement(piece, 3)).thenReturn(3);
        when(blockService.getAllowedMovementDistance(piece, 3)).thenReturn(3);

        ActionAnalysis analysis = analyzer.analyze(action);

        assertTrue(analysis.landsOnMystery());
    }

    @Test
    void shouldNotIdentifyDifferentCellAsMystery() {
        Piece piece = bluePlayer.getPieces().get(0);

        piece.enterBoard(13, Direction.COUNTERCLOCKWISE);
        mysteryCell.activate(9);

        GameAction action = new GameAction(ActionType.MOVE_PIECE, List.of(piece), 3);

        when(pieceEffectService.calculateMovement(piece, 3)).thenReturn(3);
        when(blockService.getAllowedMovementDistance(piece, 3)).thenReturn(3);

        ActionAnalysis analysis = analyzer.analyze(action);

        assertFalse(analysis.landsOnMystery());
    }
}