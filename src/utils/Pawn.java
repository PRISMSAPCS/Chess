//Grayden
package utils;

import java.util.ArrayList;

public class Pawn extends Piece {
	
	private boolean firstMove;
	
	public Pawn(boolean color) {
		super(color);
		firstMove = true;
	}

	@Override
	public ArrayList<int[]> getMoveSet(Piece[][] board, int x, int y) {
		ArrayList<int[]> moves = new ArrayList<>();
		int colorMult = this.getColor()? 1: -1;     // y direction this pawn moves
		//checks forward moves
		if((x+colorMult<ChessBoard.WIDTH&&x+colorMult>=0)&&board[x+colorMult][y]==null) {
			int[] temp = {x+colorMult,y};
			moves.add(temp);
			if((x+(2*colorMult)<board[0].length&&x+(2*colorMult)>=0)&&board[x+2*colorMult][y]==null && firstMove ) {
				int[] temp2 = {x+2*colorMult, y};
				moves.add(temp2);
			}
		}
		//checks cross attacks
		if(x+colorMult+1<board[0].length||x+colorMult>=0) {
			if((y+1<board.length&&y>=0)&&board[x+colorMult][y+1]!=null&&board[x+colorMult][y+1].getColor()!=this.getColor()) {
				int[] temp = {x+colorMult,y+1};
				moves.add(temp);
			}
			if((y<board.length&&y-1>=0)&&board[x+colorMult][y-1]!=null&&board[x+colorMult][y-1].getColor()!=this.getColor()) {
				int[] temp = {x+colorMult,y-1};
				moves.add(temp);
			}
		}
		return moves;
	}

	public void cancelFirstMove() {
		this.firstMove = false;
	}
	
	public boolean getFirstMove() {
		return firstMove;
	}

	@Override
	public String getIconFile() {
		return getColor()? "p_w.png": "p_b.png";
	}

	@Override
	public String toString() {
		return "pawn";
	}
}