package ludo.service;

import ludo.domain.enums.ActionType;
import ludo.domain.enums.PieceState;
import ludo.domain.model.ActionAnalysis;
import ludo.domain.model.GameAction;
import ludo.domain.model.GameState;
import ludo.domain.model.Piece;
import ludo.domain.model.Board;
import ludo.domain.model.MysteryCell;

import java.util.List;

public class ActionAnalyzer {

    private final GameState gameState;
    private final PieceEffectService pieceEffectService;
    private final BlockService blockService;
    private final MoveDestinationCalculator destinationCalculator;
    private final Board board;
    private final MysteryCell mysteryCell;

    public ActionAnalyzer(GameState gameState, PieceEffectService pieceEffectService, BlockService blockService,
            MoveDestinationCalculator destinationCalculator, Board board, MysteryCell mysteryCell) {
        if (gameState == null || pieceEffectService == null || blockService == null || destinationCalculator == null
                || board == null || mysteryCell == null) {
            throw new IllegalArgumentException("Action analyzer dependencies cannot be null.");
        }

        this.gameState = gameState;
        this.pieceEffectService = pieceEffectService;
        this.blockService = blockService;
        this.destinationCalculator = destinationCalculator;
        this.board = board;
        this.mysteryCell = mysteryCell;
    }

    private ActionAnalysis neutralAnalysis(GameAction action) {
        return new ActionAnalysis(action, null, false, 0, false, false);
    }

    public ActionAnalysis analyze(GameAction action) {
        if (action == null) {
            throw new IllegalArgumentException("Game action cannot be null.");
        }

        if (action.getType() != ActionType.MOVE_PIECE) {
            return neutralAnalysis(action);
        }

        Piece piece = action.getPieces().get(0);

        if (piece.getState() == PieceState.HOME_STRAIGHT) {
            return analyzeHomeStraightMove(action, piece);
        }

        if (piece.getState() != PieceState.STANDARD_PATH) {
            return neutralAnalysis(action);
        }

        int movementDistance = pieceEffectService.calculateMovement(piece, action.getRoll());
        int allowedDistance = blockService.getAllowedMovementDistance(piece, movementDistance);

        if (allowedDistance <= 0) {
            return neutralAnalysis(action);
        }

        if (board.movesBeyondApproach(piece.getPosition(), allowedDistance, piece.getColour(), piece.getDirection())) {
            return neutralAnalysis(action);
        }

        int destination = destinationCalculator.calculateStandardDestination(piece, allowedDistance);

        int distanceToHome = calculateDistanceToHome(piece, destination);

        return new ActionAnalysis(action, findCapturedPiece(piece, destination), wouldCreateBlock(piece, destination),
                distanceToHome, true, isMysteryDestination(destination));
    }

    private boolean isMysteryDestination(int destination) {
        return mysteryCell.isActive() && mysteryCell.getPosition() == destination;
    }

    private ActionAnalysis analyzeHomeStraightMove(GameAction action, Piece piece) {
        int movementDistance = pieceEffectService.calculateMovement(piece, action.getRoll());

        if (movementDistance <= 0) {
            return neutralAnalysis(action);
        }

        int destination = piece.getPosition() + movementDistance;

        if (destination > Board.HOME_STRAIGHT_SIZE) {
            return neutralAnalysis(action);
        }

        int distanceToHome = Board.HOME_STRAIGHT_SIZE - destination;

        return new ActionAnalysis(action, null, false, distanceToHome, true, false);
    }

    private Piece findCapturedPiece(Piece movingPiece, int destination) {
        List<Piece> occupants = gameState.getPiecesAtStandardPosition(destination);

        if (occupants.size() != 1) {
            return null;
        }

        Piece occupant = occupants.get(0);

        if (occupant.getColour() == movingPiece.getColour()) {
            return null;
        }

        return occupant;
    }

    private int calculateDistanceToHome(Piece piece, int destination) {
        return board.getDistanceToHome(destination, piece.getColour(), piece.getDirection());
    }

    private boolean wouldCreateBlock(Piece movingPiece, int destination) {
        return gameState.getPiecesAtStandardPosition(destination).stream()
                .anyMatch(piece -> piece.getColour() == movingPiece.getColour());
    }
}