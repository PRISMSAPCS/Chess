package utils;

import java.util.ArrayList;

public class Piece2 {
	private String name;
	private boolean color; //true=white, false=black
	public String getName() {
		return name;
	}
	
	public ArrayList<int[]> getMoveSet(Piece2[][] board){
		return null;
	}
	//getters and setters
	public void setName(String name) {
		this.name = name;
	}
	public boolean isColor() {
		return color;
	}
	public void setColor(boolean color) {
		this.color = color;
	}
}
