package ludo.service;

import ludo.domain.enums.PieceEffect;
import ludo.domain.model.Piece;
import ludo.strategy.effect.MovementEffectStrategy;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class PieceEffectService {

    private final Map<PieceEffect, MovementEffectStrategy> movementStrategies;

    public PieceEffectService(List<MovementEffectStrategy> movementStrategies) {
        if (movementStrategies == null) {
            throw new IllegalArgumentException("Movement strategies cannot be null.");
        }

        this.movementStrategies = createStrategyMap(movementStrategies);
    }

    public int calculateMovement(Piece piece, int roll) {
        validatePiece(piece);
        validateRoll(roll);

        if (piece.getEffect() == PieceEffect.NONE) {
            return roll;
        }

        MovementEffectStrategy strategy = movementStrategies.get(piece.getEffect());

        if (strategy == null) {
            return roll;
        }

        return strategy.apply(roll);
    }

    public void completeRound(Piece piece) {
        validatePiece(piece);

        piece.completeEffectRound();
    }

    private Map<PieceEffect, MovementEffectStrategy> createStrategyMap(List<MovementEffectStrategy> strategies) {
        Map<PieceEffect, MovementEffectStrategy> strategyMap = new EnumMap<>(PieceEffect.class);

        for (MovementEffectStrategy strategy : strategies) {
            validateStrategy(strategy);

            PieceEffect effect = strategy.getEffect();

            if (strategyMap.containsKey(effect)) {
                throw new IllegalArgumentException("Duplicate movement strategy for " + effect + ".");
            }

            strategyMap.put(effect, strategy);
        }

        return strategyMap;
    }

    private void validateStrategy(MovementEffectStrategy strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("Movement strategy cannot be null.");
        }

        if (strategy.getEffect() == null) {
            throw new IllegalArgumentException("Movement strategy effect cannot be null.");
        }

        if (strategy.getEffect() == PieceEffect.NONE) {
            throw new IllegalArgumentException("NONE does not require a movement strategy.");
        }
    }

    private void validatePiece(Piece piece) {
        if (piece == null) {
            throw new IllegalArgumentException("Piece cannot be null.");
        }
    }

    private void validateRoll(int roll) {
        if (roll <= 0) {
            throw new IllegalArgumentException("Roll must be positive.");
        }
    }

    public boolean canMove(Piece piece) {
        validatePiece(piece);

        return piece.getEffect() != PieceEffect.BRIEFING;
    }
}