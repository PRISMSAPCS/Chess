package utils;

import java.util.ArrayList;

public class Knight extends Piece {
	
	public Knight(boolean color) {
		super(color);
	}

	@Override
	public ArrayList<int[]> getMoveSet(Piece[][] board, int x, int y) {
		ArrayList<int[]> moves = new ArrayList<>();
		int dx,dy;
		int[][] positions = {{1,2},{2,1},{1,-2},{-2,1},{2,-1},{-1,2},{-1,-2},{-2,-1}};
		for(int i=0;i<8;i++){
			dx = positions[i][0];
			dy = positions[i][1];
			if(x+dx < ChessBoard.WIDTH && x+dx >= 0 && y+dy < ChessBoard.HEIGHT && y+dy >= 0){
				if(board[x+dx][y+dy]==null || board[x+dx][y+dy].getColor() != getColor()) {
					int[] temp = {x + dx, y + dy};
					moves.add(temp);
				}
           }
		}
		return moves;
	}
	
	public ArrayList<int[]> getAllThreats(Piece[][] board, int x, int y) {
		ArrayList<int[]> moves = new ArrayList<>();
		int dx,dy;
		int[][] positions = {{1,2},{2,1},{1,-2},{-2,1},{2,-1},{-1,2},{-1,-2},{-2,-1}};
		for(int i=0;i<8;i++){
			dx = positions[i][0];
			dy = positions[i][1];
			if(x+dx < ChessBoard.WIDTH && x+dx >= 0 && y+dy < ChessBoard.HEIGHT && y+dy >= 0){
				int[] temp = {x + dx, y + dy};
				moves.add(temp);
           }
		}
		return moves;
	}

	@Override
	public String getIconFile() {
		return getColor()? "n_w.png": "n_b.png";
	}

	@Override
	public String toString() {
		return "knight";
	}
}
