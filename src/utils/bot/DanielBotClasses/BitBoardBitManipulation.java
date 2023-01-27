package utils.bot.DanielBotClasses;

public class BitBoardBitManipulation {
	public static long getBit(long bitboard, int square) { return bitboard & (1L << square); }
	public static long setBit(long bitboard, int square) { return bitboard | (1L << square); }
	public static long popBit(long bitboard, int square) { return bitboard & ~(1L << (square)); }
	public static int countBits(long bitboard) { return Long.bitCount(bitboard); }
	public static int getLS1BIndex(long bitboard) { return Long.numberOfTrailingZeros(bitboard); }
	
	public static int encodeMove(int source, int target, int piece, int promoted, int capture, int doubleMove, int enPassant, int castling) { return source | (target << 6) | (piece << 12) | (promoted << 16) | (capture << 20) | (doubleMove << 21) | (enPassant << 22) | (castling << 23); }
	public static int getMoveSource(int move) { return move & 0x3f; }
	public static int getMoveTarget(int move) { return (move & 0xfc0) >> 6; }
	public static int getMovePiece(int move) { return (move & 0xf000) >> 12; }
	public static int getMovePromoted(int move) { return (move & 0xf0000) >> 16; }
	public static int getMoveCapture(int move) { return move & 0x100000; }
	public static int getMoveDouble(int move) { return move & 0x200000; }
	public static int getMoveEnPassant(int move) { return move & 0x400000; }
	public static int getMoveCastling(int move) { return move & 0x800000; }
}