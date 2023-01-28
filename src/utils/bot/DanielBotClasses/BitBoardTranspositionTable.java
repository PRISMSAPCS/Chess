package utils.bot.DanielBotClasses;

import static utils.bot.DanielBotClasses.BitBoardSettings.*;
import static utils.bot.DanielBotClasses.BitBoardConsts.*;
import static utils.bot.DanielBotClasses.BitBoardChessBoard.*;
import static utils.bot.DanielBotClasses.BitBoardSearch.*;

// stores eval and move of an already searched position
public class BitBoardTranspositionTable {
	// hash table
	public static Entry hashTable[] = new Entry[hashTableSize];
	
	// clear the hash table
	public static void clearHashTable() {
		for (int index = 0; index < hashTableSize; index++) {
			hashTable[index] = null;
		}
	}
	
	// read an entry (if there is one)
	public static int readHashEntry(int alpha, int beta, int depth) {
		Entry hashEntry = hashTable[getHashIndex()];
		if (hashEntry == null) return noHashEntry;
		if (hashEntry.hashKey == hashKey) {
			if (hashEntry.depth >= depth) {
				// correct the mate score based off of ply from root
				int correctedScore = correctMateScoreForRetrieval(hashEntry.score);
				
				// exact entry, return
				if (hashEntry.flag == hashFlagExact) {
					return correctedScore;
				}
				
				// alpha entry, return if it's less than alpha
				if (hashEntry.flag == hashFlagAlpha && hashEntry.score <= alpha) {
					return correctedScore;
				}
				
				// beta entry, return if it's greater than beta
				if (hashEntry.flag == hashFlagBeta && hashEntry.score >= beta) {
					return correctedScore;
				}
			}
		}
		
		return noHashEntry;
	}
	
	// write a hash entry
	public static void writeHashEntry(int score, int depth, int hashFlag, int best) {
		Entry toAdd = new Entry(hashKey, depth, hashFlag, correctMateScoreForStorage(score), best);
		
		hashTable[getHashIndex()] = toAdd;
	}
	
	// get the best move from the TT for the current position, if it's in the TT
	public static int getStoredMove() {
		Entry toCheck = hashTable[getHashIndex()];
		
		if (toCheck != null) {
			// make sure that the hash keys are the same, since collisions do occur
			if (toCheck.hashKey == hashKey) {
				return toCheck.best;
			}
		}
		
		return noHashEntry;
	}
	
	// mate score for storage is just mate score
	public static int correctMateScoreForStorage(int score) {
		if (score >= mateScore - 100) {
			return mateScore;
		} else if (score <= -(mateScore - 100)) {
			return -mateScore;
		}
		
		return score;
	}
	
	// adjust mate score to be ply from root
	public static int correctMateScoreForRetrieval(int score) {
		if (score == mateScore) {
			return mateScore - ply;
		} else if (score == -mateScore) {
			return -(mateScore - ply);
		}
		
		return score;
	}
	
	// helper function, fast hash
	public static int getHashIndex() {
		return Math.abs((int) (hashKey % hashTableSize));
	}
}