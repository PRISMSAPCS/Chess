package utils.bot.DanielBotClasses;

// general settings for the bot
public class BitBoardSettings {
	public static final int timeLimit = 5000;
	public static final int maxDepth = 100;
	public static final boolean useUCIIO = false;
	public static final int hashTableSize = 640000;
	
	public static final String bookFilePath = "bigBook.bin";
	public static final boolean bestBookMoveOnly = false;
}