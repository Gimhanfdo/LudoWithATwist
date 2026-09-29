package ludo.strategy.teleport;

import ludo.domain.enums.PieceEffect;
import ludo.domain.enums.TeleportDestination;
import ludo.domain.model.Board;
import ludo.domain.model.Piece;
import ludo.random.AlphaEffectSelector;

public class AlphaTeleportStrategy implements TeleportStrategy {

    private static final int EFFECT_DURATION_ROUNDS = 4;

    private final AlphaEffectSelector effectSelector;

    public AlphaTeleportStrategy(AlphaEffectSelector effectSelector) {
        if (effectSelector == null) {
            throw new IllegalArgumentException("Effect selector cannot be null.");
        }

        this.effectSelector = effectSelector;
    }

    @Override
    public TeleportDestination getDestination() {
        return TeleportDestination.ALPHA;
    }

    @Override
    public void teleport(Piece piece) {
        validatePiece(piece);

        PieceEffect effect = effectSelector.selectEffect();
        validateEffect(effect);

        piece.teleportToStandardPath(Board.ALPHA_POSITION);

        piece.applyEffect(effect, EFFECT_DURATION_ROUNDS);
    }

    private void validatePiece(Piece piece) {
        if (piece == null) {
            throw new IllegalArgumentException("Piece cannot be null.");
        }
    }

    private void validateEffect(PieceEffect effect) {
        if (effect != PieceEffect.ENERGISED && effect != PieceEffect.SICK) {
            throw new IllegalStateException("Alpha must produce ENERGISED or SICK.");
        }
    }
}