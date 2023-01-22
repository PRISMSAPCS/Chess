package utils.bot.DanielBotClasses;

import static utils.bot.DanielBotClasses.BitBoardChessBoard.*;

public class BitBoardRepetition {
	static byte repetitionTable[] = new byte[65536]; // 2 ^ 16
	static long pastZobrists[] = new long[1000];
	
	static int zobristIndex = 0;
	
	public static void addPosition() {
		repetitionTable[(int) (hashKey >>> 48)]++;
		pastZobrists[zobristIndex] = hashKey;
		zobristIndex++;
	}
	
	public static void removePosition() {
		repetitionTable[(int) (hashKey >>> 48)]--;
		zobristIndex--;
	}
	
	public static boolean positionRepeated() {
		if (repetitionTable[(int) (hashKey >>> 48)] >= 1) {
			int occurrences = 0;
			for (int i = 0; i < zobristIndex; i++) {
				if (pastZobrists[i] == hashKey) occurrences++;
			}
			
			if (occurrences >= 3) return true;
		}
		
		return false;
	}
	
	public static void clearRepetitionTable() {
		zobristIndex = 0;
	}
}