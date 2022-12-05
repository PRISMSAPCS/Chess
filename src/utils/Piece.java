package utils;

public class Piece {
    public enum PieceType {
        PAWN, BISHOP, KNIGHT, ROOK, QUEEN, KING
    }
    public boolean color;     // false: white; true: black
    private PieceType type;

    public Piece(PieceType type, boolean color) {
        this.type = type;
        this.color = color;
    }
}
