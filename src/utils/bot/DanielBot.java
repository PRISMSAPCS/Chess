package utils.bot;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.Scanner;

import utils.*;

public class DanielBot extends ChessBot {
	int evalMult;
	int posCounter;
	ChessBoard boardCopy;
	Move bestMove;
	private boolean inBook;
	public DanielBot(ChessBoard board, boolean side) {
		super(board);
		inBook = true;
		evalMult = (side) ? 1 : -1;
	}

	@Override
	public Move getMove() {
		if (inBook && bookMove()) {
			return bestMove;
		} else {
			inBook = false;
		}
		boardCopy = new ChessBoard(super.getBoard());
		posCounter = 0;
		miniMax(4, -100000, 100000, true, true);
		System.out.println(posCounter);
		return bestMove;
	}
	
	private int miniMax(int depth, int alpha, int beta, boolean maximizingPlayer, boolean setBestMove) {
		posCounter++;
		if (depth == 0) return quietSearch(alpha, beta, maximizingPlayer);
		ArrayList<Move> legalMoves = orderMoves(boardCopy.getAllLegalMoves());
		if (legalMoves.isEmpty()) {
			if (boardCopy.checked(boardCopy.getSide())) {
				return ((boardCopy.getSide()) ? -1 : 1) * (50000 + depth); 
			}
			return 0;
		}
		if (maximizingPlayer) {
			int max = -1000000;
			for (Move x : legalMoves) {
				boardCopy.submitMove(x);
				int eval = miniMax(depth - 1, alpha, beta, false, false) * evalMult;
				boardCopy.undoMove();
				if (max <= eval) {
					max = eval;
					if (setBestMove) { bestMove = x; }
				}
				
				if (max > beta) break;
				alpha = Math.max(max, alpha);
			}
			
			return max * evalMult;
		} else {
			int min = 1000000;
			for (Move x : legalMoves) {
				boardCopy.submitMove(x);
				int eval = miniMax(depth - 1, alpha, beta, true, false) * evalMult;
				boardCopy.undoMove();
				if (min >= eval) {
					min = eval;
				}
				
				if (min < alpha) break;
				beta = Math.min(min, beta);
			}
			
			return min * evalMult;
		}
	}
	
	private ArrayList<Move> orderMoves(ArrayList<Move> moves) {
		ArrayList<int[]> indexPair = new ArrayList<int[]>();
		for (int i = 0; i < moves.size(); i++) {
			Move x = moves.get(i);
			int score = 0;
			Piece movePiece = x.getPiece();
			Piece capturePiece = boardCopy.getBoard((x.getCapture() != null) ? x.getCapture() : x.getEnd());
			
			if (capturePiece != null) {
				score = 10 * getPieceValue(capturePiece) - getPieceValue(movePiece);
			}
			
			if (movePiece instanceof Pawn) {
				if (x instanceof PromotionMove) {
					score += 900;
				}
			} else {
				try {
				if (boardCopy.getBoard(new Pair(x.getEnd().first + 1, x.getEnd().second + ((boardCopy.getSide()) ? 1 : -1))) instanceof Pawn) {
					score -= 350;
				}
				} catch (Exception e) {
					
				}
				try {
				if (boardCopy.getBoard(new Pair(x.getEnd().first - 1, x.getEnd().second + ((boardCopy.getSide()) ? -1 : 1))) instanceof Pawn) {
					score -= 350;
				}
				} catch (Exception e) {
					
				}
			}
			
			int[] toAdd = {i, score};
			indexPair.add(toAdd);
		}
		
		Collections.sort(indexPair, new scoreSort());
		
		ArrayList<Move> toReturn = new ArrayList<Move>();
		for (int[] x : indexPair) {
			toReturn.add(moves.get(x[0]));
		}
		
		return toReturn;
	}
	
	private boolean bookMove() {
		ArrayList<Move> moves = super.getBoard().getPreviousMoves();
		System.out.println(moves.size());
		ChessBoard b = new ChessBoard();
		String PGNString = "";

		for (Move x : moves) {
	        boolean captured = b.getBoard(x.getEnd()) != null;
			if (x.getEnd2() != null) {
				if (x.getEnd().second == 2) {
            		PGNString += "O-O-O";
            	} else {
            		PGNString += "O-O";
            	}
			} else if (x instanceof PromotionMove) {
				if (captured) {
                	PGNString += x.getStart().getCol() + "x";
                }
                PGNString += x.getEnd().toChessNote() + "=Q";
			} else if (x.getPiece() instanceof Pawn) {
				if (captured || x.getCapture() != null) {
                    PGNString += x.getStart().getCol() + "x";
                }
                PGNString += x.getEnd().toChessNote();
			} else {
				if (x.getPiece() instanceof Knight) PGNString += "N";
				if (x.getPiece() instanceof Bishop) PGNString += "B";
				if (x.getPiece() instanceof Rook) PGNString += "R";
				if (x.getPiece() instanceof Queen) PGNString += "Q";
				if (x.getPiece() instanceof King) PGNString += "K";
				
				for (int i = 0; i < 8; i++) {
					if (i != x.getStart().second) {
						Piece toCheck = b.getBoard()[x.getStart().first][i];
						if (toCheck == null) continue;
						if (toCheck.getClass().equals(x.getPiece().getClass()) && toCheck.getColor() == x.getPiece().getColor()) {
							ArrayList<int[]> moveset = toCheck.getMoveSet(b.getBoard(), x.getStart().first, i);
							for (int[] finalSquare : moveset) {
								if ((new Pair(finalSquare[0], finalSquare[1])).equals(x.getEnd())) {
									PGNString += Character.toString(x.getStart().getCol());
								}
							}
						}
					}
				}
				
				for (int i = 0; i < 8; i++) {
					if (i != x.getStart().first) {
						Piece toCheck = b.getBoard()[i][x.getStart().second];
						if (toCheck == null) continue;
						if (toCheck.getClass().equals(x.getPiece().getClass()) && toCheck.getColor() == x.getPiece().getColor()) {
							ArrayList<int[]> moveset = toCheck.getMoveSet(b.getBoard(), i, x.getStart().second);
							for (int[] finalSquare : moveset) {
								if ((new Pair(finalSquare[0], finalSquare[1])).equals(x.getEnd())) {
									PGNString += Character.toString(x.getStart().getRow());
								}
							}
						}
					}
				}

                if (captured) {
                	PGNString += "x";
                }
                
                PGNString += x.getEnd().toChessNote();
			}
			
			b.submitMove(x);
			PGNString += " ";
		}
		if (PGNString.length() > 0) {
			PGNString = PGNString.substring(0, PGNString.length() - 1);
		}
		
		File file = new File("src//utils//bot//DanielBotResources//final.pgn");
		ArrayList<String> possibleContinuations = new ArrayList<String>();
		
		try {
			Scanner scanner = new Scanner(file);
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				
				if (line.length() <= PGNString.length()) continue;
				
				if (line.substring(0, PGNString.length()).equals(PGNString)) {
					if (PGNString.length() > 0) { 
						line = line.substring(PGNString.length() + 1);
					}
					possibleContinuations.add(line.split(" ", 2)[0]);
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (possibleContinuations.isEmpty()) return false;
		
		int rnd = new Random().nextInt(possibleContinuations.size());
		String result = possibleContinuations.get(rnd);
		System.out.println(result);
		Move move = null;
		
		if (Character.isLowerCase(result.charAt(0))) { // pawn move
			if (result.length() == 2) { // normal pawn move
				int moveFile = result.charAt(0) - 'a';
				int moveRank = result.charAt(1) - '1';
				if (moveRank == 3 && b.getSide() == true) { // checking if it was a double move or not
					if (b.getBoard()[2][moveFile] instanceof Pawn) {
						move = new Move(b.getBoard()[2][moveFile], 2, moveFile, 3, moveFile);
					} else {
						move = new Move(b.getBoard()[1][moveFile], 1, moveFile, 3, moveFile);
					}
				} else if (moveRank == 4 && b.getSide() == false) {
					if (b.getBoard()[5][moveFile] instanceof Pawn) {
						move = new Move(b.getBoard()[5][moveFile], 5, moveFile, 4, moveFile);
					} else {
						move = new Move(b.getBoard()[6][moveFile], 6, moveFile, 4, moveFile);
					}
				} else { // not a double move
					move = new Move(b.getBoard()[moveRank - (b.getSide() ? 1 : -1)][moveFile], moveRank - (b.getSide() ? 1 : -1), moveFile, moveRank, moveFile);
				}
			} else if (result.charAt(1) == 'x') { // pawn captured something else
				int startFile = result.charAt(0) - 'a';
				int endFile = result.charAt(2) - 'a';
				int endRank = result.charAt(3) - '1';
				int startRank = endRank - (b.getSide() ? 1 : -1);
				if (result.length() == 6) { // pawn capture promotion (exc6=Q is 6 characters)
					char piecePromote = result.charAt(5);
					Piece toPromoteTo = null;
					switch (piecePromote) {
					case 'N':
						toPromoteTo = new Knight(b.getSide());
						break;
					case 'B':
						toPromoteTo = new Bishop(b.getSide());
						break;
					case 'R':
						toPromoteTo = new Rook(b.getSide());
						break;
					case 'Q':
						toPromoteTo = new Queen(b.getSide());
						break;
					}
					
					move = new PromotionMove(b.getBoard()[startRank][startFile], startRank, startFile, endRank, endFile, toPromoteTo);
				} else if (b.getBoard()[endRank][endFile] == null) { // nothing where we captured? must be en passant
					move = new Move(b.getBoard()[startRank][startFile], startRank, startFile, endRank, endFile, endRank - (b.getSide() ? 1 : -1), endFile);
				} else { // normal pawn capture
					move = new Move(b.getBoard()[startRank][startFile], startRank, startFile, endRank, endFile);
				}
			} else if (result.charAt(2) == '=') { // promotion
				int moveFile = result.charAt(0) - 'a';
				int endRank = result.charAt(1) - '1';
				int startRank = endRank - (b.getSide() ? 1 : -1);
				char piecePromote = result.charAt(3);
				Piece toPromoteTo = null;
				
				switch (piecePromote) {
				case 'N':
					toPromoteTo = new Knight(b.getSide());
					break;
				case 'B':
					toPromoteTo = new Bishop(b.getSide());
					break;
				case 'R':
					toPromoteTo = new Rook(b.getSide());
					break;
				case 'Q':
					toPromoteTo = new Queen(b.getSide());
					break;
				}
				
				move = new PromotionMove(b.getBoard()[startRank][moveFile], startRank, moveFile, endRank, moveFile, toPromoteTo);
			}
		} else if (result.charAt(0) == 'O') { // castling
			if (result.equals("O-O")) {
				if (b.getSide()) {
					move = new Move(b.getBoard()[0][4], 0, 4, 0, 6, b.getBoard()[0][7], 0, 7, 0, 5);
				} else {
					move = new Move(b.getBoard()[7][4], 7, 4, 7, 6, b.getBoard()[7][7], 7, 7, 7, 5);
				}
			} else {
				if (b.getSide()) {
					move = new Move(b.getBoard()[0][4], 0, 4, 0, 2, b.getBoard()[0][0], 0, 0, 0, 3);
				} else {
					move = new Move(b.getBoard()[7][4], 7, 4, 7, 2, b.getBoard()[7][0], 7, 0, 7, 3);
				}
			}
		} else { // piece move
			result = result.replace("x", "");
			Piece referencePiece = null;
			
			switch (result.charAt(0)) {
			case 'N':
				referencePiece = new Knight(b.getSide());
				break;
			case 'B':
				referencePiece = new Bishop(b.getSide());
				break;
			case 'R':
				referencePiece = new Rook(b.getSide());
				break;
			case 'Q':
				referencePiece = new Queen(b.getSide());
				break;
			case 'K':
				referencePiece = new King(b.getSide());
				break;
			}
			
			int referenceFile = -1;
			int referenceRank = -1;
			int startFile = -1;
			int startRank = -1;
			int endFile = result.charAt(result.length() - 2) - 'a';
			int endRank = result.charAt(result.length() - 1) - '1';
			
			if (result.length() == 4) {
				if (Character.isDigit(result.charAt(1))) {
					referenceRank = result.charAt(1) - '1';
				} else {
					referenceFile = result.charAt(1) - 'a';
				}
			} else if (result.length() == 5) {
				referenceFile = result.charAt(1) - 'a';
				referenceRank = result.charAt(2) - '1';
			}
			
			if (referenceFile != -1 && referenceRank != -1) {
				startFile = referenceFile;
				startRank = referenceRank;
			} else if (referenceFile != -1) {
				for (int i = 0; i < 8; i++) {
					Piece possiblePiece = b.getBoard()[i][referenceFile];
					if (possiblePiece != null && possiblePiece.getClass().equals(referencePiece.getClass()) && possiblePiece.getColor() == b.getSide()) {
						ArrayList<Move> moveSet = b.getLegalMoves(i, referenceFile, false);
						for (Move pos : moveSet) {
							if (pos.getEnd().equals(new Pair(endRank, endFile))) {
								startFile = referenceFile;
								startRank = i;
							}
						}
					}
				}
			} else if (referenceRank != -1) {
				for (int i = 0; i < 8; i++) {
					Piece possiblePiece = b.getBoard()[referenceRank][i];
					if (possiblePiece != null && possiblePiece.getClass().equals(referencePiece.getClass()) && possiblePiece.getColor() == b.getSide()) {
						ArrayList<Move> moveSet = b.getLegalMoves(referenceRank, i, false);
						for (Move pos : moveSet) {
							if (pos.getEnd().equals(new Pair(endRank, endFile))) {
								startFile = i;
								startRank = referenceRank;
							}
						}
					}
				}
			} else {
				for (int i = 0; i < 8; i++) {
					for (int j = 0; j < 8; j++) {
						Piece possiblePiece = b.getBoard()[i][j];
						if (possiblePiece != null && possiblePiece.getClass().equals(referencePiece.getClass()) && possiblePiece.getColor() == b.getSide()) {
							ArrayList<Move> moveSet = b.getLegalMoves(i, j, false);
							for (Move pos : moveSet) {
								if (pos.getEnd().equals(new Pair(endRank, endFile))) {
									startFile = j;
									startRank = i;
								}
							}
						}
					}
				}
			}
			System.out.print(endRank);
			System.out.println(endFile);
			move = new Move(b.getBoard()[startRank][startFile], startRank, startFile, endRank, endFile);
		}
		
		bestMove = move;
		return true;
	}
	
	private int quietSearch(int alpha, int beta, boolean maximizingPlayer) {
		return boardCopy.evaluate();
//		posCounter++;
//		ArrayList<Move> legalMoves = pruneNonCaptures(boardCopy.getAllLegalMoves());
//		if (legalMoves.isEmpty()) return boardCopy.evaluate();
//
//		if (maximizingPlayer) {
//			int max = boardCopy.evaluate();
//			if (max >= beta) return beta;
//			if (max > alpha) alpha = max;
//			for (Move x : legalMoves) {
//				boardCopy.submitMove(x);
//				int eval = quietSearch(alpha, beta, false) * evalMult;
//				boardCopy.undoMove();
//				if (max <= eval) {
//					max = eval;
//				}
//				
//				if (max >= beta) return beta;
//				alpha = Math.max(max, alpha);
//			}
//			
//			return max * evalMult;
//		} else {
//			int min = boardCopy.evaluate();
//			if (min < alpha) return alpha;
//			if (min <= beta) beta = min;
//			for (Move x : legalMoves) {
//				boardCopy.submitMove(x);
//				int eval = quietSearch(alpha, beta, true) * evalMult;
//				boardCopy.undoMove();
//				if (min >= eval) {
//					min = eval;
//				}
//				
//				if (min <= alpha) return alpha;
//				beta = Math.min(min, beta);
//			}
//			
//			return min * evalMult;
//		}
	}
	
	private ArrayList<Move> pruneNonCaptures(ArrayList<Move> moves) {
		ArrayList<Move> temp = new ArrayList<Move>();
		for (Move x : moves) {
			if (x.getCapture() != null) {
				temp.add(x);
			} else if (boardCopy.getBoard()[x.getEnd().first][x.getEnd().second] != null) {
				temp.add(x);
			}
		}
		
		return temp;
	}
	
//	private int miniMax(int depth, boolean maximizingPlayer, boolean setBestMove) {
//		if (depth == 0) return boardCopy.evaluate();
//		ArrayList<Move> legalMoves = boardCopy.getAllLegalMoves();
//		if (maximizingPlayer) {
//			int max = -1000000;
//			for (Move x : legalMoves) {
//				boardCopy.submitMove(x);
//				int eval = miniMax(depth - 1, false, false) * evalMult;
//				if (max <= eval) {
//					max = eval;
//					if (setBestMove) { bestMove = x; }
//				}
//				
//				boardCopy.undoMove();
//			}
//			
//			return max * evalMult;
//		} else {
//			int min = 1000000;
//			for (Move x : legalMoves) {
//				boardCopy.submitMove(x);
//				int eval = miniMax(depth - 1, true, false) * evalMult;
//				if (min >= eval) {
//					min = eval;
//				}
//				
//				boardCopy.undoMove();
//			}
//			
//			return min * evalMult;
//		}
//	}
	
	private int getPieceValue(Piece piece) {
		if (piece instanceof King) {
			return 1000;
		} else if (piece instanceof Queen) {
			return 900;
		} else if (piece instanceof Bishop) {
			return 330;
		} else if (piece instanceof Knight) {
			return 320;
		} else if (piece instanceof Rook) {
			return 500;
		} else if (piece instanceof Pawn) {
			return 100;
		}
		
		return 0;
	}
	
	class scoreSort implements Comparator<int[]> {
		@Override
		public int compare(int[] o1, int[] o2) {
			// TODO Auto-generated method stub
			return o2[1] - o1[1];
		}
	}
	
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

}
