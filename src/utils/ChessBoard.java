package utils;

import java.util.ArrayList;

public class ChessBoard {
    public static int WIDTH = 8;
    public static int HEIGHT = 8;
    public Piece.PieceType[] SETUP_LAYOUT = new Piece.PieceType[] {
            Piece.PieceType.ROOK, Piece.PieceType.KNIGHT, Piece.PieceType.BISHOP, Piece.PieceType.QUEEN,
            Piece.PieceType.KING, Piece.PieceType.BISHOP, Piece.PieceType.KNIGHT, Piece.PieceType.ROOK
    };
    
    private int[] enPassant;
    private boolean move; // white = true, black = false

    private Piece[][] board;   // first index (0-7) corresponds to numbers (1-8), second index corresponds to letters (a-h)
    
    public ChessBoard() {
    	this.move = true;
        this.board = new Piece[8][8];
        this.enPassant[0] = -1;
        this.enPassant[1] = -1;
        for(int i = 0; i < 8; i++) {
            board[0][i] = new Piece(SETUP_LAYOUT[i], false);
            board[1][i] = new Piece(Piece.PieceType.PAWN, false);
            board[6][i] = new Piece(Piece.PieceType.PAWN, true);
            board[7][i] = new Piece(SETUP_LAYOUT[i], true);
        }
    }
    
    public ArrayList<Move> getLegalMoves(int x, int y) {
    	ArrayList<Move> moves = board[x][y].getMoveSet();
    	for (Move move : moves) {
    		Piece[][] boardCopy = new Piece[8][8];
    		for (int i = 0; i < 8; i++) {
    			for (int j = 0; j < 8; j++) {
    				boardCopy[i][j] = board[i][j];
    			}
    		}
    		boardCopy[move.getCapture()[0]][move.getCapture()[1]] = null;
    		boardCopy[move.getStart()[0]][move.getStart()[1]] = null;
    		boardCopy[move.getEnd()[0]][move.getEnd()[1]] = move.getPiece();
    		for (Piece[] row : board) {
    			for (Piece piece : row) {
    				if (piece != null && piece.isColor() != move) {
    					
    				}
    			}
    		}
    	}
    }

    public Piece[][] getBoard() {
        return board;
    }
}
