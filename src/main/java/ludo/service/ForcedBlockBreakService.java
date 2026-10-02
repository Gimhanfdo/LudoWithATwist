package ludo.service;

import ludo.domain.enums.PieceState;
import ludo.domain.model.Piece;
import ludo.domain.model.Player;

import java.util.List;

public class ForcedBlockBreakService {

    private final BlockService blockService;

    public ForcedBlockBreakService(BlockService blockService) {
        if (blockService == null) {
            throw new IllegalArgumentException("Block service cannot be null.");
        }

        this.blockService = blockService;
    }

    public boolean breakBlock(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null.");
        }

        for (Piece piece : player.getPieces()) {
            if (piece.getState() != PieceState.STANDARD_PATH) {
                continue;
            }

            int position = piece.getPosition();
            List<Piece> block = blockService.getBlockAt(position, player.getColour());

            if (block.isEmpty()) {
                continue;
            }

            Piece pieceToRemain = block.get(0);

            return blockService.breakBlockAfterThreeSixes(position, player.getColour(), pieceToRemain);
        }

        return false;
    }
}