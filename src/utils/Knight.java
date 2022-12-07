package utils;

import java.util.ArrayList;

public class Knight extends Piece{
	
	private boolean firstMove;
	
	public Knight(boolean col) {
		this.setName("knight");
		this.setColor(col);
		firstMove = true;
	}
	
	public ArrayList<int[]> getMoveSet(Piece[][] board, int x, int y) {
		ArrayList<int[]> moves = new ArrayList();
		return moves;
	}

	@Override
	public String getIconFile() {
		return getColor()? "p_w.png": "p_b.png";
	}
}