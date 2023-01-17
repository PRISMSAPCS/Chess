package utils.bot;

import utils.ChessBoard;
import utils.Move;
import utils.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TomChessBot extends ChessBot {
    public static final int SEARCHING_DEPTH = 3;

    public TomChessBot(ChessBoard board) {
        super(board);
    }

    /**
     * Optimize the score current player can get
     * @param board board
     * @param maximize false to minimize score, true to maximize score
     * @param depth depth for searching
     * @param minBound minimum allowed score
     * @param maxBound maximum allowed score
     * @return the move and its evaluation score
     */
    private MovePair optimizeScore(ChessBoard board, boolean maximize, int depth, int minBound, int maxBound) {
        // calculate each option
        int optScore = maximize? Integer.MIN_VALUE: Integer.MAX_VALUE;
        Move optMove = null;
        for(Move move : board.getAllLegalMoves()) {
            board.submitMove(move);
            if(depth <= 0) {
                // if depth is less than 0, use the evaluation function as current expected value
                int score = board.evaluate();
                if((score < optScore) ^ maximize) {
                    optScore = score;
                    optMove = move;
                }
            } else {
                MovePair postEvaluation = optimizeScore(board, !maximize, depth - 1, minBound, maxBound);
                if((maximize && postEvaluation.score > maxBound) ||
                        (!maximize && postEvaluation.score < minBound)) {
                    // Check for bounds. If postEvaluation score do not satisfy bound requirement, directly break.
                    board.undoMove();
                    optScore = maximize? Integer.MAX_VALUE: Integer.MIN_VALUE;
                    break;
                } else {
                    // update minimum or maximum bound
                    if(maximize) {
                        minBound = postEvaluation.score;
                    } else {
                        maxBound = postEvaluation.score;
                    }
                }
                if((postEvaluation.score < optScore) ^ maximize) {
                    optScore = postEvaluation.score;
                    optMove = move;
                }
            }
            board.undoMove();
        }
        return new MovePair(optMove, optScore);
    }

    @Override
    public Move getMove() {
        long time1 = System.currentTimeMillis();

        ChessBoard boardClone = new ChessBoard(getBoard());
        boardClone.disableLogging();
        Move move =  optimizeScore(boardClone, getBoard().getSide(), SEARCHING_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE).move;

        long time2 = System.currentTimeMillis();
        System.out.println((time2 - time1) / 1000.0F + "s");
        return move;
    }

    @Override
    public String getName() {
        return "Tom Geng's bot";
    }

    public class MovePair {
        public Move move;
        public Integer score;
        public MovePair(Move move, Integer score) {
            this.move = move;
            this.score = score;
        }
    }
}
