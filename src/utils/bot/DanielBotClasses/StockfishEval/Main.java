package utils.bot.DanielBotClasses.StockfishEval;
import static utils.bot.DanielBotClasses.StockfishEval.Global.*;
import static utils.bot.DanielBotClasses.StockfishEval.Attack.*;
import static utils.bot.DanielBotClasses.StockfishEval.Helpers.*;
import static utils.bot.DanielBotClasses.StockfishEval.King.*;
import static utils.bot.DanielBotClasses.StockfishEval.Imbalance.*;
import static utils.bot.DanielBotClasses.BitBoardConsts.*;
import static utils.bot.DanielBotClasses.BitBoard.*;
import static utils.bot.DanielBotClasses.BitBoardChessBoard.*;
import static utils.bot.DanielBotClasses.BitBoardAttacks.*;
import static utils.bot.DanielBotClasses.BitBoardBitManipulation.*;
import static utils.bot.DanielBotClasses.BitBoardIO.*;

public class Main {
	public static void main(String[] args) {
		initAll();
		parseFen("rnb1k2r/pppppppp/8/2b5/3PP3/8/PPP2qPP/RNBQKBNR w KQkq - 1 2");
		//changeColor();
		printBoard();
		
		System.out.println(kingAttackersWeight(-1));
	}
}