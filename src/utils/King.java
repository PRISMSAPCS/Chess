package utils;

import java.util.ArrayList;

public class King extends Piece {
    public static final int[][] MOVE_SET = new int[8][];
    static {
        MOVE_SET[0] = new int[] {0, 1};
        MOVE_SET[1] = new int[] {0, -1};
        MOVE_SET[2] = new int[] {1, 0};
        MOVE_SET[3] = new int[] {-1, 0};
        MOVE_SET[4] = new int[] {1, 1};
        MOVE_SET[5] = new int[] {1, -1};
        MOVE_SET[6] = new int[] {-1, 1};
        MOVE_SET[7] = new int[] {-1, -1};
    }

    public King(boolean color) {
        super(color);
    }

    @Override
    public ArrayList<int[]> getMoveSet(Piece[][] board, int x, int y) {
        ArrayList<int[]> ans = new ArrayList<>();
        for(int i = 0; i < 8; i++) {
            int endX = x + MOVE_SET[i][0], endY = y + MOVE_SET[i][1];
            if(endX >= 0 && endX < ChessBoard.WIDTH && endY >= 0 && endY < ChessBoard.HEIGHT
                && (board[endX][endY] == null || board[endX][endY].getColor() != this.getColor())) {
                ans.add(new int[] {x + MOVE_SET[i][0], y + MOVE_SET[i][1]});
            }
        }

        return ans;
    }
    
    public ArrayList<int[]> getAllThreats(Piece[][] board, int x, int y) {
        ArrayList<int[]> ans = new ArrayList<>();
        for(int i = 0; i < 8; i++) {
            int endX = x + MOVE_SET[i][0], endY = y + MOVE_SET[i][1];
            if(endX >= 0 && endX < ChessBoard.WIDTH && endY >= 0 && endY < ChessBoard.HEIGHT) {
                ans.add(new int[] {x + MOVE_SET[i][0], y + MOVE_SET[i][1]});
            }
        }

        return ans;
    }
    
    @Override
    public String getIconFile() {
        return getColor()? "k_w.png": "k_b.png";
    }
}
