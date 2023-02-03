package utils.bot;

import java.util.ArrayList;

import utils.ChessBoard;
import utils.Move;
import utils.Pair;

public class pete_bot extends ChessBot{
	
	private ChessBoard theBoard;
	
	public pete_bot(ChessBoard board) {
		super(board);
		theBoard = board;
	}
	
	public ArrayList<Move> getAllLegalMoves(boolean side){
		ArrayList<Move> allLegalMoves = new ArrayList<Move>();
		for(int i=0;i<8;i++) {
			for(int j=0;j<8;j++) {
				if(theBoard.getBoard()[i][j]!=null) {
					if(theBoard.getBoard()[i][j].getColor()==side)allLegalMoves.addAll(theBoard.getLegalMoves(i, j, side));
				}
			}
		}
		
		return allLegalMoves;
	}
	
	public Move getMove() {
		ArrayList<Move> theBestMove = new ArrayList<Move>();
		
		ArrayList<Move> FirstMove = new ArrayList<Move>();
		ArrayList<Move> SecondMove = new ArrayList<Move>();
		ArrayList<Move> ThirdMove = new ArrayList<Move>();
		ArrayList<Move> FourthMove = new ArrayList<Move>();
		
		FirstMove = getAllLegalMoves(true);
		
		int min,minimax=-69420;
		
		for(Move theFirstMove : FirstMove) {
			min = 69420;
			
			//simulate 1st move
			theBoard.submitMove(theFirstMove);
			
			
			
			//How do I know if a piece is in attack?
			//find 2nd move
			SecondMove = getAllLegalMoves(false);
			
			//simulate 2nd move
			for(Move theSecondMove : SecondMove) {
				
				if(theSecondMove.getPiece()==null)continue;
				theBoard.submitMove(theSecondMove);
				if(theBoard.evaluate()<min)min=theBoard.evaluate();
				
				
				//find 3rd move
				ThirdMove = getAllLegalMoves(true);
				
				for(Move theThirdMove : ThirdMove) {
					//simulate 3rd move
					theBoard.submitMove(theThirdMove);
					//find 4th move
					FourthMove = getAllLegalMoves(false);
					for(Move theFourthMove : FourthMove) {
						theBoard.submitMove(theFourthMove);
						
						if(theBoard.evaluate()<min)min=theBoard.evaluate();
						if(theBoard.checked(true))min=-69420;
						
						
						theBoard.undoMove();
					}
					
					theBoard.undoMove();
				}
				
				theBoard.undoMove();
				
			}
			theBoard.undoMove();
			//
			System.out.print(theFirstMove.toString());
			System.out.println(" "+min);
		
			//
			
			if(min>minimax) {
				theBestMove = new ArrayList<Move>();
				minimax = min;
			}
			if(min>=minimax) {
				theBestMove.add(theFirstMove);
			}
			
		}
		
		int a = (int)(Math.random()*theBestMove.size());
		return theBestMove.get(a);
	}
	
	public String getName() {
		return "pete_bot";
	}
	
	public ChessBoard getBoard() {
		return theBoard;
	}
	
	
}
