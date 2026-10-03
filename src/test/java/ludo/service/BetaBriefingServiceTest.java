package ludo.service;

import ludo.domain.enums.Colour;
import ludo.domain.enums.Direction;
import ludo.domain.enums.PieceEffect;
import ludo.domain.enums.PieceState;
import ludo.domain.model.Piece;
import ludo.domain.model.Player;
import ludo.strategy.teleport.BetaTeleportStrategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class BetaBriefingServiceTest {

    private BetaBriefingService service;

    @BeforeEach
    void setUp() {
        service = new BetaBriefingService(new BetaRollTracker());
    }

    @Test
    void shouldKeepBriefingPieceAtBetaBeforeThreshold() {
        Player player = new Player(Colour.RED);
        Piece piece = prepareBriefingPiece(player);

        assertTrue(service.recordRoll(player, 3).isEmpty());
        assertTrue(service.recordRoll(player, 3).isEmpty());

        assertEquals(PieceState.STANDARD_PATH, piece.getState());
        assertEquals(PieceEffect.BRIEFING, piece.getEffect());
    }

    @Test
    void shouldReturnBriefingPieceToBaseAfterThreeConsecutiveThrees() {
        Player player = new Player(Colour.RED);
        Piece piece = prepareBriefingPiece(player);

        service.recordRoll(player, 3);
        service.recordRoll(player, 3);

        List<Piece> returnedPieces = service.recordRoll(player, 3);

        assertEquals(List.of(piece), returnedPieces);
        assertEquals(PieceState.BASE, piece.getState());
        assertNull(piece.getPosition());
        assertEquals(PieceEffect.NONE, piece.getEffect());
    }

    @Test
    void shouldBreakSequenceWhenDifferentValueIsRolled() {
        Player player = new Player(Colour.RED);
        Piece piece = prepareBriefingPiece(player);

        service.recordRoll(player, 3);
        service.recordRoll(player, 3);
        service.recordRoll(player, 5);
        service.recordRoll(player, 3);

        assertEquals(PieceState.STANDARD_PATH, piece.getState());
        assertEquals(PieceEffect.BRIEFING, piece.getEffect());
    }

    @Test
    void shouldNotTrackRollsWithoutBriefingPiece() {
        Player player = new Player(Colour.RED);

        assertTrue(service.recordRoll(player, 3).isEmpty());
    }

    @Test
    void shouldRejectNullPlayer() {
        assertThrows(IllegalArgumentException.class, () -> service.recordRoll(null, 3));
    }

    private Piece prepareBriefingPiece(Player player) {
        Piece piece = player.getPieces().get(0);

        piece.enterBoard(26, Direction.CLOCKWISE);
        new BetaTeleportStrategy().teleport(piece);

        return piece;
    }

    @Test
    void shouldResetPieceInformationWhenReturnedFromBriefingToBase() {
        Player player = new Player(Colour.RED);
        Piece piece = prepareBriefingPiece(player);

        piece.recordCapture();
        piece.recordApproachPass();

        service.recordRoll(player, 3);
        service.recordRoll(player, 3);
        service.recordRoll(player, 3);

        assertEquals(PieceState.BASE, piece.getState());
        assertNull(piece.getDirection());
        assertEquals(0, piece.getCaptureCount());
        assertEquals(0, piece.getApproachPassCount());
        assertEquals(PieceEffect.NONE, piece.getEffect());
    }

    @Test
    void shouldTrackBriefingPiecesIndependently() {
        Player player = new Player(Colour.RED);
        Piece firstPiece = player.getPieces().get(0);
        Piece secondPiece = player.getPieces().get(1);

        firstPiece.enterBoard(26, Direction.CLOCKWISE);
        new BetaTeleportStrategy().teleport(firstPiece);

        service.recordRoll(player, 3);

        secondPiece.enterBoard(26, Direction.CLOCKWISE);
        new BetaTeleportStrategy().teleport(secondPiece);

        service.recordRoll(player, 3);
        List<Piece> returnedPieces = service.recordRoll(player, 3);

        assertEquals(List.of(firstPiece), returnedPieces);
        assertEquals(PieceState.BASE, firstPiece.getState());
        assertEquals(PieceState.STANDARD_PATH, secondPiece.getState());
        assertEquals(PieceEffect.BRIEFING, secondPiece.getEffect());
    }
}