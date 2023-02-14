package utils.bot.DanielBotClasses;

import static utils.bot.DanielBotClasses.BitBoardSettings.*;
import static utils.bot.DanielBotClasses.BitBoardSearch.*;
import static utils.bot.DanielBotClasses.BitBoardConsts.*;

public class ThreadInformation {
	public int ply = 0;
	
	// killer and history moves heuristic
	public int killerMoves[][] = new int[2][maxPly];
	public int historyMoves[][] = new int[12][maxPly];
	
	// triangular principle variation table
	public int pvLength[] = new int[maxPly];
	public short pvTable[][] = new short[100][maxPly];
	
	// scoring and following PV nodes for move ordering
	public boolean scorePV = true;
	public boolean followPV = true;
	
	// diagnostic
	public int transpositions;
	public int nodes;
	
	// score
	public short score;
	
	// local chessboard for the thread
	public BitBoardChessBoard board;
	
	public ThreadInformation() {
		board = new BitBoardChessBoard(bbBoard);
		
		for (int i = 0; i < historyMoves.length; i++) {
			for (int j = 0; j < historyMoves[i].length; j++) {
				historyMoves[i][j] = (int) (Math.random() * (41) - 20);
			}
		}
	}
}