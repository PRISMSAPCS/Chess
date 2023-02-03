package utils.bot.DanielBotClasses.StockfishEval;

import static utils.bot.DanielBotClasses.StockfishEval.Consts.*;
import static utils.bot.DanielBotClasses.StockfishEval.Global.*;
import static utils.bot.DanielBotClasses.BitBoardConsts.*;

public class Material {
	public static int nonPawnMaterial(int square) {
		if (square == -1) {
			int sum = 0;
			
			for (int i = 0; i < 64; i++) {
				sum += nonPawnMaterial(i);
			}
			
			return sum;
		}
		
		int piece = board(square);
		
		if (piece >= N && piece <= Q) {
			return pieceValueBonus(square, true);
		}
		
		return 0;
	}
	
	public static int pieceValueBonus(int square, boolean midgame) {
		if (square == -1) {
			int sum = 0;
			
			for (int i = 0; i < 64; i++) {
				sum += pieceValueBonus(i, midgame);
			}
			
			return sum;
		}
		
		int piece = board(square);
		
		if (piece >= P && piece <= Q) {
			if (midgame) {
				return pieceValueMG[piece];
			} else {
				return pieceValueEG[piece];
			}
		}
		
		return 0;
	}
	
	public static int psqtBonus(int square, boolean midgame) {
		if (square == -1) {
			int sum = 0;
			
			for (int i = 0; i < 64; i++) {
				sum += psqtBonus(i, midgame);
			}
			
			return sum;
		}
		
		int piece = board(square);
		
		if (piece < 0 || piece > K) return 0;
		
		if (piece == 0) {
			if (midgame) {
				return pawnPieceBonusMG[7 - square / 8][square % 8];
			} else {
				return pawnPieceBonusEG[7 - square / 8][square % 8];
			}
		} else {
			if (midgame) {
				return pieceBonusMG[piece - 1][7 - square / 8][Math.min(square % 8, 7 - square % 8)];
			} else {
				return pieceBonusEG[piece - 1][7 - square / 8][Math.min(square % 8, 7 - square % 8)];
			}
		}
	}
	
	public static int pieceValueMG(int square) {
		return pieceValueBonus(square, true);
	}
	
	public static int pieceValueEG(int square) {
		return pieceValueBonus(square, false);
	}
	
	public static int psqtMG(int square) {
		return psqtBonus(square, true);
	}
	
	public static int psqtEG(int square) {
		return psqtBonus(square, false);
	}
}