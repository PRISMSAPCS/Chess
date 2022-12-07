//Tom and Grayden
package utils;

import java.util.ArrayList;

public abstract class Piece {
	private boolean color; //true=white, false=black

	public Piece(boolean color) {
		this.color = color;
	}
	
	public ArrayList<int[]> getMoveSet(Piece[][] board, int x, int y){
		return null;
	}
	
	public abstract String getIconFile();

	public boolean getColor() {
		return color;
	}
	public void setColor(boolean color) {
		this.color = color;
	}
}
