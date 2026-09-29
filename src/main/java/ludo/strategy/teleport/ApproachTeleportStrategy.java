package ludo.strategy.teleport;

import ludo.domain.enums.TeleportDestination;
import ludo.domain.model.Board;
import ludo.domain.model.Piece;

public class ApproachTeleportStrategy implements TeleportStrategy {

    private final Board board;

    public ApproachTeleportStrategy(Board board) {
        if (board == null) {
            throw new IllegalArgumentException("Board cannot be null.");
        }

        this.board = board;
    }

    @Override
    public TeleportDestination getDestination() {
        return TeleportDestination.APPROACH;
    }

    @Override
    public void teleport(Piece piece) {
        validatePiece(piece);

        int approachPosition = board.getApproachPosition(piece.getColour());

        piece.teleportToStandardPath(approachPosition);
    }

    private void validatePiece(Piece piece) {
        if (piece == null) {
            throw new IllegalArgumentException("Piece cannot be null.");
        }
    }
}