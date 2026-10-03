package ludo.service;

import ludo.domain.enums.ActionType;
import ludo.domain.enums.PieceState;
import ludo.domain.model.GameAction;
import ludo.domain.model.Piece;
import ludo.domain.model.Player;

import java.util.ArrayList;
import java.util.List;

public class LegalActionGenerator {

    private static final int REQUIRED_ROLL_TO_ENTER_BOARD = 6;

    private final PieceEffectService pieceEffectService;
    private final BlockService blockService;
    private final MoveValidator moveValidator;

    public LegalActionGenerator(PieceEffectService pieceEffectService, BlockService blockService,
            MoveValidator moveValidator) {
        if (pieceEffectService == null) {
            throw new IllegalArgumentException("Piece effect service cannot be null.");
        }

        if (blockService == null) {
            throw new IllegalArgumentException("Block service cannot be null.");
        }

        if (moveValidator == null) {
            throw new IllegalArgumentException("Move validator cannot be null.");
        }

        this.pieceEffectService = pieceEffectService;
        this.blockService = blockService;
        this.moveValidator = moveValidator;
    }

    public List<GameAction> generateActions(Player player, int roll) {
        validatePlayer(player);
        validateRoll(roll);

        List<GameAction> actions = new ArrayList<>();

        for (Piece piece : player.getPieces()) {
            addLegalAction(actions, piece, roll);
        }

        removeRestrictedPieceMovesWhenAlternativeExists(actions);
        addBlockActions(actions, player, roll);

        return List.copyOf(actions);
    }

    private void removeRestrictedPieceMovesWhenAlternativeExists(List<GameAction> actions) {
        boolean hasUnrestrictedPieceMove = actions.stream()
                .filter(this::isPieceMove)
                .anyMatch(action -> !isRestrictedByBlock(action));

        if (!hasUnrestrictedPieceMove) {
            return;
        }

        actions.removeIf(action -> isPieceMove(action) && isRestrictedByBlock(action));
    }

    private boolean isPieceMove(GameAction action) {
        return action.getType() == ActionType.MOVE_PIECE;
    }

    private boolean isRestrictedByBlock(GameAction action) {
        Piece piece = action.getPieces().get(0);

        if (piece.getState() != PieceState.STANDARD_PATH) {
            return false;
        }

        int movementDistance = pieceEffectService.calculateMovement(piece, action.getRoll());
        int allowedDistance = blockService.getAllowedMovementDistance(piece, movementDistance);

        return allowedDistance < movementDistance;
    }

    private void addIfValid(List<GameAction> actions, GameAction action) {
        if (moveValidator.isValid(action)) {
            actions.add(action);
        }
    }

    private void addLegalAction(List<GameAction> actions, Piece piece, int roll) {
        if (piece.getState() == PieceState.BASE) {
            addEnterBoardAction(actions, piece, roll);
            return;
        }

        if (piece.getState() == PieceState.HOME) {
            return;
        }

        if (!pieceEffectService.canMove(piece)) {
            return;
        }

        if (piece.getState() == PieceState.STANDARD_PATH || piece.getState() == PieceState.HOME_STRAIGHT) {
            addIfValid(actions, new GameAction(ActionType.MOVE_PIECE, List.of(piece), roll));
        }
    }

    private void addEnterBoardAction(List<GameAction> actions, Piece piece, int roll) {
        if (roll == REQUIRED_ROLL_TO_ENTER_BOARD) {
            actions.add(new GameAction(ActionType.ENTER_BOARD, List.of(piece), roll));
        }
    }

    private void validatePlayer(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null.");
        }
    }

    private void validateRoll(int roll) {
        if (roll < 1 || roll > 6) {
            throw new IllegalArgumentException("Roll must be between 1 and 6.");
        }
    }

    private void addBlockActions(List<GameAction> actions, Player player, int roll) {
        List<Integer> processedPositions = new ArrayList<>();

        for (Piece piece : player.getPieces()) {
            if (piece.getState() != PieceState.STANDARD_PATH) {
                continue;
            }

            int position = piece.getPosition();

            if (processedPositions.contains(position)) {
                continue;
            }

            processedPositions.add(position);

            if (!blockService.hasBlockAt(position, player.getColour())) {
                continue;
            }

            List<Piece> block = blockService.getBlockAt(position, player.getColour());

            if (!canMoveBlock(block)) {
                continue;
            }

            addIfValid(actions, new GameAction(ActionType.MOVE_BLOCK, block, roll));
        }
    }

    private boolean canMoveBlock(List<Piece> block) {
        for (Piece piece : block) {
            if (!pieceEffectService.canMove(piece)) {
                return false;
            }
        }

        return true;
    }
}