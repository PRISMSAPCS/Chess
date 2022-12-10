//Tom and Grayden
package utils;

import java.util.ArrayList;

public abstract class Piece implements Cloneable {
	private boolean color; // true=white, false=black

	public Piece(boolean color) {
		this.color = color;
	}

	public Piece(Piece other) {
		this.color = other.color;
	}

	public abstract ArrayList<int[]> getMoveSet(Piece[][] board, int x, int y);

	public abstract String getIconFile();

	public boolean getColor() {
		return color;
	}

	public void setColor(boolean color) {
		this.color = color;
	}

	public boolean getFirstMove(){
		return false;
	}

	@Override
	public Piece clone() {
		try {
			return (Piece) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
}
