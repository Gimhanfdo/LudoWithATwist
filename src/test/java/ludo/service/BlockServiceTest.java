package ludo.service;

import ludo.domain.enums.Colour;
import ludo.domain.enums.Direction;
import ludo.domain.model.GameState;
import ludo.domain.model.Piece;
import ludo.domain.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BlockServiceTest {

    private Player redPlayer;
    private Player bluePlayer;

    private GameState gameState;
    private BlockService blockService;

    @BeforeEach
    void setUp() {
        redPlayer = new Player(Colour.RED);
        bluePlayer = new Player(Colour.BLUE);

        gameState = new GameState(List.of(redPlayer, bluePlayer));
        blockService = new BlockService(gameState);
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
                () -> new BlockService(null));
    }
}