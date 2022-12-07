package utils;

public class ChessBoard {
    public static int WIDTH = 8;
    public static int HEIGHT = 8;

    private Piece[][] board;   // first index (0-7) corresponds to numbers (1-8), second index corresponds to letters (a-h)

    public ChessBoard() {
        this.board = new Piece[8][8];
        for(int i = 0; i < 8; i++) {
            //// board[0][i] = new Piece(SETUP_LAYOUT[i], false);
            board[1][i] = new Pawn(true);
            board[6][i] = new Pawn(false);
            //// board[7][i] = new Piece(SETUP_LAYOUT[i], true);
        }
        board[0][0] = new Rook(true);
        board[0][7] = new Rook(true);
        board[7][0] = new Rook(false);
        board[7][7] = new Rook(false);

        board[0][1] = new Knight(true);
        board[0][6] = new Knight(true);
        board[7][1] = new Knight(false);
        board[7][6] = new Knight(false);

        board[0][2] = new Bishop(true);
        board[0][5] = new Bishop(true);
        board[7][2] = new Bishop(false);
        board[7][5] = new Bishop(false); 

        board[0][3] = new King(true);
        board[0][4] = new Queen(true);
        board[7][3] = new King(false);
        board[7][4] = new Queen(false);
    }

    public submitMove(Move themove){
        board[themove.end[0]][themove.end[1]] = themove.piece;
        board[themove.start[0]][themove.start[1]] = null;
        if(themove.capture!=null) board[themove.capture[0]][themove.capture[1]] = null;
    }

    public Piece[][] getBoard() {
        return board;
        //git test please ignore
        //testing
        //final testing
    }
}
