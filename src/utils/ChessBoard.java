package utils;

import java.util.ArrayList;

public class ChessBoard {
    public static int WIDTH = 8;
    public static int HEIGHT = 8;
    
    private int[] enPassant;
    private boolean move; // white = true, black = false

    private Piece[][] board;   // first index (0-7) corresponds to numbers (1-8), second index corresponds to letters (a-h)
    
    public ChessBoard() {
    	this.move = true;
        this.board = new Piece[8][8];
        this.enPassant[0] = -1;
        this.enPassant[1] = -1;
    	board[0][0] = new Rook(false);
    	board[0][1] = new Knight(false);
    	board[0][2] = new Bishop(false);
    	board[0][3] = new Queen(false);
    	board[0][4] = new King(false);
    	board[0][5] = new Bishop(false);
    	board[0][6] = new Knight(false);
    	board[0][7] = new Rook(false);
    	for (int i = 0; i < 8; i++) {
    		board[1][i] = new Pawn(false);
    		board[6][i] = new Pawn(true);
    	}
    	board[7][0] = new Rook(true);
    	board[7][1] = new Knight(true);
    	board[7][2] = new Bishop(true);
    	board[7][3] = new Queen(true);
    	board[7][4] = new King(true);
    	board[7][5] = new Bishop(true);
    	board[7][6] = new Knight(true);
    	board[7][7] = new Rook(true);
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
