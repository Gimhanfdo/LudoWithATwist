package ludo.service;

import ludo.domain.enums.Colour;
import ludo.domain.enums.Direction;
import ludo.domain.model.Piece;
import ludo.domain.model.Player;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ForcedBlockBreakServiceTest {

    private BlockService blockService;
    private ForcedBlockBreakService service;
    private Player player;

    @BeforeEach
    void setUp() {
        blockService = mock(BlockService.class);
        service = new ForcedBlockBreakService(blockService);
        player = new Player(Colour.GREEN);
    }

    @Test
    void shouldBreakPlayerBlock() {
        Piece firstPiece = player.getPieces().get(0);
        Piece secondPiece = player.getPieces().get(1);

        firstPiece.enterBoard(39, Direction.CLOCKWISE);
        secondPiece.enterBoard(39, Direction.COUNTERCLOCKWISE);
        firstPiece.moveTo(20);
        secondPiece.moveTo(20);

        List<Piece> block = List.of(firstPiece, secondPiece);

        when(blockService.getBlockAt(20, Colour.GREEN)).thenReturn(block);
        when(blockService.breakBlockAfterThreeSixes(20, Colour.GREEN, firstPiece)).thenReturn(true);

        boolean broken = service.breakBlock(player);

        assertTrue(broken);
        verify(blockService).breakBlockAfterThreeSixes(20, Colour.GREEN, firstPiece);
    }

    @Test
    void shouldNotBreakBlockWhenPlayerHasNoBlock() {
        Piece piece = player.getPieces().get(0);

        piece.enterBoard(39, Direction.CLOCKWISE);
        piece.moveTo(20);

        when(blockService.getBlockAt(20, Colour.GREEN)).thenReturn(List.of());

        boolean broken = service.breakBlock(player);

        assertFalse(broken);
        verify(blockService, never()).breakBlockAfterThreeSixes(anyInt(), any(), any());
    }

    @Test
    void shouldIgnorePiecesThatAreNotOnStandardPath() {
        boolean broken = service.breakBlock(player);

        assertFalse(broken);
        verifyNoInteractions(blockService);
    }

    @Test
    void shouldRejectNullPlayer() {
        assertThrows(IllegalArgumentException.class, () -> service.breakBlock(null));
    }

    @Test
    void shouldRejectNullBlockService() {
        assertThrows(IllegalArgumentException.class, () -> new ForcedBlockBreakService(null));
    }
}