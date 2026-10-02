package ludo.strategy.player;

import ludo.domain.enums.ActionType;
import ludo.domain.model.ActionAnalysis;
import ludo.domain.model.GameAction;
import ludo.domain.model.Piece;
import ludo.domain.model.Player;
import ludo.service.ActionAnalyzer;

import java.util.List;

public class GreenStrategy implements PlayerStrategy {

    private final ActionAnalyzer actionAnalyzer;

    public GreenStrategy(ActionAnalyzer actionAnalyzer) {
        if (actionAnalyzer == null) {
            throw new IllegalArgumentException("Action analyzer cannot be null.");
        }

        this.actionAnalyzer = actionAnalyzer;
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

        GameAction blockCreation = chooseBlockCreation(analyses);

        if (blockCreation != null) {
            return blockCreation;
        }

        GameAction existingBlock = chooseExistingBlock(analyses);

        if (existingBlock != null) {
            return existingBlock;
        }

        GameAction enterBoard = chooseEnterBoardOnSix(analyses);

        if (enterBoard != null) {
            return enterBoard;
        }

        GameAction neededCapture = chooseNeededCapture(analyses);

        if (neededCapture != null) {
            return neededCapture;
        }

        GameAction homeEligiblePiece = chooseHomeEligiblePiece(analyses);

        if (homeEligiblePiece != null) {
            return homeEligiblePiece;
        }

        GameAction closestToHome = chooseClosestToHome(analyses);

        if (closestToHome != null) {
            return closestToHome;
        }

        return legalActions.get(0);
    }

    private GameAction chooseExistingBlock(List<ActionAnalysis> analyses) {
        for (ActionAnalysis analysis : analyses) {
            GameAction action = analysis.getAction();

            if (action.getType() == ActionType.MOVE_BLOCK) {
                return action;
            }
        }

        return null;
    }

    private GameAction chooseBlockCreation(List<ActionAnalysis> analyses) {
        for (ActionAnalysis analysis : analyses) {
            if (analysis.createsBlock()) {
                return analysis.getAction();
            }
        }

        return null;
    }

    private GameAction chooseEnterBoardOnSix(List<ActionAnalysis> analyses) {
        for (ActionAnalysis analysis : analyses) {
            GameAction action = analysis.getAction();

            if (action.getRoll() == 6 && action.getType() == ActionType.ENTER_BOARD) {
                return action;
            }
        }

        return null;
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

    private GameAction chooseNeededCapture(List<ActionAnalysis> analyses) {
        for (ActionAnalysis analysis : analyses) {
            if (!analysis.capturesOpponent()) {
                continue;
            }

            Piece movingPiece = analysis.getAction().getPieces().get(0);

            if (!movingPiece.hasCaptured()) {
                return analysis.getAction();
            }
        }

        return null;
    }

    private GameAction chooseHomeEligiblePiece(List<ActionAnalysis> analyses) {
        ActionAnalysis bestAnalysis = null;

        for (ActionAnalysis analysis : analyses) {
            GameAction action = analysis.getAction();

            if (action.getType() != ActionType.MOVE_PIECE) {
                continue;
            }

            Piece piece = action.getPieces().get(0);

            if (!piece.hasCaptured() || !analysis.isProgressKnown()) {
                continue;
            }

            if (bestAnalysis == null || analysis.getDistanceToHome() < bestAnalysis.getDistanceToHome()) {
                bestAnalysis = analysis;
            }
        }

        return bestAnalysis == null ? null : bestAnalysis.getAction();
    }

    private GameAction chooseClosestToHome(List<ActionAnalysis> analyses) {
        ActionAnalysis bestAnalysis = null;

        for (ActionAnalysis analysis : analyses) {
            if (analysis.getAction().getType() != ActionType.MOVE_PIECE) {
                continue;
            }

            if (!analysis.isProgressKnown()) {
                continue;
            }

            if (bestAnalysis == null || analysis.getDistanceToHome() < bestAnalysis.getDistanceToHome()) {
                bestAnalysis = analysis;
            }
        }

        return bestAnalysis == null ? null : bestAnalysis.getAction();
    }
}