package utils.bot.DanielBotClasses;

import static utils.bot.DanielBotClasses.BitBoardChessBoard.*;
import static utils.bot.DanielBotClasses.BitBoardConsts.*;
import static utils.bot.DanielBotClasses.BitBoardBitManipulation.*;

public class BitBoardEvaluation {
	public static int evaluate() {
		int score = 0;
		
		long bitboard = 0;
		
		int piece, square;
		
		for (int bbPiece = P; bbPiece <= k; bbPiece++) {
			bitboard = bitboards[bbPiece];
			
			while (bitboard != 0) {
				piece = bbPiece;
				square = getLS1BIndex(bitboard);
				
				score += materialScore[bbPiece];
				
				bitboard &= ~(1L << (square));
			}
		}
		
		return (side == white) ? score : score * -1;
	}
}