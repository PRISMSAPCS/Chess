package utils.bot;

import java.util.ArrayList;
import java.util.Collections;

import utils.ChessBoard;
import utils.Move;

public class GraydenBot extends ChessBot {
	long time;
	boolean thing;
	public GraydenBot(ChessBoard board) {
		super(board);
	}

	@Override
	public Move getMove() {
		thing = false;
		boolean side;
		int alpha = Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;
		ArrayList<Move> allMove = grabMoves(this.getBoard());
		ArrayList<Integer> moveS;
		ArrayList<Integer> moveSD4;
		moveS = new ArrayList<Integer>();
		moveSD4 = new ArrayList<Integer>();
		if(this.getBoard().getSide()) {
			side = true;
			alpha = Integer.MAX_VALUE;
			beta = Integer.MIN_VALUE;
		}
		else {
			side = false;
		}
		/*long time = System.currentTimeMillis();
		for(int y=1; y<21; y++) {
			if(System.currentTimeMillis()-time<5000) {
				moveS = new ArrayList<Integer>();
				for(Move x:allMove) {
					moveS.add(miniMaxOriginal(y, this.getBoard(), !side));
				}
			}
			else {
				System.out.println("Depth:"+Integer.toString(y));
				break;
			}
		}*/
		time = System.currentTimeMillis();
		for(Move x:allMove) {
			ChessBoard temp = new ChessBoard(this.getBoard());
			temp.submitMove(x);
			moveS.add(miniMax(2,temp,!side, alpha, beta));
		}
		for(Move x:allMove) {
			ChessBoard temp = new ChessBoard(this.getBoard());
			temp.submitMove(x);
			moveSD4.add(miniMax(4,temp,!side, alpha, beta));
		}
		if(!thing) {
			moveS = moveSD4;
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

	public static int evaluate() {
		return 0;
	}
	
public int miniMax(int depth,ChessBoard board, boolean color, int alpha, int beta) {
	if(System.currentTimeMillis()-time>5000) {
		thing = true;
		return 0;
	}
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
		//System.out.println(Moves: )
		for(Move x:allMove) {
			ChessBoard temp = new ChessBoard(board);
			temp.submitMove(x);
			int tempNum = miniMax(depth-1,temp,false, alpha, beta);
			if(this.getBoard().getSide()) {
				max = Math.max(max, tempNum);
				if(max > beta) {
					break;
				}
				alpha = Math.max(alpha, max);
			}
			else {
				max = Math.min(max, tempNum);
				if(max < beta) {
					break;
				}
				alpha = Math.min(alpha, max);
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
			int tempNum = miniMax(depth-1,temp,true, alpha, beta);
			if(this.getBoard().getSide()) {
				min = Math.min(min, tempNum);
				if(min < alpha) {
					break;
				}
				beta = Math.min(beta, min);
			}
			else {
				min = Math.max(min, tempNum);
				if(min > alpha) {
					break;
				}
				beta = Math.max(beta, min);
			}
		}
		return min;
	}
	}
public int miniMaxOriginal(int depth,ChessBoard board, boolean color) {
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
		//System.out.println(Moves: )
		for(Move x:allMove) {
			ChessBoard temp = new ChessBoard(board);
			temp.submitMove(x);
			int tempNum = miniMaxOriginal(depth-1,temp,false);
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
			int tempNum = miniMaxOriginal(depth-1,temp,true);
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

