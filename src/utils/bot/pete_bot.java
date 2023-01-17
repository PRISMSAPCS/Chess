package utils.bot;

import java.util.ArrayList;

import utils.ChessBoard;
import utils.Move;

public abstract class pete_bot {
	
	
	public Move getBestMove(ChessBoard theBoard) {
		ArrayList<Move> theBestMove = new ArrayList<Move>();
		
		ArrayList<Move> FirstMove = new ArrayList<Move>();
		ArrayList<Move> SecondMove = new ArrayList<Move>();
		for(int i=0;i<8;i++) {
			for(int j=0;j<8;j++) {
				if(theBoard.getBoard()[i][j]!=null) {
					if(theBoard.getBoard()[i][j].getColor())FirstMove.addAll(theBoard.getLegalMoves(i, j, true));
				}
			}
		}
		ChessBoard Board1,Board2;
		int min,minimax=-69420;
		
		for(Move theFirstMove : FirstMove) {
			//simulate 1st move
			Board1 = theBoard;
			Board1.submitMove(theFirstMove);
			//find 2nd move
			for(int i=0;i<8;i++) {
				for(int j=0;j<8;j++) {
					if(theBoard.getBoard()[i][j]!=null) {
						if(Board1.getBoard()[i][j].getColor())SecondMove.addAll(Board1.getLegalMoves(i, j, false));
					}
				}
			}
			min = 69420;
			//simulate 2nd move
			for(Move theSecondMove : SecondMove) {
				Board2 = Board1;
				Board2.submitMove(theSecondMove);
				if(Board2.evaluate()<min)min=theBoard.evaluate();
			}
			if(min<minimax)theBestMove = new ArrayList<Move>();
			if(min<=minimax)theBestMove.add(theFirstMove);
		}
		
		int a = (int)(Math.random()*theBestMove.size());
		return theBestMove.get(a);
	}
}
