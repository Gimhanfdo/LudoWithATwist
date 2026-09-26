package ludo.random;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RandomMovementDistributorTest {

    private final RandomMovementDistributor distributor = new RandomMovementDistributor();

    @Test
    void shouldDistributeAllMovementUnits() {
        List<Integer> distribution = distributor.distribute(6, 2);

        int total = distribution.stream()
                .mapToInt(Integer::intValue)
                .sum();

        assertEquals(6, total);
    }

    @Test
    void shouldCreateMovementForEveryPiece() {
        List<Integer> distribution = distributor.distribute(6, 3);

        assertEquals(3, distribution.size());
        assertTrue(distribution.stream().allMatch(movement -> movement > 0));
    }

    @Test
    void shouldGiveEntireMovementToSinglePiece() {
        List<Integer> distribution = distributor.distribute(6, 1);

        assertEquals(List.of(6), distribution);
    }
}