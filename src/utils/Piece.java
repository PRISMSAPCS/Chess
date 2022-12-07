//Tom and Grayden
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
	
	 public String getIconFile() {
	        if(color) {
	            // black pieces
	            return switch(this.name) {
	                case "pawn" -> "p_b.png";
	                case "bishop" -> "b_b.png";
	                case "knight" -> "n_b.png";
	                case "rook" -> "r_b.png";
	                case "queen" -> "q_b.png";
	                case "king" -> "k_b.png";
	                default -> throw new IllegalArgumentException("Unexpected value: " + this.name);
	            };
	        } else {
	            // white pieces
	            return switch(this.name) {
	                case "pawn" -> "p_w.png";
	                case "bishop" -> "b_w.png";
	                case "knight" -> "n_w.png";
	                case "rook" -> "r_w.png";
	                case "queen" -> "q_w.png";
	                case "king" -> "k_w.png";
	                default -> throw new IllegalArgumentException("Unexpected value: " + this.name);
	            };
	        }
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

}
