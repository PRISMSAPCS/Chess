package utils.bot.DanielBotClasses;

import static utils.bot.DanielBotClasses.BitBoardConsts.*;
import static utils.bot.DanielBotClasses.BitBoardMagic.findMagicNumber;
import static utils.bot.DanielBotClasses.BitBoardAttacks.*;
import static utils.bot.DanielBotClasses.BitBoardBitManipulation.countBits;
import static utils.bot.DanielBotClasses.BitBoardRandom.generateMagicNumber;

import java.util.Arrays;

// literally black magic. absurdly confusing. it's like a goofy hashing thing that does stuff and works
// these functions are never used because we already computed the magic numbers and then set them as a constant
public class BitBoardMagic {
	static long findMagicNumber(int square, int relevantBits, int bishop) {
		long occupancies[] = new long[4096];
		long attacks[] = new long[4096];
		long usedAttacks[] = new long[4096];
		
		long attackMask = (bishop == 1) ? maskBishopAttacks(square) : maskRookAttacks(square);
		
		int occupancyIndices = 1 << relevantBits;
		
		for (int index = 0; index < occupancyIndices; index++) {
			occupancies[index] = setOccupancy(index, relevantBits, attackMask);
			
			attacks[index] = (bishop == 1) ? bishopAttacksOnTheFly(square, occupancies[index]) : rookAttacksOnTheFly(square, occupancies[index]);
		}
		
		for (int randomCount = 0; randomCount < 100000000; randomCount++) {
			long magicNumber = generateMagicNumber();
			
			if (countBits((attackMask * magicNumber) & 0xFF00000000000000L) < 6) continue;
			
			Arrays.fill(usedAttacks, 0L);
			
			int index, fail;
			
			for (index = 0, fail = 0; (fail == 0) && index < occupancyIndices; index++) {
				int magicIndex = (int) ((occupancies[index] * magicNumber) >>> (64 - relevantBits));
				
				if (usedAttacks[magicIndex] == 0L) {
					usedAttacks[magicIndex] = attacks[index];
				} else if (usedAttacks[magicIndex] != attacks[index]) {
					fail = 1;
				}
			}
			
			if (fail == 0) {
				return magicNumber;
			}
		}
		
		System.out.println("Magic number fails");
		return 0L;
	}
	
	public static void initMagicNumbers() {
		// this works, but it will never be used, since magic numbers have already been generated
		// and we store them in a constant
		for (int square = 0; square < 64; square++) {
			System.out.printf(" 0x%16X\n", findMagicNumber(square, rookRelevantBits[square], rook));
		}
	}
}