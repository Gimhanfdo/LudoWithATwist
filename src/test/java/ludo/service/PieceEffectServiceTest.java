package ludo.service;

import ludo.domain.enums.Colour;
import ludo.domain.enums.PieceEffect;
import ludo.domain.model.Piece;
import ludo.strategy.effect.EnergisedMovementStrategy;
import ludo.strategy.effect.MovementEffectStrategy;
import ludo.strategy.effect.SickMovementStrategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PieceEffectServiceTest {

    private PieceEffectService service;

    @BeforeEach
    void setUp() {
        service = new PieceEffectService(
                List.of(
                        new EnergisedMovementStrategy(),
                        new SickMovementStrategy()));
    }

    @Test
    void shouldUseNormalMovementWhenPieceHasNoEffect() {
        Piece piece = new Piece(Colour.RED, 1);

        assertEquals(4, service.calculateMovement(piece, 4));
    }

    @Test
    void shouldDoubleMovementWhenEnergised() {
        Piece piece = new Piece(Colour.RED, 1);

        piece.applyEffect(PieceEffect.ENERGISED, 4);

        assertEquals(8, service.calculateMovement(piece, 4));
    }

    @Test
    void shouldHalveMovementWhenSick() {
        Piece piece = new Piece(Colour.RED, 1);

        piece.applyEffect(PieceEffect.SICK, 4);

        assertEquals(2, service.calculateMovement(piece, 5));
    }

    @Test
    void shouldDecreaseEffectDurationAfterCompletedRound() {
        Piece piece = new Piece(Colour.RED, 1);

        piece.applyEffect(PieceEffect.SICK, 4);

        service.completeRound(piece);
        service.completeRound(piece);

        assertEquals(3, piece.getEffectRoundsRemaining());
    }

    @Test
    void shouldExpireEffectAfterFourCompletedRounds() {
        Piece piece = new Piece(Colour.RED, 1);

        piece.applyEffect(PieceEffect.SICK, 4);

        service.completeRound(piece); // application round

        service.completeRound(piece); // 4 -> 3
        service.completeRound(piece); // 3 -> 2
        service.completeRound(piece); // 2 -> 1
        service.completeRound(piece); // 1 -> NONE

        assertEquals(PieceEffect.NONE, piece.getEffect());
        assertEquals(0, piece.getEffectRoundsRemaining());
    }

    @Test
    void shouldRejectNullPieceWhenCalculatingMovement() {
        assertThrows(IllegalArgumentException.class, () -> service.calculateMovement(null, 4));
    }

    @Test
    void shouldRejectNonPositiveRoll() {
        Piece piece = new Piece(Colour.RED, 1);

        assertThrows(IllegalArgumentException.class, () -> service.calculateMovement(piece, 0));
    }

    @Test
    void shouldRejectNullPieceWhenCompletingRound() {
        assertThrows(IllegalArgumentException.class, () -> service.completeRound(null));
    }

    @Test
    void shouldRejectNullStrategyList() {
        assertThrows(IllegalArgumentException.class, () -> new PieceEffectService(null));
    }

    @Test
    void shouldRejectDuplicateMovementStrategies() {
        assertThrows(IllegalArgumentException.class,
                () -> new PieceEffectService(
                        List.of(
                                new EnergisedMovementStrategy(),
                                new EnergisedMovementStrategy())));
    }

    @Test
    void shouldRejectNullMovementStrategy() {
        List<MovementEffectStrategy> strategies = new ArrayList<>();

        strategies.add(new EnergisedMovementStrategy());
        strategies.add(null);

        assertThrows(IllegalArgumentException.class, () -> new PieceEffectService(strategies));
    }

    @Test
    void shouldPreventMovementDuringBriefing() {
        Piece piece = new Piece(Colour.RED, 1);

        piece.applyEffect(PieceEffect.BRIEFING, 4);

        assertFalse(service.canMove(piece));
    }

    @Test
    void shouldAllowMovementWithoutBriefing() {
        Piece piece = new Piece(Colour.RED, 1);

        assertTrue(service.canMove(piece));
    }

    @Test
    void shouldAllowMovementWhenEnergised() {
        Piece piece = new Piece(Colour.RED, 1);

        piece.applyEffect(PieceEffect.ENERGISED, 4);

        assertTrue(service.canMove(piece));
    }

    @Test
    void shouldAllowMovementAfterBriefingExpires() {
        Piece piece = new Piece(Colour.RED, 1);

        piece.applyEffect(PieceEffect.BRIEFING, 4);

        service.completeRound(piece); // application round

        for (int i = 0; i < 4; i++) {
            service.completeRound(piece);
        }

        assertEquals(PieceEffect.NONE, piece.getEffect());
        assertTrue(service.canMove(piece));
    }
}