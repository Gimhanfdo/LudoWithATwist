package ludo.strategy.teleport;

import ludo.domain.enums.TeleportDestination;
import ludo.domain.model.Piece;

public interface TeleportStrategy {

    TeleportDestination getDestination();

    void teleport(Piece piece);
}