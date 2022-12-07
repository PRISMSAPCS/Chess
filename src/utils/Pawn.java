package utils;

import java.util.ArrayList;

public class Pawn extends Piece {
	
	private boolean firstMove;
	
	public Pawn(boolean col) {
		this.setName("pawn");
		this.setColor(col);
		firstMove = true;
	}
	
	public ArrayList<int[]> getMoveSet(Piece[][] board, int x, int y) {
		ArrayList<int[]> moves = new ArrayList();
		int colorMult = 0;
		//gives direction for color
		if(this.getColor()==true) {
			colorMult = 1;
		}
		else {colorMult =-1;}
		//checks forward moves
		if((y+colorMult+1<board[0].length&&y+colorMult>=0)&&board[x][y+colorMult]==null) {
			int[] temp = {x,y+colorMult};
			moves.add(temp);
			if((y+(2*colorMult)+1<board[0].length&&y+(2*colorMult)>=0)&&board[x][y+(2*colorMult)]==null && firstMove ) {
				temp[0] = x;
				temp[1] = y+(2*colorMult);
				moves.add(temp);
			}
		}
		//checks cross attacks
		if(y+colorMult+1<board[0].length||y+colorMult>=0) {
			if((x+1<board.length&&x>=0)&&board[x+1][y+colorMult]!=null&&board[x+1][y+colorMult].getColor()!=this.getColor()) {
				int[] temp = {x+1,y+colorMult};
				moves.add(temp);
			}
			if((x+1<board.length&&x>=0)&&board[x-1][y+colorMult]!=null&&board[x-1][y+colorMult].getColor()!=this.getColor()) {
				int[] temp = {x-1,y+colorMult};
				moves.add(temp);
			}
		}
		return moves;
	}

	@Override
	public String getIconFile() {
		return getColor()? "p_w.png": "p_b.png";
	}
}