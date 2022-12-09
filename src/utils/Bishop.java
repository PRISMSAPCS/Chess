package utils;

public class Bishop extends Piece {
  
     public ArrayList<int[]> getMoveSet(Piece[][] board, int x, int y) {
     
	     ArrayList<int[]> moves = new ArrayList<>();
       int dx,dy;
       int[][] positions = {{1,2},{2,1},{1,-2},{-2,1},{2,-1},{-1,2},{-1,-2},{-2,-1}}
       for(int i=0;i<8;i++){
         dx = positions[i][0];
         dy = positions[i][1];
         if( x+dx<8 && x+dx>=0 && y+dy<8 && y+dy>=0 ){
           if(board[x+dx][y+dy]==null){
             int[] temp = {x+dx,y+dy};
             moves.add(temp);
           }
         }
       }
		
		
		return moves;
	}
}
