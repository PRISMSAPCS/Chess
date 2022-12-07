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

    /**
     * Get the icon's relative location of this chess piece.
     * @author mqcreaple
     * @return relative location under folder /resource
     */
    public String getIconFile() {
        if(color) {
            // black pieces
            return switch(type) {
                case PAWN -> "p_b.png";
                case BISHOP -> "b_b.png";
                case KNIGHT -> "n_b.png";
                case ROOK -> "r_b.png";
                case QUEEN -> "q_b.png";
                case KING -> "k_b.png";
            };
        } else {
            // white pieces
            return switch(type) {
                case PAWN -> "p_w.png";
                case BISHOP -> "b_w.png";
                case KNIGHT -> "n_w.png";
                case ROOK -> "r_w.png";
                case QUEEN -> "q_w.png";
                case KING -> "k_w.png";
            };
            
        }
    }
}
