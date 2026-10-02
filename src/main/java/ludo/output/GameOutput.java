package ludo.output;

import ludo.domain.enums.Direction;
import ludo.domain.model.Piece;
import ludo.domain.model.Player;

import java.util.List;

public interface GameOutput {

    void showPlayerPieces(Player player);

    void showInitialRoll(Player player, int roll);

    void showFirstPlayer(Player player);

    void showRoundOrder(List<Player> players);

    void showDiceRoll(Player player, int roll);

    void showPieceEnteredBoard(Piece piece, int piecesOnBoard, int piecesInBase);

    void showPieceMoved(Piece piece, int fromPosition, int toPosition, int distance, Direction direction);

    void showWinner(Player player);
}