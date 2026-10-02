package ludo.output;

import ludo.domain.model.Player;

import java.util.List;

public interface GameOutput {

    void showPlayerPieces(Player player);

    void showInitialRoll(Player player, int roll);

    void showFirstPlayer(Player player);

    void showRoundOrder(List<Player> players);

    void showDiceRoll(Player player, int roll);

    void showWinner(Player player);
}