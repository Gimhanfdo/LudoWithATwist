package ludo.command;

import ludo.domain.enums.ActionResult;
import ludo.domain.model.GameAction;

public interface GameActionCommand {

    ActionResult execute(GameAction action);
}