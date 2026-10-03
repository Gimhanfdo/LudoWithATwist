package ludo.output;

import ludo.domain.enums.ActionResult;
import ludo.domain.enums.ActionType;
import ludo.domain.enums.Colour;
import ludo.domain.enums.Direction;
import ludo.domain.model.ActionExecutionResult;
import ludo.domain.model.GameAction;
import ludo.domain.model.GameState;
import ludo.domain.model.MovementOutcome;
import ludo.domain.model.MovementResult;
import ludo.domain.model.Piece;
import ludo.domain.model.Player;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ActionReporterTest {

    private GameOutput gameOutput;
    private GameState gameState;
    private ActionReporter reporter;
    private Player red;
    private Player green;
    private Player yellow;
    private Player blue;

    @BeforeEach
    void setUp() {
        gameOutput = mock(GameOutput.class);
        red = new Player(Colour.RED);
        green = new Player(Colour.GREEN);
        yellow = new Player(Colour.YELLOW);
        blue = new Player(Colour.BLUE);
        gameState = new GameState(List.of(red, green, yellow, blue));
        reporter = new ActionReporter(gameState, gameOutput);
    }

    @Test
    void shouldReportPieceEnteringBoard() {
        Piece piece = red.getPieces().get(0);
        piece.enterBoard(26, Direction.CLOCKWISE);
        GameAction action = new GameAction(ActionType.ENTER_BOARD, List.of(piece), 6);
        ActionExecutionResult result = ActionExecutionResult.of(ActionResult.MOVED);

        reporter.report(action, result, null);

        verify(gameOutput).showPieceEnteredBoard(piece, 1, 15);
    }

    @Test
    void shouldReportStandardPathMovement() {
        Piece piece = red.getPieces().get(0);
        piece.enterBoard(26, Direction.CLOCKWISE);
        piece.moveTo(30);
        GameAction action = new GameAction(ActionType.MOVE_PIECE, List.of(piece), 4);
        MovementOutcome movementOutcome = new MovementOutcome(MovementResult.MOVED, 4, 4, 26, 30, List.of(), List.of());
        ActionExecutionResult result = ActionExecutionResult.withMovement(ActionResult.MOVED, movementOutcome);

        reporter.report(action, result, Direction.CLOCKWISE);

        verify(gameOutput).showPieceMoved(piece, 26, 30, 4, Direction.CLOCKWISE);
    }

    @Test
    void shouldReportActualDistanceWhenMovementIsShortened() {
        Piece piece = red.getPieces().get(0);
        piece.enterBoard(26, Direction.CLOCKWISE);
        piece.moveTo(29);
        GameAction action = new GameAction(ActionType.MOVE_PIECE, List.of(piece), 6);
        Piece blockerOne = new Piece(Colour.BLUE, 1);
        Piece blockerTwo = new Piece(Colour.BLUE, 2);
        MovementOutcome movementOutcome = new MovementOutcome(MovementResult.MOVED, 6, 3, 26, 29, List.of(blockerOne, blockerTwo), List.of());
        ActionExecutionResult result = ActionExecutionResult.withMovement(ActionResult.MOVED, movementOutcome);

        reporter.report(action, result, Direction.CLOCKWISE);

        verify(gameOutput).showPieceMoved(piece, 26, 29, 3, Direction.CLOCKWISE);
    }

    @Test
    void shouldNotReportFailedAction() {
        Piece piece = red.getPieces().get(0);
        GameAction action = new GameAction(ActionType.MOVE_PIECE, List.of(piece), 4);
        MovementOutcome movementOutcome = new MovementOutcome(MovementResult.NOT_MOVED, 4, 0, 26, 26, List.of(), List.of());
        ActionExecutionResult result = ActionExecutionResult.withMovement(ActionResult.NOT_MOVED, movementOutcome);

        reporter.report(action, result, Direction.CLOCKWISE);

        verifyNoInteractions(gameOutput);
    }

    @Test
    void shouldNotReportStandardMovementWithoutMovementOutcome() {
        Piece piece = red.getPieces().get(0);
        piece.enterBoard(26, Direction.CLOCKWISE);
        GameAction action = new GameAction(ActionType.MOVE_PIECE, List.of(piece), 4);
        ActionExecutionResult result = ActionExecutionResult.of(ActionResult.MOVED);

        reporter.report(action, result, Direction.CLOCKWISE);

        verifyNoInteractions(gameOutput);
    }

    @Test
    void shouldRejectNullAction() {
        ActionExecutionResult result = ActionExecutionResult.of(ActionResult.MOVED);

        assertThrows(IllegalArgumentException.class, () -> reporter.report(null, result, null));
    }

    @Test
    void shouldRejectNullExecutionResult() {
        Piece piece = red.getPieces().get(0);
        GameAction action = new GameAction(ActionType.ENTER_BOARD, List.of(piece), 6);

        assertThrows(IllegalArgumentException.class, () -> reporter.report(action, null, null));
    }

    @Test
    void shouldRejectNullGameState() {
        assertThrows(IllegalArgumentException.class, () -> new ActionReporter(null, gameOutput));
    }

    @Test
    void shouldRejectNullGameOutput() {
        assertThrows(IllegalArgumentException.class, () -> new ActionReporter(gameState, null));
    }
}