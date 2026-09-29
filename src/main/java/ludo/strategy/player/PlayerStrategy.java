package ludo.strategy.player;

import ludo.domain.model.GameAction;
import ludo.domain.model.Player;

import java.util.List;

public interface PlayerStrategy {

    GameAction chooseAction(Player player, List<GameAction> legalActions);
}