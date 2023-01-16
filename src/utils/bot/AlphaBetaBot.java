package utils.bot;

import utils.*;
import utils.bot.AlphaBetaPVSTT.*;
import java.util.ArrayList;
import java.util.Collections;

public class AlphaBetaBot extends ChessBot {

    private ArrayList<Move> getMovesInOwnSide(ChessBoard b, boolean curSide) {
        ArrayList<Move> ret = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (b.getBoard()[i][j] == null || b.getBoard()[i][j].getColor() != curSide)
                    continue;
                ret.addAll(b.getLegalMoves(i, j, true));
            }
        }
        AlphaBetaPVSTT.sortMoves(ret, b);
        return ret;
    }

    private MoveScore maximizeOpponentMin(int dep, int bestMaxerAns, int bestMinerAns, ChessBoard b) {
        // maxer -> alpha, miner -> beta

        if (dep == MAX_SEARCH_DEP || System.currentTimeMillis() - startTime > timeLimit)
            return new MoveScore(null, b.evaluate());
        nodeSearched[dep]++;
        int maxScore = Integer.MIN_VALUE;
        Move bestMove = null;
        for (Move curMove : getMovesInOwnSide(b, true)) {
            b.submitMove(curMove);
            int curScore = minimizeOpponentMax(dep + 1, bestMaxerAns, bestMinerAns, b).score; // the opponent wants to
                                                                                              // minimize
            // your max
            b.undoMove();
            bestMove = curScore > maxScore ? curMove : bestMove;
            maxScore = Math.max(maxScore, curScore);
            bestMaxerAns = Math.max(bestMaxerAns, curScore);
            if (bestMinerAns <= bestMaxerAns)
                break;
            // the previous layer of the tree is minimizing layer
            // and if the previous layer already got some answer that is very small
            // (besetAnsMiner)
            // there is no need to search further because the father not gonna choose it
            // (bestAnsMaxer is only going to be bigger)
            // anyway

        }
        return new MoveScore(bestMove, maxScore);
    }

    private MoveScore minimizeOpponentMax(int dep, int bestMaxerAns, int bestMinerAns, ChessBoard b) {

        if (dep == MAX_SEARCH_DEP || System.currentTimeMillis() - startTime > timeLimit)
            return new MoveScore(null, b.evaluate());

        nodeSearched[dep]++;
        int minScore = Integer.MAX_VALUE;
        Move bestMove = null;
        for (Move curMove : getMovesInOwnSide(b, false)) {
            b.submitMove(curMove);
            int curScore = maximizeOpponentMin(dep + 1, bestMaxerAns, bestMinerAns, b).score; // the opponent wants to
                                                                                              // maximize
            // your min
            b.undoMove();
            bestMove = curScore < minScore ? curMove : bestMove;
            minScore = Math.min(minScore, curScore);
            bestMinerAns = Math.min(bestMinerAns, curScore);
            if (bestMinerAns <= bestMaxerAns)
                break;
            // thre previous layer is maximizing layer
            // and if the previous layer already got some answer that is very big
            // (bestAnsMaxer)
            // there is no need to search further because the father not gonna choose it
            // (bestAnsMiner is only going to be smaller)
        }
        return new MoveScore(bestMove, minScore);
    }

    public AlphaBetaBot(ChessBoard board, boolean side) {
       
        super(board); this.side = side;
    }

    @Override
    public Move getMove() {
        for (int i = 0; i < MAX_SEARCH_DEP; i++)
            nodeSearched[i] = 0;

        board = new ChessBoard(getBoard());
        startTime = System.currentTimeMillis();

        Move bestMove = side ? maximizeOpponentMin(0, Integer.MIN_VALUE, Integer.MAX_VALUE, board).move
                : minimizeOpponentMax(0, Integer.MIN_VALUE, Integer.MAX_VALUE, board).move;
        System.out.println((side ? "white" : "black") + " spent " + (System.currentTimeMillis() - startTime) + "ms");
        printNodeSearched();
        return bestMove;
    }

    @Override
    public String getName() {
        return "tony's AlphaBetaBot";
    }

    private void printNodeSearched() {
        int tot = 0;
        for (int i = 0; i < MAX_SEARCH_DEP; i++) {
            System.out.println("depth " + i + " : " + nodeSearched[i]);
            tot += nodeSearched[i];
        }
        System.out.println("total: " + tot);

    }

    private ChessBoard board;
    private boolean side;
    private final int MAX_SEARCH_DEP = 4;
    private long startTime;
    private long timeLimit = 5000;
    private int[] nodeSearched = new int[MAX_SEARCH_DEP];

    public record MoveScore(Move move, int score) {
    }
}