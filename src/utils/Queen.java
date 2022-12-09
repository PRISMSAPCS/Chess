package utils;

import java.util.ArrayList;

public class Queen extends Piece {
	
	private boolean firstMove;
	
	public Queen(boolean color) {
		super(color);
		firstMove = true;
	}
	
	public ArrayList<int[]> getMoveSet(Piece[][] board, int x, int y) {
		ArrayList<int[]> moves = new ArrayList<>();

		// TODO (check for capturing enemy pieces)
		for(int i = 1; i <= 8; i++) {
			if(x + i >= board[0].length || board[x + i][y] != null)
				break;
			int[] temp = {x + i, y};
			moves.add(temp);
		}
		for(int i = 1; i <= 8; i++) {
			if(x - i < 0 || board[x - i][y] != null)
				break;
			int[] temp = {x - i, y};
			moves.add(temp);
		}
		for(int i = 1; i <= 8; i++) {
			if(y + i >= board[0].length || board[x][y + i] != null)
				break;
			int[] temp = {x, y + i};
			moves.add(temp);
		}
		for(int i = 1; i <= 8; i++) {
			if(y - i < 0 || board[x][y - i] != null)
				break;
			int[] temp = {x, y - i};
			moves.add(temp);
		}
		
		for(int i = 1; i <= 8; i++) {
			if(x + i >= board[0].length || y + i >= board[0].length || board[x + i][y + i] != null)
				break;
			int[] temp = {x + i, y + i};
			moves.add(temp);
		}
		for(int i = 1; i <= 8; i++) {
			if(x + i >= board[0].length || y - i < 0 || board[x + i][y - i] != null)
				break;
			int[] temp = {x + i, y - i};
			moves.add(temp);
		}
		for(int i = 1; i <= 8; i++) {
			if(x - i < 0 || y + i >= board[0].length || board[x - i][y + i] != null)
				break;
			int[] temp = {x - i, y + i};
			moves.add(temp);
		}
		for(int i = 1; i <= 8; i++) {
			if(x - i < 0 || y - i < 0 || board[x - i][y - i] != null)
				break;
			int[] temp = {x - i, y - i};
			moves.add(temp);
		}
		
		return moves;
	}

	@Override
	public String getIconFile() {
		return getColor()? "q_w.png": "q_b.png";
	}
}