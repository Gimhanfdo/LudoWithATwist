package ludo.strategy.teleport;

import ludo.domain.enums.Direction;
import ludo.domain.enums.TeleportDestination;
import ludo.domain.model.Board;
import ludo.domain.model.Piece;

public class GammaTeleportStrategy implements TeleportStrategy {

    private final TeleportStrategy betaStrategy;

    public GammaTeleportStrategy(TeleportStrategy betaStrategy) {
        if (betaStrategy == null) {
            throw new IllegalArgumentException("Beta strategy cannot be null.");
        }

        if (betaStrategy.getDestination() != TeleportDestination.BETA) {
            throw new IllegalArgumentException("Gamma requires a Beta teleport strategy.");
        }

        this.betaStrategy = betaStrategy;
    }

    @Override
    public TeleportDestination getDestination() {
        return TeleportDestination.GAMMA;
    }

    @Override
    public void teleport(Piece piece) {
        validatePiece(piece);

        Direction direction = piece.getDirection();

        validateDirection(direction);

        if (direction == Direction.COUNTERCLOCKWISE) {
            betaStrategy.teleport(piece);
            return;
        }

        piece.teleportToStandardPath(Board.GAMMA_POSITION);
        piece.changeDirection(Direction.COUNTERCLOCKWISE);
    }

    private void validatePiece(Piece piece) {
        if (piece == null) {
            throw new IllegalArgumentException("Piece cannot be null.");
        }
    }

    private void validateDirection(Direction direction) {
        if (direction == null) {
            throw new IllegalStateException("Piece must have a movement direction.");
        }
    }
}