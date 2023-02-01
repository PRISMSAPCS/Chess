package utils.bot.DanielBotClasses;

import static utils.bot.DanielBotClasses.BitBoardChessBoard.*;
import static utils.bot.DanielBotClasses.BitBoardConsts.*;
import static utils.bot.DanielBotClasses.BitBoardBitManipulation.*;
import static utils.bot.DanielBotClasses.BitBoardIO.*;
import static utils.bot.DanielBotClasses.BitBoardAttacks.*;

public class BitBoardEvaluation {
	// diagnostic
	public static int evalCalled = 0;
	
	// some useful stuff for basic pawn structure
	static long fileMasks[] = new long[64];
	static long rankMasks[] = new long[64];
	static long isolatedMasks[] = new long[64];
	static long passedMasks[][] = new long[2][64];
	
	// returns the file / rank mask of a square
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
	
	// initiates file, rank, isolated, and passed masks
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
	
	// gets the phase of the game, based on how much material is still on the board
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
	
	// checks if a player is drawing due to lack of mating material
	public static boolean drawing(int side) {
		// if pawns or queen exists, mate can occur
		if (bitboards[P + side * 6] != 0) return false;
		if (bitboards[Q + side * 6] != 0) return false;
		
		int knightsUs = countBits(bitboards[N + side * 6]);
		int bishopsUs = countBits(bitboards[B + side * 6]);
		int rooksUs = countBits(bitboards[R + side * 6]);
		int minorPiecesUs = knightsUs + bishopsUs;
		int allPiecesUs = minorPiecesUs + rooksUs;
		
		// no need to worry about their heavy pieces, or pawns. if we're losing, eval function won't consider our lack of mating material
		int knightsThem = countBits(bitboards[n - side * 6]);
		int bishopsThem = countBits(bitboards[b - side * 6]);
		int minorPiecesThem = knightsThem + bishopsThem;
		
		// first, consider no rooks
		if (rooksUs == 0) { 
			// no pieces on the board
			if (allPiecesUs == 0 && minorPiecesThem == 0) return true;
			
			// one piece each is a draw
			if (minorPiecesUs <= 1 && minorPiecesThem <= 1) return true;
			
			// two knights (or less) is a draw
			if (bishopsUs == 0 && knightsUs <= 2 && minorPiecesThem <= 1) return true;
			
			// bishop pair only
			if (bishopsUs == 2 && allPiecesUs == 2) {
				// bishop pair wins against knight pair
				if (knightsThem <= 2 && bishopsThem == 0) return false;
				
				// bishop pair draws against any bishop
				if (bishopsThem != 0) return true;
			}
			
			// two minor pieces against one draws (if no bishop pair)
			if (bishopsUs <= 2 && minorPiecesUs == 2 && minorPiecesThem == 1) return true; 
		}
		
		// only one rook
		if (rooksUs == 1 && allPiecesUs == 1) {
			// rook vs one minor piece is a draw, unless there are tactics.
			// tactics will be detected in the search
			if (minorPiecesThem == 1) return true;
		}
		
		// default to not a draw
		return false;
	}
	
	// basic eval function
	public static int evaluate() {
		int gamePhaseScore = getGamePhaseScore();
		
		int gamePhase = -1;
		
		// determine phase of the game based on game phase score
		if (gamePhaseScore > openingPhaseScore) gamePhase = opening;
		else if (gamePhaseScore < endgamePhaseScore) gamePhase = endgame;
		else gamePhase = midgame;
		
		// split up score into 3 categories for interpolation
		int score = 0;
		int scoreOpening = 0;
		int scoreEndgame = 0;
		
		long bitboard = 0;
		
		int piece, square;
		
		int doubledPawns = 0;
		
		// we do some stuff to find all the squares that white pawns attack, and all the squares that black pawns attack
		// we do this for mobility - pieces that can only move the squares defended by pawns really can't move at all
		long whitePawnAttacks = 0L;
		long blackPawnAttacks = 0L;
		
		long whitePawnBitboard = bitboards[P];
		long blackPawnBitboard = bitboards[p];
		
		while (whitePawnBitboard != 0) {
			int pawnSquare = getLS1BIndex(whitePawnBitboard);
			
			whitePawnAttacks |= pawnAttacks[white][pawnSquare];
			
			whitePawnBitboard &= ~(1L << pawnSquare);
		}
		
		while (blackPawnBitboard != 0) {
			int pawnSquare = getLS1BIndex(blackPawnBitboard);
			
			blackPawnAttacks |= pawnAttacks[black][pawnSquare];
			
			blackPawnBitboard &= ~(1L << pawnSquare);
		}
		
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
					
					// doubled pawns
					doubledPawns = countBits(bitboards[P] & fileMasks[square]);
					if (doubledPawns > 1) {
						scoreOpening += (doubledPawns - 1) * doubledPawnPenaltyOpening;
						scoreEndgame += (doubledPawns - 1) * doubledPawnPenaltyEndgame;
					}
					
					// isolated pawns
					if ((bitboards[P] & isolatedMasks[square]) == 0) {
						scoreOpening += isolatedPawnPenaltyOpening;
						scoreEndgame += isolatedPawnPenaltyEndgame;
					}
					
					// passed pawn bonus
					if ((passedMasks[white][square] & bitboards[p]) == 0) {
						scoreOpening += passedPawnBonus[getRank[square]];
						scoreEndgame += passedPawnBonus[getRank[square]];
					}
					break;
				case N:
					scoreOpening += positionalScore[opening][KNIGHT][square];
					scoreEndgame += positionalScore[endgame][KNIGHT][square];
					
					// added score for knight mobility
					scoreOpening += (countBits(knightAttacks[square] & ~blackPawnAttacks) - knightUnit) * knightMobilityOpening;
					scoreEndgame += (countBits(knightAttacks[square] & ~blackPawnAttacks) - knightUnit) * knightMobilityEndgame;
					break;
				case B:
					scoreOpening += positionalScore[opening][BISHOP][square];
					scoreEndgame += positionalScore[endgame][BISHOP][square];
					
					// added score for bishop mobility
					scoreOpening += (countBits(getBishopAttacks(square, occupancies[both]) & ~blackPawnAttacks) - bishopUnit) * bishopMobilityOpening;
					scoreEndgame += (countBits(getBishopAttacks(square, occupancies[both]) & ~blackPawnAttacks) - bishopUnit) * bishopMobilityEndgame;
					break;
				case R:
					scoreOpening += positionalScore[opening][ROOK][square];
					scoreEndgame += positionalScore[endgame][ROOK][square];
					
					// bonus for semiopen and open files
					if ((bitboards[P] & fileMasks[square]) == 0) {
						score += semiOpenFileScore;
					}
					
					if (((bitboards[P] | bitboards[p]) & fileMasks[square]) == 0) {
						score += openFileScore;
					}
					
					// added score for rook mobility
					scoreOpening += (countBits(getRookAttacks(square, occupancies[both]) & ~blackPawnAttacks) - rookUnit) * rookMobilityOpening;
					scoreEndgame += (countBits(getRookAttacks(square, occupancies[both]) & ~blackPawnAttacks) - rookUnit) * rookMobilityEndgame;
					break;
				case Q:
					scoreOpening += positionalScore[opening][QUEEN][square];
					scoreEndgame += positionalScore[endgame][QUEEN][square];
					
					// added score for queen mobility
					scoreOpening += (countBits(getQueenAttacks(square, occupancies[both]) & ~blackPawnAttacks) - queenUnit) * queenMobilityOpening;
					scoreEndgame += (countBits(getQueenAttacks(square, occupancies[both]) & ~blackPawnAttacks) - queenUnit) * queenMobilityEndgame;
					break;
				case K:
					scoreOpening += positionalScore[opening][KING][square];
					scoreEndgame += positionalScore[endgame][KING][square];
					
					// penalty for being on a semiopen or open file
					if ((bitboards[P] & fileMasks[square]) == 0) {
						scoreOpening -= semiOpenFileScore;
					}
					
					if (((bitboards[P] | bitboards[p]) & fileMasks[square]) == 0) {
						scoreOpening -= openFileScore;
					}
					
					// king safety bonus
					scoreOpening += countBits(kingAttacks[square] & occupancies[white]) * kingShieldBonus;
					scoreEndgame += countBits(kingAttacks[square] & occupancies[white]) * kingShieldBonus;
					
					break;
				
				// everything below is the same thing, but for black. so we invert the sign
				case p:
					scoreOpening -= positionalScore[opening][PAWN][mirrorScore[square]];
					scoreEndgame -= positionalScore[endgame][PAWN][mirrorScore[square]];

					doubledPawns = countBits(bitboards[p] & fileMasks[square]);
					if (doubledPawns > 1) {
						scoreOpening -= doubledPawns * doubledPawnPenaltyOpening;
						scoreEndgame -= doubledPawns * doubledPawnPenaltyEndgame;
					}
					
					if ((bitboards[p] & isolatedMasks[square]) == 0) {
						scoreOpening -= isolatedPawnPenaltyOpening;
						scoreEndgame -= isolatedPawnPenaltyEndgame;
					}
					
					if ((passedMasks[black][square] & bitboards[P]) == 0) {
						scoreOpening -= passedPawnBonus[getRank[mirrorScore[square]]];
						scoreEndgame -= passedPawnBonus[getRank[mirrorScore[square]]];
					}
					break;
				case n:
					scoreOpening -= positionalScore[opening][KNIGHT][mirrorScore[square]];
					scoreEndgame -= positionalScore[endgame][KNIGHT][mirrorScore[square]];
					
					scoreOpening -= (countBits(knightAttacks[square] & ~whitePawnAttacks) - knightUnit) * knightMobilityOpening;
					scoreEndgame -= (countBits(knightAttacks[square] & ~whitePawnAttacks) - knightUnit) * knightMobilityEndgame;
					break;
				case b:
					scoreOpening -= positionalScore[opening][BISHOP][mirrorScore[square]];
					scoreEndgame -= positionalScore[endgame][BISHOP][mirrorScore[square]];
					
					scoreOpening -= (countBits(getBishopAttacks(square, occupancies[both]) & ~whitePawnAttacks) - bishopUnit) * bishopMobilityOpening;
					scoreEndgame -= (countBits(getBishopAttacks(square, occupancies[both]) & ~whitePawnAttacks) - bishopUnit) * bishopMobilityEndgame;
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
					
					scoreOpening -= (countBits(getRookAttacks(square, occupancies[both]) & ~whitePawnAttacks) - rookUnit) * rookMobilityOpening;
					scoreEndgame -= (countBits(getRookAttacks(square, occupancies[both]) & ~whitePawnAttacks) - rookUnit) * rookMobilityEndgame;
					break;
				case q:
					scoreOpening -= positionalScore[opening][QUEEN][mirrorScore[square]];
					scoreEndgame -= positionalScore[endgame][QUEEN][mirrorScore[square]];
					
					scoreOpening -= (countBits(getQueenAttacks(square, occupancies[both]) & ~whitePawnAttacks) - queenUnit) * queenMobilityOpening;
					scoreEndgame -= (countBits(getQueenAttacks(square, occupancies[both]) & ~whitePawnAttacks) - queenUnit) * queenMobilityEndgame;
					break;
				case k:
					scoreOpening -= positionalScore[opening][KING][mirrorScore[square]];
					scoreEndgame -= positionalScore[endgame][KING][mirrorScore[square]];
					
					if ((bitboards[p] & fileMasks[square]) == 0) {
						scoreOpening += semiOpenFileScore;
					}
					
					if (((bitboards[P] | bitboards[p]) & fileMasks[square]) == 0) {
						scoreOpening += openFileScore;
					}
					
					// king safety bonus
					scoreOpening -= countBits(kingAttacks[square] & occupancies[black]) * kingShieldBonus;
					scoreEndgame -= countBits(kingAttacks[square] & occupancies[black]) * kingShieldBonus;
					
					break;
				}
				
				bitboard &= ~(1L << (square));
			}
		}
		
		// interpolate scores. this is called tapered eval
		if (gamePhase == midgame) {
			score += (scoreOpening * openingPhaseScore + scoreEndgame * (openingPhaseScore - gamePhaseScore)) / openingPhaseScore;
		} else if (gamePhase == opening) {
			score += scoreOpening;
		} else if (gamePhase == endgame) {
			score += scoreEndgame;
		}
		
		score += mopUpEval();
		
		// white is winning, but lacks sufficient material to mate
		if (score > 0 && drawing(white)) score /= 16;
		
		// black is winning, but lacks sufficient material to mate
		if (score < 0 && drawing(black)) score /= 16;
		
		// since we use negamax, return in the perspective of the side to play
		return (side == white) ? score : score * -1;
	}
	
	// gets how much material one side has
	public static int materialScore(int side) {
		int materialScores = 0;
		
		if (side == white) {
			for (int piece = P; piece <= Q; piece++) {
				materialScores += countBits(bitboards[piece]) * materialScore[opening][piece];
			}
		} else {
			for (int piece = p; piece <= q; piece++) {
				materialScores += countBits(bitboards[piece]) * -materialScore[opening][piece];
			}
		}
		
		return materialScores;
	}
	
	public static double mopUpEval() {
		int mopUpScore = 0;
		
		int whiteMaterialScore = materialScore(white);
		int blackMaterialScore = materialScore(black);
		
		double multiplier = (double) (whiteMaterialScore - blackMaterialScore) / ((whiteMaterialScore + blackMaterialScore) * (whiteMaterialScore + blackMaterialScore)) * 3000;
		
		int friendlyKingRank = -1, friendlyKingFile = -1;
		int enemyKingRank = -1, enemyKingFile = -1;
		
		if (multiplier > 0) {
			int friendlySquare = getLS1BIndex(bitboards[K]);
			int enemySquare = getLS1BIndex(bitboards[k]);
			
			friendlyKingRank = friendlySquare / 8;
			friendlyKingFile = friendlySquare % 8;
			
			enemyKingRank = enemySquare / 8;
			enemyKingFile = enemySquare % 8;
		} else {
			int friendlySquare = getLS1BIndex(bitboards[k]);
			int enemySquare = getLS1BIndex(bitboards[K]);
			
			friendlyKingRank = friendlySquare / 8;
			friendlyKingFile = friendlySquare % 8;
			
			enemyKingRank = enemySquare / 8;
			enemyKingFile = enemySquare % 8;
		}
		
		int enemyKingDstFromCenter = Math.max(3 - enemyKingRank, enemyKingRank - 4) + Math.max(3 - enemyKingFile, enemyKingFile - 4);
		int kingDistance = Math.abs(friendlyKingRank - enemyKingRank) + Math.abs(friendlyKingFile - enemyKingFile);
		
		mopUpScore += enemyKingDstFromCenter * 10;
		mopUpScore += (14 - kingDistance) * 4;
		
		return mopUpScore * multiplier;
	}
}