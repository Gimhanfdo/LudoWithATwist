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

class ApproachTeleportStrategyTest {

    private Board board;
    private ApproachTeleportStrategy strategy;

    @BeforeEach
    void setUp() {
        board = new Board();
        strategy = new ApproachTeleportStrategy(board);
    }

    @Test
    void shouldHandleApproachDestination() {
        assertEquals(TeleportDestination.APPROACH, strategy.getDestination());
    }

    @Test
    void shouldTeleportRedPieceToRedApproach() {
        Piece piece = new Piece(Colour.RED, 1);

        piece.enterBoard(26, Direction.CLOCKWISE);
        piece.moveTo(10);

        strategy.teleport(piece);

        assertEquals(PieceState.STANDARD_PATH, piece.getState());
        assertEquals(board.getApproachPosition(Colour.RED), piece.getPosition());
    }

    @Test
    void shouldPreserveDirectionWhenTeleportedToApproach() {
        Piece piece = new Piece(Colour.RED, 1);

        piece.enterBoard(26, Direction.COUNTERCLOCKWISE);
        piece.moveTo(10);

        strategy.teleport(piece);

        assertEquals(Direction.COUNTERCLOCKWISE, piece.getDirection());
    }

    @Test
    void shouldRejectNullPiece() {
        assertThrows(IllegalArgumentException.class, () -> strategy.teleport(null));
    }

    @Test
    void shouldRejectNullBoard() {
        assertThrows(IllegalArgumentException.class, () -> new ApproachTeleportStrategy(null));
    }

    @Test
    void shouldPreserveCaptureInformationWhenTeleportedToApproach() {
        Piece piece = new Piece(Colour.RED, 1);

        piece.enterBoard(26, Direction.CLOCKWISE);
        piece.recordCapture();

        strategy.teleport(piece);

        assertTrue(piece.hasCaptured());
    }
}