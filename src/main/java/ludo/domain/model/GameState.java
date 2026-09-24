package ludo.domain.model;

import ludo.domain.enums.PieceState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameState {

    private final List<Player> players;

    public GameState(List<Player> players) {

        if (players == null) {
            throw new IllegalArgumentException("Players cannot be null.");
        }

        if (players.isEmpty()) {
            throw new IllegalArgumentException("Players cannot be empty.");
        }

        if (players.contains(null)) {
            throw new IllegalArgumentException("Players cannot contain null.");
        }

        this.players = new ArrayList<>(players);
    }

    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public List<Piece> getPiecesAtStandardPosition(int position) {
        List<Piece> piecesAtPosition = new ArrayList<>();

        for (Player player : players) {

            for (Piece piece : player.getPieces()) {

                if (isAtStandardPosition(piece, position)) {
                    piecesAtPosition.add(piece);
                }
            }
        }

        return Collections.unmodifiableList(piecesAtPosition);
    }

    private boolean isAtStandardPosition(Piece piece, int position) {
        return piece.getState() == PieceState.STANDARD_PATH
                && piece.getPosition() != null
                && piece.getPosition() == position;
    }
}