package utils.bot.DanielBotClasses;

import static utils.bot.DanielBotClasses.BitBoardChessBoard.*;

// checks for 3 fold repetition
public class BitBoardRepetition {
	// a dedicated hash table for repetition checking. it takes the last 16 bits of the zobrist key as a key
	static byte repetitionTable[] = new byte[65536]; // 2 ^ 16
	
	// we still actually need to store the past zobrist keys, since there will be collisions
	// but we won't have to iterate over the whole thing all the time
	static long pastZobrists[] = new long[1000];
	
	// instead of using an arraylist, which is slow, we just keep an index
	static int zobristIndex = 0;
	
	// add a position to the hash table and past zobrists
	public static void addPosition() {
		repetitionTable[(int) (hashKey >>> 48)]++;
		pastZobrists[zobristIndex] = hashKey;
		zobristIndex++;
	}
	
	// remove a position from the hash table and past zobrists
	public static void removePosition() {
		repetitionTable[(int) (hashKey >>> 48)]--;
		zobristIndex--;
	}
	
	// check if a position has been repeated twice before
	public static boolean positionRepeated() {
		// at least one repetition in the hash table
		if (repetitionTable[(int) (hashKey >>> 48)] >= 1) {
			// so now we manually loop over the past zobrists table, and check all occurences
			int occurrences = 0;
			for (int i = 0; i < zobristIndex; i++) {
				if (pastZobrists[i] == hashKey) occurrences++;
			}
			
			// 3fold
			if (occurrences >= 3) return true;
		}
		
		// no 3fold
		return false;
	}
	
	// clear the table for when we load a fen, or start a new game (UCI)
	public static void clearRepetitionTable() {
		zobristIndex = 0;
	}
}