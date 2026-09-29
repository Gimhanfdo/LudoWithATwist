package ludo.service;

import ludo.domain.enums.PieceState;
import ludo.domain.enums.TeleportDestination;
import ludo.domain.model.MysteryCell;
import ludo.domain.model.Piece;

public class MysteryLandingService {

    private final MysteryCell mysteryCell;
    private final MysteryTeleportService teleportService;

    public MysteryLandingService(MysteryCell mysteryCell, MysteryTeleportService teleportService) {
        if (mysteryCell == null) {
            throw new IllegalArgumentException("Mystery cell cannot be null.");
        }

        if (teleportService == null) {
            throw new IllegalArgumentException("Teleport service cannot be null.");
        }

        this.mysteryCell = mysteryCell;
        this.teleportService = teleportService;
    }

    public TeleportDestination resolveLanding(Piece piece) {
        validatePiece(piece);

        if (!hasLandedOnMysteryCell(piece)) {
            return null;
        }

        return teleportService.teleport(piece);
    }

    private boolean hasLandedOnMysteryCell(Piece piece) {
        if (!mysteryCell.isActive()) {
            return false;
        }

        if (piece.getState() != PieceState.STANDARD_PATH) {
            return false;
        }

        return piece.getPosition().equals(mysteryCell.getPosition());
    }

    private void validatePiece(Piece piece) {
        if (piece == null) {
            throw new IllegalArgumentException("Piece cannot be null.");
        }
    }
}