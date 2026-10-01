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

    public LegalActionGenerator(PieceEffectService pieceEffectService) {
        if (pieceEffectService == null) {
            throw new IllegalArgumentException("Piece effect service cannot be null.");
        }

        this.pieceEffectService = pieceEffectService;
    }

    public List<GameAction> generateActions(Player player, int roll) {
        validatePlayer(player);
        validateRoll(roll);

        List<GameAction> actions = new ArrayList<>();

        for (Piece piece : player.getPieces()) {
            addLegalAction(actions, piece, roll);
        }

        return List.copyOf(actions);
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
            actions.add(new GameAction(ActionType.MOVE_PIECE, List.of(piece), roll));
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
}