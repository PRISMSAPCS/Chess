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
		if (hashEntry.getHashKey() == (hashKey >>> 40)) {
			if (hashEntry.getDepth() >= depth) {
				// correct the mate score based off of ply from root
				int correctedScore = correctScoreForRetrieval(hashEntry.score, depth);
				int flag = hashEntry.getFlag();
				
				// exact entry, return
				if (flag == hashFlagExact) {
					return correctedScore;
				}
				
				// alpha entry, return if it's less than alpha
				if (flag == hashFlagAlpha && hashEntry.score <= alpha) {
					return correctedScore;
				}
				
				// beta entry, return if it's greater than beta
				if (flag == hashFlagBeta && hashEntry.score >= beta) {
					return correctedScore;
				}
			}
		}
		
		return noHashEntry;
	}
	
	// write a hash entry
	public static void writeHashEntry(int score, int depth, int hashFlag, int best) {
		Entry toAdd = new Entry(hashKey, depth, hashFlag, correctScoreForStorage(score), best);
		
		hashTable[getHashIndex()] = toAdd;
	}
	
	// get the best move from the TT for the current position, if it's in the TT
	public static int getStoredMove() {
		Entry toCheck = hashTable[getHashIndex()];
		
		if (toCheck != null) {
			// make sure that the hash keys are the same, since collisions do occur
			if (toCheck.getHashKey() == (hashKey >>> 40)) {
				return toCheck.best;
			}
		}
		
		return noHashEntry;
	}
	
	// mate score for storage is just mate score
	public static int correctScoreForStorage(int score) {
		score *= sideMultiplier;
		
		if (score >= mateScoreThreshold) {
			return mateScore + ply;
		} else if (score <= -mateScoreThreshold) {
			return -(mateScore + ply);
		}
		
		return score;
	}
	
	// adjust mate score to be ply from root
	public static int correctScoreForRetrieval(int score, int depth) {
		score *= sideMultiplier;
		
		if (score == mateScore) {
			return mateScore - ply;
		} else if (score == -mateScore) {
			return -(mateScore - ply);
		}
		
		return score;
	}
	
	// helper function, fast hash
	public static int getHashIndex() {
		return Math.abs(((int) hashKey % hashTableSize));
	}
}