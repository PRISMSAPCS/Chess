package utils.bot.DanielBotClasses;

import static utils.bot.DanielBotClasses.BitBoardSettings.*;
import static utils.bot.DanielBotClasses.BitBoardConsts.*;
import static utils.bot.DanielBotClasses.BitBoardChessBoard.*;
import static utils.bot.DanielBotClasses.BitBoardSearch.*;
import static utils.bot.DanielBotClasses.BitBoardBitManipulation.*;

// stores eval and move of an already searched position
public class BitBoardTranspositionTable {
	// hash table
	public static long hashTable[] = new long[hashTableSize];
	
	// clear the hash table
	public static void clearHashTable() {
		for (int index = 0; index < hashTableSize; index++) {
			hashTable[index] = 0;
		}
	}
	
	// read an entry (if there is one)
	public static short readHashEntry(int alpha, int beta, int depth, long hashKey, int ply) {
		long hashEntry = hashTable[getHashIndex(hashKey)];
		//hashEntry = 0;
		if (hashEntry == 0) return noHashEntry;
		if (getEntryHashKey(hashEntry) == (hashKey >>> 40)) {
			if (getEntryDepth(hashEntry) >= depth) {
				// correct the mate score based off of ply from root
				short correctedScore = correctScoreForRetrieval(getEntryScore(hashEntry), depth, ply);
				int flag = getEntryFlag(hashEntry);
				
				// exact entry, return
				if (flag == hashFlagExact) {
					return correctedScore;
				}
				
				// alpha entry, return if it's less than alpha
				if (flag == hashFlagAlpha && correctedScore <= alpha) {
					return correctedScore;
				}
				
				// beta entry, return if it's greater than beta
				if (flag == hashFlagBeta && correctedScore >= beta) {
					return correctedScore;
				}
			}
		}
		
		return noHashEntry;
	}
	
	// write a hash entry
	public static void writeHashEntry(short score, int depth, int hashFlag, short best, long hashKey, int ply) {
		long toAdd = encodeEntry(hashKey, depth, hashFlag, correctScoreForStorage(score, ply), best);

		hashTable[getHashIndex(hashKey)] = toAdd;
	}
	
	// get the best move from the TT for the current position, if it's in the TT
	public static int getStoredMove(long hashKey) {
		long toCheck = hashTable[getHashIndex(hashKey)];
		
		if (toCheck != 0) {
			// make sure that the hash keys are the same, since collisions do occur
			if (getEntryHashKey(toCheck) == (hashKey >>> 40)) {
				return getEntryMove(toCheck);
			}
		}
		
		return noHashEntry;
	}
	
	// mate score for storage is just mate score
	public static short correctScoreForStorage(short score, int ply) {
		//score *= sideMultiplier;
		
		if (score >= mateScoreThreshold) {
			return (short) (mateScore + ply);
		} else if (score <= -mateScoreThreshold) {
			return (short) -(mateScore + ply);
		}
		
		return score;
	}
	
	// adjust mate score to be ply from root
	public static short correctScoreForRetrieval(short score, int depth, int ply) {
		//score *= sideMultiplier;
		
		if (score >= mateScoreThreshold) {
			return (short) (mateScore - ply);
		} else if (score <= -mateScoreThreshold) {
			return (short) -(mateScore - ply);
		}
		
		return score;
	}
	
	// helper function, fast hash
	public static int getHashIndex(long hashKey) {
		return Math.abs(((int) hashKey % hashTableSize));
	}
}