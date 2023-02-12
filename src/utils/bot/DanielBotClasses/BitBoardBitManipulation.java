package utils.bot.DanielBotClasses;

import static utils.bot.DanielBotClasses.BitBoardChessBoard.*;

public class BitBoardBitManipulation {
	// bit macros relating to getting and setting information from/for a bitboard
	public static long getBit(long bitboard, int square) { return bitboard & (1L << square); }
	public static long setBit(long bitboard, int square) { return bitboard | (1L << square); }
	public static long popBit(long bitboard, int square) { return bitboard & ~(1L << (square)); }
	public static int countBits(long bitboard) { return Long.bitCount(bitboard); }
	public static int getLS1BIndex(long bitboard) { return Long.numberOfTrailingZeros(bitboard); }
	
	// bit macros relating to getting and setting information from/for a move
	// moves are formatted as such
	/**
	 * 0000 0000 0011 1111 source square
	 * 0000 1111 1100 0000 target square
	 * 1111 0000 0000 0000 flags, denoting promotion, capture, castling, en passant, etc.
	 */
	
	// flag bits are formatted as such
	/**
	 * 0000 quiet moves					0001 double pawn push
	 * 0010 castle						0011 unused
	 * 0100 captures					0101 en passant capture
	 * 0110 unused						0111 unused
	 * 1000 knight promotion			1001 bishop promotion
	 * 1010 rook promotion				1011 queen promotion
	 * 1100 knight promotion capture	1101 bishop promotion capture
	 * 1110 rook promotion capture		1111 queen promotion capture
	 */
	
	public static short encodeMove(int source, int target, int flags) {
		return (short) (source | (target << 6) | (flags << 12));
	}
	
	public static int getMoveSource(int move) 		 { return (move & 0x3f); }
	public static int getMoveTarget(int move) 		 { return (move & 0xfc0) >> 6; }
	public static int getMovePiece(int move) 		 { 
		int source = getMoveSource(move);
		for (int bbPiece = side * 6; bbPiece < 6 + side * 6; bbPiece++) { if (getBit(bitboards[bbPiece], source) != 0) return bbPiece; }
		return 0;
	}
	public static int getMovePromoted(int move) 	 {
		if ((move & 0b1000_000000000000) == 0) return 0;
		return ((move & 0b0011_000000000000) >>> 12) + 1 + side * 6;
	}
	public static boolean getMoveCapture(int move) 	 { return ((move >> 12 & 0b0100) != 0); }
	public static boolean getMoveDouble(int move) 	 { return (move >> 12 == 0b0001); }
	public static boolean getMoveEnPassant(int move) { return (move >> 12 == 0b0101); }
	public static boolean getMoveCastling(int move)  { return (move >> 12 == 0b0010); }
}