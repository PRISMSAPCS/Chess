package utils.bot.DanielBotClasses;
import static utils.bot.DanielBotClasses.BitBoardConsts.*;
import static utils.bot.DanielBotClasses.BitBoardBitManipulation.*;
import static utils.bot.DanielBotClasses.BitBoardRandom.*;
import static utils.bot.DanielBotClasses.BitBoardIO.*;
import static utils.bot.DanielBotClasses.BitBoardAttacks.*;
import static utils.bot.DanielBotClasses.BitBoardMagic.*;
import static utils.bot.DanielBotClasses.BitBoardChessBoard.*;
import static utils.bot.DanielBotClasses.BitBoardPerformanceTesting.*;
import static utils.bot.DanielBotClasses.BitBoardEvaluation.*;
import static utils.bot.DanielBotClasses.BitBoardSearch.*;
import static utils.bot.DanielBotClasses.BitBoardUCI.*;
import static utils.bot.DanielBotClasses.BitBoardZobrist.*;
import static utils.bot.DanielBotClasses.BitBoardTranspositionTable.*;
import static utils.bot.DanielBotClasses.BitBoardBook.*;

import static utils.bot.DanielBotClasses.NNUE.NNUE.*;

import java.util.ArrayList;

import utils.*;
import utils.bot.DanielBotClasses.NNUE.*;

public class BitBoard {	
	public static short getBitBoardMove(ChessBoard board) {
		setUpBitBoard(board);
		bbBoard.clearHistory();
		return searchPosition();
	}
	
	public static void setUpBitBoard(ChessBoard board) {
		bbBoard.clearRepetitionTable();
		ArrayList<Move> previousMoves = board.getPreviousMoves();
		bbBoard.parseFen(startPosition);
		for (Move m : previousMoves) {
			String note = "";
			note += m.getStart().toChessNote();
			note += m.getEnd().toChessNote();
			
			if (m instanceof PromotionMove) {
				Piece piece = ((PromotionMove) m).getPromoteTo();
				
				if (piece instanceof Knight) note += "n";
				if (piece instanceof Bishop) note += "b";
				if (piece instanceof Rook) note += "r";
				if (piece instanceof Queen) note += "q";
			}
			
			bbBoard.makeMove(parseMove(note), allMoves);
		}
	}
	
	public static void initAll() {
		initLeapersAttacks();
		initSlidersAttacks(bishop);
		initSlidersAttacks(rook);
		initRandomKeys();
		initEvaluationMasks();
		openBook();
		bbBoard = new BitBoardChessBoard();
	}
	
	public static void main(String[] args) {
		initAll();
		/**
		 * 				 "rnbqkbnr"
						+"pppppppp"
						+"11111111"
						+"11111111"
						+"11111111"
						+"11111111"
						+"PPPPPPPP"
						+"RNBQKBNR"
						+" w - - 0 0"
		 */
		
		bbBoard.parseFen("r1b2B1k/1p1rN2p/p4R2/8/2Pp2P1/P6P/3n2K1/8 w - - 0 1");
		printBoard(bbBoard);
		searchPosition();
		thPool.shutdown();
	}
}