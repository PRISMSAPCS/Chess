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
import static utils.bot.DanielBotClasses.BitBoardUCI.*;
import static utils.bot.DanielBotClasses.BitBoardZobrist.*;
import static utils.bot.DanielBotClasses.BitBoardTranspositionTable.*;
import static utils.bot.DanielBotClasses.BitBoardRepetition.*;
import static utils.bot.DanielBotClasses.BitBoardBook.*;

import static utils.bot.DanielBotClasses.NNUE.NNUE.*;

import java.util.ArrayList;

import utils.*;
import utils.bot.DanielBotClasses.NNUE.*;

public class BitBoard {	
	public static int getBitBoardMove(ChessBoard board) {
		setUpBitBoard(board);
		clearHistory();
		return searchPosition();
	}
	
	public static void setUpBitBoard(ChessBoard board) {
		clearRepetitionTable();
		ArrayList<Move> previousMoves = board.getPreviousMoves();
		parseFen(startPosition);
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
			
			makeMove(parseMove(note), allMoves);
		}
	}
	
	public static void initAll() {
		initLeapersAttacks();
		initSlidersAttacks(bishop);
		initSlidersAttacks(rook);
		initRandomKeys();
		initEvaluationMasks();
		openBook();
	}
	
	public static void main(String[] args) {
		initAll();
		parseFen("1r1k3r/p1p1q3/1pQbpp1p/1B1p1bp1/3P4/2P1P3/PP1N1PPP/R3K2R b - - 0 0");
		printBoard();
		searchPosition();
	}
}