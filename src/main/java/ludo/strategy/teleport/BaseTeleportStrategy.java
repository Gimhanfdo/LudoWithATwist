package ludo.strategy.teleport;

import ludo.domain.enums.TeleportDestination;
import ludo.domain.model.Piece;

public class BaseTeleportStrategy implements TeleportStrategy {

    @Override
    public TeleportDestination getDestination() {
        return TeleportDestination.BASE;
    }

    @Override
    public void teleport(Piece piece) {
        if (piece == null) {
            throw new IllegalArgumentException("Piece cannot be null.");
        }

        piece.reset();
    }
}