package utils.bot.DanielBotClasses.StockfishEval;
import static utils.bot.DanielBotClasses.StockfishEval.Global.*;
import static utils.bot.DanielBotClasses.StockfishEval.Attack.*;
import static utils.bot.DanielBotClasses.StockfishEval.Helpers.*;
import static utils.bot.DanielBotClasses.StockfishEval.King.*;
import static utils.bot.DanielBotClasses.StockfishEval.Imbalance.*;
import static utils.bot.DanielBotClasses.StockfishEval.Material.*;
import static utils.bot.DanielBotClasses.StockfishEval.Mobility.*;
import static utils.bot.DanielBotClasses.BitBoardConsts.*;
import static utils.bot.DanielBotClasses.BitBoard.*;
import static utils.bot.DanielBotClasses.BitBoardChessBoard.*;
import static utils.bot.DanielBotClasses.BitBoardAttacks.*;
import static utils.bot.DanielBotClasses.BitBoardBitManipulation.*;
import static utils.bot.DanielBotClasses.BitBoardIO.*;

public class Main {
	public static void main(String[] args) {
		initAll();
		parseFen("rnb1k2r/pppppppp/8/8/1b1PP3/8/PPP2qPP/RNBQKBNR w KQkq - 1 2");
		long start = System.currentTimeMillis();
		for (int i = 0; i < 1000; i++) {
			int j = kingMG();
		}
		
		System.out.println(System.currentTimeMillis() - start);
//		changeColor();
//		printBoard();
//
//		System.out.println(kingEG());
	}
}