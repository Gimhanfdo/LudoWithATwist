package ludo.strategy.player;

import ludo.domain.enums.Direction;
import ludo.domain.model.ActionAnalysis;
import ludo.domain.model.GameAction;
import ludo.domain.model.Piece;
import ludo.domain.model.Player;
import ludo.random.ActionSelector;
import ludo.service.ActionAnalyzer;

import java.util.List;

public class BlueStrategy implements PlayerStrategy {

    private final ActionAnalyzer actionAnalyzer;
    private final ActionSelector actionSelector;
    private int nextPieceIndex;

    public BlueStrategy(ActionAnalyzer actionAnalyzer, ActionSelector actionSelector) {
        if (actionAnalyzer == null) {
            throw new IllegalArgumentException("Action analyzer cannot be null.");
        }

        if (actionSelector == null) {
            throw new IllegalArgumentException("Action selector cannot be null.");
        }

        this.actionAnalyzer = actionAnalyzer;
        this.actionSelector = actionSelector;
        this.nextPieceIndex = 0;
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

        for (int offset = 0; offset < player.getPieces().size(); offset++) {
            int pieceIndex = (nextPieceIndex + offset) % player.getPieces().size();
            Piece piece = player.getPieces().get(pieceIndex);
            List<ActionAnalysis> pieceActions = findActionsForPiece(piece, analyses);

            if (pieceActions.isEmpty()) {
                continue;
            }

            GameAction chosen = chooseForPiece(piece, pieceActions);
            nextPieceIndex = (pieceIndex + 1) % player.getPieces().size();

            return chosen;
        }

        return legalActions.get(0);
    }

    private List<ActionAnalysis> findActionsForPiece(Piece piece, List<ActionAnalysis> analyses) {
        return analyses.stream()
                .filter(analysis -> analysis.getAction().getPieces().contains(piece))
                .toList();
    }

    private GameAction chooseForPiece(Piece piece, List<ActionAnalysis> analyses) {
        List<GameAction> preferredActions = getPreferredActions(piece, analyses);

        return actionSelector.select(preferredActions);
    }

    private List<GameAction> getPreferredActions(Piece piece, List<ActionAnalysis> analyses) {
        if (piece.getDirection() == Direction.COUNTERCLOCKWISE) {
            List<GameAction> mysteryActions = getMysteryActions(analyses);

            if (!mysteryActions.isEmpty()) {
                return mysteryActions;
            }
        }

        if (piece.getDirection() == Direction.CLOCKWISE) {
            List<GameAction> nonMysteryActions = getNonMysteryActions(analyses);

            if (!nonMysteryActions.isEmpty()) {
                return nonMysteryActions;
            }
        }

        return getActions(analyses);
    }

    private List<GameAction> getMysteryActions(List<ActionAnalysis> analyses) {
        return analyses.stream()
                .filter(ActionAnalysis::landsOnMystery)
                .map(ActionAnalysis::getAction)
                .toList();
    }

    private List<GameAction> getNonMysteryActions(List<ActionAnalysis> analyses) {
        return analyses.stream()
                .filter(analysis -> !analysis.landsOnMystery())
                .map(ActionAnalysis::getAction)
                .toList();
    }

    private List<GameAction> getActions(List<ActionAnalysis> analyses) {
        return analyses.stream()
                .map(ActionAnalysis::getAction)
                .toList();
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