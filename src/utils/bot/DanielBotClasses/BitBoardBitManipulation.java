package utils.bot.DanielBotClasses;

public class BitBoardBitManipulation {
	public static long getBit(long bitboard, int square) { return bitboard & (0b1L << square); }
	public static long setBit(long bitboard, int square) { return bitboard | (1L << square); }
	public static long popBit(long bitboard, int square) { return (getBit(bitboard, square) != 0) ? (bitboard ^= 1L << square) : 0; }
	public static int countBits(long bitboard) { return Long.bitCount(bitboard); }
	public static int getLS1BIndex(long bitboard) { if (bitboard != 0) return countBits((bitboard & (bitboard * -1)) - 1); return -1; }
}