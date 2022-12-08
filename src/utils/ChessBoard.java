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
    	// board[0][2] = new Bishop(false);
    	board[0][3] = new Queen(false);
    	board[0][4] = new King(false);
    	// board[0][5] = new Bishop(false);
    	board[0][6] = new Knight(false);
    	board[0][7] = new Rook(false);
    	for (int i = 0; i < 8; i++) {
    		board[1][i] = new Pawn(false);
    		board[6][i] = new Pawn(true);
    	}
    	board[7][0] = new Rook(true);
    	board[7][1] = new Knight(true);
    	/// board[7][2] = new Bishop(true);
    	board[7][3] = new Queen(true);
    	board[7][4] = new King(true);
    	// board[7][5] = new Bishop(true);
    	board[7][6] = new Knight(true);
    	board[7][7] = new Rook(true);
    	
    }
    
    public void submitMove(Move theMove){
        board[theMove.getEnd()[0]][theMove.getEnd()[1]] = theMove.getPiece();
        board[theMove.getStart()[0]][theMove.getStart()[1]] = null;
        if(theMove.getCapture() != null) board[theMove.getEnd()[0]][theMove.getEnd()[1]] = null;
    }
    
    private boolean checkLegal(int x, int y, Move move) {
    	// copies the board - in this function, we make the move, then check if the king is in check
		Piece[][] boardCopy = new Piece[8][8];
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				boardCopy[i][j] = board[i][j];
			}
		}
		
		// emulate the move
		boardCopy[x][y] = null;
		boardCopy[move.getEnd()[0]][move.getEnd()[1]] = board[x][y];
		boardCopy[move.getCapture()[0]][move.getCapture()[1]] = null;
		
		// find location of king
		int kingX = -1;
		int kingY = -1;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				// checks if piece exists, is a king, and is same color as turn
				if (boardCopy[i][j] != null && boardCopy[i][j] instanceof King && boardCopy[i][j].getColor() == this.move) {
					kingX = i;
					kingY = j;
				}
			}
		}
		
		// check if king is in check after piece move
		boolean leave = false;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				Piece piece = boardCopy[i][j]; // get piece at square
				if (piece != null && piece.getColor() != this.move) { // ensure that piece exists, and is opposite color
					ArrayList<int[]> enemyMoves = boardCopy[x][y].getMoveSet(boardCopy, i, j); // get moves that this piece can make
					
					// check if any of these moves can hit the king
					for (int[] enemyMove : enemyMoves) {
						if (enemyMove[0] == kingX && enemyMove[1] == kingY) {
							leave = true;
						}
					}
				}
			}
		}
		
		return !leave;
    }
    
    public ArrayList<Move> getLegalMoves(int x, int y) { // returns an ArrayList of legal moves
    	ArrayList<Move> legalMoves = new ArrayList<>();
    	ArrayList<int[]> moves = board[x][y].getMoveSet(board, x, y);
    	for (int[] move : moves) {
    		// create the move object
    		Move toAdd = null;
    		
    		if (board[x][y] == null) {
    			toAdd = new Move(board[x][y], x, y, move[0], move[1]);
    		} else if (board[x][y].getColor() != this.move) {
    			toAdd = new Move(board[x][y], x, y, move[0], move[1], move[0], move[1]);
    		}
    		
    		if (checkLegal(x, y, toAdd) == true) legalMoves.add(toAdd);
    	}
    	
    	// checks if piece is a pawn, then checks if en passant pawn is to the left or right of selected pawn
    	if (board[x][y] instanceof Pawn) {
    		if (x == enPassant[0]) {
    			// checks to left and right, then checks legality
    			if (y - enPassant[1] == 1) {
    				Move temp = new Move(board[x][y], x, y, x + (this.move ? -1 : 1), y - 1, x + (this.move ? -1 : 1), y);
    				if (checkLegal(x, y, temp)) legalMoves.add(temp);
    			} else if (y - enPassant[1] == -1) {
    				Move temp = new Move(board[x][y], x, y, x + (this.move ? -1 : 1), y + 1, x + (this.move ? -1 : 1), y);
    				if (checkLegal(x, y, temp)) legalMoves.add(temp);
    			}
    		}
    	}
    	
    	return legalMoves;
    }

    public Piece[][] getBoard() {
        return board;
        //git test please ignore
        //testing
        //final testing
    }
}
