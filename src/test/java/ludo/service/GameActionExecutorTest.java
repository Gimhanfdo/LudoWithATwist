package ludo.service;

import ludo.command.GameActionCommand;
import ludo.domain.enums.ActionType;
import ludo.domain.enums.Colour;
import ludo.domain.model.GameAction;
import ludo.domain.model.Piece;
import ludo.factory.GameActionCommandFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameActionExecutorTest {

    private GameActionCommandFactory commandFactory;
    private GameActionCommand command;
    private GameActionExecutor executor;

    @BeforeEach
    void setUp() {
        commandFactory = mock(GameActionCommandFactory.class);
        command = mock(GameActionCommand.class);
        executor = new GameActionExecutor(commandFactory);
    }

    @Test
    void shouldExecuteCommandForAction() {
        Piece piece = new Piece(Colour.RED, 1);
        GameAction action = new GameAction(ActionType.MOVE_PIECE, List.of(piece), 4);

        when(commandFactory.getCommand(ActionType.MOVE_PIECE)).thenReturn(command);

        executor.execute(action);

        verify(commandFactory).getCommand(ActionType.MOVE_PIECE);
        verify(command).execute(action);
    }

    @Test
    void shouldRejectNullAction() {
        assertThrows(IllegalArgumentException.class, () -> executor.execute(null));
        verifyNoInteractions(commandFactory);
    }

    @Test
    void shouldRejectNullCommandFactory() {
        assertThrows(IllegalArgumentException.class, () -> new GameActionExecutor(null));
    }
}