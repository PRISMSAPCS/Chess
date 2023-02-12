package utils.bot.DanielBotClasses;

// transposition table entry
public class Entry {
	// keyDepthFlag is formatted as such:
	/**
	 * 0000 0000 1111 1111 1111 1111 1111 1111	zobrist key, shortened to 24 bits
	 * 0011 1111 0000 0000 0000 0000 0000 0000	depth
	 * 1100 0000 0000 0000 0000 0000 0000 0000	flag type
	 */
	int keyDepthFlag;
	short score;
	short best;
	
	public Entry(long HashKey, int Depth, int Flag, int Score, int Move) {
		keyDepthFlag = (int) (HashKey >>> 40 | (Depth << 24) | (Flag << 30));
		score = (short) Score;
		best = (short) Move;
	}
	
	public int getHashKey() {
		return keyDepthFlag & 0xFFFFFF;
	}
	
	public int getDepth() {
		return keyDepthFlag >> 24 & 0x3F;
	}
	
	public int getFlag() {
		return keyDepthFlag >> 30 & 0x3;
	}
}