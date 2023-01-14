package utils.bot;

import java.util.ArrayList;

import utils.ChessBoard;
import utils.Move;

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
		miniMax(4, -100000, 100000, true, true);
		System.out.println(posCounter);
		return bestMove;
	}
	
	private int miniMax(int depth, int alpha, int beta, boolean maximizingPlayer, boolean setBestMove) {
		posCounter++;
		if (depth == 0) return boardCopy.evaluate();
		ArrayList<Move> legalMoves = boardCopy.getAllLegalMoves();
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
	
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

}
