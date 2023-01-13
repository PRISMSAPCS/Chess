package utils;

import java.util.ArrayList;

public class Rook extends Piece {
	public Rook(boolean color) {
		super(color);
	}
	
	public ArrayList<int[]> getMoveSet(Piece[][] board, int x, int y) {
		ArrayList<int[]> moves = new ArrayList<>();

		for(int i = 1; i <= 8; i++) {
			if(x + i >= board[0].length || (board[x + i][y] != null && board[x + i][y].getColor() == getColor()))
				break;
			int[] temp = {x + i, y};
			moves.add(temp);
			if(board[x + i][y] != null)
				break;
		}
		for(int i = 1; i <= 8; i++) {
			if(x - i < 0 || (board[x - i][y] != null && board[x - i][y].getColor() == getColor()))
				break;
			int[] temp = {x - i, y};
			moves.add(temp);
			if(board[x - i][y] != null)
				break;
		}
		for(int i = 1; i <= 8; i++) {
			if(y + i >= board[0].length || (board[x][y + i] != null && board[x][y + i].getColor() == getColor()))
				break;
			int[] temp = {x, y + i};
			moves.add(temp);
			if(board[x][y + i] != null)
				break;
		}
		for(int i = 1; i <= 8; i++) {
			if(y - i < 0 || (board[x][y - i] != null && board[x][y - i].getColor() == getColor()))
				break;
			int[] temp = {x, y - i};
			moves.add(temp);
			if(board[x][y - i] != null)
				break;
		}
		
		return moves;
	}

	@Override
	public String getIconFile() {
		return getColor()? "r_w.png": "r_b.png";
	}

	@Override
	public String toString() {
		return "rook";
	}
}