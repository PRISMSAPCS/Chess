package utils.bot.DanielBotClasses;

import static utils.bot.DanielBotClasses.BitBoardSettings.*;
import static utils.bot.DanielBotClasses.BitBoardConsts.*;
import static utils.bot.DanielBotClasses.BitBoardChessBoard.*;
import static utils.bot.DanielBotClasses.BitBoardSearch.*;
import static utils.bot.DanielBotClasses.BitBoardBitManipulation.*;

// stores eval and move of an already searched position
public class BitBoardTranspositionTable {
	// hash table
	public static long hashTable[] = new long[(hashTableSizeMB << 20) / 8];
	public static final int hashTableSize = (hashTableSizeMB << 20) / 8;
	public static final int clusterSize = 4;
	public static byte generation = Byte.MIN_VALUE;
	
	// clear the hash table
	public static void clearHashTable() {
		for (int index = 0; index < hashTableSize; index++) {
			hashTable[index] = 0;
		}
	}
	
	// read an entry (if there is one)
	public static short readHashEntry(int alpha, int beta, int depth, long hashKey, int ply) {
		int firstEntry = getHashIndex(hashKey);
		long entryKey, entryData;
		
		for (int entryIndex = firstEntry; entryIndex < firstEntry + clusterSize * 2; entryIndex += 2) {
			entryKey = hashTable[entryIndex];
			entryData = hashTable[entryIndex + 1];
			entryKey ^= entryData;
			
			if (hashKey != entryKey) continue;
			
			if (getEntryDepth(entryData) >= depth) {
				// correct the mate score based off of ply from root
				short correctedScore = correctScoreForRetrieval(getEntryScore(entryData), ply);
				int flag = getEntryFlag(entryData);
				
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
		int firstEntry = getHashIndex(hashKey);
		long entryKey, entryData;
		
		int c1, c2, c3;
		int toReplace = firstEntry;
		byte replaceGeneration = getEntryGeneration(hashTable[toReplace]);
		int replaceDepth = getEntryDepth(hashTable[toReplace]);
		byte temp1;
		int temp2;
		
		for (int entryIndex = firstEntry; entryIndex < firstEntry + clusterSize * 2; entryIndex += 2) {
			entryKey = hashTable[entryIndex];
			entryData = hashTable[entryIndex + 1];
			entryKey ^= entryData;
			
			if (entryKey == 0 || entryKey == hashKey) {
				long toAdd = encodeEntryData(depth, hashFlag, correctScoreForStorage(score, ply), best, generation);
				hashKey ^= toAdd;
				hashTable[entryIndex] = hashKey;
				hashTable[entryIndex + 1] = toAdd;
				return;
			}
			
			temp1 = getEntryGeneration(entryData);
			temp2 = getEntryDepth(entryData);
			c1 = (replaceGeneration == generation ? 2 : 0);
			c2 = (temp1 == generation || getEntryFlag(entryData) == hashFlagExact) ? -2 : 0;
			c3 = (temp2 < replaceDepth) ? 1 : 0;
			
			if (c1 + c2 + c3 > 0) {
				replaceGeneration = temp1;
				replaceDepth = temp2;
				toReplace = entryIndex;
			}
		}
		
		long toAdd = encodeEntryData(depth, hashFlag, correctScoreForStorage(score, ply), best, generation);
		hashKey ^= toAdd;
		hashTable[toReplace] = hashKey;
		hashTable[toReplace + 1] = toAdd;
	}
	
	// get the best move from the TT for the current position, if it's in the TT
	public static int getStoredMove(long hashKey) {
		int firstEntry = getHashIndex(hashKey);
		long entryKey, entryData;
		
		for (int entryIndex = firstEntry; entryIndex < firstEntry + clusterSize * 2; entryIndex += 2) {
			entryKey = hashTable[entryIndex];
			entryData = hashTable[entryIndex + 1];
			entryKey ^= entryData;
			
			if (entryKey == hashKey) {
				return getEntryMove(entryData);
			}
		}

		return noHashEntry;
	}
	
	// mate score for storage is just mate score
	public static short correctScoreForStorage(short score, int ply) {
		score *= sideMultiplier;
		
		if (score >= mateScoreThreshold) {
			return (short) (mateScore + ply);
		} else if (score <= -mateScoreThreshold) {
			return (short) -(mateScore + ply);
		}
		
		return score;
	}
	
	// adjust mate score to be ply from root
	public static short correctScoreForRetrieval(short score, int ply) {
		score *= sideMultiplier;
		
		if (score >= mateScoreThreshold) {
			return (short) (mateScore - ply);
		} else if (score <= -mateScoreThreshold) {
			return (short) -(mateScore - ply);
		}
		
		return score;
	}
	
	// helper function, fast hash
	public static int getHashIndex(long hashKey) {
		return (Math.abs((int) hashKey % hashTableSize)) & (~0b111);
	}
	
	// called to increment generation
	public static void newSearch() {
		generation++;
	}
}