package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class ChessBoard {
	private class unMove {
		public Piece piece;
		public Pair location;
		public unMove(Piece Piece, Pair Location) {
			piece = Piece;
			location = Location;
		}
	}
	
	public static int WIDTH = 8;
	public static int HEIGHT = 8;

	private Pair enPassant;
	private boolean side; // white = true, black = false
	private int moveRule;
	private boolean logging = false;
	private MatchLogging logger;
	private Piece[][] board; // first index (0-7) corresponds to numbers (1-8), second index corresponds to
								// letters (a-h)
	private Pair[] kingPos;  // position of each sides' kings; Author: @MqCreaple

	private ArrayList<ArrayList<unMove>> undoMoveStack;
	private ArrayList<Integer> undoMoveRuleStack;
	private ArrayList<Pair> undoEnPassantStack;
	private ArrayList<Move> previousMoves;
	private ArrayList<Long> previousZobrists;
	
	private long zobristKey;
	private long[] zobristArray;
	static final int BLACK_MOVE = 768;
	static final int BLACK_SHORT = 769;
	static final int BLACK_LONG = 770;
	static final int WHITE_SHORT = 771;
	static final int WHITE_LONG = 772;
	static final int EN_PASSANT = 773;
	
    private boolean proceed = false;
    private boolean loadingSimulation = false;

	public ChessBoard() {
		this.side = true;
		this.board = new Piece[8][8];
		this.kingPos = new Pair[2];
		this.enPassant = new Pair(-1, -1);
		this.moveRule = 0;

		board[0][0] = new Rook(true);
		board[0][1] = new Knight(true);
		board[0][2] = new Bishop(true);
		board[0][3] = new Queen(true);
		board[0][4] = new King(true);
		kingPos[1] = new Pair(0, 4);
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
		kingPos[0] = new Pair(7, 4);
		board[7][5] = new Bishop(false);
		board[7][6] = new Knight(false);
		board[7][7] = new Rook(false);
		this.undoMoveStack = new ArrayList<ArrayList<unMove>>();
		this.undoMoveRuleStack = new ArrayList<Integer>();
		this.undoEnPassantStack = new ArrayList<Pair>();
		this.previousMoves = new ArrayList<Move>();
		this.previousZobrists = new ArrayList<Long>();
		this.zobristArray = getZobristArray();
		this.zobristKey = initializeZobristKey();
	}

	public ChessBoard(ChessBoard other) {
		// copy constructor
		this.side = other.side;
		this.board = new Piece[8][8];
		this.kingPos = Arrays.copyOf(other.kingPos, other.kingPos.length);
		this.moveRule = other.getMoveRule();
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (other.board[i][j] != null)
					this.board[i][j] = other.board[i][j].clone();
			}
		}
		this.enPassant = new Pair(other.enPassant.first, other.enPassant.second);
		this.undoMoveStack = new ArrayList<ArrayList<unMove>>();
		this.undoMoveRuleStack = new ArrayList<Integer>();
		this.undoEnPassantStack = new ArrayList<Pair>();
		this.previousMoves = new ArrayList<Move>();
		this.previousZobrists = new ArrayList<Long>();
		this.zobristArray = getZobristArray();
		this.zobristKey = other.getZobristKey();
	}

	public void enableLogging(String whiteName, String blackName) {
		this.logging = true;
		logger = new MatchLogging("./log.pgn", whiteName, blackName);
	}

	public void disableLogging() {
		this.logging = false;
	}

	/**
	 * Submit a move and perform the move on the board.
	 * 
	 * @param theMove Move object being performed
	 */
	public void submitMove(Move theMove) {
		previousMoves.add(theMove);
		previousZobrists.add(zobristKey);
		ChessBoard oldBoard = new ChessBoard(this);
		// update move rule
		undoMoveRuleStack.add(moveRule);
		if (theMove.getPiece() instanceof Pawn) {
			moveRule = 0;
		} else if (theMove.getCapture() != null || (board[theMove.getEnd().first][theMove.getEnd().second] != null
				&& board[theMove.getEnd().first][theMove.getEnd().second].getColor() != this.side)) {
			moveRule = 0;
		}
		if (this.side == true) {
			moveRule++;
		}
		
		ArrayList<unMove> toAdd = new ArrayList<unMove>();
		if (theMove.getCapture() != null) {
			// update king pos if king has been captured.
			if(getBoard(theMove.getCapture()) instanceof King) {
				kingPos[this.side? 0: 1] = new Pair(-1, -1);
			}
			toAdd.add(new unMove(board[theMove.getCapture().first][theMove.getCapture().second], theMove.getCapture()));
			zobristKey ^= zobristArray[pieceToZobristIndex(theMove.getCapture().first, theMove.getCapture().second)];
			board[theMove.getCapture().first][theMove.getCapture().second] = null;
		}
		toAdd.add(new unMove(board[theMove.getEnd().first][theMove.getEnd().second], theMove.getEnd()));
		board[theMove.getEnd().first][theMove.getEnd().second] = theMove.getPiece();
		zobristKey ^= zobristArray[pieceToZobristIndex(theMove.getEnd().first, theMove.getEnd().second)];
		
		toAdd.add(new unMove(board[theMove.getStart().first][theMove.getStart().second], theMove.getStart()));
		zobristKey ^= zobristArray[pieceToZobristIndex(theMove.getStart().first, theMove.getStart().second)];
		board[theMove.getStart().first][theMove.getStart().second] = null;
		
		if (theMove.getPiece2() != null) {
			toAdd.add(new unMove(board[theMove.getEnd2().first][theMove.getEnd2().second], theMove.getEnd2()));
			board[theMove.getEnd2().first][theMove.getEnd2().second] = theMove.getPiece2();
			zobristKey ^= zobristArray[pieceToZobristIndex(theMove.getEnd2().first, theMove.getEnd2().second)];

			toAdd.add(new unMove(board[theMove.getStart2().first][theMove.getStart2().second], theMove.getStart2()));
			zobristKey ^= zobristArray[pieceToZobristIndex(theMove.getStart2().first, theMove.getStart2().second)];
			board[theMove.getStart2().first][theMove.getStart2().second] = null;
		}
		
		undoMoveStack.add(toAdd);
		
		// check for promotion
		// @author mqcreaple
		if (theMove instanceof PromotionMove) {
			zobristKey ^= zobristArray[pieceToZobristIndex(theMove.getEnd().first, theMove.getEnd().second)];
			if (((PromotionMove) theMove).getPromoteTo() != null) {
				// automatically select the piece
				board[theMove.getEnd().first][theMove.getEnd().second] = ((PromotionMove) theMove).getPromoteTo();
			} else {
				Piece newPiece = GUI.getPromotion(theMove.getPiece().getColor());
				((PromotionMove) theMove).setPromoteTo(newPiece);
				board[theMove.getEnd().first][theMove.getEnd().second] = newPiece;
			}
			
			zobristKey ^= zobristArray[pieceToZobristIndex(theMove.getEnd().first, theMove.getEnd().second)];
		}
		if (logging)
			logger.logMove(theMove, oldBoard);

		// set enPassant array
		undoEnPassantStack.add(enPassant);
		if (enPassant.second != -1) {
			zobristKey ^= zobristArray[EN_PASSANT + enPassant.second];
		}
		enPassant.first = -1;
		enPassant.second = -1;
		if (theMove.getPiece() instanceof Pawn && ((Pawn) theMove.getPiece()).getFirstMove() == true
				&& (theMove.getEnd().first == 3 || theMove.getEnd().first == 4)) {
			enPassant.first = theMove.getEnd().first;
			enPassant.second = theMove.getEnd().second;
			zobristKey ^= zobristArray[EN_PASSANT + enPassant.second];
		}

		// set the piece's firstMove field to false
		boolean[] beforeMove = castlingRights();
		theMove.getPiece().updateMoveCounter();
		if (theMove.getPiece2() != null) theMove.getPiece2().updateMoveCounter();
		boolean[] afterMove = castlingRights();
		for (int i = 0; i < 4; i++) {
			if (beforeMove[i] != afterMove[i]) {
				zobristKey ^= zobristArray[BLACK_SHORT + i];
			}
		}
		
		// update kingPos array
		if (theMove.getPiece() instanceof King) {
			this.kingPos[this.side? 1: 0] = theMove.getEnd();
		}
		
		// change side
		this.side = !this.side;
		zobristKey ^= zobristArray[BLACK_MOVE];
		//// System.out.println(kingPos[0].first + " " + kingPos[0].second);

		//// if(this.loadingSimulation == false)
		//// 	System.out.println(evaluate());
	}

	private boolean checkLegal(int x, int y, Move move) { // Author: Daniel - checks if a move is legal
		// copies the board - in this function, we make the move, then check if the king
		// is in check
		ChessBoard newBoard = new ChessBoard(this);
		Piece[][] boardCopy = newBoard.getBoard();

		// emulate the move
		boardCopy[x][y] = null;
		if (move.getCapture() != null) {
			boardCopy[move.getCapture().first][move.getCapture().second] = null;
		}
		boardCopy[move.getEnd().first][move.getEnd().second] = board[x][y];
		if(move.getPiece() instanceof King) {
			// update king pos in the new board
			newBoard.kingPos[newBoard.getSide()? 1: 0] = move.getEnd();
		}

		// find location of king
		int kingX = newBoard.kingPos[this.side? 1: 0].first;
		int kingY = newBoard.kingPos[this.side? 1: 0].second;

		// check if king is in check after piece move
		boolean leave = false;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				Piece piece = boardCopy[i][j]; // get piece at square
				if (piece != null && piece.getColor() != this.side) { // ensure that piece exists, and is opposite color
					ArrayList<int[]> enemyMoves = boardCopy[i][j].getMoveSet(boardCopy, i, j); // get moves that this
																								// piece can make

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
    //restarts board
    public void restart() {
    	this.side = true;
        this.board = new Piece[8][8];
        this.enPassant = new Pair(-1, -1);
        this.moveRule = 0;
        
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
    
    public ArrayList<Move> convertIntPairToMoves(ArrayList<int[]> toConvert, int x, int y) {
    	ArrayList<Move> toReturn = new ArrayList<Move>();
    	for (int[] i : toConvert) {
    		toReturn.add(new Move(board[x][y], x, y, i[0], i[1]));
    	}
    	
    	return toReturn;
    }
	/**
	 * Get all the possible legal moves of piece on position (x, y).
	 * 
	 * @param x    x coordinate
	 * @param y    y coordinate
	 * @param auto if the piece is automatically played by a bot
	 * @return an array list of all possible moves
	 */
	public ArrayList<Move> getLegalMoves(int x, int y, boolean auto) { // Author: Daniel - returns an ArrayList of legal
																		// moves
		ArrayList<Move> legalMoves = new ArrayList<>();
		ArrayList<int[]> moves = board[x][y].getMoveSet(board, x, y);
		
		// Author: Daniel - checks if a piece is pinned or not, and generates its moveset accordingly 
		if (!(board[x][y] instanceof King) && !(board[x][y] instanceof Pawn) && !checked(this.side)) {
			// check distance from king
			int xDiff = x - kingPos[this.side ? 1 : 0].first;
			int yDiff = y - kingPos[this.side ? 1 : 0].second;
			// piece on same bottom right top left diagonal as king
			if (xDiff == yDiff) {
				// possible pinning piece is to the down and right of the bishop
				if (xDiff > 0) {
					// check for pin
					for (int i = 1; i < xDiff; i++) {
						// piece isn't pinned, just return its moveset
						if (board[x - i][y - i] != null) {
							return convertIntPairToMoves(moves, x, y);
						}
					}
					
					// piece may be pinned, need to check for pinning piece
					for (int i = 1; i < 8; i++) {
						if (x + i >= 8 || y + i >= 8) {
							break;
						}
						
						// only bishop or queen can pin diagonally
						if (board[x + i][y + i] instanceof Bishop || board[x + i][y + i] instanceof Queen) {
							// pinning piece is not on our side
							if (board[x + i][y + i].getColor() != this.side) {
								// generate moves with bottom right top left pin, convert into ArrayList of moves
								if (board[x][y] instanceof Bishop) {
									return convertIntPairToMoves(((Bishop) board[x][y]).getMoveSet(board, x, y, false), x, y);
								} else if (board[x][y] instanceof Knight) {
									return new ArrayList<Move>();
								} else if (board[x][y] instanceof Rook) {
									return new ArrayList<Move>();
								} else if (board[x][y] instanceof Queen) {
									return convertIntPairToMoves(((Queen) board[x][y]).getMoveSet(board, x, y, false, true, false, false), x, y);
								}
							}
						}
						
						// if there is something that is not empty space, that thing is gonna block any possible pin, so just break out
						if (board[x + i][y + i] != null) {
							break;
						}
					}
					
					// no pin, return normal moveset
					return convertIntPairToMoves(moves, x, y);
				} else { // possible pinning piece is to the up and left of the bishop
					// check for pin
					for (int i = 1; i < xDiff * -1; i++) {
						// piece isn't pinned, just return its moveset
						if (board[x + i][y + i] != null) {
							return convertIntPairToMoves(moves, x, y);
						}
					}
					
					// piece may be pinned, need to check for pinning piece
					for (int i = 1; i < 8; i++) {
						if (x - i < 0 || y - i < 0) {
							break;
						}
						
						// only bishop or queen can pin diagonally
						if (board[x - i][y - i] instanceof Bishop || board[x - i][y - i] instanceof Queen) {
							// pinning piece is not on our side
							if (board[x - i][y - i].getColor() != this.side) {
								// generate moves with bottom right top left pin, convert into ArrayList of moves
								if (board[x][y] instanceof Bishop) {
									return convertIntPairToMoves(((Bishop) board[x][y]).getMoveSet(board, x, y, false), x, y);
								} else if (board[x][y] instanceof Knight) {
									return new ArrayList<Move>();
								} else if (board[x][y] instanceof Rook) {
									return new ArrayList<Move>();
								} else if (board[x][y] instanceof Queen) {
									return convertIntPairToMoves(((Queen) board[x][y]).getMoveSet(board, x, y, false, true, false, false), x, y);
								}
							}
						}
						
						// if there is something that is not empty space, that thing is gonna block any possible pin, so just break out
						if (board[x - i][y - i] != null) {
							break;
						}
					}
					
					// no pin, return normal moveset
					return convertIntPairToMoves(moves, x, y);
				}
			} else if (xDiff == -yDiff) { // Bottom Left to Top Right diagonal
				// possible pinning piece is to the down and left of the bishop
				if (xDiff > 0) {
					// check for pin
					for (int i = 1; i < xDiff; i++) {
						// piece isn't pinned, just return its moveset
						if (board[x - i][y + i] != null) {
							return convertIntPairToMoves(moves, x, y);
						}
					}
					
					// piece may be pinned, need to check for pinning piece
					for (int i = 1; i < 8; i++) {
						if (x + i >= 8 || y - i < 0) {
							break;
						}
						
						// only bishop or queen can pin diagonally
						if (board[x + i][y - i] instanceof Bishop || board[x + i][y - i] instanceof Queen) {
							// pinning piece is not on our side
							if (board[x + i][y - i].getColor() != this.side) {
								// generate moves with bottom left top right pin, convert into ArrayList of moves
								if (board[x][y] instanceof Bishop) {
									return convertIntPairToMoves(((Bishop) board[x][y]).getMoveSet(board, x, y, true), x, y);
								} else if (board[x][y] instanceof Knight) {
									return new ArrayList<Move>();
								} else if (board[x][y] instanceof Rook) {
									return new ArrayList<Move>();
								} else if (board[x][y] instanceof Queen) {
									return convertIntPairToMoves(((Queen) board[x][y]).getMoveSet(board, x, y, true, false, false, false), x, y);
								}
							}
						}
						
						// if there is something that is not empty space, that thing is gonna block any possible pin, so just break out
						if (board[x + i][y - i] != null) {
							break;
						}
					}
					
					// no pin, return normal moveset
					return convertIntPairToMoves(moves, x, y);
				} else { // possible pinning piece is to the up and right of the piece
					// check for pin
					for (int i = 1; i < xDiff * -1; i++) {
						// piece isn't pinned, just return its moveset
						if (board[x + i][y - i] != null) {
							return convertIntPairToMoves(moves, x, y);
						}
					}
					
					// piece may be pinned, need to check for pinning piece
					for (int i = 1; i < 8; i++) {
						if (x - i < 0 || y + i >= 8) {
							break;
						}
						
						// only bishop or queen can pin diagonally
						if (board[x - i][y + i] instanceof Bishop || board[x - i][y + i] instanceof Queen) {
							// pinning piece is not on our side
							if (board[x - i][y + i].getColor() != this.side) {
								// generate moves with bottom left top rightpin, convert into ArrayList of moves
								if (board[x][y] instanceof Bishop) {
									return convertIntPairToMoves(((Bishop) board[x][y]).getMoveSet(board, x, y, true), x, y);
								} else if (board[x][y] instanceof Knight) {
									return new ArrayList<Move>();
								} else if (board[x][y] instanceof Rook) {
									return new ArrayList<Move>();
								} else if (board[x][y] instanceof Queen) {
									return convertIntPairToMoves(((Queen) board[x][y]).getMoveSet(board, x, y, true, false, false, false), x, y);
								}
							}
						}
						
						// if there is something that is not empty space, that thing is gonna block any possible pin, so just break out
						if (board[x - i][y + i] != null) {
							break;
						}
					}
					
					// no pin, return normal moveset
					return convertIntPairToMoves(moves, x, y);
				}
			} else if (xDiff == 0) { // vertical pin
				if (yDiff > 0) { // pinning piece is below pinned piece
					for (int i = 1; i < yDiff; i++) {
						if (board[x][y - i] != null) {
							// no pin
							return convertIntPairToMoves(moves, x, y);
						}
					}
					
					// possible pin
					for (int i = 1; i < 8; i++) {
						if (y + i >= 8) {
							break;
						}
						
						// only rook or queen can pin vertically
						if (board[x][y + i] instanceof Rook || board[x][y + i] instanceof Queen) {
							// pinning piece is not on our side
							if (board[x][y + i].getColor() != this.side) {
								// generate moves with vertical pin, convert into ArrayList of moves
								if (board[x][y] instanceof Bishop) {
									return new ArrayList<Move>();
								} else if (board[x][y] instanceof Knight) {
									return new ArrayList<Move>();
								} else if (board[x][y] instanceof Rook) {
									return convertIntPairToMoves(((Rook) board[x][y]).getMoveSet(board, x, y, false), x, y);
								} else if (board[x][y] instanceof Queen) {
									return convertIntPairToMoves(((Queen) board[x][y]).getMoveSet(board, x, y, false, false, false, true), x, y);
								}
							}
						}
						
						// if there is something that is not empty space, that thing is gonna block any possible pin, so just break out
						if (board[x][y + i] != null) {
							break;
						}
					}
					
					// no pin, return normal moveset
					return convertIntPairToMoves(moves, x, y);
				} else { // pinning piece is above pinned piece
					for (int i = 1; i < yDiff; i++) {
						if (board[x][y + i] != null) {
							// no pin
							return convertIntPairToMoves(moves, x, y);
						}
					}
					
					// possible pin
					for (int i = 1; i < 8; i++) {
						if (y - i < 0) {
							break;
						}
						
						// only rook or queen can pin vertically
						if (board[x][y - i] instanceof Rook || board[x][y - i] instanceof Queen) {
							// pinning piece is not on our side
							if (board[x][y - i].getColor() != this.side) {
								// generate moves with vertical pin, convert into ArrayList of moves
								if (board[x][y] instanceof Bishop) {
									return new ArrayList<Move>();
								} else if (board[x][y] instanceof Knight) {
									return new ArrayList<Move>();
								} else if (board[x][y] instanceof Rook) {
									return convertIntPairToMoves(((Rook) board[x][y]).getMoveSet(board, x, y, false), x, y);
								} else if (board[x][y] instanceof Queen) {
									return convertIntPairToMoves(((Queen) board[x][y]).getMoveSet(board, x, y, false, false, false, true), x, y);
								}
							}
						}
						
						// if there is something that is not empty space, that thing is gonna block any possible pin, so just break out
						if (board[x][y - i] != null) {
							break;
						}
					}
					
					// no pin, return normal moveset
					return convertIntPairToMoves(moves, x, y);
				}
			} else if (yDiff == 0) { // horizontal pin
				if (xDiff > 0) { // pinning piece is to the right of pinned piece
					for (int i = 1; i < xDiff; i++) {
						if (board[x - i][y] != null) {
							// no pin
							return convertIntPairToMoves(moves, x, y);
						}
					}
					
					// possible pin
					for (int i = 1; i < 8; i++) {
						if (x + i >= 8) {
							break;
						}
						
						// only rook or queen can pin vertically
						if (board[x + i][y] instanceof Rook || board[x + i][y] instanceof Queen) {
							// pinning piece is not on our side
							if (board[x + i][y].getColor() != this.side) {
								// generate moves with horizontal pin, convert into ArrayList of moves
								if (board[x][y] instanceof Bishop) {
									return new ArrayList<Move>();
								} else if (board[x][y] instanceof Knight) {
									return new ArrayList<Move>();
								} else if (board[x][y] instanceof Rook) {
									return convertIntPairToMoves(((Rook) board[x][y]).getMoveSet(board, x, y, true), x, y);
								} else if (board[x][y] instanceof Queen) {
									return convertIntPairToMoves(((Queen) board[x][y]).getMoveSet(board, x, y, false, false, true, false), x, y);
								}
							}
						}
						
						// if there is something that is not empty space, that thing is gonna block any possible pin, so just break out
						if (board[x + i][y] != null) {
							break;
						}
					}
					
					// no pin, return normal moveset
					return convertIntPairToMoves(moves, x, y);
				} else { // pinning piece is to the left of pinned piece
					for (int i = 1; i < xDiff; i++) {
						if (board[x + i][y] != null) {
							// no pin
							return convertIntPairToMoves(moves, x, y);
						}
					}
					
					// possible pin
					for (int i = 1; i < 8; i++) {
						if (x - i < 0) {
							break;
						}
						
						// only rook or queen can pin vertically
						if (board[x - i][y] instanceof Rook || board[x - i][y] instanceof Queen) {
							// pinning piece is not on our side
							if (board[x - i][y].getColor() != this.side) {
								// generate moves with horizontal pin, convert into ArrayList of moves
								if (board[x][y] instanceof Bishop) {
									return new ArrayList<Move>();
								} else if (board[x][y] instanceof Knight) {
									return new ArrayList<Move>();
								} else if (board[x][y] instanceof Rook) {
									return convertIntPairToMoves(((Rook) board[x][y]).getMoveSet(board, x, y, true), x, y);
								} else if (board[x][y] instanceof Queen) {
									return convertIntPairToMoves(((Queen) board[x][y]).getMoveSet(board, x, y, false, false, true, false), x, y);
								}
							}
						}
						
						// if there is something that is not empty space, that thing is gonna block any possible pin, so just break out
						if (board[x - i][y] != null) {
							break;
						}
					}
					
					// no pin, return normal moveset
					return convertIntPairToMoves(moves, x, y);
				}
			} else {
				// piece is not on the same diagonal, rank, or file as the king, so it cannot be pinned
				return convertIntPairToMoves(moves, x, y);
			}
		}

		// castle logic, special. Author: Kevin
		try {
		if (board[x][y] instanceof King) {
			if (board[x][y].getFirstMove()) {
				boolean validCastle = true;
				if (y + 3 < 8 && board[x][y + 3] instanceof Rook) {
					if (board[x][y + 3].getFirstMove()) {
						for (int i = y; i < y + 3; i++) {
							if ((board[x][i] == null || board[x][i] instanceof King)
									&& (checkLegal(x, y, new Move(board[x][y], x, y, x, i)))) {
								validCastle = true;
							} else {
								validCastle = false;
								break;
							}
						}
						if (validCastle)
							legalMoves.add(new Move(board[x][y], x, y, x, y + 2, board[x][y + 3], x, y + 3, x, y + 1));
					}
				}

				if (y - 4 >= 0 && board[x][y - 4] instanceof Rook) {
					if (board[x][y - 4].getFirstMove()) {
						for (int i = y; i > y - 4; i--) {
							if ((board[x][i] == null || board[x][i] instanceof King)
									&& (checkLegal(x, y, new Move(board[x][y], x, y, x, i)))) {
								validCastle = true;
							} else {
								validCastle = false;
								break;
							}
						}
						if (validCastle)
							legalMoves.add(new Move(board[x][y], x, y, x, y - 2, board[x][y - 4], x, y - 4, x, y - 1));
					}
				}
			}
		}
		} catch (Exception e) {
			
		}
		for (int[] move : moves) {
			// create the move object
			Move toAdd;

			if (board[move[0]][move[1]] == null) {
				if (board[x][y] instanceof Pawn && (move[0] == 0 || move[0] == ChessBoard.WIDTH - 1)) {
					toAdd = new PromotionMove(board[x][y], x, y, move[0], move[1]);
					if (auto) {
						((PromotionMove) toAdd).setPromoteTo(new Queen(this.side));
					}
				} else {
					toAdd = new Move(board[x][y], x, y, move[0], move[1]);
				}
			} else if (board[move[0]][move[1]].getColor() != this.side) {
				if (board[x][y] instanceof Pawn && (move[0] == 0 || move[0] == ChessBoard.WIDTH - 1)) {
					toAdd = new PromotionMove(board[x][y], x, y, move[0], move[1], move[0], move[1]);
					if (auto) {
						((PromotionMove) toAdd).setPromoteTo(new Queen(this.side));
					}
				} else {
					toAdd = new Move(board[x][y], x, y, move[0], move[1], move[0], move[1]);
				}
			} else {
				continue;
			}

			if (checkLegal(x, y, toAdd))
				legalMoves.add(toAdd);
		}

		// checks if piece is a pawn, then checks if en passant pawn is to the left or
		// right of selected pawn
		if (board[x][y] instanceof Pawn) {
			if (x == enPassant.first) {
				// checks to left and right, then checks legality
				if (y - enPassant.second == 1) {
					Move temp = new Move(board[x][y], x, y, x + (this.side ? 1 : -1), y - 1, x, y - 1);
					if (checkLegal(x, y, temp))
						legalMoves.add(temp);
				} else if (y - enPassant.second == -1) {
					Move temp = new Move(board[x][y], x, y, x + (this.side ? 1 : -1), y + 1, x, y + 1);
					if (checkLegal(x, y, temp))
						legalMoves.add(temp);
				}
			}
		}

		return legalMoves;
	}

	public ArrayList<Move> getLegalMoves(Pair pos, boolean auto) {
		return getLegalMoves(pos.first, pos.second, auto);
	}

	public Move chooseRandomMove() { // Author: Daniel - gets a random legal move
		ArrayList<Move> allLegalMoves = new ArrayList<Move>();
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (board[i][j] != null && board[i][j].getColor() == this.side) {
					ArrayList<Move> temp = getLegalMoves(i, j, true);
					for (Move x : temp) {
						if (x instanceof PromotionMove) {
							((PromotionMove) x).setPromoteTo(new Queen(this.side));
						}
						allLegalMoves.add(x);
					}
				}
			}
		}

		int rnd = new Random().nextInt(allLegalMoves.size());
		return allLegalMoves.get(rnd);
	}

	public ArrayList<Move> getAllLegalMoves() {
		ArrayList<Move> allLegalMoves = new ArrayList<Move>();
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (board[i][j] != null && board[i][j].getColor() == this.side) {
					ArrayList<Move> temp = getLegalMoves(i, j, true);
					for (Move x : temp) {
						if (x instanceof PromotionMove) {
							((PromotionMove) x).setPromoteTo(new Queen(this.side));
						}
						allLegalMoves.add(x);
					}
				}
			}
		}

		return allLegalMoves;
	}

	public int evaluate() { // Author: Daniel - evaluates a position, returns centipawn advantage
		boolean endgame = true;
		boolean endgame2 = true;
		int occurrences = 0;
		for (Piece[] x : board) {
			for (Piece y : x) {
				if (y instanceof Queen) {
					endgame = false;
				}
				
				if (y instanceof Bishop || y instanceof Knight || y instanceof Rook) {
					endgame2 = false;
					occurrences++;
				}
			}
		}
		
		endgame = (endgame && occurrences <= 6) || endgame2;

		int points = 0;
		for (int row = 0; row < 8; row++) {
			for (int column = 0; column < 8; column++) {
				if (board[row][column] != null && board[row][column].getColor()) {
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
						if (!endgame) {
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
				if (board[row][column] != null && !board[row][column].getColor()) {
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
						if (!endgame) {
							points -= Eval.kingmid[row][column];
						} else {
							points -= Eval.kingend[row][column];
						}
					}
				}
			}
		}
		Eval.flip();
		return points;
	}

	public boolean checked(boolean color) { // author: Benjamin, return false if king's not checked, return true if
											// king's checked
		int kx = kingPos[color? 1: 0].first, ky = kingPos[color? 1: 0].second;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (board[i][j] != null && board[i][j].getColor() != color) {
					ArrayList<int[]> moves = board[i][j].getMoveSet(board, i, j);
					for (int[] move : moves) {
						if (move[0] == kx && move[1] == ky)
							return true;
					}
				}
			}
		}
		return false;
	}

	public int gameOver(boolean color) { // author: Benjamin, return 0 for not game over, 1 for checkmate, and 2 for
											// stalemate
		int occurrences = 0;
		for (long x : previousZobrists) {
			if (x == getZobristKey()) occurrences++;
		}
		
		if (occurrences >= 2) {
			return 2;
		}
		if (moveRule >= 50) {
			return 2;
		}
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (board[i][j] != null && board[i][j].getColor() == color && !getLegalMoves(i, j, true).isEmpty())
					return 0;
			}
		}
		if (checked(color))
			return 1;
		return 2;
	}
	
	public void undoMove() {
		int last = undoMoveStack.size() - 1;
		ArrayList<unMove> moves = undoMoveStack.remove(last);
		moveRule = undoMoveRuleStack.remove(last);
		enPassant = undoEnPassantStack.remove(last);
		zobristKey = previousZobrists.remove(last);
		previousMoves.remove(last);
		side = !side;
		for (int i = moves.size() - 1; i >= 0; i--) {
			unMove toUndo = moves.get(i);
			Pair location = toUndo.location;
			Piece piece = toUndo.piece;
			if (piece != null) {
				piece.undoMoveCounter();
			}
			board[location.first][location.second] = piece;
			// undo the position of king
			if(piece instanceof King) {
				kingPos[piece.getColor()? 1: 0] = location;
			}
		}
	}
	
	public ArrayList<Pair> spacesThreat(Pair pos) {
		ArrayList<Move> a = getLegalMoves(pos, true);
		ArrayList<Pair> toReturn = new ArrayList<Pair>();
		for (Move x : a) {
			if (x.getCapture() != null) {
				toReturn.add(x.getCapture());
			} else {
				toReturn.add(x.getEnd());
			}
		}
		return toReturn;
	}

	public ArrayList<Pair> spacesThreat(int row, int column) {
		return spacesThreat(new Pair(row, column));
	}

	public ArrayList<Pair> piecesThreatened(Pair pos) {
		ArrayList<Pair> toReturn = new ArrayList<Pair>();
		for (int row = 0; row < 8; row++) {
			for (int column = 0; column < 8; column++) {
				if (board[row][column] != null && board[row][column].getColor() != this.side) {
					ArrayList<Move> a = getLegalMoves(row, column, true);
					for (Move x : a) {
						if (x.getCapture() != null && x.getCapture().equals(pos)) {
							toReturn.add(new Pair(row, column));
						} else if (x.getEnd().equals(pos)) {
							toReturn.add(new Pair(row, column));
						}
					}
				}
			}
		}

		return toReturn;
	}
	
	public ArrayList<Pair> piecesThreatened(Pair pos, boolean color) {
		ArrayList<Pair> toReturn = new ArrayList<>();
		for (int row = 0; row < 8; row++) {
			for (int column = 0; column < 8; column++) {
				if (board[row][column] != null && board[row][column].getColor() == color) {
					ArrayList<Move> a = getLegalMoves(row, column, true);
					for (Move x : a) {
						if (x.getCapture() != null && x.getCapture().equals(pos)) {
							toReturn.add(new Pair(row, column));
						} else if (x.getEnd().equals(pos)) {
							toReturn.add(new Pair(row, column));
						}
					}
				}
			}
		}

		return toReturn;
	}

	public ArrayList<Pair> piecesThreatened(int row, int column) {
		return piecesThreatened(new Pair(row, column));
	}

	public ArrayList<Pair> piecesThreatening(Pair pos) {
		ArrayList<Pair> moves = null;
		for (Move move : getLegalMoves(pos.first, pos.second, true)) {
			if (board[move.getEnd().first][move.getEnd().second] != null) {
				moves.add(new Pair(move.getEnd().first, move.getEnd().second));
			}
		}
		return moves;
	}

	public ArrayList<Pair> piecesThreatening(int row, int column) {
		return piecesThreatening(new Pair(row, column));
	}

	public ArrayList<Move> simulatePlay(String fileName) { 
		File log = new File(fileName);
		ArrayList<Move> moves = new ArrayList<Move> ();
		Scanner myScanner = null;
		try {
			myScanner = new Scanner(log);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		boolean side = false;
		boolean hasCapture;
		boolean started = false;
		Move curMove = null;
		while (myScanner.hasNext()) {
			String next = myScanner.next();
			hasCapture = false;
			if (next.charAt(next.length() - 1) == '.') {
				started = true;
				continue;
			}
			if(started) {
				if (next.contains("x")) {
					next = next.substring(0, next.indexOf('x')) + next.substring(next.indexOf('x') + 1);
					hasCapture = true;
				}
				if (next.charAt(next.length() - 1) == '+' || next.charAt(next.length() - 1) == '#')
					next = next.substring(0, next.length() - 1);
				if (!isLowerCaseLetter(next.charAt(0)) && !isUpperCaseLetter(next.charAt(0)))
					continue;
				else if (next.charAt(0) == 'K' || next.charAt(0) == 'Q' || next.charAt(0) == 'R'
						|| next.charAt(0) == 'B' || next.charAt(0) == 'N') {
					if(isNumber(next.charAt(next.length() - 1)) == false)
						continue;
					if (side == true)
						side = false;
					else
						side = true;
					int x = -1, y = -1;
					if (next.length() == 3) {
						for (Pair pos : piecesThreatened(new Pair(next.charAt(2) - '1', next.charAt(1) - 'a'), side)) {
							switch (next.charAt(0)) {
								case 'K':
									if (board[pos.first][pos.second] instanceof King) {
										x = pos.first;
										y = pos.second;
									}
									break;
								case 'Q':
									if (board[pos.first][pos.second] instanceof Queen) {
										x = pos.first;
										y = pos.second;
									}
									break;
								case 'R':
									if (board[pos.first][pos.second] instanceof Rook) {
										x = pos.first;
										y = pos.second;
									}
									break;
								case 'B':
									if (board[pos.first][pos.second] instanceof Bishop) {
										x = pos.first;
										y = pos.second;
									}
									break;
								case 'N':
									if (board[pos.first][pos.second] instanceof Knight) {
										x = pos.first;
										y = pos.second;
									}
									break;
							}
						}
						if(hasCapture == true)
							curMove = new Move(board[x][y], x, y, next.charAt(2) - '1', next.charAt(1) - 'a', next.charAt(2) - '1', next.charAt(1) - 'a');
						else
							curMove = new Move(board[x][y], x, y, next.charAt(2) - '1', next.charAt(1) - 'a');
					} else if (next.length() == 4) {
						for (Pair pos : piecesThreatened(new Pair(next.charAt(3) - '1', next.charAt(2) - 'a'), side)) {
							if ((isLowerCaseLetter(next.charAt(1)) && pos.second != next.charAt(1) - 'a')
									|| (isNumber(next.charAt(1)) && pos.first != next.charAt(1) - '1'))
								continue;
							switch (next.charAt(0)) {
								case 'K':
									if (board[pos.first][pos.second] instanceof King) {
										x = pos.first;
										y = pos.second;
									}
									break;
								case 'Q':
									if (board[pos.first][pos.second] instanceof Queen) {
										x = pos.first;
										y = pos.second;
									}
									break;
								case 'R':
									if (board[pos.first][pos.second] instanceof Rook) {
										x = pos.first;
										y = pos.second;
									}
									break;
								case 'B':
									if (board[pos.first][pos.second] instanceof Bishop) {
										x = pos.first;
										y = pos.second;
									}
									break;
								case 'N':
									if (board[pos.first][pos.second] instanceof Knight) {
										x = pos.first;
										y = pos.second;
									}
									break;
							}
						}
						if(hasCapture == true)
							curMove = new Move(board[x][y], x, y, next.charAt(3) - '1', next.charAt(2) - 'a', next.charAt(3) - '1', next.charAt(2) - 'a');
						else
							curMove = new Move(board[x][y], x, y, next.charAt(3) - '1', next.charAt(2) - 'a');
					} else {
						if(hasCapture == true)
							curMove = new Move(board[next.charAt(2) - '1'][next.charAt(1) - 'a'], next.charAt(2) - '1',
									next.charAt(1) - 'a', next.charAt(4) - '1', next.charAt(3) - 'a', next.charAt(4) - '1', next.charAt(3) - 'a');
						else
							curMove = new Move(board[next.charAt(2) - '1'][next.charAt(1) - 'a'], next.charAt(2) - '1',
									next.charAt(1) - 'a', next.charAt(4) - '1', next.charAt(3) - 'a');
					}
				} else if (next.charAt(0) == 'O' && next.charAt(1) == '-') {
					if (side == true)
						side = false;
					else
						side = true;
					if (next.length() == 3) {
						if (side == true) {
							curMove = new Move(board[0][4], 0, 4, 0, 6, board[0][7], 0, 7, 0, 5);
						} else {
							curMove = new Move(board[7][4], 7, 4, 7, 6, board[7][7], 7, 7, 7, 5);
						}
					} else {
						if (side == true) {
							curMove = new Move(board[0][4], 0, 4, 0, 2, board[0][7], 0, 0, 0, 3);
						} else {
							curMove = new Move(board[7][4], 7, 4, 7, 2, board[7][7], 7, 0, 7, 3);
						}
					}
				} else {
					if (next.contains("=") == false) {
						if(isNumber(next.charAt(next.length() - 1)) == false)
							continue;
						if (side == true)
							side = false;
						else
							side = true;
						if (next.length() == 2) {
							if (side == true) {
								if(board[next.charAt(1) - '1' - 1][next.charAt(0) - 'a'] != null) {
									if(hasCapture == true)
										curMove = new Move(board[next.charAt(1) - '1' - 1][next.charAt(0) - 'a'],
												next.charAt(1) - '1' - 1, next.charAt(0) - 'a', next.charAt(1) - '1',
												next.charAt(0) - 'a', next.charAt(1) - '1',
												next.charAt(0) - 'a');
									else
										curMove = new Move(board[next.charAt(1) - '1' - 1][next.charAt(0) - 'a'],
												next.charAt(1) - '1' - 1, next.charAt(0) - 'a', next.charAt(1) - '1',
												next.charAt(0) - 'a');
								}
								else {
									if(hasCapture == true)
										curMove = new Move(board[next.charAt(1) - '1' - 2][next.charAt(0) - 'a'],
												next.charAt(1) - '1' - 2, next.charAt(0) - 'a', next.charAt(1) - '1',
												next.charAt(0) - 'a', next.charAt(1) - '1',
												next.charAt(0) - 'a');
									else
										curMove = new Move(board[next.charAt(1) - '1' - 2][next.charAt(0) - 'a'],
												next.charAt(1) - '1' - 2, next.charAt(0) - 'a', next.charAt(1) - '1',
												next.charAt(0) - 'a');
								}
							} else {
								if(board[next.charAt(1) - '1' + 1][next.charAt(0) - 'a'] != null) {
									if(hasCapture == true)
										curMove = new Move(board[next.charAt(1) - '1' + 1][next.charAt(0) - 'a'],
												next.charAt(1) - '1' + 1, next.charAt(0) - 'a', next.charAt(1) - '1',
												next.charAt(0) - 'a', next.charAt(1) - '1',
												next.charAt(0) - 'a');
									else
										curMove = new Move(board[next.charAt(1) - '1' + 1][next.charAt(0) - 'a'],
												next.charAt(1) - '1' + 1, next.charAt(0) - 'a', next.charAt(1) - '1',
												next.charAt(0) - 'a');
								}
								else {
									if(hasCapture == true)
										curMove = new Move(board[next.charAt(1) - '1' + 2][next.charAt(0) - 'a'],
												next.charAt(1) - '1' + 2, next.charAt(0) - 'a', next.charAt(1) - '1',
												next.charAt(0) - 'a', next.charAt(1) - '1',
												next.charAt(0) - 'a');
									else
										curMove = new Move(board[next.charAt(1) - '1' + 2][next.charAt(0) - 'a'],
												next.charAt(1) - '1' + 2, next.charAt(0) - 'a', next.charAt(1) - '1',
												next.charAt(0) - 'a');
								}
							}
						} else {
							if(board[next.charAt(2) - '1'][next.charAt(1)- 'a'] == null) {
								if (side == true) {
									curMove = new Move(board[next.charAt(2) - '1' - 1][next.charAt(0) - 'a'],
											next.charAt(2) - '1' - 1, next.charAt(0) - 'a', next.charAt(2) - '1',
											next.charAt(1) - 'a', next.charAt(2) - '1' - 1, next.charAt(1) - 'a');
								} else {
									curMove = new Move(board[next.charAt(2) - '1' + 1][next.charAt(0) - 'a'],
											next.charAt(2) - '1' + 1, next.charAt(0) - 'a', next.charAt(2) - '1',
											next.charAt(1) - 'a', next.charAt(2) - '1' + 1, next.charAt(1) - 'a');
								}
							} else {
								if (side == true) {
									if(hasCapture == true)
										curMove = new Move(board[next.charAt(2) - '1' - 1][next.charAt(0) - 'a'],
												next.charAt(2) - '1' - 1, next.charAt(0) - 'a', next.charAt(2) - '1',
												next.charAt(1) - 'a', next.charAt(2) - '1',
												next.charAt(1) - 'a');
									else
										curMove = new Move(board[next.charAt(2) - '1' - 1][next.charAt(0) - 'a'],
												next.charAt(2) - '1' - 1, next.charAt(0) - 'a', next.charAt(2) - '1',
												next.charAt(1) - 'a');
								} else {
									if(hasCapture == true)
										curMove = new Move(board[next.charAt(2) - '1' + 1][next.charAt(0) - 'a'],
												next.charAt(2) - '1' + 1, next.charAt(0) - 'a', next.charAt(2) - '1',
												next.charAt(1) - 'a', next.charAt(2) - '1',
												next.charAt(1) - 'a');
									else
										curMove = new Move(board[next.charAt(2) - '1' + 1][next.charAt(0) - 'a'],
												next.charAt(2) - '1' + 1, next.charAt(0) - 'a', next.charAt(2) - '1',
												next.charAt(1) - 'a');
								}
							}
						}
					} else {
						if (side == true)
							side = false;
						else
							side = true;
						Piece newPiece = null;
						switch (next.charAt(next.indexOf("=") + 1)) {
							case 'Q':
								newPiece = new King(side);
								break;
							case 'R':
								newPiece = new Rook(side);
								break;
							case 'N':
								newPiece = new Knight(side);
								break;
							case 'B':
								newPiece = new Bishop(side);
								break;
						}
						if (next.length() == 4) {
							if (side == true) {
								if(hasCapture == true)
									curMove = new PromotionMove(board[next.charAt(1) - '1' - 1][next.charAt(0) - 'a'],
											next.charAt(1) - '1' - 1, next.charAt(0) - 'a', next.charAt(1) - '1',
											next.charAt(0) - 'a', next.charAt(1) - '1',
											next.charAt(0) - 'a', newPiece);
								else
									curMove = new PromotionMove(board[next.charAt(1) - '1' - 1][next.charAt(0) - 'a'],
											next.charAt(1) - '1' - 1, next.charAt(0) - 'a', next.charAt(1) - '1',
											next.charAt(0) - 'a', newPiece);
							} else {
								if(hasCapture == true)
									curMove = new PromotionMove(board[next.charAt(1) - '1' + 1][next.charAt(0) - 'a'],
											next.charAt(1) - '1' + 1, next.charAt(0) - 'a', next.charAt(1) - '1',
											next.charAt(0) - 'a', next.charAt(1) - '1',
											next.charAt(0) - 'a', newPiece);
								else
									curMove = new PromotionMove(board[next.charAt(1) - '1' + 1][next.charAt(0) - 'a'],
											next.charAt(1) - '1' + 1, next.charAt(0) - 'a', next.charAt(1) - '1',
											next.charAt(0) - 'a', newPiece);
							}
						} else {
							if (side == true) {
								if(hasCapture == true)
									curMove = new PromotionMove(board[next.charAt(2) - '1' - 1][next.charAt(0) - 'a'],
											next.charAt(2) - '1' - 1, next.charAt(0) - 'a', next.charAt(2) - '1',
											next.charAt(1) - 'a', next.charAt(2) - '1',
											next.charAt(1) - 'a', newPiece);
								else
									curMove = new PromotionMove(board[next.charAt(2) - '1' - 1][next.charAt(0) - 'a'],
											next.charAt(2) - '1' - 1, next.charAt(0) - 'a', next.charAt(2) - '1',
											next.charAt(1) - 'a', newPiece);
							} else {
								if(hasCapture == true)
									curMove = new PromotionMove(board[next.charAt(2) - '1' + 1][next.charAt(0) - 'a'],
											next.charAt(2) - '1' + 1, next.charAt(0) - 'a', next.charAt(2) - '1',
											next.charAt(1) - 'a', next.charAt(2) - '1',
											next.charAt(1) - 'a', newPiece);
								else
									curMove = new PromotionMove(board[next.charAt(2) - '1' + 1][next.charAt(0) - 'a'],
											next.charAt(2) - '1' + 1, next.charAt(0) - 'a', next.charAt(2) - '1',
											next.charAt(1) - 'a', newPiece);
							}
						}
					}
				}
				moves.add(curMove);
				submitMove(curMove);
			}
		}
		return moves;
	}
	
	private long[] getZobristArray() {
		Random randGen = new Random(1283717); // set seed to make it deterministic
		long[] toReturn = new long[781];
		for (int i = 0; i < 781; i++) {
			toReturn[i] = randGen.nextLong();
		}
		return toReturn;
	}
	
	private long initializeZobristKey() {
		long toReturn = 0;
		for (int r = 0; r < 8; r++) {
			for (int c = 0; c < 8; c++) {
				if (board[r][c] != null) {
					toReturn ^= zobristArray[pieceToZobristIndex(r, c)];
				}
			}
		}
		
		toReturn ^= zobristArray[BLACK_SHORT];
		toReturn ^= zobristArray[BLACK_LONG];
		toReturn ^= zobristArray[WHITE_SHORT];
		toReturn ^= zobristArray[WHITE_LONG];
		
		return toReturn;
	}
	
	private int pieceToZobristIndex(int r, int c) {
		int index = 0;
		Piece piece = board[r][c];
		
		if (piece instanceof Pawn) {
			;
		} else if (piece instanceof Knight) {
			index += 1;
		} else if (piece instanceof Bishop) {
			index += 2;
		} else if (piece instanceof Rook) {
			index += 3;
		} else if (piece instanceof Queen) {
			index += 4;
		} else if (piece instanceof King) {
			index += 5;
		}
		
		if (piece.getColor()) {
			index += 6;
		}
		
		index *= 64;
		
		index += r * 8 + c;
		
		return index;
	}
	
	private boolean[] castlingRights() {
		boolean blackKing = board[7][4] instanceof King && board[7][4].getColor() == false && board[7][4].getFirstMove();
		boolean blackShortRook = board[7][7] instanceof Rook && board[7][7].getColor() == false && board[7][7].getFirstMove();
		boolean blackLongRook = board[7][0] instanceof Rook && board[7][0].getColor() == false && board[7][0].getFirstMove();
		boolean whiteKing = board[0][4] instanceof King && board[0][4].getColor() == true && board[0][4].getFirstMove();
		boolean whiteShortRook = board[0][7] instanceof Rook && board[0][7].getColor() == true && board[0][7].getFirstMove();
		boolean whiteLongRook = board[0][0] instanceof Rook && board[0][0].getColor() == true && board[0][0].getFirstMove();
		
		boolean[] toReturn = new boolean[4];
		toReturn[0] = blackKing && blackShortRook;
		toReturn[1] = blackKing && blackLongRook;
		toReturn[2] = whiteKing && whiteShortRook;
		toReturn[3] = whiteKing && whiteLongRook;
		
		return toReturn;
	}
	
	public ArrayList<Long> getPreviousZobrists() {
		return previousZobrists;
	}
	
	public long getZobristKey() {
		return zobristKey;
	}
	
	boolean isNumber(char c) {
		if (c >= '0' && c <= '9')
			return true;
		return false;
	}

	boolean isLowerCaseLetter(char c) {
		if (c >= 'a' && c <= 'z')
			return true;
		return false;
	}

	boolean isUpperCaseLetter(char c) {
		if (c >= 'A' && c <= 'Z')
			return true;
		return false;
	}

	public boolean getSide() {
		return side;
	}

	public Piece[][] getBoard() {
		return board;
		// git test please ignore
		// testing
		// final testing
	}

	public Piece getBoard(Pair pos) {
		return board[pos.first][pos.second];
	}

	public int getMoveRule() {
		return moveRule;
	}


    public static int getWIDTH() {
		return WIDTH;
	}

	public static void setWIDTH(int wIDTH) {
		WIDTH = wIDTH;
	}

	public static int getHEIGHT() {
		return HEIGHT;
	}

	public static void setHEIGHT(int hEIGHT) {
		HEIGHT = hEIGHT;
	}

	public Pair getEnPassant() {
		return enPassant;
	}

	public void setEnPassant(Pair enPassant) {
		this.enPassant = enPassant;
	}

	public boolean isLogging() {
		return logging;
	}

	public void setLogging(boolean logging) {
		this.logging = logging;
	}

	public MatchLogging getLogger() {
		return logger;
	}

	public void setLogger(MatchLogging logger) {
		this.logger = logger;
	}

	public ArrayList<ArrayList<unMove>> getUndoMoveStack() {
		return undoMoveStack;
	}

	public void setUndoMoveStack(ArrayList<ArrayList<unMove>> undoMoveStack) {
		this.undoMoveStack = undoMoveStack;
	}

	public ArrayList<Integer> getUndoMoveRuleStack() {
		return undoMoveRuleStack;
	}

	public void setUndoMoveRuleStack(ArrayList<Integer> undoMoveRuleStack) {
		this.undoMoveRuleStack = undoMoveRuleStack;
	}

	public ArrayList<Pair> getUndoEnPassantStack() {
		return undoEnPassantStack;
	}

	public void setUndoEnPassantStack(ArrayList<Pair> undoEnPassantStack) {
		this.undoEnPassantStack = undoEnPassantStack;
	}

	public boolean getProceed() {
    	return this.proceed;
    }
    
    public void setProceed(boolean proceed) {
    	this.proceed = proceed;
    }
    
    public boolean getLoadingSimulation() {
    	return this.loadingSimulation;
    }
    
    public void setLoadingSimulation(boolean loadingSimulation) {
    	this.loadingSimulation = loadingSimulation;
    }
    
    public ArrayList<Move> getPreviousMoves() {
    	return previousMoves;
    }
}
