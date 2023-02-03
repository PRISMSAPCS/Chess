package utils.bot.DanielBotClasses.StockfishEval;

import static utils.bot.DanielBotClasses.BitBoardConsts.*;
import static utils.bot.DanielBotClasses.BitBoardIO.*;
import static utils.bot.DanielBotClasses.StockfishEval.Global.*;
import static utils.bot.DanielBotClasses.StockfishEval.Helpers.*;
import static utils.bot.DanielBotClasses.StockfishEval.King.*;
import static utils.bot.DanielBotClasses.StockfishEval.Attack.*;
import static utils.bot.DanielBotClasses.StockfishEval.Consts.*;

public class Mobility {
	public static int mobility(int square) {
		if (square == -1) {
			int sum = 0;
			
			for (int i = 0; i < 64; i++) {
				sum += mobility(i);
			}
			
			return sum;
		}
		
		int toReturn = 0;
		int piece = board(square);
		
		if (piece < N || piece > Q) return 0;
		
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				int s2 = x + y * 8;
				
				if (mobilityArea(s2) == 0) continue;
				
				if (piece == N && knightAttack(s2, square) != 0 && board(s2) != Q) toReturn++;
				if (piece == B && bishopXRayAttack(s2, square) != 0 && board(s2) != Q) toReturn++;
				if (piece == R && rookXRayAttack(s2, square) != 0) toReturn++;
				if (piece == Q && queenAttack(s2, square) != 0) toReturn++;
			}
		}
		
		return toReturn;
	}
	
	public static int mobilityArea(int square) {
		if (square == -1) {
			int sum = 0;

			for (int i = 0; i < 64; i++) {
				sum += mobilityArea(i);
			}
			
			return sum;
		}
		
		int x = square % 8;
		int y = square / 8;
		
		if (board(square) == K) return 0;
		if (board(square) == Q) return 0;
		if (board(x - 1, y - 1) == p) return 0;
		if (board(x + 1, y - 1) == p) return 0;
		if (board(square) == P
			&& (rank(square) < 4 || board(x, y - 1) != -1)) return 0;
		
		changeColor();
		if (blockersForKing(x + (7 - y) * 8) != 0) { changeColor(); return 0; }
		changeColor();
		
		return 1;
	}
	
	public static int mobilityBonus(int square, boolean midgame) {
		if (square == -1) {
			int sum = 0;
			
			for (int i = 0; i < 64; i++) {
				sum += mobilityBonus(i, midgame);
			}
			
			return sum;
		}
		
		int piece = board(square);
		
		if (piece < N || piece > Q) return 0;
		
		if (midgame) {
			return mobilityBonusMG[piece - 1][mobility(square)];
		} else {
			return mobilityBonusEG[piece - 1][mobility(square)];
		}
	}
	
	public static int mobilityMG(int square) {
		return mobilityBonus(square, true);
	}
	
	public static int mobilityEG(int square) {
		return mobilityBonus(square, false);
	}
}