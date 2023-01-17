//Grayden
package utils;

import java.util.ArrayList;

public class Pawn extends Piece {
	public Pawn(boolean color) {
		super(color);
	}

	@Override
	//returns array of potential coordinates of moves
	public ArrayList<int[]> getMoveSet(Piece[][] board, int x, int y) {
		//moves list of potential spaces the pawn can move to
		ArrayList<int[]> moves = new ArrayList<>();
		int colorMult = this.getColor()? 1: -1;     // y direction this pawn moves
		//checks forward moves
		if((x+colorMult<ChessBoard.WIDTH&&x+colorMult>=0)&&board[x+colorMult][y]==null) {
			int[] temp = {x+colorMult,y};
			moves.add(temp);
			if((x+(2*colorMult)<board[0].length&&x+(2*colorMult)>=0)&&board[x+2*colorMult][y]==null && super.getFirstMove()) {
				int[] temp2 = {x+2*colorMult, y};
				moves.add(temp2);
			}
		}
		//checks cross attacks
		if(x+colorMult<board[0].length&&x+colorMult>=0) {
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
	
	public ArrayList<int[]> getAllThreats(Piece[][] board, int x, int y) {
		//moves list of potential spaces the pawn can move to
		ArrayList<int[]> moves = new ArrayList<>();
		int colorMult = this.getColor()? 1: -1;     // y direction this pawn moves
		//checks cross attacks
		if(x+colorMult<board[0].length&&x+colorMult>=0) {
			if((y+1<board.length&&y>=0)) {
				int[] temp = {x+colorMult,y+1};
				moves.add(temp);
			}
			if((y<board.length&&y-1>=0)) {
				int[] temp = {x+colorMult,y-1};
				moves.add(temp);
			}
		}
		return moves;
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