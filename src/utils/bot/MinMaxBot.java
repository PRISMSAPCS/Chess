package utils.bot;
import utils.*;


public class MinMaxBot extends ChessBot {

    public MinMaxBot(ChessBoard board) {
        super(board);
    }

    public Move getMove() {
        board = new ChessBoard(getBoard());
        side = board.getSide();
        // positive in eval means white side is better, vice versa

        int curMax = Integer.MIN_VALUE;
        int curMin = Integer.MAX_VALUE;
        Move bestMove = null;

        // if white, maximize opponent's min
        // if black, minimize opponent's max
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board.getBoard()[i][j] == null || board.getBoard()[i][j].getColor() != side)
                    continue;
                for (Move curMove : board.getLegalMoves(i, j, true)) {
                    board.submitMove(curMove);
                    if (side == true){
                        int curEval = minimizeOpponentMax(searchDep - 1, board); // the opponent wants to minimize your max
                        if (curEval > curMax){
                            curMax = curEval;
                            bestMove = curMove;
                        }
                    }else{
                        int curEval = maximizeOpponentMin(searchDep - 1, board); // the opponent wants to maximize your min
                        if (curEval < curMin){
                            curMin = curEval;
                            bestMove = curMove;
                        }
                    }
                    board.undoMove();           
                }
            }
        }
        return bestMove;
    }

    private int maximizeOpponentMin(int dep, ChessBoard b) {
        if (dep == 0)
            return b.evaluate();
        int curMax = Integer.MIN_VALUE;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (b.getBoard()[i][j] == null || b.getBoard()[i][j].getColor() != side)
                    continue;
                for (Move curMove : b.getLegalMoves(i, j, true)) {
                    b.submitMove(curMove);
                    int curEval = minimizeOpponentMax(dep - 1, b); // the opponent wants to minimize your max
                    curMax = Math.max(curMax, curEval);
                    b.undoMove();
                }
            }
        }
        return curMax;
    }

    private int minimizeOpponentMax(int dep, ChessBoard b) {
        if (dep == 0)
            return b.evaluate();
        int curMin = Integer.MAX_VALUE;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (b.getBoard()[i][j] == null || b.getBoard()[i][j].getColor() != side)
                    continue;
                for (Move curMove : b.getLegalMoves(i, j, true)) {
                    b.submitMove(curMove);
                    int curEval = minimizeOpponentMax(dep - 1, b); // the opponent wants to minimize your max
                    curMin = Math.min(curMin, curEval);
                    b.undoMove();
                }
            }
        }
        return curMin;
    }

    public String getName() {
        return "tony's min max bot";
    }

    private ChessBoard board;
    private boolean side; // 0 -> black, 1 -> white
    private int searchDep = 4;

}
