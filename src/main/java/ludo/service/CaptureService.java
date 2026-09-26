package ludo.service;

import ludo.domain.enums.PieceState;
import ludo.domain.model.Piece;
import ludo.domain.model.GameState;

public class CaptureService {

    public boolean canCapture(Piece attacker, Piece opponent) {
        validatePieces(attacker, opponent);

        if (attacker.getColour() == opponent.getColour()) {
            return false;
        }

        if (attacker.getState() != PieceState.STANDARD_PATH || opponent.getState() != PieceState.STANDARD_PATH) {
            return false;
        }

        return attacker.getPosition().equals(opponent.getPosition());
    }

    public boolean capture(Piece attacker, Piece opponent) {
        if (!canCapture(attacker, opponent)) {
            return false;
        }

        opponent.reset();
        attacker.recordCapture();

        return true;
    }

    private void validatePieces(Piece attacker, Piece opponent) {
        validateAttacker(attacker);

        if (opponent == null) {
            throw new IllegalArgumentException("Opponent cannot be null.");
        }
    }

    private void validateAttacker(Piece attacker) {

        if (attacker == null) {
            throw new IllegalArgumentException(
                    "Attacker cannot be null.");
        }
    }

    public boolean resolveCapture(Piece attacker, GameState gameState) {
        validateAttacker(attacker);

        if (gameState == null) {
            throw new IllegalArgumentException("Game state cannot be null.");
        }

        if (attacker.getState() != PieceState.STANDARD_PATH) {
            return false;
        }

        for (Piece occupant : gameState.getPiecesAtStandardPosition(
                attacker.getPosition())) {

            if (occupant == attacker) {
                continue;
            }

            if (canCapture(attacker, occupant)) {
                return capture(attacker, occupant);
            }
        }

        return false;
    }
}