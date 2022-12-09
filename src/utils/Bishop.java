package utils;

import java.util.ArrayList;

public class Bishop extends Piece {

	public Bishop(boolean color) {
		super(color);
	}

	public ArrayList<int[]> getMoveSet(Piece[][] board, int x, int y) {
		ArrayList<int[]> moves = new ArrayList<>();
		
		for(int i = 1; i <= 8; i++) {
			if(x + i >= board[0].length || y + i >= board[0].length || board[x + i][y + i] != null){
				if ( board[ x + i ][ y + i ].getColor() != this.getColor() ){
					int[] temp = {x + i, y + i};
					moves.add(temp);
				}
				break;
			}
			int[] temp = {x + i, y + i};
			moves.add(temp);
		}
		for(int i = 1; i <= 8; i++) {
			if(x + i >= board[0].length || y - i < 0 || board[x + i][y - i] != null){
				if ( board[ x + i ][ y - i ].getColor() != this.getColor() ){
					int[] temp = {x + i, y - i};
					moves.add(temp);
				}
				break;
			}
			int[] temp = {x + i, y - i};
			moves.add(temp);
		}
		for(int i = 1; i <= 8; i++) {
			if(x - i < 0 || y + i >= board[0].length || board[x - i][y + i] != null){
				if ( board[ x - i ][ y + i ].getColor() != this.getColor() ){
					int[] temp = {x - i, y + i};
					moves.add(temp);
				}
				break;
			}
			int[] temp = {x - i, y + i};
			moves.add(temp);
		}
		for(int i = 1; i <= 8; i++) {
			if(x - i < 0 || y - i < 0 || board[x - i][y - i] != null){
				if ( board[ x - i ][ y - i ].getColor() != this.getColor() ){
					int[] temp = {x - i, y - i};
					moves.add(temp);
				}
				break;
			}
			int[] temp = {x - i, y - i};
			moves.add(temp);
		}
		
		return moves;
	}

	@Override
	public String getIconFile() {
		return getColor()? "b_w.png": "b_b.png";
	}

}
