package utils.bot.DanielBotClasses;

import static utils.bot.DanielBotClasses.BitBoardChessBoard.*;
import static utils.bot.DanielBotClasses.BitBoardConsts.*;
import static utils.bot.DanielBotClasses.BitBoardAttacks.*;
import static utils.bot.DanielBotClasses.BitBoardBitManipulation.*;

public class BitBoardMoveGeneration {
	public static int isSquareAttacked(int square, int side) {
		// white pawn attack
		if ((side == white) && ((pawnAttacks[black][square] & bitboards[P]) != 0)) return 1;
		
		// black pawn attack
		if ((side == black) && ((pawnAttacks[white][square] & bitboards[p]) != 0)) return 1;
		
		// knight attack
		if ((knightAttacks[square] & ((side == white) ? bitboards[N] : bitboards[n])) != 0) return 1;
		
		// slider attacks
		if ((getBishopAttacks(square, occupancies[both]) & ((side == white) ? bitboards[B] : bitboards[b])) != 0) return 1;
		if ((getRookAttacks(square, occupancies[both]) & ((side == white) ? bitboards[R] : bitboards[r])) != 0) return 1;
		if ((getQueenAttacks(square, occupancies[both]) & ((side == white) ? bitboards[Q] : bitboards[q])) != 0) return 1;
		
		// king attack
		if ((kingAttacks[square] & ((side == white) ? bitboards[K] : bitboards[k])) != 0) return 1;
		
		// no attack (default)
		return 0;
	}
	
	public static void generateMoves() {
		int sourceSquare, targetSquare;
		
		long bitboard, attacks;
		
		for (int piece = P; piece <= k; piece++) {
			bitboard = bitboards[piece];
			
			//generate white pawn and castling moves
			if (side == white) {
				// get white pawns
				if (piece == P) {
					// loop over entire bitboard
					while (bitboard != 0) {
						sourceSquare = getLS1BIndex(bitboard);
						targetSquare = sourceSquare - 8;
						
						// generate quiet pawn moves
						if (targetSquare >= a8 && getBit(occupancies[both], targetSquare) == 0) {
							// pawn promotion
							if (sourceSquare >= a7 && sourceSquare <= h7) {
								// pawn promotes
								
								
								
								
							} else {
								// one square
								
								
								// two squares
								if (sourceSquare >= a2 && sourceSquare <= h2 && getBit(occupancies[both], targetSquare - 8) == 0) {
									
								}
							}
						}
						
						// pop bit
						bitboard &= ~(1L << (sourceSquare));
					}
				}
			} else { // generate black pawn and castling moves
				// get black pawns
				if (piece == p) {
					// loop over entire bitboard
					while (bitboard != 0) {
						sourceSquare = getLS1BIndex(bitboard);
						targetSquare = sourceSquare + 8;
						
						// generate quiet pawn moves
						if (targetSquare <= h1 && getBit(occupancies[both], targetSquare) == 0) {
							// pawn promotion
							if (sourceSquare >= a2 && sourceSquare <= h2) {
								// pawn promotes
								
								
								
								
							} else {
								// one square
								
								
								// two squares
								if (sourceSquare >= a7 && sourceSquare <= h7 && getBit(occupancies[both], targetSquare + 8) == 0) {
									
								}
							}
						}
						
						// pop bit
						bitboard &= ~(1L << (sourceSquare));
					}
				}
			}
			
			// generate knight moves
			
			// generate bishop moves
			
			// generate rook moves
			
			// generate queen moves
			
			// generate king moves
		}
	}
}