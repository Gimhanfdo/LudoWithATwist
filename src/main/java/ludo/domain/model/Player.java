package ludo.domain.model;

import ludo.domain.enums.Colour;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Player {

    private static final int PIECES_PER_PLAYER = 4;

    private final Colour colour;
    private final List<Piece> pieces;

    public Player(Colour colour) {

        if (colour == null) {
            throw new IllegalArgumentException(
                    "Player colour cannot be null."
            );
        }

        this.colour = colour;
        this.pieces = createPieces();
    }

    private List<Piece> createPieces() {

        List<Piece> createdPieces = new ArrayList<>();

        for (int number = 1;
             number <= PIECES_PER_PLAYER;
             number++) {

            createdPieces.add(
                    new Piece(colour, number)
            );
        }

        return createdPieces;
    }

    public Colour getColour() {
        return colour;
    }

    public List<Piece> getPieces() {
        return Collections.unmodifiableList(pieces);
    }
}