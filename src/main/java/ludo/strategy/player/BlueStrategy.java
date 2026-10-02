package ludo.strategy.player;

import ludo.domain.enums.Direction;
import ludo.domain.model.ActionAnalysis;
import ludo.domain.model.GameAction;
import ludo.domain.model.Piece;
import ludo.domain.model.Player;
import ludo.service.ActionAnalyzer;

import java.util.List;

public class BlueStrategy implements PlayerStrategy {

    private final ActionAnalyzer actionAnalyzer;
    private int nextPieceIndex;

    public BlueStrategy(ActionAnalyzer actionAnalyzer) {
        if (actionAnalyzer == null) {
            throw new IllegalArgumentException("Action analyzer cannot be null.");
        }

        this.actionAnalyzer = actionAnalyzer;
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
        if (piece.getDirection() == Direction.COUNTERCLOCKWISE) {
            GameAction mysteryAction = chooseMysteryAction(analyses);

            if (mysteryAction != null) {
                return mysteryAction;
            }
        }

        if (piece.getDirection() == Direction.CLOCKWISE) {
            GameAction nonMysteryAction = chooseNonMysteryAction(analyses);

            if (nonMysteryAction != null) {
                return nonMysteryAction;
            }
        }

        return analyses.get(0).getAction();
    }

    private GameAction chooseMysteryAction(List<ActionAnalysis> analyses) {
        for (ActionAnalysis analysis : analyses) {
            if (analysis.landsOnMystery()) {
                return analysis.getAction();
            }
        }

        return null;
    }

    private GameAction chooseNonMysteryAction(List<ActionAnalysis> analyses) {
        for (ActionAnalysis analysis : analyses) {
            if (!analysis.landsOnMystery()) {
                return analysis.getAction();
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
}