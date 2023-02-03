package utils.bot.DanielBotClasses.NNUE;

import static utils.bot.DanielBotClasses.NNUE.NNUESettings.*;

public class NN_Network {
	float W0[] = new float[40960*NN_SIZE];
	public float B0[] = new float[NN_SIZE];
	float W1[] = new float[NN_SIZE*2*32];
	float B1[] = new float[32];
	float W2[] = new float[32*32];
	float B2[] = new float[32];
	float W3[] = new float[32*1];
	float B3[] = new float[1];
	
	public NN_Network() {
		
	}
}