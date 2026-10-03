package ludo.output;

import ludo.domain.model.Piece;
import ludo.domain.model.Player;
import ludo.domain.enums.Direction;

import java.util.List;
import java.util.stream.Collectors;

public class ConsoleGameOutput implements GameOutput {

    @Override
    public void showPlayerPieces(Player player) {
        validatePlayer(player);

        String colour = getColourName(player);
        List<String> pieceNames = player.getPieces().stream()
                .map(piece -> piece.getName())
                .toList();

        String formattedNames = pieceNames.get(0) + ", "
                + pieceNames.get(1) + ", "
                + pieceNames.get(2) + ", and "
                + pieceNames.get(3);

        System.out.println("The " + colour + " player has four (04) pieces named " + formattedNames + ".");
    }

    @Override
    public void showInitialRoll(Player player, int roll) {
        validatePlayer(player);
        validateRoll(roll);

        System.out.println(getColourName(player) + " rolls " + roll);
    }

    @Override
    public void showFirstPlayer(Player player) {
        validatePlayer(player);

        System.out.println(getColourName(player) + " player has the highest roll and will begin the game.");
    }

    @Override
    public void showRoundOrder(List<Player> players) {
        validatePlayers(players);

        String order = players.stream().map(this::getColourName).collect(Collectors.joining(", "));

        System.out.println("The order of a single round is " + order + ".");
    }

    @Override
    public void showDiceRoll(Player player, int roll) {
        validatePlayer(player);
        validateRoll(roll);

        System.out.println(getColourName(player) + " player rolled " + roll + ".");
    }

    @Override
    public void showPieceEnteredBoard(Piece piece, int piecesOnBoard, int piecesInBase) {
        if (piece == null) {
            throw new IllegalArgumentException("Piece cannot be null.");
        }

        System.out.println(piece.getColour().name().toLowerCase() + " player moves piece " + piece.getName()
                + " to the starting point.");
        System.out.println(piecesOnBoard + " pieces are on the board and " + piecesInBase + " pieces are in the base.");
    }

    @Override
    public void showPieceMoved(Piece piece, int fromPosition, int toPosition, int distance, Direction direction) {
        if (piece == null) {
            throw new IllegalArgumentException("Piece cannot be null.");
        }

        if (direction == null) {
            throw new IllegalArgumentException("Direction cannot be null.");
        }

        String directionName = direction == Direction.CLOCKWISE ? "clockwise" : "counterclockwise";

        System.out.println(piece.getColour().name().toLowerCase() + " moves piece " + piece.getName()
                + " from location " + fromPosition + " to " + toPosition + " by " + distance + " units in "
                + directionName + " direction.");
    }

    @Override
    public void showPieceCaptured(Piece attacker, Piece capturedPiece, int position, int piecesOnBoard,
            int piecesInBase) {
        System.out.println(attacker.getColour().name().toLowerCase() + " piece " + attacker.getName() + " lands on square " + position
                + ", captures " + capturedPiece.getColour().name().toLowerCase() + " piece " + capturedPiece.getName()
                + ", and returns it to the base.");

        System.out.println(
                "There are " + piecesOnBoard + " pieces on the board and " + piecesInBase + " pieces in the base.");
    }

    @Override
    public void showWinner(Player player) {
        validatePlayer(player);

        System.out.println(getColourName(player) + " player wins!!!");
    }

    private String getColourName(Player player) {
        return player.getColour().name().toLowerCase();
    }

    private void validatePlayer(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null.");
        }
    }

    private void validatePlayers(List<Player> players) {
        if (players == null || players.isEmpty()) {
            throw new IllegalArgumentException("Players cannot be null or empty.");
        }

        for (Player player : players) {
            validatePlayer(player);
        }
    }

    private void validateRoll(int roll) {
        if (roll < 1 || roll > 6) {
            throw new IllegalArgumentException("Roll must be between 1 and 6.");
        }
    }

    @Override
    public void showPieceBlocked(Piece piece, int fromPosition, int blockedPosition, Piece blockingPiece) {
        System.out.println((piece.getColour().name().toLowerCase()) + " piece " + piece.getName()
                + " is blocked from moving from " + fromPosition + " to " + blockedPosition + " by "
                + (blockingPiece.getColour().name().toLowerCase()) + " piece " + blockingPiece.getName() + ".");
    }

    @Override
    public void showBlockedPieceNotMoved(Piece piece) {
        System.out.println((piece.getColour().name().toLowerCase())
                + " does not have other pieces in the board to move instead of the blocked piece. "
                + "Ignoring the throw and moving on to the next player.");
    }

    @Override
    public void showBlockedPieceMoved(Piece piece, int position) {
        System.out.println((piece.getColour().name().toLowerCase())
                + " does not have other pieces in the board to move instead of the blocked piece. "
                + "Moved the piece to square " + position + " which is the cell before the block.");
    }
}