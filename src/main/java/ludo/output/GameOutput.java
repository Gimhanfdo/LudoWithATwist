package ludo.output;

import ludo.domain.enums.Direction;
import ludo.domain.enums.PieceEffect;
import ludo.domain.enums.TeleportDestination;
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

    void showPieceBlocked(Piece piece, int fromPosition, int blockedPosition, Piece blockingPiece);

    void showBlockedPieceNotMoved(Piece piece);

    void showBlockedPieceMoved(Piece piece, int position);

    void showPieceCaptured(Piece attacker, Piece capturedPiece, int position, int piecesOnBoard, int piecesInBase);

    void showMysteryTeleport(Piece piece, TeleportDestination destination);

    void showAlphaEffect(Piece piece, PieceEffect effect);

    void showBetaBriefing(Piece piece);

    void showGammaDirectionChanged(Piece piece);

    void showGammaRedirectedToBeta(Piece piece);

    void showMysteryCellSpawned(int position);

    void showWinner(Player player);
}