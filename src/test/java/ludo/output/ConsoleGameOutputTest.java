package ludo.output;

import ludo.domain.enums.Colour;
import ludo.domain.enums.PieceEffect;
import ludo.domain.enums.TeleportDestination;
import ludo.domain.model.Piece;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

    private String getOutput() {
        return output.toString().trim();
    }
}