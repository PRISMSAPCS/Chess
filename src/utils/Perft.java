package utils;

import java.util.ArrayList;

public class Perft {
	ChessBoard chessBoard;
	long nodes;
	
	public Perft(ChessBoard board) {
		chessBoard = board;
		nodes = 0;
	}
	
	// sets # of nodes from given depth
	public void perftDriver(int depth) {
		if (depth == 0) {
			nodes++;
			return;
		}
		
		ArrayList<Move> movelist = chessBoard.getAllLegalMoves();
		
		// iterate through all moves, make move, make a recursive call, undo move
		for (Move m : movelist) {
			chessBoard.submitMove(m);
			
			perftDriver(depth - 1);
			
			chessBoard.undoMove();
		}
	}
	
	// it's like perftDriver, but it prints the number of nodes that each legal move has
	// this allows for debugging to find which move is causing unexpected behavior
	public void perftTest(int depth) {
		System.out.println("\n     Performance Test \n");
		
		nodes = 0;
		ArrayList<Move> movelist = chessBoard.getAllLegalMoves();
		
		long start = System.currentTimeMillis();
		
		for (int i = 0; i < movelist.size(); i++) {
			Move m = movelist.get(i);
			chessBoard.submitMove(m);
			
			// we need this to find the # of nodes from a single move
			long cumulativeNodes = nodes;
			
			perftDriver(depth - 1);
			
			long oldNodes = nodes - cumulativeNodes;

			chessBoard.undoMove();
			
			// print move
	        System.out.printf("     move: %s nodes: %d\n", m.toString(),
	                                                 oldNodes);
		}
		
		System.out.printf("\n    Depth: %d\n", depth);
		System.out.printf("    Nodes: %d", nodes);
		System.out.printf("    Time: %d\n\n", System.currentTimeMillis() - start);
	}
}