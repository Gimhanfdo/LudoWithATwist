package ludo.app;

import ludo.engine.GameEngine;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class LudoApplicationTest {

    @Test
    void shouldCreateFullyConfiguredGame() {
        GameEngine gameEngine = LudoApplication.createGame();

        assertNotNull(gameEngine);
    }
}