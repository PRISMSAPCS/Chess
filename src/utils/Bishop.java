package utils;

public class Bishop extends Piece {
  
     public ArrayList<int[]> getMoveSet(Piece[][] board, int x, int y) {
		ArrayList<int[]> moves = new ArrayList<>();
		
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
	
}
