package ludo.service;

import ludo.domain.enums.Colour;
import ludo.domain.enums.Direction;
import ludo.domain.model.GameState;
import ludo.domain.model.Piece;
import ludo.domain.model.Player;
import ludo.domain.model.Board;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BlockServiceTest {

    private Player redPlayer;
    private Player bluePlayer;
    private Board board;
    private GameState gameState;
    private BlockService blockService;

    @BeforeEach
    void setUp() {
        redPlayer = new Player(Colour.RED);
        bluePlayer = new Player(Colour.BLUE);

        gameState = new GameState(List.of(redPlayer, bluePlayer));
        board = new Board();
        blockService = new BlockService(gameState, board);
    }

    @Test
    void shouldDetectBlockWhenTwoSameColourPiecesOccupyPosition() {

        Piece firstPiece = redPlayer.getPieces().get(0);
        Piece secondPiece = redPlayer.getPieces().get(1);

        firstPiece.enterBoard(26, Direction.CLOCKWISE);
        secondPiece.enterBoard(26, Direction.COUNTERCLOCKWISE);

        firstPiece.moveTo(20);
        secondPiece.moveTo(20);

        assertTrue(blockService.hasBlockAt(20, Colour.RED));
    }

    @Test
    void shouldNotDetectBlockForSinglePiece() {

        Piece redPiece = redPlayer.getPieces().get(0);

        redPiece.enterBoard(26, Direction.CLOCKWISE);
        redPiece.moveTo(20);

        assertFalse(blockService.hasBlockAt(20, Colour.RED));
    }

    @Test
    void shouldReturnPiecesParticipatingInBlock() {

        Piece firstPiece = redPlayer.getPieces().get(0);
        Piece secondPiece = redPlayer.getPieces().get(1);

        firstPiece.enterBoard(26, Direction.CLOCKWISE);
        secondPiece.enterBoard(26, Direction.COUNTERCLOCKWISE);

        firstPiece.moveTo(20);
        secondPiece.moveTo(20);

        List<Piece> block = blockService.getBlockAt(20, Colour.RED);

        assertEquals(2, block.size());
        assertTrue(block.contains(firstPiece));
        assertTrue(block.contains(secondPiece));
    }

    @Test
    void shouldDetectBlockContainingMoreThanTwoPieces() {
        Piece firstPiece = redPlayer.getPieces().get(0);
        Piece secondPiece = redPlayer.getPieces().get(1);
        Piece thirdPiece = redPlayer.getPieces().get(2);

        firstPiece.enterBoard(26, Direction.CLOCKWISE);
        secondPiece.enterBoard(26, Direction.COUNTERCLOCKWISE);
        thirdPiece.enterBoard(26, Direction.CLOCKWISE);

        firstPiece.moveTo(20);
        secondPiece.moveTo(20);
        thirdPiece.moveTo(20);

        List<Piece> block = blockService.getBlockAt(20, Colour.RED);

        assertEquals(3, block.size());
        assertTrue(blockService.hasBlockAt(20, Colour.RED));
    }

    @Test
    void shouldNotDetectBlockWhenSameColourPiecesAreSeparated() {
        Piece firstPiece = redPlayer.getPieces().get(0);
        Piece secondPiece = redPlayer.getPieces().get(1);

        firstPiece.enterBoard(26, Direction.CLOCKWISE);
        secondPiece.enterBoard(26, Direction.CLOCKWISE);

        firstPiece.moveTo(20);
        secondPiece.moveTo(21);

        assertFalse(blockService.hasBlockAt(20, Colour.RED));
        assertFalse(blockService.hasBlockAt(21, Colour.RED));
    }

    @Test
    void shouldStopDetectingBlockWhenPieceMovesAway() {
        Piece firstPiece = redPlayer.getPieces().get(0);
        Piece secondPiece = redPlayer.getPieces().get(1);

        firstPiece.enterBoard(26, Direction.CLOCKWISE);
        secondPiece.enterBoard(26, Direction.CLOCKWISE);

        firstPiece.moveTo(20);
        secondPiece.moveTo(20);

        assertTrue(blockService.hasBlockAt(20, Colour.RED));

        secondPiece.moveTo(21);

        assertFalse(blockService.hasBlockAt(20, Colour.RED));
    }

    @Test
    void shouldReturnEmptyListWhenNoBlockExists() {
        Piece redPiece = redPlayer.getPieces().get(0);

        redPiece.enterBoard(26, Direction.CLOCKWISE);
        redPiece.moveTo(20);

        List<Piece> block = blockService.getBlockAt(20, Colour.RED);

        assertTrue(block.isEmpty());
    }

    @Test
    void shouldReturnUnmodifiableBlockList() {
        Piece firstPiece = redPlayer.getPieces().get(0);
        Piece secondPiece = redPlayer.getPieces().get(1);

        firstPiece.enterBoard(26, Direction.CLOCKWISE);
        secondPiece.enterBoard(26, Direction.CLOCKWISE);

        firstPiece.moveTo(20);
        secondPiece.moveTo(20);

        List<Piece> block = blockService.getBlockAt(20, Colour.RED);

        assertThrows(UnsupportedOperationException.class,
                () -> block.add(new Piece(Colour.RED, 3)));
    }

    @Test
    void shouldRejectNullColour() {
        assertThrows(IllegalArgumentException.class,
                () -> blockService.hasBlockAt(20, null));
    }

    @Test
    void shouldRejectNullGameState() {
        assertThrows(IllegalArgumentException.class,
                () -> new BlockService(null, board));
    }

    @Test
    void shouldRejectNullBoard() {
        assertThrows(IllegalArgumentException.class,
                () -> new BlockService(gameState, null));
    }

    @Test
    void shouldStopBeforeOpponentBlockWhenMovingClockwise() {

        Piece redPiece = redPlayer.getPieces().get(0);
        Piece bluePieceOne = bluePlayer.getPieces().get(0);
        Piece bluePieceTwo = bluePlayer.getPieces().get(1);

        redPiece.enterBoard(26, Direction.CLOCKWISE);
        bluePieceOne.enterBoard(13, Direction.CLOCKWISE);
        bluePieceTwo.enterBoard(13, Direction.CLOCKWISE);

        redPiece.moveTo(10);
        bluePieceOne.moveTo(14);
        bluePieceTwo.moveTo(14);

        int allowedDistance = blockService.getAllowedMovementDistance(redPiece, 6);

        assertEquals(3, allowedDistance);
    }

    @Test
    void shouldStopBeforeOpponentBlockWhenMovingCounterclockwise() {

        Piece redPiece = redPlayer.getPieces().get(0);
        Piece bluePieceOne = bluePlayer.getPieces().get(0);
        Piece bluePieceTwo = bluePlayer.getPieces().get(1);

        redPiece.enterBoard(26, Direction.COUNTERCLOCKWISE);
        bluePieceOne.enterBoard(13, Direction.CLOCKWISE);
        bluePieceTwo.enterBoard(13, Direction.CLOCKWISE);

        redPiece.moveTo(20);
        bluePieceOne.moveTo(16);
        bluePieceTwo.moveTo(16);

        int allowedDistance = blockService.getAllowedMovementDistance(redPiece, 6);

        assertEquals(3, allowedDistance);
    }

    @Test
    void shouldAllowFullDistanceWhenNoOpponentBlockExists() {

        Piece redPiece = redPlayer.getPieces().get(0);

        redPiece.enterBoard(26, Direction.CLOCKWISE);
        redPiece.moveTo(10);

        int allowedDistance = blockService.getAllowedMovementDistance(redPiece, 6);

        assertEquals(6, allowedDistance);
    }

    @Test
    void shouldAllowPassingSingleOpponentPiece() {

        Piece redPiece = redPlayer.getPieces().get(0);
        Piece bluePiece = bluePlayer.getPieces().get(0);

        redPiece.enterBoard(26, Direction.CLOCKWISE);
        bluePiece.enterBoard(13, Direction.CLOCKWISE);

        redPiece.moveTo(10);
        bluePiece.moveTo(14);

        int allowedDistance = blockService.getAllowedMovementDistance(redPiece, 6);

        assertEquals(6, allowedDistance);
    }

    @Test
    void shouldDetectOpponentBlockAcrossBoardBoundary() {

        Piece redPiece = redPlayer.getPieces().get(0);
        Piece bluePieceOne = bluePlayer.getPieces().get(0);
        Piece bluePieceTwo = bluePlayer.getPieces().get(1);

        redPiece.enterBoard(26, Direction.CLOCKWISE);
        bluePieceOne.enterBoard(13, Direction.CLOCKWISE);
        bluePieceTwo.enterBoard(13, Direction.CLOCKWISE);

        redPiece.moveTo(50);
        bluePieceOne.moveTo(2);
        bluePieceTwo.moveTo(2);

        int allowedDistance = blockService.getAllowedMovementDistance(redPiece, 6);

        assertEquals(3, allowedDistance);
    }

    @Test
    void shouldAllowZeroMovementWhenOpponentBlockIsImmediatelyAhead() {

        Piece redPiece = redPlayer.getPieces().get(0);
        Piece bluePieceOne = bluePlayer.getPieces().get(0);
        Piece bluePieceTwo = bluePlayer.getPieces().get(1);

        redPiece.enterBoard(26, Direction.CLOCKWISE);
        bluePieceOne.enterBoard(13, Direction.CLOCKWISE);
        bluePieceTwo.enterBoard(13, Direction.CLOCKWISE);

        redPiece.moveTo(10);
        bluePieceOne.moveTo(11);
        bluePieceTwo.moveTo(11);

        int allowedDistance = blockService.getAllowedMovementDistance(redPiece, 6);

        assertEquals(0, allowedDistance);
    }

    @Test
    void shouldMoveBlockInDirectionOfPieceFurthestFromHome() {

        Piece clockwisePiece = redPlayer.getPieces().get(0);
        Piece counterclockwisePiece = redPlayer.getPieces().get(1);

        clockwisePiece.enterBoard(26, Direction.CLOCKWISE);
        counterclockwisePiece.enterBoard(26, Direction.COUNTERCLOCKWISE);

        clockwisePiece.moveTo(20);
        counterclockwisePiece.moveTo(20);

        Direction direction = blockService.getBlockMovementDirection(20, Colour.RED);

        assertEquals(Direction.COUNTERCLOCKWISE, direction);
    }

    @Test
    void shouldUseSharedDirectionWhenBlockPiecesMoveSameDirection() {

        Piece firstPiece = redPlayer.getPieces().get(0);
        Piece secondPiece = redPlayer.getPieces().get(1);

        firstPiece.enterBoard(26, Direction.CLOCKWISE);
        secondPiece.enterBoard(26, Direction.CLOCKWISE);

        firstPiece.moveTo(20);
        secondPiece.moveTo(20);

        Direction direction = blockService.getBlockMovementDirection(20, Colour.RED);

        assertEquals(Direction.CLOCKWISE, direction);
    }

    @Test
    void shouldDivideDiceValueByNumberOfPiecesInBlock() {
        int distance = blockService.getBlockMovementDistance(6, 2);

        assertEquals(3, distance);
    }

    @Test
    void shouldCalculateMovementForThreePieceBlock() {
        int distance = blockService.getBlockMovementDistance(6, 3);

        assertEquals(2, distance);
    }

    @Test
    void shouldReturnZeroWhenDiceValueIsTooSmallToMoveBlock() {
        int distance = blockService.getBlockMovementDistance(1, 2);

        assertEquals(0, distance);
    }

    @Test
    void shouldRejectMovementDistanceForInvalidBlockSize() {
        assertThrows(IllegalArgumentException.class,
                () -> blockService.getBlockMovementDistance(6, 1));
    }

    @Test
    void shouldRejectNonPositiveDiceValueForBlockMovement() {
        assertThrows(IllegalArgumentException.class,
                () -> blockService.getBlockMovementDistance(0, 2));
    }

    @Test
    void shouldRejectDirectionRequestWhenNoBlockExists() {
        assertThrows(IllegalArgumentException.class,
                () -> blockService.getBlockMovementDirection(20, Colour.RED));
    }

    @Test
    void shouldMoveSameDirectionBlockAsUnit() {

        Piece firstPiece = redPlayer.getPieces().get(0);
        Piece secondPiece = redPlayer.getPieces().get(1);

        firstPiece.enterBoard(26, Direction.CLOCKWISE);
        secondPiece.enterBoard(26, Direction.CLOCKWISE);

        firstPiece.moveTo(20);
        secondPiece.moveTo(20);

        boolean moved = blockService.moveBlock(20, Colour.RED, 6);

        assertTrue(moved);
        assertEquals(23, firstPiece.getPosition());
        assertEquals(23, secondPiece.getPosition());
    }

    @Test
    void shouldMoveOppositeDirectionBlockUsingPieceFurthestFromHome() {

        Piece clockwisePiece = redPlayer.getPieces().get(0);
        Piece counterclockwisePiece = redPlayer.getPieces().get(1);

        clockwisePiece.enterBoard(26, Direction.CLOCKWISE);
        counterclockwisePiece.enterBoard(26, Direction.COUNTERCLOCKWISE);

        clockwisePiece.moveTo(20);
        counterclockwisePiece.moveTo(20);

        boolean moved = blockService.moveBlock(20, Colour.RED, 6);

        assertTrue(moved);
        assertEquals(17, clockwisePiece.getPosition());
        assertEquals(17, counterclockwisePiece.getPosition());
    }

    @Test
    void shouldPreserveOriginalDirectionsAfterBlockMovement() {

        Piece clockwisePiece = redPlayer.getPieces().get(0);
        Piece counterclockwisePiece = redPlayer.getPieces().get(1);

        clockwisePiece.enterBoard(26, Direction.CLOCKWISE);
        counterclockwisePiece.enterBoard(26, Direction.COUNTERCLOCKWISE);

        clockwisePiece.moveTo(20);
        counterclockwisePiece.moveTo(20);

        blockService.moveBlock(20, Colour.RED, 6);

        assertEquals(Direction.CLOCKWISE, clockwisePiece.getDirection());
        assertEquals(Direction.COUNTERCLOCKWISE, counterclockwisePiece.getDirection());
    }

    @Test
    void shouldMoveThreePieceBlockUsingDividedDistance() {

        Piece firstPiece = redPlayer.getPieces().get(0);
        Piece secondPiece = redPlayer.getPieces().get(1);
        Piece thirdPiece = redPlayer.getPieces().get(2);

        firstPiece.enterBoard(26, Direction.CLOCKWISE);
        secondPiece.enterBoard(26, Direction.CLOCKWISE);
        thirdPiece.enterBoard(26, Direction.CLOCKWISE);

        firstPiece.moveTo(20);
        secondPiece.moveTo(20);
        thirdPiece.moveTo(20);

        boolean moved = blockService.moveBlock(20, Colour.RED, 6);

        assertTrue(moved);
        assertEquals(22, firstPiece.getPosition());
        assertEquals(22, secondPiece.getPosition());
        assertEquals(22, thirdPiece.getPosition());
    }

    @Test
    void shouldNotMoveBlockWhenCalculatedDistanceIsZero() {

        Piece firstPiece = redPlayer.getPieces().get(0);
        Piece secondPiece = redPlayer.getPieces().get(1);

        firstPiece.enterBoard(26, Direction.CLOCKWISE);
        secondPiece.enterBoard(26, Direction.CLOCKWISE);

        firstPiece.moveTo(20);
        secondPiece.moveTo(20);

        boolean moved = blockService.moveBlock(20, Colour.RED, 1);

        assertFalse(moved);
        assertEquals(20, firstPiece.getPosition());
        assertEquals(20, secondPiece.getPosition());
    }

    @Test
    void shouldNotMoveWhenNoBlockExists() {

        Piece redPiece = redPlayer.getPieces().get(0);

        redPiece.enterBoard(26, Direction.CLOCKWISE);
        redPiece.moveTo(20);

        boolean moved = blockService.moveBlock(20, Colour.RED, 6);

        assertFalse(moved);
        assertEquals(20, redPiece.getPosition());
    }

    @Test
    void shouldMoveBlockAcrossStandardPathBoundary() {
        
        Piece firstPiece = redPlayer.getPieces().get(0);
        Piece secondPiece = redPlayer.getPieces().get(1);

        firstPiece.enterBoard(26, Direction.CLOCKWISE);
        secondPiece.enterBoard(26, Direction.CLOCKWISE);

        firstPiece.moveTo(50);
        secondPiece.moveTo(50);

        boolean moved = blockService.moveBlock(50, Colour.RED, 6);

        assertTrue(moved);
        assertEquals(1, firstPiece.getPosition());
        assertEquals(1, secondPiece.getPosition());
    }
}