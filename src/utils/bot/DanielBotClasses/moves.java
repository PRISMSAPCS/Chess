package utils.bot.DanielBotClasses;

// movelist class. it's literally an arraylist, but since arraylists are slow, we have a count index
public class moves {
	public int moves[];
	public int count;
	
	public moves() {
		moves = new int[256];
		count = 0;
	}
}