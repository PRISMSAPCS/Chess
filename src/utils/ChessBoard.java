package utils;

import java.util.ArrayList;

public class ChessBoard {
    public static int WIDTH = 8;
    public static int HEIGHT = 8;
<<<<<<< HEAD
    public Piece.PieceType[] SETUP_LAYOUT = new Piece.PieceType[] {
            Piece.PieceType.ROOK, Piece.PieceType.KNIGHT, Piece.PieceType.BISHOP, Piece.PieceType.QUEEN,
            Piece.PieceType.KING, Piece.PieceType.BISHOP, Piece.PieceType.KNIGHT, Piece.PieceType.ROOK
    };
    
    private int[] enPassant;
    private boolean move; // white = true, black = false
=======
>>>>>>> 85d6edccc4da6af64a51e5e837b7b4179a9d905c

    private Piece[][] board;   // first index (0-7) corresponds to numbers (1-8), second index corresponds to letters (a-h)
    
    public ChessBoard() {
    	this.move = true;
        this.board = new Piece[8][8];
        this.enPassant[0] = -1;
        this.enPassant[1] = -1;
        for(int i = 0; i < 8; i++) {
            //// board[0][i] = new Piece(SETUP_LAYOUT[i], false);
            board[1][i] = new Pawn(true);
            board[6][i] = new Pawn(false);
            //// board[7][i] = new Piece(SETUP_LAYOUT[i], true);
        }
    }
    
    public ArrayList<Move> getLegalMoves(int x, int y) {
    	ArrayList<int[]> moves = board[x][y].getMoveSet();
    	for (int[] move : moves) {
    		
    		Piece[][] boardCopy = new Piece[8][8];
    		for (int i = 0; i < 8; i++) {
    			for (int j = 0; j < 8; j++) {
    				boardCopy[i][j] = board[i][j];
    			}
    		}
    		boardCopy[x][y] = null;
    		boardCopy[move[0]][move[1]] = board[x][y];
    		for (int i = 0; i < 8; i++) {
    			for (int j = 0; j < 8; j++) {
    				if (boardCopy[i][j] != null && boardCopy[i][j].getName().equals("king") && boardCopy[i][j].isColor() == this.move) {
    					
    				}
    			}
    		}
    		for (Piece[] row : board) {
    			for (Piece piece : row) {
    				if (piece != null && piece.isColor() != this.move) {
    					ArrayList<int[]> enemyMoves = board[x][y].getMoveSet();
    					
    				}
    			}
    		}
    	}
    }

    public Piece[][] getBoard() {
        return board;
        //git test please ignore
        //testing
        //final testing
    }
}
