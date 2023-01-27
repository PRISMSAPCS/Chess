package utils.bot.DanielBotClasses;

import static utils.bot.DanielBotClasses.BitBoardMoveGeneration.*;
import static utils.bot.DanielBotClasses.BitBoardConsts.*;
import static utils.bot.DanielBotClasses.BitBoardChessBoard.*;
import static utils.bot.DanielBotClasses.BitBoardBitManipulation.*;
import static utils.bot.DanielBotClasses.BitBoardIO.*;


public class BitBoardPerformanceTesting {
	public static long nodes = 0;
	
	static void perftDriver(int depth) {
		if (depth == 1) {
			nodes++;
			return;
		}
		
		moves moveList = new moves();
		
		generateMoves(moveList);
		
		for (int i = 0; i < moveList.count; i++) {
			if (!makeMove(moveList.moves[i], allMoves)) {
				continue;
			}
			
			perftDriver(depth - 1);
			
			takeBack();
		}
	}
	
	static void perftTest(int depth) {
		System.out.println("\n     Performance Test \n");
		
		nodes = 0;
		moves moveList = new moves();
		
		generateMoves(moveList);
		
		long start = System.currentTimeMillis();
		
		for (int i = 0; i < moveList.count; i++) {
			if (!makeMove(moveList.moves[i], allMoves)) {
				continue;
			}
			
			long cumulativeNodes = nodes;
			
			perftDriver(depth - 1);
			
			long oldNodes = nodes - cumulativeNodes;
			
			takeBack();

			// print move
	        System.out.printf("     move: %s%s%c  nodes: %d\n", squareToCoordinates[getMoveSource(moveList.moves[i])],
	                                                 squareToCoordinates[getMoveTarget(moveList.moves[i])],
	                                                 (getMovePromoted(moveList.moves[i]) != 0) ? asciiPieces[getMovePromoted(moveList.moves[i])] : ' ',
	                                                 oldNodes);
		}
		
		System.out.printf("\n    Depth: %d\n", depth);
		System.out.printf("    Nodes: %d", nodes);
		System.out.printf("    Time: %d\n\n", System.currentTimeMillis() - start);
	}
}