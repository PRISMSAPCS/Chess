package utils.bot.DanielBotClasses;

// general settings for the bot
public class BitBoardSettings {
	public static final int timeLimit = 5000;
	public static final int timeLimitMargin = 5;
	public static final int maxDepth = 64;
	public static final boolean useUCIIO = false;
	public static final int hashTableSize = 40_960_000;
	
	public static final boolean useBook = true;
	public static final String bookFilePath = "bigBook.bin";
	public static final boolean bestBookMoveOnly = true;
}