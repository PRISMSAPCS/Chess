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
		
		parseFen(startPosition);
		
		generateMoves();
		
		int move = encodeMove(e2, e4, P, Q, 1, 0, 0, 0);
		
		System.out.println(squareToCoordinates[getMoveSource(move)]);
		System.out.println(squareToCoordinates[getMoveTarget(move)]);
		System.out.println(asciiPieces[getMovePiece(move)]);
		System.out.println(asciiPieces[getMovePromoted(move)]);
		System.out.println(getMoveCapture(move) != 0);
		System.out.println(getMoveDouble(move) != 0);
		System.out.println(getMoveEnPassant(move) != 0);
		System.out.println(getMoveCastling(move) != 0);
	}
}