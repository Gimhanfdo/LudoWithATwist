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

        List<Piece> occupants = gameState.getPiecesAtStandardPosition(attacker.getPosition());

        for (Piece occupant : occupants) {
            if (occupant == attacker) {
                continue;
            }

            if (!canCapture(attacker, occupant)) {
                continue;
            }

            if (isPartOfBlock(occupant, occupants)) {
                continue;
            }

            return capture(attacker, occupant);
        }

        return false;
    }

    private boolean isPartOfBlock(Piece piece, List<Piece> occupants) {
        long sameColourCount = occupants.stream()
                .filter(occupant -> occupant.getColour() == piece.getColour())
                .count();

        return sameColourCount >= 2;
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

    public boolean resolveBlockCapture(List<Piece> attackingBlock, GameState gameState) {

        validateBlock(attackingBlock);

        if (gameState == null) {
            throw new IllegalArgumentException("Game state cannot be null.");
        }

        if (!formsBlock(attackingBlock)) {
            return false;
        }

        int position = attackingBlock.get(0).getPosition();
        List<Piece> occupants = gameState.getPiecesAtStandardPosition(position);
        Colour attackingColour = attackingBlock.get(0).getColour();

        for (Colour colour : Colour.values()) {
            if (colour == attackingColour) {
                continue;
            }

            List<Piece> defendingBlock = getPiecesOfColour(occupants, colour);

            if (defendingBlock.size() != attackingBlock.size()) {
                continue;
            }

            if (captureBlock(attackingBlock, defendingBlock)) {
                return true;
            }
        }

        return false;
    }

    private List<Piece> getPiecesOfColour(List<Piece> pieces, Colour colour) {
        return pieces.stream()
                .filter(piece -> piece.getColour() == colour)
                .toList();
    }

    private boolean formsBlock(List<Piece> pieces) {

        Piece firstPiece = pieces.get(0);

        if (firstPiece.getState() != PieceState.STANDARD_PATH || firstPiece.getPosition() == null) {
            return false;
        }

        Colour colour = firstPiece.getColour();
        int position = firstPiece.getPosition();

        return pieces.stream()
                .allMatch(piece -> piece.getColour() == colour
                        && piece.getState() == PieceState.STANDARD_PATH
                        && piece.getPosition() != null
                        && piece.getPosition() == position);
    }

    public List<Piece> getCapturablePieces(Piece attacker, GameState gameState) {
        validateAttacker(attacker);

        if (gameState == null) {
            throw new IllegalArgumentException("Game state cannot be null.");
        }

        if (attacker.getState() != PieceState.STANDARD_PATH) {
            return List.of();
        }

        List<Piece> occupants = gameState.getPiecesAtStandardPosition(attacker.getPosition());

        for (Piece occupant : occupants) {
            if (occupant == attacker) {
                continue;
            }

            if (!canCapture(attacker, occupant)) {
                continue;
            }

            if (isPartOfBlock(occupant, occupants)) {
                continue;
            }

            return List.of(occupant);
        }

        return List.of();
    }

    public boolean resolveCaptureForReporting(Piece attacker, GameState gameState) {
        List<Piece> capturablePieces = getCapturablePieces(attacker, gameState);

        if (capturablePieces.isEmpty()) {
            return false;
        }

        return capture(attacker, capturablePieces.get(0));
    }
}