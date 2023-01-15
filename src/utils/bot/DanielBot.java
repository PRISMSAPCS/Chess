package utils.bot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import utils.*;

public class DanielBot extends ChessBot {
	int evalMult;
	int posCounter;
	ChessBoard boardCopy;
	Move bestMove;
	public DanielBot(ChessBoard board, boolean side) {
		super(board);
		evalMult = (side) ? 1 : -1;
	}

	@Override
	public Move getMove() {
		boardCopy = new ChessBoard(super.getBoard());
		posCounter = 0;
		System.out.println(miniMax(4, -100000, 100000, true, true));
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
				if (boardCopy.getBoard(new Pair(x.getEnd().first + 1, x.getEnd().second + ((boardCopy.getSide()) ? -1 : 1))) instanceof Pawn) {
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
