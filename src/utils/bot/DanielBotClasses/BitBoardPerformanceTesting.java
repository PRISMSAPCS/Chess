package utils.bot.DanielBotClasses;

import static utils.bot.DanielBotClasses.BitBoardConsts.*;
import static utils.bot.DanielBotClasses.BitBoardChessBoard.*;
import static utils.bot.DanielBotClasses.BitBoardBitManipulation.*;
import static utils.bot.DanielBotClasses.BitBoardIO.*;
import static utils.bot.DanielBotClasses.BitBoardSearch.*;

// performance testing class to ensure that move generation and make move logic is functioning properly
// also gives time, so you can compare speed
public class BitBoardPerformanceTesting {
	public static long nodes = 0;
	
	// sets # of nodes from given depth
	static void perftDriver(int depth) {
		if (depth == 0) {
			nodes++;
			return;
		}
		
		moves moveList = new moves();
		
		bbBoard.generateMoves(moveList);
		
		// iterate through all moves, make move, make a recursive call, undo move
		for (int i = 0; i < moveList.count; i++) {
			if (!bbBoard.makeMove(moveList.moves[i], allMoves)) {
				continue;
			}
			
			perftDriver(depth - 1);
			
			bbBoard.takeBack();
		}
	}
	
	// it's like perftDriver, but it prints the number of nodes that each legal move has
	// this allows for debugging to find which move is causing unexpected behavior
	static void perftTest(int depth) {
		System.out.println("\n     Performance Test \n");
		
		nodes = 0;
		moves moveList = new moves();
		
		bbBoard.generateMoves(moveList);
		
		long start = System.currentTimeMillis();
		
		for (int i = 0; i < moveList.count; i++) {
			if (!bbBoard.makeMove(moveList.moves[i], allMoves)) {
				continue;
			}
			
			// we need this to find the # of nodes from a single move
			long cumulativeNodes = nodes;
			
			perftDriver(depth - 1);
			
			long oldNodes = nodes - cumulativeNodes;
			
			bbBoard.takeBack();

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