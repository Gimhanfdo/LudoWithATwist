package ludo.observer;

import ludo.domain.enums.Colour;
import ludo.domain.model.GameState;
import ludo.domain.model.Piece;
import ludo.domain.model.Player;
import ludo.service.PieceEffectService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PieceEffectRoundObserverTest {

    private GameState gameState;
    private PieceEffectService pieceEffectService;
    private PieceEffectRoundObserver observer;
    private Player red;
    private Player blue;

    @BeforeEach
    void setUp() {
        red = new Player(Colour.RED);
        blue = new Player(Colour.BLUE);
        gameState = new GameState(List.of(red, blue));
        pieceEffectService = mock(PieceEffectService.class);
        observer = new PieceEffectRoundObserver(gameState, pieceEffectService);
    }

    @Test
    void shouldCompleteRoundForEveryPiece() {
        observer.onRoundCompleted();

        for (Player player : gameState.getPlayers()) {
            for (Piece piece : player.getPieces()) {
                verify(pieceEffectService).completeRound(piece);
            }
        }
    }

    @Test
    void shouldRejectNullGameState() {
        assertThrows(IllegalArgumentException.class, () -> new PieceEffectRoundObserver(null, pieceEffectService));
    }

    @Test
    void shouldRejectNullPieceEffectService() {
        assertThrows(IllegalArgumentException.class, () -> new PieceEffectRoundObserver(gameState, null));
    }
}