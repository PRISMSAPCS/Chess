package utils.bot.DanielBotClasses.StockfishEval;

import static utils.bot.DanielBotClasses.BitBoardChessBoard.*;
import static utils.bot.DanielBotClasses.BitBoardConsts.*;
import static utils.bot.DanielBotClasses.BitBoardBitManipulation.*;
import static utils.bot.DanielBotClasses.BitBoardAttacks.*;

public class Helpers {
	public static int rank(int square) {
		return 8 - square / 8;
	}
	
	public static int file(int square) {
		return square % 8 + 1;
	}
	
	public static int bishopCount() {
		return countBits(bitboards[B]);
	}
	
	public static int knightCount() {
		return countBits(bitboards[N]);
	}
	
	public static int queenCount() {
		return countBits(bitboards[Q]);
	}
	
	public static int pawnCount() {
		return countBits(bitboards[P]);
	}

	public static int rookCount() {
		return countBits(bitboards[R]);
	}
	
	// check for opposite colored bishops
	public static boolean oppositeBishops() {
		if (countBits(bitboards[B]) != 1 || countBits(bitboards[b]) != 1) return false; // can only have opposite bishops if one bishop on both sides
		
		int square[] = { getLS1BIndex(bitboards[B]), getLS1BIndex(bitboards[b]) };
		
		int color[] = { (square[0] % 8 + square[0] / 8) % 2, (square[1] % 8 + square[1] / 8) % 2 };
		
		return color[0] != color[1];
	}
	
	// calculate king distance from a square
	public static int kingDistance(int square) {
		int kingSquare = getLS1BIndex(bitboards[K]);
		
		return Math.max(Math.abs(square % 8 - kingSquare % 8), Math.abs(square / 8 - kingSquare / 8));
	}
	
	// check if a square is within 8 squares around king, and isn't defended by 2 pawns (if flag isn't set)
	public static int kingRing(int square, boolean ignorePawnDefense) {
		if (square == -1) {
			int kingSquare = getLS1BIndex(bitboards[k]);
			int x = kingSquare % 8;
			int y = kingSquare / 8;
			
			int sum = 0;
			
			for (int i = Math.max(0, x - 2); i <= Math.min(7, x + 2); i++) {
				for (int j = Math.max(0, y - 2); j <= Math.min(7, y + 2); j++) {
					sum += kingRing(i + j * 8, ignorePawnDefense);
				}
			}
			
			return sum;
		}
		
		if (!ignorePawnDefense) {
			long bitboard = pawnAttacks[side][square];
			
			if (countBits(bitboards[p] & bitboard) == 2) return 0;
		}
		
		int kingSquare = getLS1BIndex(bitboards[k]);
		
		int x = square % 8;
		int y = square / 8;
		int kx = kingSquare % 8;
		int ky = kingSquare / 8;
		
		int dx = kx - x;
		int dy = ky - y;
		
		if (dx >= -2 && dx <= 2 && dy >= -2 && dy <= 2) {
			if (((dx >= -1 && dx <= 1) || (kx == 0) || (kx == 7)) && ((dy >= -1 && dy <= 1) || (ky == 0) || (ky == 7))) {
				return 1;
			}
		}
		
		return 0;
	}
	
	public static int pieceCount() {
		int sum = 0;
		for (int bbPiece = P; bbPiece <= K; bbPiece++) {
			sum += countBits(bitboards[bbPiece]);
		}
		
		return sum;
	}
	
	
}