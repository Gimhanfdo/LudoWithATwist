package ludo.output;

import ludo.domain.enums.Colour;
import ludo.domain.enums.Direction;
import ludo.domain.enums.PieceEffect;
import ludo.domain.enums.TeleportDestination;
import ludo.domain.model.MysteryCell;
import ludo.domain.model.Piece;
import ludo.domain.model.Player;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsoleGameOutputTest {

    private ByteArrayOutputStream output;
    private ConsoleGameOutput gameOutput;
    private PrintStream originalOutput;
    private Piece piece;

    @BeforeEach
    void setUp() {
        originalOutput = System.out;
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        gameOutput = new ConsoleGameOutput();
        piece = new Piece(Colour.RED, 1);
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOutput);
    }

    @Test
    void shouldShowMysteryLanding() {
        gameOutput.showMysteryLanding(piece, TeleportDestination.ALPHA);

        assertEquals("red player lands on a mystery cell and is teleported to Alpha.", getOutput());
    }

    @Test
    void shouldShowAlphaTeleport() {
        gameOutput.showMysteryTeleport(piece, TeleportDestination.ALPHA);

        assertEquals("red piece R1 teleported to Alpha.", getOutput());
    }

    @Test
    void shouldShowBetaTeleport() {
        gameOutput.showMysteryTeleport(piece, TeleportDestination.BETA);

        assertEquals("red piece R1 teleported to Beta.", getOutput());
    }

    @Test
    void shouldShowGammaTeleport() {
        gameOutput.showMysteryTeleport(piece, TeleportDestination.GAMMA);

        assertEquals("red piece R1 teleported to Gamma.", getOutput());
    }

    @Test
    void shouldShowApproachTeleport() {
        gameOutput.showMysteryTeleport(piece, TeleportDestination.APPROACH);

        assertEquals("red piece R1 teleported to Approach.", getOutput());
    }

    @Test
    void shouldShowXTeleport() {
        gameOutput.showMysteryTeleport(piece, TeleportDestination.X);

        assertEquals("red piece R1 teleported to X.", getOutput());
    }

    @Test
    void shouldShowBaseTeleport() {
        gameOutput.showMysteryTeleport(piece, TeleportDestination.BASE);

        assertEquals("red piece R1 teleported to Base.", getOutput());
    }

    @Test
    void shouldShowEnergisedAlphaEffect() {
        gameOutput.showAlphaEffect(piece, PieceEffect.ENERGISED);

        assertEquals("red piece R1 feels energized, and movement speed doubles.", getOutput());
    }

    @Test
    void shouldShowSickAlphaEffect() {
        gameOutput.showAlphaEffect(piece, PieceEffect.SICK);

        assertEquals("red piece R1 feels sick, and movement speed halves.", getOutput());
    }

    @Test
    void shouldShowBetaBriefing() {
        gameOutput.showBetaBriefing(piece);

        assertEquals("red piece R1 attends briefing and cannot move for four rounds.", getOutput());
    }

    @Test
    void shouldShowGammaDirectionChange() {
        gameOutput.showGammaDirectionChanged(piece);

        assertEquals("The red piece R1, which was moving clockwise, has changed to moving counterclockwise.",
                getOutput());
    }

    @Test
    void shouldShowGammaRedirectToBeta() {
        gameOutput.showGammaRedirectedToBeta(piece);

        assertEquals("The red piece R1 is moving in a counterclockwise direction. Teleporting to Beta from Gamma.",
                getOutput());
    }

    @Test
    void shouldShowMysteryCellSpawn() {
        gameOutput.showMysteryCellSpawned(20);

        assertEquals("A mystery cell has spawned in location 20 and will be at this location for the next four rounds.",
                getOutput());
    }

    @Test
    void shouldShowBriefingPieceReturnedToBase() {
        gameOutput.showBriefingPieceReturnedToBase(piece);

        assertEquals("red piece R1 is movement-restricted and has rolled three consecutively. "
                + "Teleporting piece R1 to base.", getOutput());
    }

    @Test
    void shouldShowPlayerPieceCountWhenAllPiecesAreInBase() {
        Player player = new Player(Colour.RED);

        gameOutput.showPlayerPieceCount(player);

        assertEquals("red player now has 0/4 on pieces on the board and 4/4 pieces on the base.", getOutput());
    }

    @Test
    void shouldShowPlayerPieceCountWithPiecesOnStandardPath() {
        Player player = new Player(Colour.RED);

        player.getPieces().get(0).enterBoard(26, Direction.CLOCKWISE);
        player.getPieces().get(1).enterBoard(26, Direction.CLOCKWISE);

        gameOutput.showPlayerPieceCount(player);

        assertEquals("red player now has 2/4 on pieces on the board and 2/4 pieces on the base.", getOutput());
    }

    @Test
    void shouldCountHomeStraightPieceAsOnBoard() {
        Player player = new Player(Colour.RED);
        Piece piece = player.getPieces().get(0);

        piece.enterBoard(26, Direction.CLOCKWISE);
        piece.recordCapture();
        piece.enterHomeStraight(0);

        gameOutput.showPlayerPieceCount(player);

        assertEquals("red player now has 1/4 on pieces on the board and 3/4 pieces on the base.", getOutput());
    }

    private String getOutput() {
        return output.toString().trim();
    }

    @Test
    void shouldShowLocationsWhenAllPiecesAreInBase() {
        Player player = new Player(Colour.RED);

        gameOutput.showPieceLocations(player);

        assertEquals("============================" + System.lineSeparator()
                + "Location of pieces red" + System.lineSeparator()
                + "============================" + System.lineSeparator()
                + "Piece R1 -> Base." + System.lineSeparator()
                + "Piece R2 -> Base." + System.lineSeparator()
                + "Piece R3 -> Base." + System.lineSeparator()
                + "Piece R4 -> Base.", getOutput());
    }

    @Test
    void shouldShowStandardPathPieceLocation() {
        Player player = new Player(Colour.RED);
        player.getPieces().get(0).enterBoard(26, Direction.CLOCKWISE);

        gameOutput.showPieceLocations(player);

        assertTrue(getOutput().contains("Piece R1 -> 26."));
    }

    @Test
    void shouldShowHomeStraightPieceLocation() {
        Player player = new Player(Colour.RED);
        Piece piece = player.getPieces().get(0);

        piece.enterBoard(26, Direction.CLOCKWISE);
        piece.recordCapture();
        piece.enterHomeStraight(2);

        gameOutput.showPieceLocations(player);

        assertTrue(getOutput().contains("Piece R1 -> redhomepath[2]."));
    }

    @Test
    void shouldShowHomePieceLocation() {
        Player player = new Player(Colour.RED);
        Piece piece = player.getPieces().get(0);

        piece.enterBoard(26, Direction.CLOCKWISE);
        piece.recordCapture();
        piece.enterHomeStraight(4);
        piece.reachHome();

        gameOutput.showPieceLocations(player);

        assertTrue(getOutput().contains("Piece R1 -> Home."));
    }

    @Test
    void shouldShowMysteryCellStatusWhenNewlyActivated() {
        MysteryCell mysteryCell = new MysteryCell();
        mysteryCell.activate(20);

        gameOutput.showMysteryCellStatus(mysteryCell);

        assertEquals(
                "The mystery cell is at 20 and will be at that location for the next 4 values",
                getOutput());
    }

    @Test
    void shouldShowRemainingMysteryCellDuration() {
        MysteryCell mysteryCell = new MysteryCell();
        mysteryCell.activate(20);
        mysteryCell.completeRound();

        gameOutput.showMysteryCellStatus(mysteryCell);

        assertEquals(
                "The mystery cell is at 20 and will be at that location for the next 3 values",
                getOutput());
    }

    @Test
    void shouldNotShowMysteryCellStatusWhenInactive() {
        MysteryCell mysteryCell = new MysteryCell();

        gameOutput.showMysteryCellStatus(mysteryCell);

        assertEquals("", getOutput());
    }

    @Test
    void shouldRejectNullMysteryCellWhenShowingStatus() {
        assertThrows(
                IllegalArgumentException.class,
                () -> gameOutput.showMysteryCellStatus(null));
    }
}