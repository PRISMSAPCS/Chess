package utils;

public class Main {
    public static void main(String[] args){
        ChessBoard board = new ChessBoard();
        GUI gui = new GUI(board);
        gui.applyMove(new Move(new Piece(Piece.PieceType.PAWN, false), new int[]{1, 0}, new int[]{2, 0}));
        gui.applyMove(new Move(new Piece(Piece.PieceType.PAWN, false), new int[]{2, 0}, new int[]{4, 0}));
    }
}
