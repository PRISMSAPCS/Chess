package utils.bot.DanielBotClasses;

// a deterministic pseudorandom number generator
// deterministic randomness makes stuff like zobrist keys always the same, so zobrist hashes will always be the same
public class BitBoardRandom {
	static int randomState = 1804289383;
	
	static int getRandomInt() {
		int number = randomState;
		
		number ^= number << 13;
		number ^= number >>> 17;
		number ^= number << 5;
		
		randomState = number;
		
		return number;
	}
	
	static long getRandomLong() {
		long n1, n2, n3, n4;
		
		n1 = (long) (getRandomInt()) & 0xFFFF;
		n2 = (long) (getRandomInt()) & 0xFFFF;
		n3 = (long) (getRandomInt()) & 0xFFFF;
		n4 = (long) (getRandomInt()) & 0xFFFF;
		
		return n1 | (n2 << 16) | (n3 << 32) | (n4 << 48);
	}
	
	static long generateMagicNumber() {
		return getRandomLong() & getRandomLong() & getRandomLong();
	}
}