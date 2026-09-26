package ludo.random;

import java.util.List;

public interface MovementDistributor {

    List<Integer> distribute(int totalMovement, int numberOfPieces);
}