package utils.bot.DanielBotClasses;
import static utils.bot.DanielBotClasses.BitBoardConsts.*;
import static utils.bot.DanielBotClasses.BitBoardBitManipulation.*;
import static utils.bot.DanielBotClasses.BitBoardRandom.*;
import static utils.bot.DanielBotClasses.BitBoardIO.*;
import static utils.bot.DanielBotClasses.BitBoardAttacks.*;
import static utils.bot.DanielBotClasses.BitBoardMagic.*;
import static utils.bot.DanielBotClasses.BitBoardChessBoard.*;
import static utils.bot.DanielBotClasses.BitBoardMoveGeneration.*;
import static utils.bot.DanielBotClasses.BitBoardPerformanceTesting.*;
import static utils.bot.DanielBotClasses.BitBoardEvaluation.*;
import static utils.bot.DanielBotClasses.BitBoardSearch.*;

import java.util.Random;

public class BitBoard {
	public static void loadFen(String fen) { parseFen(fen); }
	
	public static int randomMove() {
		moves moveList = new moves();
		
		generateMoves(moveList, allMoves);
		
		Random asdf = new Random();
		int randomNum = asdf.nextInt(moveList.count);
		while (!makeMove(moveList.moves[randomNum])) {
			randomNum = asdf.nextInt(moveList.count);
		}
		return moveList.moves[randomNum];
	}
	
	public static void initAll() {
		initLeapersAttacks();
		initSlidersAttacks(bishop);
		initSlidersAttacks(rook);
	}
	
	public static void main(String[] args) {
		initAll();
		
		parseFen("r111k11r/p1ppRpb1/bn11pnp1/111PN111/1p11P111/11N11Q1p/PPPBBPPP/R111K11R KQkq - 0 0");
		System.out.println(evaluate());
		
		printBoard();
	}
}