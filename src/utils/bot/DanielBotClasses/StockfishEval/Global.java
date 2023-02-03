package utils.bot.DanielBotClasses.StockfishEval;
import static utils.bot.DanielBotClasses.BitBoardConsts.*;
import static utils.bot.DanielBotClasses.BitBoardChessBoard.*;

public class Global {
	public static int board(int x, int y) {
		if (x >= 0 && x <= 7 && y >= 0 && y <= 7) {
			for (int bbPiece = P; bbPiece <= k; bbPiece++) {
				long bitboard = bitboards[bbPiece];
				
				int square = x + y * 8;
				
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
		
		for (int bbPiece = P; bbPiece <= k; bbPiece++) {
			long bitboard = bitboards[bbPiece];
			long newBitboard = 0L;
			
			for (int rank = 0; rank < 8; rank++) {
				for (int file = 0; file < 8; file++) {
					int square = rank * 8 + file;
					int newSquare = (7 - rank) * 8 + file;
					
					newBitboard |= ((bitboard & (1L << square)) != 0) ? 1L << newSquare : 0;
				}
			}
			
			bitboards[bbPiece] = newBitboard;
		}
		
		for (int occupancySide = white; occupancySide <= both; occupancySide++) {
			long bitboard = occupancies[occupancySide];
			long newBitboard = 0L;
			
			for (int rank = 0; rank < 8; rank++) {
				for (int file = 0; file < 8; file++) {
					int square = rank * 8 + file;
					int newSquare = (7 - rank) * 8 + file;
					
					newBitboard |= ((bitboard & (1L << square)) != 0) ? 1L << newSquare : 0;
				}
			}
			
			occupancies[occupancySide] = newBitboard;
		}
		
		side ^= 1;
		
		int castleCopy = castle;
		castle = 0;
		
		if ((castleCopy & wk) != 0) castle |= bk;
		if ((castleCopy & wq) != 0) castle |= bq;
		if ((castleCopy & bk) != 0) castle |= wk;
		if ((castleCopy & bq) != 0) castle |= wq;
		
		if (enPassant != no_sq) {
			int rank = enPassant / 8;
			int file = enPassant % 8;
			
			enPassant = (7 - rank) * 8 + file;
		}

	}
}