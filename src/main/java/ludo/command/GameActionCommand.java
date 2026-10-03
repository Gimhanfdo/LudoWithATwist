package ludo.command;

import ludo.domain.model.ActionExecutionResult;
import ludo.domain.model.GameAction;

public interface GameActionCommand {

    ActionExecutionResult execute(GameAction action);
}