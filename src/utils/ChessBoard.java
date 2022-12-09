package utils;

import java.util.ArrayList;
import java.util.Random;

public class ChessBoard {
    public static int WIDTH = 8;
    public static int HEIGHT = 8;
    
    private Pair enPassant;
    private boolean side; // white = true, black = false

    private Piece[][] board;   // first index (0-7) corresponds to numbers (1-8), second index corresponds to letters (a-h)
    
    public ChessBoard() {
    	this.side = true;
        this.board = new Piece[8][8];
        this.enPassant = new Pair(-1, -1);
        
    	board[0][0] = new Rook(true);
    	board[0][1] = new Knight(true);
    	board[0][2] = new Bishop(true);
    	board[0][3] = new Queen(true);
    	board[0][4] = new King(true);
    	board[0][5] = new Bishop(true);
    	board[0][6] = new Knight(true);
    	board[0][7] = new Rook(true);
    	for (int i = 0; i < 8; i++) {
    		board[1][i] = new Pawn(true);
    		board[6][i] = new Pawn(false);
    	}
    	board[7][0] = new Rook(false);
    	board[7][1] = new Knight(false);
    	board[7][2] = new Bishop(false);
    	board[7][3] = new Queen(false);
    	board[7][4] = new King(false);
    	board[7][5] = new Bishop(false);
    	board[7][6] = new Knight(false);
    	board[7][7] = new Rook(false);
    }
	
	public ChessBoard(ChessBoard other){
		// copy constructor
		this.side = other.side;
		this.board = new Piece[8][8];
		for(int i = 0; i < 8; i++){
			for(int j = 0; j < 8; j++){
				if(other.board[i][j] != null) this.board[i][j] = other.board[i][j].clone();
			}
		}
		this.enPassant = new Pair(other.enPassant.first, other.enPassant.second);
	}


    public void submitMove(Move theMove){
		if(theMove.getCapture() != null) {
			board[theMove.getEnd().first][theMove.getEnd().second] = null;
		}
        board[theMove.getEnd().first][theMove.getEnd().second] = theMove.getPiece();
        board[theMove.getStart().first][theMove.getStart().second] = null;
        if (theMove.getCapture() != null) {
        	board[theMove.getCapture().first][theMove.getCapture().second] = null;
        }
        
		// change side
		this.side = !this.side;
		
		// set enPassant array
		if (theMove.getPiece() instanceof Pawn && ((Pawn) theMove.getPiece()).getFirstMove() == true) {
			enPassant.first = theMove.getEnd().first;
			enPassant.second = theMove.getEnd().second;
		}
		
		// set the pawn's firstMove field to false
		if(theMove.getPiece() instanceof Pawn) {
			((Pawn) theMove.getPiece()).cancelFirstMove();
		}
    }
    
    private boolean checkLegal(int x, int y, Move move) { // Author: Daniel - checks if a move is legal
    	// copies the board - in this function, we make the move, then check if the king is in check
		Piece[][] boardCopy = (new ChessBoard(this)).getBoard();
		
		// emulate the move
		boardCopy[x][y] = null;
		boardCopy[move.getEnd().first][move.getEnd().second] = board[x][y];
		if(move.getCapture() != null) {
			boardCopy[move.getCapture().first][move.getCapture().second] = null;
		}
		
		// find location of king
		int kingX = -1;
		int kingY = -1;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				// checks if piece exists, is a king, and is same color as turn
				if (boardCopy[i][j] != null && boardCopy[i][j] instanceof King && boardCopy[i][j].getColor() == this.side) {
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
				if (piece != null && piece.getColor() != this.side) { // ensure that piece exists, and is opposite color
					ArrayList<int[]> enemyMoves = boardCopy[i][j].getMoveSet(boardCopy, i, j); // get moves that this piece can make
					
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
    
    public ArrayList<Move> getLegalMoves(int x, int y) { // Author: Daniel - returns an ArrayList of legal moves
    	ArrayList<Move> legalMoves = new ArrayList<>();
    	ArrayList<int[]> moves = board[x][y].getMoveSet(board, x, y);
    	for (int[] move : moves) {
    		// create the move object
    		Move toAdd;
    		
    		if (board[move[0]][move[1]] == null) {
    			toAdd = new Move(board[x][y], x, y, move[0], move[1]);
    		} else if (board[move[0]][move[1]].getColor() != this.side) {
    			toAdd = new Move(board[x][y], x, y, move[0], move[1], move[0], move[1]);
    		} else {
				continue;
			}

    		if (checkLegal(x, y, toAdd)) legalMoves.add(toAdd);
    	}
    	
    	// checks if piece is a pawn, then checks if en passant pawn is to the left or right of selected pawn
    	if (board[x][y] instanceof Pawn) {
    		if (x == enPassant.first) {
    			// checks to left and right, then checks legality
    			if (y - enPassant.second == 1) {
    				Move temp = new Move(board[x][y], x, y, x + (this.side ? 1 : -1), y - 1, x, y - 1);
    				if (checkLegal(x, y, temp)) legalMoves.add(temp);
    			} else if (y - enPassant.second == -1) {
    				Move temp = new Move(board[x][y], x, y, x + (this.side ? 1 : -1), y + 1, x, y + 1);
    				if (checkLegal(x, y, temp)) legalMoves.add(temp);
    			}
    		}
    	}
    	
    	return legalMoves;
    }
    
    public Move chooseRandomMove() { // Author: Daniel - gets a random legal move
    	ArrayList<Move> allLegalMoves = new ArrayList<Move>();
    	for (int i = 0; i < 8; i++) {
    		for (int j = 0; j < 8; j++) {
    			if (board[i][j].getColor() == this.side) {
    				ArrayList<Move> temp = getLegalMoves(i, j);
    				for (Move x : temp) {
    					allLegalMoves.add(x);
    				}
    			}
    		}
    	}
    	
    	int rnd = new Random().nextInt(allLegalMoves.size());
        return allLegalMoves.get(rnd);
    }
    
    public int evaluate() { // Author: Daniel - evaluates a position
    	boolean middlegame = true;
    	for (Piece[] x : board) {
    		for (Piece y : x) {
    			if (y instanceof Queen) {
    				middlegame = false;
    			}
    		}
    	}
    	
    	int points = 0;
    	for (int row = 0; row < 8; row++) {
    		for (int column = 0; column < 8; column++) {
    			if (board[row][column].getColor() == this.side) {
    				Piece piece = board[row][column];
    				if (piece instanceof Pawn) {
    					points += 100;
    					points += Eval.pawn[row][column];
    				} else if (piece instanceof Knight) {
    					points += 320;
    					points += Eval.knight[row][column];
    				} else if (piece instanceof Bishop) {
    					points += 330;
    					points += Eval.bishop[row][column];
    				} else if (piece instanceof Rook) {
    					points += 500;
    					points += Eval.rook[row][column];
    				} else if (piece instanceof Queen) {
    					points += 900;
    					points += Eval.queen[row][column];
    				} else if (piece instanceof King) {
    					points += 20000;
    					if (middlegame) {
    						points += Eval.kingmid[row][column];
    					} else {
    						points += Eval.kingend[row][column];
    					}
    				}
    			}
    		}
    	}
    	
    	Eval.flip();
    	for (int row = 0; row < 8; row++) {
    		for (int column = 0; column < 8; column++) {
    			if (board[row][column].getColor() == this.side) {
    				Piece piece = board[row][column];
    				if (piece instanceof Pawn) {
    					points -= 100;
    					points -= Eval.pawn[row][column];
    				} else if (piece instanceof Knight) {
    					points -= 320;
    					points -= Eval.knight[row][column];
    				} else if (piece instanceof Bishop) {
    					points -= 330;
    					points -= Eval.bishop[row][column];
    				} else if (piece instanceof Rook) {
    					points -= 500;
    					points -= Eval.rook[row][column];
    				} else if (piece instanceof Queen) {
    					points -= 900;
    					points -= Eval.queen[row][column];
    				} else if (piece instanceof King) {
    					points -= 20000;
    					if (middlegame) {
    						points -= Eval.kingmid[row][column];
    					} else {
    						points -= Eval.kingend[row][column];
    					}
    				}
    			}
    		}
    	}
    	
    	Eval.flip();
    	
    	return (this.side) ? points : points * -1;
    }
    
	public boolean getSide() {
		return side;
	}

    public Piece[][] getBoard() {
        return board;
        //git test please ignore
        //testing
        //final testing
    }
    public Piece getBoard(Pair pos){
        return board[pos.first][pos.second];
    }

}
