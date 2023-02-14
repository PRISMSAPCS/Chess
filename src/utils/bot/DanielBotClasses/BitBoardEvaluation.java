package utils.bot.DanielBotClasses;

import static utils.bot.DanielBotClasses.BitBoardChessBoard.*;
import static utils.bot.DanielBotClasses.BitBoardConsts.*;
import static utils.bot.DanielBotClasses.BitBoardBitManipulation.*;
import static utils.bot.DanielBotClasses.BitBoardIO.*;
import static utils.bot.DanielBotClasses.BitBoardAttacks.*;

public class BitBoardEvaluation {
	// diagnostic
	public static int evalCalled = 0;
	
	// some useful stuff for basic pawn structure
	static long fileMasks[] = new long[64];
	static long rankMasks[] = new long[64];
	static long isolatedMasks[] = new long[64];
	static long passedMasks[][] = new long[2][64];
	
	// king ring squares
	static long kingRingMasks[] = new long[64];
	
	// returns the file / rank mask of a square
	public static long setFileRankMask(int file, int rank) {
		long mask = 0L;
		
		for (int r = 0; r < 8; r++) {
			for (int f = 0; f < 8; f++) {
				int square = r * 8 + f;
				
				if (file != -1) {
					if (f == file) {
						mask |= (1L << square);
					}
				} else if (rank != -1) {
					if (r == rank) {
						mask |= (1L << square);
					}
				}
			}
		}
		
		return mask;
	}
	
	// returns the king ring mask of a square
	public static long setKingRingMask(int file, int rank) {
		long mask = 0L;
		
		for (int r = 0; r < 8; r++) {
			for (int f = 0; f < 8; f++) {
				int square = r * 8 + f;
				
				int fileMargin = 1;
				int rankMargin = 1;
				
				if (file == 0 || file == 7) fileMargin = 2;
				if (rank == 0 || rank == 7) rankMargin = 2;
				
				if (Math.abs(f - file) <= fileMargin && Math.abs(r - rank) <= rankMargin) {
					mask |= (1L << square);
				}
			}
		}
		
		return mask;
	}
	
	// initiates file, rank, isolated, and passed masks
	public static void initEvaluationMasks() {
		for (int rank = 0; rank < 8; rank++) {
			for (int file = 0; file < 8; file++) {
				int square = rank * 8 + file;
				
				fileMasks[square] = setFileRankMask(file, -1);
				rankMasks[square] = setFileRankMask(-1, rank);
				isolatedMasks[square] = setFileRankMask(file - 1, -1);
				isolatedMasks[square] |= setFileRankMask(file + 1, -1);
				
				passedMasks[white][square] = setFileRankMask(file - 1, -1);
				passedMasks[white][square] |= setFileRankMask(file, -1);
				passedMasks[white][square] |= setFileRankMask(file + 1, -1);
				
				passedMasks[black][square] = setFileRankMask(file - 1, -1);
				passedMasks[black][square] |= setFileRankMask(file, -1);
				passedMasks[black][square] |= setFileRankMask(file + 1, -1);
				
				for (int i = 0; i < 8; i++) {
					if (i < 8 - rank) {
						passedMasks[white][square] &= ~setFileRankMask(-1, 7 - i);
					} else {
						passedMasks[black][square] &= ~setFileRankMask(-1, 7 - i);
					}
				}
				
				kingRingMasks[square] = setKingRingMask(file, rank);
			}
		}
	}
}