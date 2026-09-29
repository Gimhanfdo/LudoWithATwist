package ludo.strategy.effect;

import ludo.domain.enums.PieceEffect;

public class SickMovementStrategy implements MovementEffectStrategy {

    @Override
    public PieceEffect getEffect() {
        return PieceEffect.SICK;
    }

    @Override
    public int apply(int roll) {
        validateRoll(roll);

        return roll / 2;
    }

    private void validateRoll(int roll) {
        if (roll <= 0) {
            throw new IllegalArgumentException("Roll must be positive.");
        }
    }
}