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
						
						// init pawn attack bitboard
						attacks = pawnAttacks[side][sourceSquare] & occupancies[black];
						
						// generate pawn captures
						while (attacks != 0) {
							targetSquare = getLS1BIndex(attacks);
							
							// pawn promotion
							if (sourceSquare >= a7 && sourceSquare <= h7) {
								// pawn promotion capture
								
								
							} else { // one square move
								// pawn capture
							}
							
							attacks &= ~(1L << (targetSquare));
						}
						
						// generate enPassant captures
						if (enPassant != no_sq) {
							long enPassantAttacks = pawnAttacks[side][sourceSquare] & (1L << enPassant);
							
							// if en passant is available
							if (enPassantAttacks != 0) {
								int targetEnPassant = getLS1BIndex(enPassantAttacks);
								
								// en passant capture
							}
						}
						
						// pop bit
						bitboard &= ~(1L << (sourceSquare));
					}
				}
				
				// castling moves
				if (piece == K) {
					// king side castling is available
					if ((castle & wk) != 0) {
						// make sure there are no pieces between king and rook
						if (getBit(occupancies[both], f1) == 0 && getBit(occupancies[both], g1) == 0) {
							if (isSquareAttacked(e1, black) == 0 && isSquareAttacked(f1, black) == 0) {
								// kingside castle
								
							}
						}
					}
					
					if ((castle & wq) != 0) {
						// make sure there are no pieces between king and rook
						if (getBit(occupancies[both], d1) == 0 && getBit(occupancies[both], c1) == 0 && getBit(occupancies[both], b1) == 0) {
							if (isSquareAttacked(e1, black) == 0 && isSquareAttacked(d1, black) == 0) {
								// queenside castle
								
							}
						}
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
						
						// init pawn attack bitboard
						attacks = pawnAttacks[side][sourceSquare] & occupancies[white];
						
						// generate pawn captures
						while (attacks != 0) {
							targetSquare = getLS1BIndex(attacks);
							
							// pawn promotion
							if (sourceSquare >= a2 && sourceSquare <= h2) {
								// pawn promotion capture
								
								
							} else { // one square move
								// pawn capture
							}
							
							attacks &= ~(1L << (targetSquare));
						}
						
						// generate enPassant captures
						if (enPassant != no_sq) {
							long enPassantAttacks = pawnAttacks[side][sourceSquare] & (1L << enPassant);
							
							// if en passant is available
							if (enPassantAttacks != 0) {
								int targetEnPassant = getLS1BIndex(enPassantAttacks);
								
								// en passant capture
							}
						}
						
						// pop bit
						bitboard &= ~(1L << (sourceSquare));
					}
				}
				
				// castling moves
				if (piece == k) {
					// king side castling is available
					if ((castle & bk) != 0) {
						// make sure there are no pieces between king and rook
						if (getBit(occupancies[both], f8) == 0 && getBit(occupancies[both], g8) == 0) {
							if (isSquareAttacked(e8, white) == 0 && isSquareAttacked(f8, white) == 0) {
								// kingside castle
								
							}
						}
					}
					
					if ((castle & bq) != 0) {
						// make sure there are no pieces between king and rook
						if (getBit(occupancies[both], d8) == 0 && getBit(occupancies[both], c8) == 0 && getBit(occupancies[both], b8) == 0) {
							if (isSquareAttacked(e8, white) == 0 && isSquareAttacked(d8, white) == 0) {
								// queenside castle
								
							}
						}
					}
				}
			}
			
			// generate knight moves
			if ((side == white) ? piece == N : piece == n) {
				// loop over bitboard
				while (bitboard != 0) {
					sourceSquare = getLS1BIndex(bitboard);
					
					// initialize attacks array - ignore squares which already have friendly pieces
					attacks = knightAttacks[sourceSquare] & ((side == white) ? ~occupancies[white] : ~occupancies[black]);
					
					// loop over target squares
					while (attacks != 0) {
						targetSquare = getLS1BIndex(attacks);
						
						// quiet moves
						if (getBit(((side == white) ? occupancies[black] : occupancies[white]), targetSquare) == 0) {
							// quiet piece move
							
						} else {
							// capture move
							
						}
						
						// pop bit
						attacks &= ~(1L << (targetSquare));
					}
					
					bitboard &= ~(1L << (sourceSquare));
				}
			}
			
			// generate bishop moves
			if ((side == white) ? piece == B : piece == b) {
				// loop over bitboard
				while (bitboard != 0) {
					sourceSquare = getLS1BIndex(bitboard);
					
					// initialize attacks array - ignore squares which already have friendly pieces
					attacks = getBishopAttacks(sourceSquare, occupancies[both]) & ((side == white) ? ~occupancies[white] : ~occupancies[black]);
					
					// loop over target squares
					while (attacks != 0) {
						targetSquare = getLS1BIndex(attacks);
						
						// quiet moves
						if (getBit(((side == white) ? occupancies[black] : occupancies[white]), targetSquare) == 0) {
							// quiet piece move
							
						} else {
							// capture move
							
						}
						
						// pop bit
						attacks &= ~(1L << (targetSquare));
					}
					
					bitboard &= ~(1L << (sourceSquare));
				}
			}
			
			// generate rook moves
			if ((side == white) ? piece == R : piece == r) {
				// loop over bitboard
				while (bitboard != 0) {
					sourceSquare = getLS1BIndex(bitboard);
					
					// initialize attacks array - ignore squares which already have friendly pieces
					attacks = getRookAttacks(sourceSquare, occupancies[both]) & ((side == white) ? ~occupancies[white] : ~occupancies[black]);
					
					// loop over target squares
					while (attacks != 0) {
						targetSquare = getLS1BIndex(attacks);
						
						// quiet moves
						if (getBit(((side == white) ? occupancies[black] : occupancies[white]), targetSquare) == 0) {
							// quiet piece move
							
						} else {
							// capture move
							
						}
						
						// pop bit
						attacks &= ~(1L << (targetSquare));
					}
					
					bitboard &= ~(1L << (sourceSquare));
				}
			}
			
			// generate queen moves
			if ((side == white) ? piece == Q : piece == q) {
				// loop over bitboard
				while (bitboard != 0) {
					sourceSquare = getLS1BIndex(bitboard);
					
					// initialize attacks array - ignore squares which already have friendly pieces
					attacks = getQueenAttacks(sourceSquare, occupancies[both]) & ((side == white) ? ~occupancies[white] : ~occupancies[black]);
					
					// loop over target squares
					while (attacks != 0) {
						targetSquare = getLS1BIndex(attacks);
						
						// quiet moves
						if (getBit(((side == white) ? occupancies[black] : occupancies[white]), targetSquare) == 0) {
							// quiet piece move
							
						} else {
							// capture move
							
						}
						
						// pop bit
						attacks &= ~(1L << (targetSquare));
					}
					
					bitboard &= ~(1L << (sourceSquare));
				}
			}
			
			// generate king moves
			if ((side == white) ? piece == K : piece == k) {
				// loop over bitboard
				while (bitboard != 0) {
					sourceSquare = getLS1BIndex(bitboard);
					
					// initialize attacks array - ignore squares which already have friendly pieces
					attacks = kingAttacks[sourceSquare] & ((side == white) ? ~occupancies[white] : ~occupancies[black]);
					
					// loop over target squares
					while (attacks != 0) {
						targetSquare = getLS1BIndex(attacks);
						
						// quiet moves
						if (getBit(((side == white) ? occupancies[black] : occupancies[white]), targetSquare) == 0) {
							// quiet piece move
							
						} else {
							// capture move
							
						}
						
						// pop bit
						attacks &= ~(1L << (targetSquare));
					}
					
					bitboard &= ~(1L << (sourceSquare));
				}
			}
		}
	}
}