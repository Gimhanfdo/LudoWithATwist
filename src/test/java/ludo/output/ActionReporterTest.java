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
    void shouldNotReportFailedAction() {
        Piece piece = red.getPieces().get(0);
        GameAction action = new GameAction(ActionType.MOVE_PIECE, List.of(piece), 4);
        MovementOutcome movementOutcome = new MovementOutcome(MovementResult.NOT_MOVED, 4, 0, 26, 26, List.of(),
                List.of());
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
    void shouldReportCompletelyBlockedMovement() {
        Piece piece = red.getPieces().get(0);
        piece.enterBoard(26, Direction.CLOCKWISE);

        Piece blockerOne = green.getPieces().get(0);
        Piece blockerTwo = green.getPieces().get(1);
        blockerOne.enterBoard(39, Direction.CLOCKWISE);
        blockerTwo.enterBoard(39, Direction.CLOCKWISE);
        blockerOne.moveTo(27);
        blockerTwo.moveTo(27);

        GameAction action = new GameAction(ActionType.MOVE_PIECE, List.of(piece), 4);
        MovementOutcome movementOutcome = new MovementOutcome(MovementResult.NOT_MOVED, 4, 0, 26, 26,
                List.of(blockerOne, blockerTwo), List.of());
        ActionExecutionResult result = ActionExecutionResult.withMovement(ActionResult.NOT_MOVED, movementOutcome);

        reporter.report(action, result, Direction.CLOCKWISE);

        verify(gameOutput).showPieceBlocked(piece, 26, 27, blockerOne);
        verify(gameOutput).showBlockedPieceNotMoved(piece);
        verify(gameOutput, never()).showBlockedPieceMoved(any(Piece.class), anyInt());
    }

    @Test
    void shouldReportMovementShortenedByBlock() {
        Piece piece = red.getPieces().get(0);
        piece.enterBoard(26, Direction.CLOCKWISE);
        piece.moveTo(29);

        Piece blockerOne = green.getPieces().get(0);
        Piece blockerTwo = green.getPieces().get(1);
        blockerOne.enterBoard(39, Direction.CLOCKWISE);
        blockerTwo.enterBoard(39, Direction.CLOCKWISE);
        blockerOne.moveTo(30);
        blockerTwo.moveTo(30);

        GameAction action = new GameAction(ActionType.MOVE_PIECE, List.of(piece), 6);
        MovementOutcome movementOutcome = new MovementOutcome(MovementResult.MOVED, 6, 3, 26, 29,
                List.of(blockerOne, blockerTwo), List.of());
        ActionExecutionResult result = ActionExecutionResult.withMovement(ActionResult.MOVED, movementOutcome);

        reporter.report(action, result, Direction.CLOCKWISE);

        verify(gameOutput).showPieceBlocked(piece, 26, 30, blockerOne);
        verify(gameOutput).showBlockedPieceMoved(piece, 29);
        verify(gameOutput, never()).showBlockedPieceNotMoved(any(Piece.class));
    }

    @Test
    void shouldReportCapturedPiece() {
        Piece attacker = red.getPieces().get(0);
        Piece opponent = blue.getPieces().get(0);

        attacker.enterBoard(26, Direction.CLOCKWISE);
        opponent.enterBoard(13, Direction.CLOCKWISE);
        attacker.moveTo(30);

        GameAction action = new GameAction(ActionType.MOVE_PIECE, List.of(attacker), 4);
        MovementOutcome movementOutcome = new MovementOutcome(MovementResult.CAPTURED, 4, 4, 26, 30, List.of(),
                List.of(opponent));
        opponent.reset();
        ActionExecutionResult result = ActionExecutionResult.withMovement(ActionResult.CAPTURED, movementOutcome);

        reporter.report(action, result, Direction.CLOCKWISE);

        verify(gameOutput).showPieceCaptured(attacker, opponent, 30, 1, 15);
        verify(gameOutput, never()).showPieceMoved(any(Piece.class), anyInt(), anyInt(), anyInt(),
                any(Direction.class));
    }

    @Test
    void shouldReportEachCapturedPiece() {
        Piece attacker = red.getPieces().get(0);
        Piece opponentOne = blue.getPieces().get(0);
        Piece opponentTwo = blue.getPieces().get(1);

        attacker.enterBoard(26, Direction.CLOCKWISE);
        attacker.moveTo(30);

        GameAction action = new GameAction(ActionType.MOVE_PIECE, List.of(attacker), 4);
        MovementOutcome movementOutcome = new MovementOutcome(MovementResult.CAPTURED, 4, 4, 26, 30, List.of(),
                List.of(opponentOne, opponentTwo));
        ActionExecutionResult result = ActionExecutionResult.withMovement(ActionResult.CAPTURED, movementOutcome);

        reporter.report(action, result, Direction.CLOCKWISE);

        verify(gameOutput).showPieceCaptured(attacker, opponentOne, 30, 1, 15);
        verify(gameOutput).showPieceCaptured(attacker, opponentTwo, 30, 1, 15);
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