package ludo.strategy.player;

import ludo.domain.enums.ActionType;
import ludo.domain.enums.PieceState;
import ludo.domain.model.ActionAnalysis;
import ludo.domain.model.Board;
import ludo.domain.model.GameAction;
import ludo.domain.model.Piece;
import ludo.domain.model.Player;
import ludo.service.ActionAnalyzer;

import java.util.List;

public class RedStrategy implements PlayerStrategy {

    private final ActionAnalyzer actionAnalyzer;
    private final Board board;

    public RedStrategy(ActionAnalyzer actionAnalyzer, Board board) {
        if (actionAnalyzer == null) {
            throw new IllegalArgumentException("Action analyzer cannot be null.");
        }

        if (board == null) {
            throw new IllegalArgumentException("Board cannot be null.");
        }

        this.actionAnalyzer = actionAnalyzer;
        this.board = board;
    }

    @Override
    public GameAction chooseAction(Player player, List<GameAction> legalActions) {
        validatePlayer(player);
        validateActions(legalActions);

        if (legalActions.isEmpty()) {
            return null;
        }

        List<ActionAnalysis> analyses = legalActions.stream()
                .map(actionAnalyzer::analyze)
                .toList();

        GameAction capture = chooseCapture(analyses);

        if (capture != null) {
            return capture;
        }

        List<ActionAnalysis> preferredActions = analyses.stream()
                .filter(analysis -> !analysis.createsBlock())
                .toList();

        if (preferredActions.isEmpty()) {
            preferredActions = analyses;
        }

        GameAction existingPieceMove = chooseExistingPieceOnSix(player, preferredActions);

        if (existingPieceMove != null) {
            return existingPieceMove;
        }

        return preferredActions.get(0).getAction();
    }

    private GameAction chooseCapture(List<ActionAnalysis> analyses) {
        ActionAnalysis bestCapture = null;
        int shortestOpponentDistance = Integer.MAX_VALUE;

        for (ActionAnalysis analysis : analyses) {
            if (!analysis.capturesOpponent()) {
                continue;
            }

            Piece opponent = analysis.getCapturedPiece();
            int opponentDistance = board.getDistanceToHome(opponent.getPosition(), opponent.getColour(),
                    opponent.getDirection());

            if (opponentDistance < shortestOpponentDistance) {
                shortestOpponentDistance = opponentDistance;
                bestCapture = analysis;
            }
        }

        return bestCapture == null ? null : bestCapture.getAction();
    }

    private GameAction chooseExistingPieceOnSix(Player player, List<ActionAnalysis> analyses) {
        if (!hasPieceOnStandardPath(player)) {
            return null;
        }

        for (ActionAnalysis analysis : analyses) {
            GameAction action = analysis.getAction();

            if (action.getRoll() == 6
                    && action.getType() == ActionType.MOVE_PIECE
                    && action.getPieces().get(0).getState() == PieceState.STANDARD_PATH) {
                return action;
            }
        }

        return null;
    }

    private boolean hasPieceOnStandardPath(Player player) {
        return player.getPieces().stream()
                .anyMatch(piece -> piece.getState() == PieceState.STANDARD_PATH);
    }

    private void validatePlayer(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null.");
        }
    }

    private void validateActions(List<GameAction> legalActions) {
        if (legalActions == null) {
            throw new IllegalArgumentException("Legal actions cannot be null.");
        }

        for (GameAction action : legalActions) {
            if (action == null) {
                throw new IllegalArgumentException("Legal actions cannot contain null.");
            }
        }
    }
}