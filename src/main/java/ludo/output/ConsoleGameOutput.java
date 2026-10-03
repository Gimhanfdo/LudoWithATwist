package ludo.output;

import ludo.domain.model.Board;
import ludo.domain.model.MysteryCell;
import ludo.domain.model.Piece;
import ludo.domain.model.Player;
import ludo.service.MysteryCellService;
import ludo.domain.enums.Direction;
import ludo.domain.enums.PieceEffect;
import ludo.domain.enums.PieceState;
import ludo.domain.enums.TeleportDestination;

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
        System.out.println(attacker.getColour().name().toLowerCase() + " piece " + attacker.getName()
                + " lands on square " + position
                + ", captures " + capturedPiece.getColour().name().toLowerCase() + " piece " + capturedPiece.getName()
                + ", and returns it to the base.");

        System.out.println(
                "There are " + piecesOnBoard + " pieces on the board and " + piecesInBase + " pieces in the base.");
    }

    @Override
    public void showMysteryLanding(Piece piece, TeleportDestination destination) {
        validatePiece(piece);

        if (destination == null) {
            throw new IllegalArgumentException("Teleport destination cannot be null.");
        }

        System.out.println(getColourName(piece)
                + " player lands on a mystery cell and is teleported to "
                + formatTeleportDestination(destination) + ".");
    }

    @Override
    public void showMysteryTeleport(Piece piece, TeleportDestination destination) {
        validatePiece(piece);

        if (destination == null) {
            throw new IllegalArgumentException("Teleport destination cannot be null.");
        }

        System.out.println(getColourName(piece) + " piece " + piece.getName()
                + " teleported to " + formatTeleportDestination(destination) + ".");
    }

    @Override
    public void showAlphaEffect(Piece piece, PieceEffect effect) {
        validatePiece(piece);

        if (effect == PieceEffect.ENERGISED) {
            System.out.println(getColourName(piece) + " piece " + piece.getName()
                    + " feels energized, and movement speed doubles.");
            return;
        }

        if (effect == PieceEffect.SICK) {
            System.out.println(getColourName(piece) + " piece " + piece.getName()
                    + " feels sick, and movement speed halves.");
            return;
        }

        throw new IllegalArgumentException("Alpha effect must be ENERGISED or SICK.");
    }

    @Override
    public void showBetaBriefing(Piece piece) {
        validatePiece(piece);

        System.out.println(getColourName(piece) + " piece " + piece.getName()
                + " attends briefing and cannot move for four rounds.");
    }

    @Override
    public void showGammaDirectionChanged(Piece piece) {
        validatePiece(piece);

        System.out.println("The " + getColourName(piece) + " piece " + piece.getName()
                + ", which was moving clockwise, has changed to moving counterclockwise.");
    }

    @Override
    public void showGammaRedirectedToBeta(Piece piece) {
        validatePiece(piece);

        System.out.println("The " + getColourName(piece) + " piece " + piece.getName()
                + " is moving in a counterclockwise direction. Teleporting to Beta from Gamma.");
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

    @Override
    public void showMysteryCellSpawned(int position) {
        validateStandardPathPosition(position);

        System.out.println("A mystery cell has spawned in location " + position
                + " and will be at this location for the next four rounds.");
    }

    @Override
    public void showBriefingPieceReturnedToBase(Piece piece) {
        validatePiece(piece);

        System.out.println(getColourName(piece) + " piece " + piece.getName()
                + " is movement-restricted and has rolled three consecutively. "
                + "Teleporting piece " + piece.getName() + " to base.");
    }

    @Override
    public void showPlayerPieceCount(Player player) {
        validatePlayer(player);

        long piecesOnBoard = player.getPieces().stream()
                .filter(this::isOnBoard)
                .count();

        long piecesInBase = player.getPieces().stream()
                .filter(piece -> piece.getState() == PieceState.BASE)
                .count();

        System.out.println(getColourName(player) + " player now has " + piecesOnBoard + "/4 on pieces on the board and "
                + piecesInBase + "/4 pieces on the base.");
    }

    private boolean isOnBoard(Piece piece) {
        return piece.getState() == PieceState.STANDARD_PATH || piece.getState() == PieceState.HOME_STRAIGHT;
    }

    @Override
    public void showPieceLocations(Player player) {
        validatePlayer(player);

        System.out.println("============================");
        System.out.println("Location of pieces " + getColourName(player));
        System.out.println("============================");

        for (Piece piece : player.getPieces()) {
            System.out.println("Piece " + piece.getName() + " -> " + formatPieceLocation(piece) + ".");
        }
    }

    @Override
    public void showMysteryCellStatus(MysteryCell mysteryCell) {
        if (mysteryCell == null) {
            throw new IllegalArgumentException("Mystery Cell cannot be null.");
        }

        if (!mysteryCell.isActive()) {
            return;
        }

        int remainingRounds = MysteryCellService.ACTIVE_ROUNDS_BEFORE_RELOCATION - mysteryCell.getRoundsActive();

        System.out.println("The mystery cell is at " + mysteryCell.getPosition()
                + " and will be at that location for the next " + remainingRounds + " values");
    }

    @Override
    public void showWinner(Player player) {
        validatePlayer(player);

        System.out.println(getColourName(player) + " player wins!!!");
    }

    private String formatPieceLocation(Piece piece) {
        return switch (piece.getState()) {
            case BASE -> "Base";
            case STANDARD_PATH -> String.valueOf(piece.getPosition());
            case HOME_STRAIGHT -> getColourName(piece) + "homepath[" + piece.getPosition() + "]";
            case HOME -> "Home";
        };
    }

    private void validateStandardPathPosition(int position) {
        if (position < 0 || position >= Board.STANDARD_PATH_SIZE) {
            throw new IllegalArgumentException("Position must be on the standard path.");
        }
    }

    private String formatTeleportDestination(TeleportDestination destination) {
        return switch (destination) {
            case ALPHA -> "Alpha";
            case BETA -> "Beta";
            case GAMMA -> "Gamma";
            case BASE -> "Base";
            case X -> "X";
            case APPROACH -> "Approach";
        };
    }

    private String getColourName(Player player) {
        return player.getColour().name().toLowerCase();
    }

    private void validatePlayer(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null.");
        }
    }

    private String getColourName(Piece piece) {
        return piece.getColour().name().toLowerCase();
    }

    private void validatePiece(Piece piece) {
        if (piece == null) {
            throw new IllegalArgumentException("Piece cannot be null.");
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
}