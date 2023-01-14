package utils.bot;

import java.util.ArrayList;
import java.util.Collections;

import utils.ChessBoard;
import utils.Move;

public class GraydenBot extends ChessBot {
	
	public GraydenBot(ChessBoard board) {
		super(board);
	}

	@Override
	public Move getMove() {
		boolean side;
		ArrayList<Move> allMove = grabMoves(this.getBoard());
		ArrayList<Integer> moveS;
		moveS = new ArrayList<Integer>();
		if(this.getBoard().getSide()) {
			side = true;
		}
		else {
			side = false;
		}
		for(Move x:allMove) {
			moveS.add(miniMax(3, this.getBoard(), side));
		}
		if(this.getBoard().getSide()) {
			return allMove.get(moveS.indexOf(Collections.max(moveS)));
		}
		else {
			return allMove.get(moveS.indexOf(Collections.min(moveS)));
		}
	}
	
	public ArrayList<Move> grabMoves(ChessBoard board) { // Author: Daniel - gets a random legal move
			ArrayList<Move> allLegalMoves = new ArrayList<Move>();
			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {
					if (board.getBoard()[i][j] != null && board.getBoard()[i][j].getColor() == board.getSide()) {
						ArrayList<Move> temp = board.getLegalMoves(i, j, true);
						for (Move x : temp) {
							allLegalMoves.add(x);
						}
					}
				}
			}
			return allLegalMoves;
	}

	@Override
	public String getName() {
		return "GraydenBot";
	}

	public int miniMax(int depth,ChessBoard board, boolean color) {
	if(depth == 0) {
		return board.evaluate();
	}
	
	if(color==this.getBoard().getSide()) {
		int max;
		if(this.getBoard().getSide()) {
			max = Integer.MIN_VALUE;
		}
		else {
			max = Integer.MAX_VALUE;
		}
		ArrayList<Move> allMove = grabMoves(board);
		for(Move x:allMove) {
			ChessBoard temp = new ChessBoard(board);
			temp.submitMove(x);
			int tempNum = miniMax(depth-1,temp,false);
			if(this.getBoard().getSide()) {
				max = Math.max(max, tempNum);
			}
			else {
				max = Math.min(max, tempNum);
			}
		}
		return max;
	}
	else {
		int min;
		if(this.getBoard().getSide()) {
			min = Integer.MAX_VALUE;
		}
		else {
			min = Integer.MIN_VALUE;
		}
		ArrayList<Move> allMove = grabMoves(board);
		for(Move x:allMove) {
			ChessBoard temp = new ChessBoard(board);
			temp.submitMove(x);
			int tempNum = miniMax(depth-1,temp,true);
			if(this.getBoard().getSide()) {
				min = Math.min(min, tempNum);
			}
			else {
				min = Math.max(min, tempNum);
			}
		}
		return min;
	}
	}
}

