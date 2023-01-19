package utils.bot.DanielBotClasses;
import static utils.bot.DanielBotClasses.BitBoardConsts.*;
import static utils.bot.DanielBotClasses.BitBoardBitManipulation.*;
import static utils.bot.DanielBotClasses.BitBoardRandom.*;
import static utils.bot.DanielBotClasses.BitBoardIO.*;
import static utils.bot.DanielBotClasses.BitBoardAttacks.*;
import static utils.bot.DanielBotClasses.BitBoardMagic.*;
import static utils.bot.DanielBotClasses.BitBoardChessBoard.*;
import static utils.bot.DanielBotClasses.BitBoardMoveGeneration.*;


public class BitBoard {		
	public static int getSquare(int rank, int file) { return rank * 8 + file; }
	
	public static void initAll() {
		initLeapersAttacks();
		initSlidersAttacks(bishop);
		initSlidersAttacks(rook);
	}
	
	public static void main(String[] args) {
		initAll();
		
		parseFen("r3k2r/p1ppppb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R b KQkq - 0 1 ");
		
		long start = System.currentTimeMillis();
		for (int counter = 0; counter < 1000000; counter++) {
			moves moveList = new moves();
			
			generateMoves(moveList, allMoves);
			for (int i = 0; i < moveList.count; i++) {
				if (!makeMove(moveList.moves[i])) { continue; }
				takeBack();
			}
		}
		
		System.out.printf("time taken to execute: %d", System.currentTimeMillis() - start);
	}
}