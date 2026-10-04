package ludo.engine;

import ludo.command.EnterBoardCommand;
import ludo.command.MoveBlockCommand;
import ludo.command.MovePieceCommand;
import ludo.domain.enums.Colour;
import ludo.domain.enums.Direction;
import ludo.domain.enums.PieceState;
import ludo.domain.model.Board;
import ludo.domain.model.GameState;
import ludo.domain.model.Piece;
import ludo.domain.model.Player;
import ludo.factory.GameActionCommandFactory;
import ludo.factory.PlayerStrategyFactory;
import ludo.output.ActionReporter;
import ludo.output.GameOutput;
import ludo.random.Dice;
import ludo.random.MovementDistributor;
import ludo.service.BetaBriefingService;
import ludo.service.BetaRollTracker;
import ludo.service.BlockService;
import ludo.service.CaptureService;
import ludo.service.ConsecutiveSixTracker;
import ludo.service.ForcedBlockBreakService;
import ludo.service.GameActionExecutor;
import ludo.service.LegalActionGenerator;
import ludo.service.MoveExecutor;
import ludo.service.MoveValidator;
import ludo.service.MovementCoordinator;
import ludo.service.MovementService;
import ludo.service.MysteryLandingService;
import ludo.service.PieceEffectService;
import ludo.strategy.effect.EnergisedMovementStrategy;
import ludo.strategy.effect.MovementEffectStrategy;
import ludo.strategy.effect.SickMovementStrategy;
import ludo.strategy.player.FirstAvailableStrategy;
import ludo.strategy.player.PlayerStrategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TurnManagerIntegrationTest {

    private Player red;
    private Piece piece;
    private GameOutput gameOutput;
    private TurnManager turnManager;

    @BeforeEach
    void setUp() {
        Board board = new Board();

        red = new Player(Colour.RED);
        Player green = new Player(Colour.GREEN);
        Player yellow = new Player(Colour.YELLOW);
        Player blue = new Player(Colour.BLUE);

        GameState gameState = new GameState(List.of(red, green, yellow, blue));

        piece = red.getPieces().get(0);
        piece.enterBoard(board.getStartPosition(Colour.RED), Direction.CLOCKWISE);

        Dice dice = mock(Dice.class);
        when(dice.roll()).thenReturn(2);

        gameOutput = mock(GameOutput.class);

        MovementEffectStrategy energisedMovementStrategy = new EnergisedMovementStrategy();
        MovementEffectStrategy sickMovementStrategy = new SickMovementStrategy();
        PieceEffectService pieceEffectService = new PieceEffectService(
                List.of(energisedMovementStrategy, sickMovementStrategy));

        MovementDistributor movementDistributor = mock(MovementDistributor.class);

        MoveExecutor moveExecutor = new MoveExecutor(board, mock(ludo.random.Coin.class));
        CaptureService captureService = new CaptureService();
        BlockService blockService = new BlockService(gameState, board, movementDistributor);
        MovementService movementService = new MovementService(moveExecutor, captureService, blockService, gameState);
        MoveValidator moveValidator = new MoveValidator(pieceEffectService, blockService);
        LegalActionGenerator legalActionGenerator = new LegalActionGenerator(pieceEffectService, blockService, moveValidator);

        MysteryLandingService mysteryLandingService = mock(MysteryLandingService.class);
        MovementCoordinator movementCoordinator = new MovementCoordinator(
                movementService, pieceEffectService, mysteryLandingService);

        EnterBoardCommand enterBoardCommand = new EnterBoardCommand(moveExecutor);
        MovePieceCommand movePieceCommand = new MovePieceCommand(movementCoordinator);
        MoveBlockCommand moveBlockCommand = new MoveBlockCommand(movementService);
        GameActionCommandFactory commandFactory = new GameActionCommandFactory(
                enterBoardCommand, movePieceCommand, moveBlockCommand);

        ActionReporter actionReporter = new ActionReporter(gameState, gameOutput);
        GameActionExecutor actionExecutor = new GameActionExecutor(commandFactory, actionReporter);

        PlayerStrategy strategy = new FirstAvailableStrategy();
        PlayerStrategyFactory strategyFactory = new PlayerStrategyFactory(strategy, strategy, strategy, strategy);

        ConsecutiveSixTracker consecutiveSixTracker = new ConsecutiveSixTracker();
        ForcedBlockBreakService forcedBlockBreakService = new ForcedBlockBreakService(blockService);

        BetaRollTracker betaRollTracker = new BetaRollTracker();
        BetaBriefingService betaBriefingService = new BetaBriefingService(betaRollTracker);

        turnManager = new TurnManager(dice, legalActionGenerator, strategyFactory, actionExecutor,
                consecutiveSixTracker, forcedBlockBreakService, betaBriefingService, gameOutput);
    }

    @Test
    void shouldMovePieceThroughRealTurnPipeline() {
        turnManager.takeTurn(red);

        assertEquals(PieceState.STANDARD_PATH, piece.getState());
        assertEquals(28, piece.getPosition());

        verify(gameOutput).showDiceRoll(red, 2);
        verify(gameOutput).showPieceMoved(piece, 26, 28, 2, Direction.CLOCKWISE);
    }
}