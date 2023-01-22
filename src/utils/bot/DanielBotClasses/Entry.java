package utils.bot.DanielBotClasses;

public class Entry {
	long hashKey;
	byte depth;
	byte flag;
	int score;
	int best;
	
	public Entry(long HashKey, int Depth, int Flag, int Score, int Move) {
		hashKey = HashKey;
		depth = (byte) Depth;
		flag = (byte) Flag;
		score = Score;
		best = Move;
	}
}