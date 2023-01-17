package utils.bot.DanielBotClasses;
import static utils.bot.DanielBotClasses.BitBoardConsts.*;
import static utils.bot.DanielBotClasses.BitBoardBitManipulation.*;
import static utils.bot.DanielBotClasses.BitBoardRandom.*;
import static utils.bot.DanielBotClasses.BitBoardIO.*;
import static utils.bot.DanielBotClasses.BitBoardAttacks.*;


public class BitBoard {		
	public static int getSquare(int rank, int file) { return rank * 8 + file; }
	
	public static void main(String[] args) {
		initLeapersAttacks();
		
		long randomNumber = (long) getRandomInt();
		print(randomNumber);
		print(getRandomInt() & 0xFFFF);
		print(getRandomLong());
		print(generateMagicNumber());
	}
}