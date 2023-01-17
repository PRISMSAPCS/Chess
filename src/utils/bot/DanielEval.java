package utils.bot;

import java.util.ArrayList;

import utils.*;

public class DanielEval {
	static final int wp = 0;
	static final int wn = 1;
	static final int wb = 2;
	static final int wr = 3;
	static final int wq = 4;
	static final int bp = 5;
	static final int bn = 6;
	static final int bb = 7;
	static final int br = 8;
	static final int bq = 9;
	
	static final int PawnPhase = 0;
	static final int KnightPhase = 1;
	static final int BishopPhase = 1;
	static final int RookPhase = 2;
	static final int QueenPhase = 4;
	static final int TotalPhase = PawnPhase*16 + KnightPhase*4 + BishopPhase*4 + RookPhase*4 + QueenPhase*2;
	
	static int[] pieceCount = new int[10];
	
	public static int evaluate(Piece[][] board) { // Author: Daniel - evaluates a position, returns centipawn advantage
		for (int i = 0; i < 10; i++) pieceCount[i] = 0;
		
		int pointsMaterial = 0;
		int pointsMidgame = 0;
		int pointsKingMid = 0;
		int pointsEndgame = 0;
		
		Pair whiteKingPos = null;
		Pair blackKingPos = null;
		
		for (int row = 0; row < 8; row++) {
			for (int column = 0; column < 8; column++) {
				if (board[row][column] != null && board[row][column].getColor()) {
					Piece piece = board[row][column];
					if (piece instanceof Pawn) {
						pointsMaterial += 100;
						pointsMidgame += Eval.pawn[row][column];
						pieceCount[wp]++;
					} else if (piece instanceof Knight) {
						pointsMaterial += 320;
						pointsMidgame += Eval.knight[row][column];
						pieceCount[wn]++;
					} else if (piece instanceof Bishop) {
						pointsMaterial += 330;
						pointsMidgame += Eval.bishop[row][column];
						pieceCount[wb]++;
					} else if (piece instanceof Rook) {
						pointsMaterial += 500;
						pointsMidgame += Eval.rook[row][column];
						pieceCount[wr]++;
					} else if (piece instanceof Queen) {
						pointsMaterial += 900;
						pointsMidgame += Eval.queen[row][column];
						pieceCount[wq]++;
					} else if (piece instanceof King) {
						whiteKingPos = new Pair(row, column);
						pointsKingMid += Eval.kingmid[row][column];
						pointsEndgame += Eval.kingend[row][column];
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
						pointsMaterial -= 100;
						pointsMidgame -= Eval.pawn[row][column];
						pieceCount[bp]++;
					} else if (piece instanceof Knight) {
						pointsMaterial -= 320;
						pointsMidgame -= Eval.knight[row][column];
						pieceCount[bn]++;
					} else if (piece instanceof Bishop) {
						pointsMaterial -= 330;
						pointsMidgame -= Eval.bishop[row][column];
						pieceCount[bb]++;
					} else if (piece instanceof Rook) {
						pointsMaterial -= 500;
						pointsMidgame -= Eval.rook[row][column];
						pieceCount[br]++;
					} else if (piece instanceof Queen) {
						pointsMaterial -= 900;
						pointsMidgame -= Eval.queen[row][column];
						pieceCount[bq]++;
					} else if (piece instanceof King) {
						blackKingPos = new Pair(row, column);
						pointsKingMid -= Eval.kingmid[row][column];
						pointsEndgame -= Eval.kingend[row][column];
					}
				}
			}
		}
		Eval.flip();
		
		float endgamePhaseWeight = getEndgamePhaseWeight();
		
		int eval = 0;
		eval += pointsMaterial;
		eval += pointsMidgame;
		eval += (int) (pointsKingMid * (1 - endgamePhaseWeight / 256));
		eval += (int) (pointsEndgame * (endgamePhaseWeight / 256));
		eval += mopUpEval(whiteKingPos, blackKingPos, pointsMaterial, endgamePhaseWeight);
		eval -= mopUpEval(blackKingPos, whiteKingPos, pointsMaterial * -1, endgamePhaseWeight);
		
		return eval;
	}
	
	private static float getEndgamePhaseWeight() {
		int phase = TotalPhase;
		phase -= pieceCount[wp] * PawnPhase;
		phase -= pieceCount[wn] * KnightPhase;
		phase -= pieceCount[wb] * BishopPhase;
		phase -= pieceCount[wr] * RookPhase;
		phase -= pieceCount[wq] * QueenPhase;
		phase -= pieceCount[bp] * PawnPhase;
		phase -= pieceCount[bn] * KnightPhase;
		phase -= pieceCount[bb] * BishopPhase;
		phase -= pieceCount[br] * RookPhase;
		phase -= pieceCount[bq] * QueenPhase;
		
		return (phase * 256) / TotalPhase;
	}
	
	private static int mopUpEval(Pair friendlyKingPos, Pair enemyKingPos, int materialDiff, float endgamePhaseWeight) {
		int mopUpScore = 0;
		if (materialDiff >= 200 && endgamePhaseWeight < 90) {
			int enemyKingDstFromCenter = Math.max(3 - enemyKingPos.first, enemyKingPos.first - 4) + Math.max(3 - enemyKingPos.second, enemyKingPos.second - 4);
			int kingDistance = Math.abs(friendlyKingPos.first - enemyKingPos.first) + Math.abs(friendlyKingPos.second - enemyKingPos.second);
			
			mopUpScore += enemyKingDstFromCenter * 10;
			mopUpScore += (14 - kingDistance) * 4;
			
			return (int) (mopUpScore * endgamePhaseWeight / 256);
		}
		
		return 0;
	}
}
