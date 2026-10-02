package ludo.factory;

import ludo.command.GameActionCommand;
import ludo.domain.enums.ActionType;

import java.util.EnumMap;
import java.util.Map;

public class GameActionCommandFactory {

    private final Map<ActionType, GameActionCommand> commands;

    public GameActionCommandFactory(GameActionCommand enterBoardCommand, GameActionCommand movePieceCommand,
                                    GameActionCommand moveBlockCommand) {
        validateCommand(enterBoardCommand, "Enter board");
        validateCommand(movePieceCommand, "Move piece");
        validateCommand(moveBlockCommand, "Move block");

        commands = new EnumMap<>(ActionType.class);

        commands.put(ActionType.ENTER_BOARD, enterBoardCommand);
        commands.put(ActionType.MOVE_PIECE, movePieceCommand);
        commands.put(ActionType.MOVE_BLOCK, moveBlockCommand);
    }

    public GameActionCommand getCommand(ActionType type) {
        if (type == null) {
            throw new IllegalArgumentException("Action type cannot be null.");
        }

        return commands.get(type);
    }

    private void validateCommand(GameActionCommand command, String name) {
        if (command == null) {
            throw new IllegalArgumentException(name + " command cannot be null.");
        }
    }
}