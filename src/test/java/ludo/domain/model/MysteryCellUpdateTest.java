package ludo.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MysteryCellUpdateTest {

    @Test
    void shouldCreateNoChangeUpdate() {
        MysteryCellUpdate update = MysteryCellUpdate.none();

        assertEquals(MysteryCellUpdate.Type.NONE, update.getType());
        assertNull(update.getPosition());
        assertFalse(update.hasChanged());
    }

    @Test
    void shouldCreateSpawnedUpdate() {
        MysteryCellUpdate update = MysteryCellUpdate.spawned(20);

        assertEquals(MysteryCellUpdate.Type.SPAWNED, update.getType());
        assertEquals(20, update.getPosition());
        assertTrue(update.hasChanged());
    }

    @Test
    void shouldCreateRelocatedUpdate() {
        MysteryCellUpdate update = MysteryCellUpdate.relocated(30);

        assertEquals(MysteryCellUpdate.Type.RELOCATED, update.getType());
        assertEquals(30, update.getPosition());
        assertTrue(update.hasChanged());
    }
}