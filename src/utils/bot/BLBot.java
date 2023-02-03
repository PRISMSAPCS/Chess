package utils.bot;

import java.util.concurrent.atomic.AtomicIntegerArray;

import utils.ChessBoard;
import utils.Move;
import utils.Piece;

public class BLBot extends ChessBot {
	
	int depth = 3;
	Move bestMove = null;
	int initialSide;
	
	public BLBot(ChessBoard board) {
		super(board);
	}
	
	public BLBot(ChessBoard board, boolean side) {
		super(board);
		if(side == true)
			initialSide = 1;
		else
			initialSide = -1;
	}
	
	public int bestMove(ChessBoard curBoard, int d, int a, int b, int f) {
		if(d > depth)
			return curBoard.evaluate();
		int maxVal = Integer.MIN_VALUE, temp;
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
					temp = -bestMove(tempBoard, d + 1, -b, -a, -f);
					if(temp > maxVal)
					{
						maxVal = temp;
						theMove = move;
					}
					a = Integer.max(a, maxVal);
					if(a >= b)
						break;
				}
			}
		if(d == 1)
			bestMove = theMove;
		if(haveSon == false)
			return curBoard.evaluate();
		return maxVal;
	}
	
	public Move getMove() {
		bestMove(getBoard(), 1, Integer.MIN_VALUE, Integer.MAX_VALUE, initialSide);
		return bestMove;
	}
	
	
	public String getName() {
		return "BLChessBoard";
	}
	
	
}
