package utils.bot.DanielBotClasses.StockfishEval;
import static utils.bot.DanielBotClasses.StockfishEval.Global.*;
import static utils.bot.DanielBotClasses.StockfishEval.Attack.*;
import static utils.bot.DanielBotClasses.StockfishEval.Helpers.*;
import static utils.bot.DanielBotClasses.BitBoardConsts.*;
import static utils.bot.DanielBotClasses.BitBoard.*;
import static utils.bot.DanielBotClasses.BitBoardChessBoard.*;
import static utils.bot.DanielBotClasses.BitBoardIO.*;

public class Main {
	public static void main(String[] args) {
		initAll();
		parseFen("rnbq2nr/pppppppp/8/8/7k/8/PPPPPPPP/RNBQK1NR b KQkq - 1 1");
		printBoard();
		changeColor();
		System.out.println(pieceCount());
	}
}