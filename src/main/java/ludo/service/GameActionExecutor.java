package ludo.service;

import ludo.command.GameActionCommand;
import ludo.domain.enums.ActionResult;
import ludo.domain.model.GameAction;
import ludo.factory.GameActionCommandFactory;

public class GameActionExecutor {

    private final GameActionCommandFactory commandFactory;

    public GameActionExecutor(GameActionCommandFactory commandFactory) {
        if (commandFactory == null) {
            throw new IllegalArgumentException("Command factory cannot be null.");
        }

        this.commandFactory = commandFactory;
    }

    public ActionResult execute(GameAction action) {
        if (action == null) {
            throw new IllegalArgumentException("Game action cannot be null.");
        }

        GameActionCommand command = commandFactory.getCommand(action.getType());

        return command.execute(action);
    }
}