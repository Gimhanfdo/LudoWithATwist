package ludo.strategy.teleport;

import ludo.domain.enums.Colour;
import ludo.domain.enums.Direction;
import ludo.domain.enums.PieceState;
import ludo.domain.enums.TeleportDestination;
import ludo.domain.model.Board;
import ludo.domain.model.Piece;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class XTeleportStrategyTest {

    private Board board;
    private XTeleportStrategy strategy;

    @BeforeEach
    void setUp() {
        board = new Board();
        strategy = new XTeleportStrategy(board);
    }

    @Test
    void shouldHandleXDestination() {
        assertEquals(TeleportDestination.X, strategy.getDestination());
    }

    @Test
    void shouldTeleportRedPieceToRedX() {
        Piece piece = new Piece(Colour.RED, 1);

        piece.enterBoard(26, Direction.COUNTERCLOCKWISE);
        piece.moveTo(15);

        strategy.teleport(piece);

        assertEquals(PieceState.STANDARD_PATH, piece.getState());
        assertEquals(board.getStartPosition(Colour.RED), piece.getPosition());
    }

    @Test
    void shouldPreserveOriginalDirectionWhenTeleportedToX() {
        Piece piece = new Piece(Colour.RED, 1);

        piece.enterBoard(26, Direction.COUNTERCLOCKWISE);
        piece.moveTo(15);

        strategy.teleport(piece);

        assertEquals(Direction.COUNTERCLOCKWISE, piece.getDirection());
    }

    @Test
    void shouldRejectNullPiece() {
        assertThrows(IllegalArgumentException.class, () -> strategy.teleport(null));
    }

    @Test
    void shouldRejectNullBoard() {
        assertThrows(IllegalArgumentException.class, () -> new XTeleportStrategy(null));
    }

    @Test
    void shouldPreserveCaptureInformationWhenTeleportedToX() {
        Piece piece = new Piece(Colour.RED, 1);

        piece.enterBoard(26, Direction.CLOCKWISE);
        piece.recordCapture();

        strategy.teleport(piece);

        assertTrue(piece.hasCaptured());
    }
}