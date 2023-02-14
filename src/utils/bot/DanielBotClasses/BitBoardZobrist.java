package utils.bot.DanielBotClasses;

import static utils.bot.DanielBotClasses.BitBoardRandom.*;
import static utils.bot.DanielBotClasses.BitBoardConsts.*;
import static utils.bot.DanielBotClasses.BitBoardChessBoard.*;
import static utils.bot.DanielBotClasses.BitBoardBitManipulation.*;

// handles zobrist hashing
public class BitBoardZobrist {
	// our random numbers
	public static long pieceKeys[][] = new long[12][64];
	public static long enPassantKeys[] = new long[64];
	public static long castleKeys[] = new long[16];
	public static long sideKey;
	
	// initiate our random keys
	public static void initRandomKeys() {
		// reset random state to make our keys deterministic
		randomState = 1804289383;
		
		// piece keys
		for (int piece = P; piece <= k; piece++) {
			for (int square = 0; square < 64; square++) {
				pieceKeys[piece][square] = getRandomLong();
			}
		}
		
		// en passant keys
		for (int square = 0; square < 64; square++) {
			enPassantKeys[square] = getRandomLong();
		}
		
		// castling keys
		for (int index = 0; index < 16; index++) {
			castleKeys[index] = getRandomLong();
		}
		
		// side key
		sideKey = getRandomLong();
	}
	
	// generate a hash key from the current position. used once when initiating board, but not later
	// because incremental updating is much faster (incrementing when making a move, popping from stack when undoing)
	public static long generateHashKey(BitBoardChessBoard board) {
		long finalKey = 0L;
		
		long bitboard = 0L;
		
		// piece keys
		for (int piece = P; piece <= k; piece++) {
			bitboard = board.bitboards[piece];
			
			while (bitboard != 0) {
				int square = getLS1BIndex(bitboard);
				
				finalKey ^= pieceKeys[piece][square];
				
				bitboard &= ~(1L << square);
			}
		}
		
		// en passant key
		if (board.enPassant != no_sq) {
			finalKey ^= enPassantKeys[board.enPassant];
		}
		
		// castling rights key
		finalKey ^= castleKeys[board.castle];
		
		// side key
		if (board.side == black) finalKey ^= sideKey;
		
		return finalKey;
	}
}