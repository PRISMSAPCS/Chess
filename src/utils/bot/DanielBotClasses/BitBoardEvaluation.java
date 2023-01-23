package utils.bot.DanielBotClasses;

import static utils.bot.DanielBotClasses.BitBoardChessBoard.*;
import static utils.bot.DanielBotClasses.BitBoardConsts.*;
import static utils.bot.DanielBotClasses.BitBoardBitManipulation.*;
import static utils.bot.DanielBotClasses.BitBoardIO.*;

public class BitBoardEvaluation {
	static long fileMasks[] = new long[64];
	static long rankMasks[] = new long[64];
	static long isolatedMasks[] = new long[64];
	static long passedMasks[][] = new long[2][64];

	public static long setFileRankMask(int file, int rank) {
		long mask = 0L;
		
		for (int r = 0; r < 8; r++) {
			for (int f = 0; f < 8; f++) {
				int square = r * 8 + f;
				
				if (file != -1) {
					if (f == file) {
						mask |= (1L << square);
					}
				} else if (rank != -1) {
					if (r == rank) {
						mask |= (1L << square);
					}
				}
			}
		}
		
		return mask;
	}
	
	public static void initEvaluationMasks() {
		for (int rank = 0; rank < 8; rank++) {
			for (int file = 0; file < 8; file++) {
				int square = rank * 8 + file;
				
				fileMasks[square] = setFileRankMask(file, -1);
				rankMasks[square] = setFileRankMask(-1, rank);
				isolatedMasks[square] = setFileRankMask(file - 1, -1);
				isolatedMasks[square] |= setFileRankMask(file + 1, -1);
				
				passedMasks[white][square] = setFileRankMask(file - 1, -1);
				passedMasks[white][square] |= setFileRankMask(file, -1);
				passedMasks[white][square] |= setFileRankMask(file + 1, -1);
				
				passedMasks[black][square] = setFileRankMask(file - 1, -1);
				passedMasks[black][square] |= setFileRankMask(file, -1);
				passedMasks[black][square] |= setFileRankMask(file + 1, -1);
				
				for (int i = 0; i < 8; i++) {
					if (i < 8 - rank) {
						passedMasks[white][square] &= ~setFileRankMask(-1, 7 - i);
					} else {
						passedMasks[black][square] &= ~setFileRankMask(-1, 7 - i);
					}
				}
			}
		}
	}
	
	public static int getGamePhaseScore() {
		int whitePieceScores = 0, blackPieceScores = 0;
		
		for (int piece = N; piece <= Q; piece++) {
			whitePieceScores += countBits(bitboards[piece]) * materialScore[opening][piece];
		}
		
		for (int piece = n; piece <= q; piece++) {
			blackPieceScores += countBits(bitboards[piece]) * -materialScore[opening][piece];
		}
		
		return whitePieceScores + blackPieceScores;
	}
	
	public static int evaluate() {
		int gamePhaseScore = getGamePhaseScore();
		
		int gamePhase = -1;
		
		if (gamePhaseScore > openingPhaseScore) gamePhase = opening;
		else if (gamePhaseScore < endgamePhaseScore) gamePhase = endgame;
		else gamePhase = midgame;
		
		int score = 0;
		int scoreOpening = 0;
		int scoreEndgame = 0;
		
		long bitboard = 0;
		
		int piece, square;
		
		int doubledPawns = 0;
		
		for (int bbPiece = P; bbPiece <= k; bbPiece++) {
			bitboard = bitboards[bbPiece];
			
			while (bitboard != 0) {
				piece = bbPiece;
				square = getLS1BIndex(bitboard);
				
				scoreOpening += materialScore[opening][piece];
				scoreEndgame += materialScore[endgame][piece];
				
				switch (piece) {
				case P: 
					scoreOpening += positionalScore[opening][PAWN][square];
					scoreEndgame += positionalScore[endgame][PAWN][square];
					
					doubledPawns = countBits(bitboards[P] & fileMasks[square]);
					if (doubledPawns > 1) {
						score += doubledPawns * doubledPawnPenalty;
					}
					
					if ((bitboards[P] & isolatedMasks[square]) == 0) {
						score += isolatedPawnPenalty;
					}
					
					if ((passedMasks[white][square] & bitboards[p]) == 0) {
						score += passedPawnBonus[getRank[square]];
					}
					break;
				case N:
					scoreOpening += positionalScore[opening][KNIGHT][square];
					scoreEndgame += positionalScore[endgame][KNIGHT][square];
					break;
				case B:
					scoreOpening += positionalScore[opening][BISHOP][square];
					scoreEndgame += positionalScore[endgame][BISHOP][square];
					break;
				case R:
					scoreOpening += positionalScore[opening][ROOK][square];
					scoreEndgame += positionalScore[endgame][ROOK][square];
					if ((bitboards[P] & fileMasks[square]) == 0) {
						score += semiOpenFileScore;
					}
					
					if (((bitboards[P] | bitboards[p]) & fileMasks[square]) == 0) {
						score += openFileScore;
					}
					
					break;
				case Q:
					scoreOpening += positionalScore[opening][QUEEN][square];
					scoreEndgame += positionalScore[endgame][QUEEN][square];
					break;
				case K:
					scoreOpening += positionalScore[opening][KING][square];
					scoreEndgame += positionalScore[endgame][KING][square];
					if ((bitboards[P] & fileMasks[square]) == 0) {
						score -= semiOpenFileScore;
					}
					
					if (((bitboards[P] | bitboards[p]) & fileMasks[square]) == 0) {
						score -= openFileScore;
					}
					
					break;
				
				case p:
					scoreOpening -= positionalScore[opening][PAWN][mirrorScore[square]];
					scoreEndgame -= positionalScore[endgame][PAWN][mirrorScore[square]];

					doubledPawns = countBits(bitboards[p] & fileMasks[square]);
					if (doubledPawns > 1) {
						score -= doubledPawns * doubledPawnPenalty;
					}
					
					if ((bitboards[p] & isolatedMasks[square]) == 0) {
						score -= isolatedPawnPenalty;
					}
					
					if ((passedMasks[black][square] & bitboards[P]) == 0) {
						score -= passedPawnBonus[getRank[mirrorScore[square]]];
					}
					break;
				case n:
					scoreOpening -= positionalScore[opening][KNIGHT][mirrorScore[square]];
					scoreEndgame -= positionalScore[endgame][KNIGHT][mirrorScore[square]];
					break;
				case b:
					scoreOpening -= positionalScore[opening][BISHOP][mirrorScore[square]];
					scoreEndgame -= positionalScore[endgame][BISHOP][mirrorScore[square]];
					break;
				case r:
					scoreOpening -= positionalScore[opening][ROOK][mirrorScore[square]];
					scoreEndgame -= positionalScore[endgame][ROOK][mirrorScore[square]];
					
					if ((bitboards[p] & fileMasks[square]) == 0) {
						score -= semiOpenFileScore;
					}
					
					if (((bitboards[P] | bitboards[p]) & fileMasks[square]) == 0) {
						score -= openFileScore;
					}
					
					break;
				case q:
					scoreOpening -= positionalScore[opening][QUEEN][mirrorScore[square]];
					scoreEndgame -= positionalScore[endgame][QUEEN][mirrorScore[square]];
					
					break;
				case k:
					scoreOpening -= positionalScore[opening][KING][mirrorScore[square]];
					scoreEndgame -= positionalScore[endgame][KING][mirrorScore[square]];
					
					if ((bitboards[p] & fileMasks[square]) == 0) {
						score += semiOpenFileScore;
					}
					
					if (((bitboards[P] | bitboards[p]) & fileMasks[square]) == 0) {
						score += openFileScore;
					}
					
					break;
				}
				
				bitboard &= ~(1L << (square));
			}
		}
		
		if (gamePhase == midgame) {
			score += (scoreOpening * openingPhaseScore + scoreEndgame * (openingPhaseScore - gamePhaseScore)) / openingPhaseScore;
		} else if (gamePhase == opening) {
			score += scoreOpening;
		} else if (gamePhase == endgame) {
			score += scoreEndgame;
		}
		
		return (side == white) ? score : score * -1;
	}
}