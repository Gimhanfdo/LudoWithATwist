package ludo.service;

import ludo.domain.enums.ActionType;
import ludo.domain.enums.PieceState;
import ludo.domain.model.ActionAnalysis;
import ludo.domain.model.GameAction;
import ludo.domain.model.GameState;
import ludo.domain.model.Piece;

import java.util.List;

public class ActionAnalyzer {

    private final GameState gameState;
    private final PieceEffectService pieceEffectService;
    private final BlockService blockService;
    private final MoveDestinationCalculator destinationCalculator;

    public ActionAnalyzer(GameState gameState, PieceEffectService pieceEffectService, BlockService blockService,
                          MoveDestinationCalculator destinationCalculator) {
        if (gameState == null || pieceEffectService == null || blockService == null || destinationCalculator == null) {
            throw new IllegalArgumentException("Action analyzer dependencies cannot be null.");
        }

        this.gameState = gameState;
        this.pieceEffectService = pieceEffectService;
        this.blockService = blockService;
        this.destinationCalculator = destinationCalculator;
    }

    public ActionAnalysis analyze(GameAction action) {
        if (action == null) {
            throw new IllegalArgumentException("Game action cannot be null.");
        }

        if (action.getType() != ActionType.MOVE_PIECE) {
            return new ActionAnalysis(action, false, false);
        }

        Piece piece = action.getPieces().get(0);

        if (piece.getState() != PieceState.STANDARD_PATH) {
            return new ActionAnalysis(action, false, false);
        }

        int movementDistance = pieceEffectService.calculateMovement(piece, action.getRoll());
        int allowedDistance = blockService.getAllowedMovementDistance(piece, movementDistance);

        if (allowedDistance <= 0) {
            return new ActionAnalysis(action, false, false);
        }

        int destination = destinationCalculator.calculateStandardDestination(piece, allowedDistance);

        return new ActionAnalysis(action, wouldCapture(piece, destination), wouldCreateBlock(piece, destination));
    }

    private boolean wouldCapture(Piece movingPiece, int destination) {
        List<Piece> occupants = gameState.getPiecesAtStandardPosition(destination);

        return occupants.size() == 1 && occupants.get(0).getColour() != movingPiece.getColour();
    }

    private boolean wouldCreateBlock(Piece movingPiece, int destination) {
        return gameState.getPiecesAtStandardPosition(destination).stream()
                .anyMatch(piece -> piece.getColour() == movingPiece.getColour());
    }
}