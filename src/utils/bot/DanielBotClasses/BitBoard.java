package utils.bot.DanielBotClasses;
import static utils.bot.DanielBotClasses.BitBoardConsts.*;
import static utils.bot.DanielBotClasses.BitBoardBitManipulation.*;
import static utils.bot.DanielBotClasses.BitBoardRandom.*;
import static utils.bot.DanielBotClasses.BitBoardIO.*;
import static utils.bot.DanielBotClasses.BitBoardAttacks.*;
import static utils.bot.DanielBotClasses.BitBoardMagic.*;


public class BitBoard {		
	public static int getSquare(int rank, int file) { return rank * 8 + file; }
	
	public static void initAll() {
		initLeapersAttacks();
		initSlidersAttacks(bishop);
		initSlidersAttacks(rook);
	}
	
	public static void main(String[] args) {
		initAll();
		int char_pieces[] = new int[1000];
		char_pieces['P'] = P;
		char_pieces['N'] = N;
		char_pieces['B'] = B;
		char_pieces['R'] = R;
		char_pieces['Q'] = Q;
		char_pieces['K'] = K;
		char_pieces['p'] = p;
		char_pieces['n'] = n;
		char_pieces['b'] = b;
		char_pieces['r'] = r;
		char_pieces['q'] = q;
		char_pieces['k'] = k;
		
		int counter = 0;
		
		System.out.print("int charPieces[] = { ");
		for (int x : char_pieces) {
			System.out.printf("%d, ", x);
			if (x != 0) {
				counter++;
			}
			
			if (counter == 11) {
				break;
			}
		}
	}
}