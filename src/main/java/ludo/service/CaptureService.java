package ludo.service;

import java.util.List;

import ludo.domain.enums.Colour;
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

    public boolean captureBlock(List<Piece> attackingBlock, List<Piece> defendingBlock) {

        validateBlock(attackingBlock);
        validateBlock(defendingBlock);

        if (attackingBlock.size() != defendingBlock.size()) {
            return false;
        }

        if (!areOpposingBlocks(attackingBlock, defendingBlock)) {
            return false;
        }

        if (!occupySamePosition(attackingBlock, defendingBlock)) {
            return false;
        }

        for (Piece defender : defendingBlock) {
            defender.reset();
        }

        for (Piece attacker : attackingBlock) {
            attacker.recordCapture();
        }

        return true;
    }

    private boolean occupySamePosition(List<Piece> attackingBlock, List<Piece> defendingBlock) {
        Integer position = attackingBlock.get(0).getPosition();

        if (position == null) {
            return false;
        }

        return allPiecesAtPosition(attackingBlock, position)
                && allPiecesAtPosition(defendingBlock, position);
    }

    private boolean allPiecesAtPosition(List<Piece> pieces, int position) {
        return pieces.stream()
                .allMatch(piece -> piece.getState() == PieceState.STANDARD_PATH
                        && piece.getPosition() != null
                        && piece.getPosition() == position);
    }

    private void validateBlock(List<Piece> block) {

        if (block == null) {
            throw new IllegalArgumentException("Block cannot be null.");
        }

        if (block.size() < 2) {
            throw new IllegalArgumentException("Block must contain at least two pieces.");
        }

        if (block.stream().anyMatch(piece -> piece == null)) {
            throw new IllegalArgumentException("Block cannot contain null pieces.");
        }
    }

    private boolean areOpposingBlocks(List<Piece> attackingBlock, List<Piece> defendingBlock) {

        Colour attackingColour = attackingBlock.get(0).getColour();
        Colour defendingColour = defendingBlock.get(0).getColour();

        if (attackingColour == defendingColour) {
            return false;
        }

        return allPiecesHaveColour(attackingBlock, attackingColour)
                && allPiecesHaveColour(defendingBlock, defendingColour);
    }

    private boolean allPiecesHaveColour(List<Piece> pieces, Colour colour) {
        return pieces.stream().allMatch(piece -> piece.getColour() == colour);
    }
}