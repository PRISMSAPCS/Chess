package utils;

import java.util.ArrayList;

public abstract class Piece {
	private String name;
	private boolean color; //true=white, false=black
	public String getName() {
		return name;
	}
	
	public ArrayList<int[]> getMoveSet(Piece[][] board){
		return null;
	}
	//getters and setters
	public void setName(String name) {
		this.name = name;
	}
	public boolean getColor() {
		return color;
	}
	public void setColor(boolean color) {
		this.color = color;
	}

	public abstract String getIconFile();
}
