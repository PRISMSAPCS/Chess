package utils.bot;

import java.util.ArrayList;
import java.util.Collections;
import utils.Piece;
import utils.ChessBoard;
import utils.King;
import utils.Move;
import utils.Pawn;

public class GraydenBot extends ChessBot {
	long time;
	boolean thing;
	int test1;
	int test2;
	int mode;
	boolean side;
	public GraydenBot(ChessBoard board,int mode) {
		super(board);
		this.mode = mode;
	}

	@Override
	public Move getMove() {
		thing = false;
		int alpha = Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;
		ArrayList<Move> allMove = grabMoves(this.getBoard());
		ArrayList<Integer> moveS;
		ArrayList<Integer> moveSD;
		moveS = new ArrayList<Integer>();
		moveSD = new ArrayList<Integer>();
		if(this.getBoard().getSide()) {
			side = true;
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
		if(mode==1) {
			time = System.currentTimeMillis();
			for(int x=2; x<20; x+=2) {
				for(Move y:allMove) {
					ChessBoard temp = new ChessBoard(this.getBoard());
					temp.submitMove(y);
					moveSD.add(negamax(x, temp, !side/*, alpha, beta*/));
				}
				if(System.currentTimeMillis()-time>5000) {
					System.out.println("DepthGB:"+Integer.toString(x-1));
					break;
				}
				else {
					moveS = moveSD;
					moveSD = new ArrayList<Integer>();
				}
			}
			System.out.println("NegaMax = Cuts:"+Integer.toString(test1)+" Evals:"+Integer.toString(test2)+" Color:"+this.getBoard().getSide());
		}
		else {
			time = System.currentTimeMillis();
			for(int x=2; x<20; x+=2) {
				for(Move y:allMove) {
					ChessBoard temp = new ChessBoard(this.getBoard());
					temp.submitMove(y);
					moveSD.add(miniMaxOriginal(x, temp, !side/*, alpha, beta*/));
				}
				if(System.currentTimeMillis()-time>5000) {
					System.out.println("DepthGB:"+Integer.toString(x-1));
					break;
				}
				else {
					moveS = moveSD;
					moveSD = new ArrayList<Integer>();
				}
			}
			System.out.println("miniMax = Cuts:"+Integer.toString(test1)+" Evals:"+Integer.toString(test2)+" Color:"+this.getBoard().getSide());
		}
		return allMove.get(moveS.indexOf(Collections.max(moveS)));
		/*if(this.getBoard().getSide()) {
			return allMove.get(moveS.indexOf(Collections.max(moveS)));
		}
		else {
			return allMove.get(moveS.indexOf(Collections.min(moveS)));
		}*/
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
	
public int negamax(int depth,ChessBoard board, boolean color/*, int alpha, int beta*/) {
	if(System.currentTimeMillis()-time>5000) {
		return 0;
	}
	if(depth == 0) {
		test2++;
		double evalMod = 0;
		for(int x = 0; x<8; x++) {
			for(int y = 0; y<8; y++) {
				if(board.getBoard()[x][y] instanceof Pawn) {
					if(board.getBoard()[x+1][y+1] instanceof Pawn && board.getBoard()[x+1][y+1].getColor()==board.getBoard()[x][y].getColor()) {
						if(board.getBoard()[x][y].getColor()) {
							evalMod += 5;
						}
						else {
							evalMod -= 5;
						}
					}
					if(board.getBoard()[x-1][y+1] instanceof Pawn && board.getBoard()[x-1][y+1].getColor()==board.getBoard()[x][y].getColor()) {
						if(board.getBoard()[x][y].getColor()) {
							evalMod += 5;
						}
						else {
							evalMod -= 5;
						}
					}
					if(board.getBoard()[x+1][y-1] instanceof Pawn && board.getBoard()[x+1][y-1].getColor()==board.getBoard()[x][y].getColor()) {
						if(board.getBoard()[x][y].getColor()) {
							evalMod += 5;
						}
						else {
							evalMod -= 5;
						}
					}
					if(board.getBoard()[x-1][y-1] instanceof Pawn && board.getBoard()[x-1][y-1].getColor()==board.getBoard()[x][y].getColor()) {
						if(board.getBoard()[x][y].getColor()) {
							evalMod += 5;
						}
						else {
							evalMod -= 5;
						}
					}
				}
			}
		}
		if(this.getBoard().getSide()) {
			return (int) (board.evaluate() + evalMod);
		}
		else {
			return (int) ((board.evaluate()*-1) + evalMod);
		}
	}
	int value = Integer.MIN_VALUE;
	ArrayList<Move> allM = new ArrayList<Move>(this.grabMoves(board));
	for(Move x:allM) {
		ChessBoard temp = new ChessBoard(board);
		temp.submitMove(x);
		value = Math.max(value, -negamax(depth-1, temp, !color/*, -beta, -alpha*/));
		/*alpha = Math.max(alpha, value);
		if(alpha >= beta) {
			test1++;
			break;
		}*/
	}
	return value;
	}
public int miniMaxOriginal(int depth,ChessBoard board, boolean color) {
	if(System.currentTimeMillis()-time>5000) {
		return 0;
	}
	if(depth == 0) {
		int eval = board.evaluate();
		double evalMod = 0;
		for(int x = 0; x<8; x++) {
			for(int y = 0; y<8; y++) {
				if(board.getBoard()[x][y] instanceof Pawn) {
					if(board.getBoard()[x+1][y+1] instanceof Pawn && board.getBoard()[x+1][y+1].getColor()==board.getBoard()[x][y].getColor()) {
						if(board.getBoard()[x][y].getColor()) {
							evalMod += 5;
						}
						else {
							evalMod -= 5;
						}
					}
					if(board.getBoard()[x-1][y+1] instanceof Pawn && board.getBoard()[x-1][y+1].getColor()==board.getBoard()[x][y].getColor()) {
						if(board.getBoard()[x][y].getColor()) {
							evalMod += 5;
						}
						else {
							evalMod -= 5;
						}
					}
					if(board.getBoard()[x+1][y-1] instanceof Pawn && board.getBoard()[x+1][y-1].getColor()==board.getBoard()[x][y].getColor()) {
						if(board.getBoard()[x][y].getColor()) {
							evalMod += 5;
						}
						else {
							evalMod -= 5;
						}
					}
					if(board.getBoard()[x-1][y-1] instanceof Pawn && board.getBoard()[x-1][y-1].getColor()==board.getBoard()[x][y].getColor()) {
						if(board.getBoard()[x][y].getColor()) {
							evalMod += 5;
						}
						else {
							evalMod -= 5;
						}
					}
				}
			}
			eval += (int)evalMod;
		}
			evalMod = 0;
			double endGameW;
			int piece = 0;
			int opKingRank = 0;
			int opKingFile = 0;
			int frKingRank = 0;
			int frKingFile = 0;
			for(int x = 0; x<8; x++) {
				for(int y = 0; y<8; y++) {
					if(this.getBoard().getBoard()[x][y] instanceof Piece && this.getBoard().getBoard()[x][y].getColor() != this.side) {
						piece += 1;
					}
					else if(this.getBoard().getBoard()[x][y] instanceof King) {
						opKingRank = x;
						opKingFile = y;
					}
					else if(this.getBoard().getBoard()[x][y] instanceof King && this.getBoard().getBoard()[x][y].getColor() == this.side) {
						frKingRank = x;
						frKingFile = y;
					}
				}
			}
			endGameW = (1/16)*piece;
			int opKingCentRank =  Math.max(3 - opKingRank, opKingRank - 4);
			int opKingCentFile = Math.max(3 - opKingFile, opKingFile - 4);
			int opKingDisCent = opKingCentFile + opKingCentRank;
			evalMod += opKingDisCent;
			int disKingRank = Math.abs(frKingRank - opKingRank);
			int disKingFile = Math.abs(frKingFile - opKingFile);
			evalMod += 14 - (disKingRank+disKingFile);
			eval += (evalMod*10*endGameW);
			return eval;
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

