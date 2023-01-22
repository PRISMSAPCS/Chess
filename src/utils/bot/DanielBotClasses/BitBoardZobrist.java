package utils.bot.DanielBotClasses;

import static utils.bot.DanielBotClasses.BitBoardRandom.*;
import static utils.bot.DanielBotClasses.BitBoardConsts.*;
import static utils.bot.DanielBotClasses.BitBoardChessBoard.*;
import static utils.bot.DanielBotClasses.BitBoardBitManipulation.*;

public class BitBoardZobrist {
	public static long pieceKeys[][] = new long[12][64];
	public static long enPassantKeys[] = new long[64];
	public static long castleKeys[] = new long[16];
	
	public static long sideKey;
	
	public static void initRandomKeys() {
		randomState = 1804289383;
		
		for (int piece = P; piece <= k; piece++) {
			for (int square = 0; square < 64; square++) {
				pieceKeys[piece][square] = getRandomLong();
			}
		}
		
		for (int square = 0; square < 64; square++) {
			enPassantKeys[square] = getRandomLong();
		}
		
		for (int index = 0; index < 16; index++) {
			castleKeys[index] = getRandomLong();
		}
		
		sideKey = getRandomLong();
	}
	
	public static long generateHashKey() {
		long finalKey = 0L;
		
		long bitboard = 0L;
		
		for (int piece = P; piece <= k; piece++) {
			bitboard = bitboards[piece];
			
			while (bitboard != 0) {
				int square = getLS1BIndex(bitboard);
				
				finalKey ^= pieceKeys[piece][square];
				
				bitboard &= ~(1L << square);
			}
		}
		
		if (enPassant != no_sq) {
			finalKey ^= enPassantKeys[enPassant];
		}
		
		finalKey ^= castleKeys[castle];
		
		if (side == black) finalKey ^= sideKey;
		
		return finalKey;
	}
}