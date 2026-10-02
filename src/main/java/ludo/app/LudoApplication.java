package ludo.app;

import ludo.command.EnterBoardCommand;
import ludo.command.MoveBlockCommand;
import ludo.command.MovePieceCommand;
import ludo.domain.enums.Colour;
import ludo.domain.model.Board;
import ludo.domain.model.GameState;
import ludo.domain.model.MysteryCell;
import ludo.domain.model.Player;
import ludo.engine.GameEngine;
import ludo.engine.RoundManager;
import ludo.engine.TurnManager;
import ludo.factory.GameActionCommandFactory;
import ludo.factory.PlayerStrategyFactory;
import ludo.observer.MysteryCellRoundObserver;
import ludo.observer.PieceEffectRoundObserver;
import ludo.observer.RoundNotifier;
import ludo.random.*;
import ludo.service.*;
import ludo.strategy.effect.EnergisedMovementStrategy;
import ludo.strategy.effect.MovementEffectStrategy;
import ludo.strategy.effect.SickMovementStrategy;
import ludo.strategy.player.BlueStrategy;
import ludo.strategy.player.GreenStrategy;
import ludo.strategy.player.RedStrategy;
import ludo.strategy.player.YellowStrategy;
import ludo.strategy.teleport.*;
import ludo.output.ConsoleGameOutput;
import ludo.output.GameOutput;

import java.util.List;

public class LudoApplication {

    public static void main(String[] args) {
        GameEngine gameEngine = createGame();
        gameEngine.play();
    }

    static GameEngine createGame() {

        // Domain
        Board board = new Board();
        Player red = new Player(Colour.RED);
        Player green = new Player(Colour.GREEN);
        Player yellow = new Player(Colour.YELLOW);
        Player blue = new Player(Colour.BLUE);
        GameState gameState = new GameState(List.of(red, green, yellow, blue));
        MysteryCell mysteryCell = new MysteryCell();

        GameOutput gameOutput = new ConsoleGameOutput();

        // Random implementations
        Dice dice = new RandomDice();
        Coin coin = new RandomCoin();
        MovementDistributor movementDistributor = new RandomMovementDistributor();
        PositionGenerator positionGenerator = new RandomPositionGenerator();
        TeleportDestinationSelector destinationSelector = new RandomTeleportDestinationSelector();
        AlphaEffectSelector alphaEffectSelector = new RandomAlphaEffectSelector();
        ActionSelector actionSelector = new RandomActionSelector();

        FirstPlayerSelector firstPlayerSelector = new FirstPlayerSelector(dice, gameOutput);

        // Movement effects
        MovementEffectStrategy energisedMovementStrategy = new EnergisedMovementStrategy();
        MovementEffectStrategy sickMovementStrategy = new SickMovementStrategy();
        PieceEffectService pieceEffectService = new PieceEffectService(
                List.of(energisedMovementStrategy, sickMovementStrategy));

        // Core movement services
        MoveExecutor moveExecutor = new MoveExecutor(board, coin);
        CaptureService captureService = new CaptureService();
        BlockService blockService = new BlockService(gameState, board, movementDistributor);
        MovementService movementService = new MovementService(moveExecutor, captureService, blockService, gameState);
        MoveValidator moveValidator = new MoveValidator(pieceEffectService, blockService);
        MoveDestinationCalculator destinationCalculator = new MoveDestinationCalculator();

        // Mystery-cell position management
        MysteryPositionSelector mysteryPositionSelector = new MysteryPositionSelector(gameState, positionGenerator);
        MysteryCellService mysteryCellService = new MysteryCellService(mysteryCell, mysteryPositionSelector, gameState);

        // Mystery teleport strategies
        TeleportStrategy alphaTeleportStrategy = new AlphaTeleportStrategy(alphaEffectSelector);
        TeleportStrategy betaTeleportStrategy = new BetaTeleportStrategy();
        TeleportStrategy gammaTeleportStrategy = new GammaTeleportStrategy(betaTeleportStrategy);
        TeleportStrategy baseTeleportStrategy = new BaseTeleportStrategy();
        TeleportStrategy xTeleportStrategy = new XTeleportStrategy(board);
        TeleportStrategy approachTeleportStrategy = new ApproachTeleportStrategy(board);

        MysteryTeleportService mysteryTeleportService = new MysteryTeleportService(
                destinationSelector,
                List.of(alphaTeleportStrategy, betaTeleportStrategy, gammaTeleportStrategy,
                        baseTeleportStrategy, xTeleportStrategy, approachTeleportStrategy));

        MysteryLandingService mysteryLandingService = new MysteryLandingService(mysteryCell, mysteryTeleportService);

        // Movement coordination
        MovementCoordinator movementCoordinator = new MovementCoordinator(
                movementService, pieceEffectService, mysteryLandingService);

        // Action analysis and generation
        ActionAnalyzer actionAnalyzer = new ActionAnalyzer(
                gameState, pieceEffectService, blockService, destinationCalculator, board, mysteryCell);

        LegalActionGenerator legalActionGenerator = new LegalActionGenerator(
                pieceEffectService, blockService, moveValidator);

        // Player strategies
        RedStrategy redStrategy = new RedStrategy(actionAnalyzer, board);
        GreenStrategy greenStrategy = new GreenStrategy(actionAnalyzer);
        YellowStrategy yellowStrategy = new YellowStrategy(actionAnalyzer);
        BlueStrategy blueStrategy = new BlueStrategy(actionAnalyzer, actionSelector);

        PlayerStrategyFactory playerStrategyFactory = new PlayerStrategyFactory(
                redStrategy, greenStrategy, yellowStrategy, blueStrategy);

        // Commands
        EnterBoardCommand enterBoardCommand = new EnterBoardCommand(moveExecutor);
        MovePieceCommand movePieceCommand = new MovePieceCommand(movementCoordinator);
        MoveBlockCommand moveBlockCommand = new MoveBlockCommand(movementService);

        GameActionCommandFactory commandFactory = new GameActionCommandFactory(
                enterBoardCommand, movePieceCommand, moveBlockCommand);

        GameActionExecutor actionExecutor = new GameActionExecutor(commandFactory, gameState, gameOutput);

        // Turn rules
        ConsecutiveSixTracker consecutiveSixTracker = new ConsecutiveSixTracker();
        ForcedBlockBreakService forcedBlockBreakService = new ForcedBlockBreakService(blockService);
        BetaRollTracker betaRollTracker = new BetaRollTracker();
        BetaBriefingService betaBriefingService = new BetaBriefingService(betaRollTracker);

        TurnManager turnManager = new TurnManager(
                dice, legalActionGenerator, playerStrategyFactory, actionExecutor,
                consecutiveSixTracker, forcedBlockBreakService, betaBriefingService, gameOutput);

        // Round observers
        MysteryCellRoundObserver mysteryCellRoundObserver = new MysteryCellRoundObserver(mysteryCellService);
        PieceEffectRoundObserver pieceEffectRoundObserver = new PieceEffectRoundObserver(gameState, pieceEffectService);

        RoundNotifier roundNotifier = new RoundNotifier(
                List.of(mysteryCellRoundObserver, pieceEffectRoundObserver));

        // Game lifecycle
        GameCompletionService gameCompletionService = new GameCompletionService();
        RoundManager roundManager = new RoundManager(turnManager, roundNotifier, gameCompletionService);

        return new GameEngine(gameState, roundManager, firstPlayerSelector, gameOutput);
    }
}