package utils.bot.DanielBotClasses.NNUE;

import static utils.bot.DanielBotClasses.NNUE.NNUESettings.*;

public class NN_Board {
	public long pieces[] = new long[12];
	float accumulator[][] = new float[2][NN_SIZE];
	
	public NN_Board() {
		
	}
}