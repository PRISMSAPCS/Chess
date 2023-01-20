package utils.bot.DanielBotClasses.StockfishEval;
import static utils.bot.DanielBotClasses.BitBoardConsts.*;
import static utils.bot.DanielBotClasses.BitBoardChessBoard.*;

public class Global {
	public static int board(int x, int y) {
		if (x >= 0 && x <= 7 && y >= 0 && y <= 7) {
			for (int bbPiece = P; bbPiece <= k; bbPiece++) {
				long bitboard = bitboards[bbPiece];
				
				int square = x * 8 + y;
				
				if ((bitboard & (1L << square)) != 0) {
					return bbPiece;
				}
			}
		}
		
		return -1;
	}
	
	public static int board(int square) {
		for (int bbPiece = P; bbPiece <= k; bbPiece++) {
			long bitboard = bitboards[bbPiece];
			
			if ((bitboard & (1L << square)) != 0) {
				return bbPiece;
			}
		}
	
		return -1;
	}
	
	public static void changeColor() {
		for (int bbPiece = P; bbPiece <= K; bbPiece++) {
			long temp = bitboards[bbPiece];
			
			bitboards[bbPiece] = bitboards[bbPiece + p - P];
			bitboards[bbPiece + p - P] = temp;
		}
		
		side ^= 1;
	}
}