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
				
				switch (piece) {
				case P: score += pawnScore[square]; break;
				case N: score += knightScore[square]; break;
				case B: score += bishopScore[square]; break;
				case R: score += rookScore[square]; break;
				case K: score += kingScore[square]; break;
				
				case p: score -= pawnScore[mirrorScore[square]]; break;
				case n: score -= knightScore[mirrorScore[square]]; break;
				case b: score -= bishopScore[mirrorScore[square]]; break;
				case r: score -= rookScore[mirrorScore[square]]; break;
				case k: score -= kingScore[mirrorScore[square]]; break;
				}
				
				bitboard &= ~(1L << (square));
			}
		}
		
		return (side == white) ? score : score * -1;
	}
}