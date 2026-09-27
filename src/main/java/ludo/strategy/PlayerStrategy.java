package ludo.strategy;

import ludo.domain.model.Movement;
import ludo.domain.model.Player;

import java.util.List;

public interface PlayerStrategy {

    Movement chooseMovement(Player player, List<Movement> legalMovements);
}