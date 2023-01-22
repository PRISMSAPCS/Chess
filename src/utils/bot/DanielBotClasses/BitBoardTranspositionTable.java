package utils.bot.DanielBotClasses;

import static utils.bot.DanielBotClasses.BitBoardSettings.*;
import static utils.bot.DanielBotClasses.BitBoardConsts.*;
import static utils.bot.DanielBotClasses.BitBoardChessBoard.*;
import static utils.bot.DanielBotClasses.BitBoardSearch.*;

public class BitBoardTranspositionTable {
	public static Entry hashTable[] = new Entry[hashTableSize];
	
	public static void clearHashTable() {
		for (int index = 0; index < hashTableSize; index++) {
			hashTable[index] = null;
		}
	}
	
	public static int readHashEntry(int alpha, int beta, int depth) {
		Entry hashEntry = hashTable[getHashIndex()];
		if (hashEntry == null) return noHashEntry;
		if (hashEntry.hashKey == hashKey) {
			if (hashEntry.depth >= depth) {
				int correctedScore = correctMateScoreForRetrieval(hashEntry.score, depth);
				
				if (hashEntry.flag == hashFlagExact) {
					return correctedScore;
				}
				
				if (hashEntry.flag == hashFlagAlpha && hashEntry.score <= alpha) {
					return correctedScore;
				}
				
				if (hashEntry.flag == hashFlagBeta && hashEntry.score >= beta) {
					return correctedScore;
				}
			}
		}
		
		return noHashEntry;
	}
	
	public static void writeHashEntry(int score, int depth, int hashFlag, int best) {
		Entry toAdd = new Entry(hashKey, depth, hashFlag, correctMateScoreForStorage(score), best);
		
		hashTable[getHashIndex()] = toAdd;
	}
	
	public static int getStoredMove() {
		Entry toCheck = hashTable[getHashIndex()];
		
		if (toCheck != null) {
			if (toCheck.hashKey == hashKey) {
				return toCheck.best;
			}
		}
		
		return noHashEntry;
	}
	
	public static int correctMateScoreForStorage(int score) {
		if (score >= mateScore - 100) {
			return mateScore;
		} else if (score <= -(mateScore - 100)) {
			return -mateScore;
		}
		
		return score;
	}
	
	public static int correctMateScoreForRetrieval(int score, int depth) {
		if (score == mateScore) {
			return mateScore - ply - depth;
		} else if (score == -mateScore) {
			return -(mateScore - ply - depth);
		}
		
		return score;
	}
	
	public static int getHashIndex() {
		return Math.abs((int) (hashKey % hashTableSize));
	}
}