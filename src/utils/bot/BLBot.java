package utils.bot;

import java.util.concurrent.atomic.AtomicIntegerArray;

import utils.ChessBoard;
import utils.Move;
import utils.Piece;

public class BLBot extends ChessBot {
	
	int depth = 4;
	Move bestMove = null;
	
	public BLBot(ChessBoard board) {
		super(board);
	}
	
	public int bestMove(ChessBoard curBoard, int d, boolean f) {
		if(d > depth)
			return curBoard.evaluate();
		int minVal = Integer.MAX_VALUE, maxVal = Integer.MIN_VALUE;
		Move theMove = null;
		boolean haveSon = false;
		for(int i = 0; i < 8; i++)
			for(int j = 0; j < 8; j++) {
				if(curBoard.getBoard()[i][j] == null)
					continue;
				for(Move move: curBoard.getLegalMoves(i, j, true)) {
					if(curBoard.getBoard()[move.getStart().first][move.getStart().second] == null || curBoard.getBoard()[move.getStart().first][move.getStart().second].getColor() != curBoard.getSide())
						continue;
					haveSon = true;
					ChessBoard tempBoard = new ChessBoard(curBoard);
					tempBoard.submitMove(move);
					if(getBoard().getSide() == true) {
						if(f == true) {
							int temp = bestMove(tempBoard, d + 1, false);
							if(temp > maxVal) {
								maxVal = temp;
								theMove = move;
							}
						}
						else {
							int temp = bestMove(tempBoard, d + 1, true);
							if(temp < minVal) {
								minVal = temp;
								theMove = move;
							}
						}
					}
					else {
						if(f == false) {
							int temp = bestMove(tempBoard, d + 1, true);
							if(temp > maxVal) {
								maxVal = temp;
								theMove = move;
							}
						}
						else {
							int temp = bestMove(tempBoard, d + 1, false);
							if(temp < minVal) {
								minVal = temp;
								theMove = move;
							}
						}
					}
				}
			}
		if(d == 1)
			bestMove = theMove;
		if(haveSon == false)
			return curBoard.evaluate();
		if(getBoard().getSide() == true) {
			if(f == true)
				return maxVal;
			return minVal;
		}
		if(f == false)
			return maxVal;
		return minVal;
	}
	
	public Move getMove() {
		bestMove(getBoard(), 1, true);
		return bestMove;
	}
	
	
	public String getName() {
		return "BLChessBoard";
	}
	
	
}
