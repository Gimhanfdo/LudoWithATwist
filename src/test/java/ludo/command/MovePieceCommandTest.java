package ludo.command;

import ludo.domain.enums.ActionType;
import ludo.domain.enums.Colour;
import ludo.domain.model.GameAction;
import ludo.domain.model.Piece;
import ludo.service.MovementCoordinator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MovePieceCommandTest {

    private MovementCoordinator movementCoordinator;
    private MovePieceCommand command;

    @BeforeEach
    void setUp() {
        movementCoordinator = mock(MovementCoordinator.class);
        command = new MovePieceCommand(movementCoordinator);
    }

    @Test
    void shouldExecutePieceMovement() {
        Piece piece = new Piece(Colour.YELLOW, 1);
        GameAction action = new GameAction(ActionType.MOVE_PIECE, List.of(piece), 4);

        command.execute(action);

        verify(movementCoordinator).move(piece, 4);
    }

    @Test
    void shouldRejectWrongActionType() {
        Piece piece = new Piece(Colour.YELLOW, 1);
        GameAction action = new GameAction(ActionType.ENTER_BOARD, List.of(piece), 6);

        assertThrows(IllegalArgumentException.class, () -> command.execute(action));
        verifyNoInteractions(movementCoordinator);
    }

    @Test
    void shouldRejectNullAction() {
        assertThrows(IllegalArgumentException.class, () -> command.execute(null));
    }

    @Test
    void shouldRejectNullMovementCoordinator() {
        assertThrows(IllegalArgumentException.class, () -> new MovePieceCommand(null));
    }
}