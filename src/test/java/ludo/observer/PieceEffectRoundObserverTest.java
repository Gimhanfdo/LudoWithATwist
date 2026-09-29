package ludo.observer;

import ludo.domain.enums.Colour;
import ludo.domain.enums.PieceEffect;
import ludo.domain.model.GameState;
import ludo.domain.model.Piece;
import ludo.domain.model.Player;
import ludo.service.PieceEffectService;
import ludo.strategy.effect.EnergisedMovementStrategy;
import ludo.strategy.effect.SickMovementStrategy;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PieceEffectRoundObserverTest {

    @Test
    void shouldCompleteEffectRoundForEveryPiece() {
        Player redPlayer = new Player(Colour.RED);
        Player bluePlayer = new Player(Colour.BLUE);

        GameState gameState = new GameState(List.of(redPlayer, bluePlayer));
        PieceEffectService pieceEffectService = mock(PieceEffectService.class);
        RoundObserver observer = new PieceEffectRoundObserver(gameState, pieceEffectService);

        observer.onRoundCompleted();

        for (Player player : gameState.getPlayers()) {
            for (Piece piece : player.getPieces()) {
                verify(pieceEffectService).completeRound(piece);
            }
        }
    }

    @Test
    void shouldRejectNullGameState() {
        PieceEffectService pieceEffectService = mock(PieceEffectService.class);

        assertThrows(IllegalArgumentException.class,
                () -> new PieceEffectRoundObserver(null, pieceEffectService));
    }

    @Test
    void shouldRejectNullPieceEffectService() {
        Player player = new Player(Colour.RED);
        GameState gameState = new GameState(List.of(player));

        assertThrows(IllegalArgumentException.class,
                () -> new PieceEffectRoundObserver(gameState, null));
    }

    @Test
    void shouldReduceActiveEffectWhenRoundCompletes() {
        Player player = new Player(Colour.RED);
        GameState gameState = new GameState(List.of(player));
        Piece piece = player.getPieces().get(0);

        piece.applyEffect(PieceEffect.ENERGISED, 4);

        PieceEffectService pieceEffectService = new PieceEffectService(
                List.of(
                        new EnergisedMovementStrategy(),
                        new SickMovementStrategy()));

        RoundObserver observer = new PieceEffectRoundObserver(gameState, pieceEffectService);

        observer.onRoundCompleted();

        assertEquals(3, piece.getEffectRoundsRemaining());
    }
}