package utils.bot.DanielBotClasses;

public class TEST {
	
	// load NNUE probe JNI
	static {
		System.loadLibrary("//src//utils//bot//DanielBotClasses//nnue_probe");
	}
	
	private native int nnueEvaluate(int player, long[] pieces, long[] occupancies);
	public static native void nnueInit(String path);
	
	public static void main(String[] args) {
		TEST asdf = new TEST();
		nnueInit("hello");
	}
}