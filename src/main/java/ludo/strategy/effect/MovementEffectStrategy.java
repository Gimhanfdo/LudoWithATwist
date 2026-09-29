package ludo.strategy.effect;

import ludo.domain.enums.PieceEffect;

public interface MovementEffectStrategy {

    PieceEffect getEffect();

    int apply(int roll);
}