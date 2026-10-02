package ludo.service;

import ludo.domain.enums.Colour;
import ludo.domain.enums.Direction;
import ludo.domain.model.Board;
import ludo.domain.model.Piece;
import ludo.domain.model.Player;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameCompletionServiceTest {

    private GameCompletionService service;
    private Player player;

    private void movePieceHome(Piece piece) {
        piece.enterBoard(26, Direction.CLOCKWISE);
        piece.enterHomeStraight(Board.HOME_STRAIGHT_SIZE - 1);
        piece.reachHome();
    }

    @BeforeEach
    void setUp() {
        service = new GameCompletionService();
        player = new Player(Colour.RED);
    }

    @Test
    void shouldNotWinWhenPiecesAreStillInBase() {
        assertFalse(service.hasWon(player));
    }

    @Test
    void shouldNotWinWhenLessThanFourPiecesAreHome() {
        movePieceHome(player.getPieces().get(0));
        movePieceHome(player.getPieces().get(1));
        movePieceHome(player.getPieces().get(2));

        assertFalse(service.hasWon(player));
    }

    @Test
    void shouldWinWhenAllPiecesAreHome() {
        for (Piece piece : player.getPieces()) {
            movePieceHome(piece);
        }

        assertTrue(service.hasWon(player));
    }

    @Test
    void shouldRejectNullPlayer() {
        assertThrows(IllegalArgumentException.class, () -> service.hasWon(null));
    }
}