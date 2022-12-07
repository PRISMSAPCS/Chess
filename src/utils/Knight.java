package utils;

import java.util.ArrayList;

public class Knight extends Piece{
	
	private boolean firstMove;
	
	public Knight(boolean color) {
		super(color);
		firstMove = true;
	}
	
	public ArrayList<int[]> getMoveSet(Piece[][] board, int x, int y) {
		ArrayList<int[]> moves = new ArrayList();
		return moves;
	}

	@Override
	public String getIconFile() {
		return getColor()? "n_w.png": "n_b.png";
	}
}