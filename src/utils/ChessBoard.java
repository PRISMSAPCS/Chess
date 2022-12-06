package utils;

public class ChessBoard {
    public static int WIDTH = 8;
    public static int HEIGHT = 8;
    public Piece.PieceType[] SETUP_LAYOUT = new Piece.PieceType[] {
            Piece.PieceType.ROOK, Piece.PieceType.KNIGHT, Piece.PieceType.BISHOP, Piece.PieceType.QUEEN,
            Piece.PieceType.KING, Piece.PieceType.BISHOP, Piece.PieceType.KNIGHT, Piece.PieceType.ROOK
    };

    private Piece[][] board;   // first index (0-7) corresponds to numbers (1-8), second index corresponds to letters (a-h)

    public ChessBoard() {
        this.board = new Piece[8][8];
        for(int i = 0; i < 8; i++) {
            board[0][i] = new Piece(SETUP_LAYOUT[i], false);
            board[1][i] = new Piece(Piece.PieceType.PAWN, false);
            board[6][i] = new Piece(Piece.PieceType.PAWN, true);
            board[7][i] = new Piece(SETUP_LAYOUT[i], true);
        }
    }

    public Piece[][] getBoard() {
        return board;
    }
}
