package ludo.command;

import ludo.domain.model.GameAction;

public interface GameActionCommand {

    void execute(GameAction action);
}