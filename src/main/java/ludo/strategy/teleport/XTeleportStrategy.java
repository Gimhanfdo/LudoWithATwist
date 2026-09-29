package ludo.strategy.teleport;

import ludo.domain.enums.TeleportDestination;
import ludo.domain.model.Board;
import ludo.domain.model.Piece;

public class XTeleportStrategy implements TeleportStrategy {

    private final Board board;

    public XTeleportStrategy(Board board) {
        if (board == null) {
            throw new IllegalArgumentException("Board cannot be null.");
        }

        this.board = board;
    }

    @Override
    public TeleportDestination getDestination() {
        return TeleportDestination.X;
    }

    @Override
    public void teleport(Piece piece) {
        validatePiece(piece);

        int xPosition = board.getStartPosition(piece.getColour());

        piece.teleportToStandardPath(xPosition);
    }

    private void validatePiece(Piece piece) {
        if (piece == null) {
            throw new IllegalArgumentException("Piece cannot be null.");
        }
    }
}