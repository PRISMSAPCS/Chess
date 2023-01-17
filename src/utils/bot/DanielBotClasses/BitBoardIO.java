package utils.bot.DanielBotClasses;

import static utils.bot.DanielBotClasses.BitBoardBitManipulation.getBit;

public class BitBoardIO {
	public static void print(long bitboard) {
		for (int rank = 0; rank < 8; rank++) {
			for (int file = 0; file < 8; file++) {
				// print ranks
				if (file == 0)
					System.out.printf("  %d ", 8 - rank);
					
				System.out.printf(" %c", (getBit(bitboard, rank * 8 + file) != 0) ? '1' : '.');
			}
			
			System.out.println();
		}
		
		// print board files
		System.out.println("\n     a b c d e f g h");
		
		System.out.printf("       Bitboard: %d\n\n", bitboard);
	}
}