package utils;

import java.util.ArrayList;

public class Queen extends Piece {
	
	//Ben Li Edit
	
	private boolean firstMove;
	
	public Queen(boolean color) {
		super(color);
		firstMove = true;
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
		
		for(int i = 1; i <= 8; i++) {
			if(x + i >= board[0].length || y + i >= board[0].length || (board[x + i][y + i] != null && board[x + i][y + i].getColor() == getColor()))
				break;
			int[] temp = {x + i, y + i};
			moves.add(temp);
			if(board[x + i][y + i] != null)
				break;
		}
		for(int i = 1; i <= 8; i++) {
			if(x + i >= board[0].length || y - i < 0 || (board[x + i][y - i] != null && board[x + i][y - i].getColor() == getColor()))
				break;
			int[] temp = {x + i, y - i};
			moves.add(temp);
			if(board[x + i][y - i] != null)
				break;
		}
		for(int i = 1; i <= 8; i++) {
			if(x - i < 0 || y + i >= board[0].length || (board[x - i][y + i] != null && board[x - i][y + i].getColor() == getColor()))
				break;
			int[] temp = {x - i, y + i};
			moves.add(temp);
			if(board[x - i][y + i] != null)
				break;
		}
		for(int i = 1; i <= 8; i++) {
			if(x - i < 0 || y - i < 0 || (board[x - i][y - i] != null && board[x - i][y - i].getColor() == getColor()))
				break;
			int[] temp = {x - i, y - i};
			moves.add(temp);
			if(board[x - i][y - i] != null)
				break;
		}
		
		return moves;
	}
	
	public ArrayList<int[]> getMoveSet(Piece[][] board, int x, int y, boolean pinnedBottomLeftTopRight, boolean pinnedBottomRightTopLeft, boolean pinnedHorizontal, boolean pinnedVertical) {
		ArrayList<int[]> moves = new ArrayList<>();
		
		if (pinnedHorizontal) {
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
		} else if (pinnedVertical) {
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
		} else if (pinnedBottomRightTopLeft) {
			for(int i = 1; i <= 8; i++) {
				if(x + i >= board[0].length || y + i >= board[0].length || (board[x + i][y + i] != null && board[x + i][y + i].getColor() == getColor()))
					break;
				int[] temp = {x + i, y + i};
				moves.add(temp);
				if(board[x + i][y + i] != null)
					break;
			}
			for(int i = 1; i <= 8; i++) {
				if(x - i < 0 || y - i < 0 || (board[x - i][y - i] != null && board[x - i][y - i].getColor() == getColor()))
					break;
				int[] temp = {x - i, y - i};
				moves.add(temp);
				if(board[x - i][y - i] != null)
					break;
			}
		} else if (pinnedBottomLeftTopRight) {
			for(int i = 1; i <= 8; i++) {
				if(x - i < 0 || y + i >= board[0].length || (board[x - i][y + i] != null && board[x - i][y + i].getColor() == getColor()))
					break;
				int[] temp = {x - i, y + i};
				moves.add(temp);
				if(board[x - i][y + i] != null)
					break;
			}
			for(int i = 1; i <= 8; i++) {
				if(x + i >= board[0].length || y - i < 0 || (board[x + i][y - i] != null && board[x + i][y - i].getColor() == getColor()))
					break;
				int[] temp = {x + i, y - i};
				moves.add(temp);
				if(board[x + i][y - i] != null)
					break;
			}
		}
		
		return moves;
	}

	@Override
	public String getIconFile() {
		return getColor()? "q_w.png": "q_b.png";
	}

	@Override
	public String toString() {
		return "queen";
	}
}