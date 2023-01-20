package utils.bot.DanielBotClasses.StockfishEval;

import static utils.bot.DanielBotClasses.StockfishEval.Global.*;
import static utils.bot.DanielBotClasses.StockfishEval.Helpers.*;
import static utils.bot.DanielBotClasses.StockfishEval.Consts.*;
import static utils.bot.DanielBotClasses.BitBoardConsts.*;

public class Imbalance {
	public static int imbalance(int square) {
		if (square == -1) {
			int sum = 0;
			
			for (int toCheck = 0; toCheck < 64; toCheck++) {
				sum += imbalance(toCheck);
			}
			
			return sum;
		}
		
		int j = board(square);
		
		if (j < 0 || j > 4) return 0;
		
		int bishop[] = {0, 0};
		int toReturn = 0;
		
		for (int toCheck = 0; toCheck < 64; toCheck++) {
			int i = board(toCheck);
			
			if (i < 0) continue;
			if (i == 8) bishop[0]++;
			if (i == 2) bishop[1]++;
			if (i % 6 > j) continue;
			if (i > 4) toReturn += qt[j + 1][i - 5];
			else toReturn += qo[j + 1][i + 1];
		}
		
		if (bishop[0] > 1) toReturn += qt[j + 1][0];
		if (bishop[1] > 1) toReturn += qo[j + 1][0];
		
		return toReturn;
	}
	
	public static int bishopPair() {
		if (bishopCount() < 2) return 0;
		
		return 1438;
	}
	
	public static int imbalanceTotal() {
		int toReturn = 0;
		toReturn += imbalance(-1);
		toReturn += bishopPair();
		changeColor();
		toReturn -= imbalance(-1);
		toReturn -= bishopPair();
		changeColor();
		
		return (toReturn / 16);
	}
}