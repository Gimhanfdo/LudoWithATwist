package ludo.factory;

import ludo.command.GameActionCommand;
import ludo.domain.enums.ActionType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameActionCommandFactoryTest {

    private GameActionCommand enterBoardCommand;
    private GameActionCommand movePieceCommand;
    private GameActionCommand moveBlockCommand;
    private GameActionCommandFactory factory;

    @BeforeEach
    void setUp() {
        enterBoardCommand = mock(GameActionCommand.class);
        movePieceCommand = mock(GameActionCommand.class);
        moveBlockCommand = mock(GameActionCommand.class);

        factory = new GameActionCommandFactory(enterBoardCommand, movePieceCommand, moveBlockCommand);
    }

    @Test
    void shouldReturnEnterBoardCommand() {
        assertSame(enterBoardCommand, factory.getCommand(ActionType.ENTER_BOARD));
    }

    @Test
    void shouldReturnMovePieceCommand() {
        assertSame(movePieceCommand, factory.getCommand(ActionType.MOVE_PIECE));
    }

    @Test
    void shouldReturnMoveBlockCommand() {
        assertSame(moveBlockCommand, factory.getCommand(ActionType.MOVE_BLOCK));
    }

    @Test
    void shouldRejectNullActionType() {
        assertThrows(IllegalArgumentException.class, () -> factory.getCommand(null));
    }

    @Test
    void shouldRejectNullEnterBoardCommand() {
        assertThrows(IllegalArgumentException.class,
                () -> new GameActionCommandFactory(null, movePieceCommand, moveBlockCommand));
    }

    @Test
    void shouldRejectNullMovePieceCommand() {
        assertThrows(IllegalArgumentException.class,
                () -> new GameActionCommandFactory(enterBoardCommand, null, moveBlockCommand));
    }

    @Test
    void shouldRejectNullMoveBlockCommand() {
        assertThrows(IllegalArgumentException.class,
                () -> new GameActionCommandFactory(enterBoardCommand, movePieceCommand, null));
    }
}