package ludo.strategy.teleport;

import ludo.domain.enums.PieceEffect;
import ludo.domain.enums.TeleportDestination;
import ludo.domain.model.Board;
import ludo.domain.model.Piece;

public class BetaTeleportStrategy implements TeleportStrategy {

    private static final int BRIEFING_DURATION_ROUNDS = 4;

    @Override
    public TeleportDestination getDestination() {
        return TeleportDestination.BETA;
    }

    @Override
    public void teleport(Piece piece) {
        validatePiece(piece);

        piece.teleportToStandardPath(Board.BETA_POSITION);
        piece.applyEffect(PieceEffect.BRIEFING, BRIEFING_DURATION_ROUNDS);
    }

    private void validatePiece(Piece piece) {
        if (piece == null) {
            throw new IllegalArgumentException("Piece cannot be null.");
        }
    }
}