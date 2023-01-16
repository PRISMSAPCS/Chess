package utils.bot;

import utils.ChessBoard;
import utils.Move;

/**
 * Tom Geng (@MqCreaple)'s old chess bot, using MiniMax
 */
public class TomChessBotOld extends ChessBot {
    public static final int SEARCHING_DEPTH = 3;

    public TomChessBotOld(ChessBoard board) {
        super(board);
    }

    /**
     * Optimize the score current player can get
     * @param board board
     * @param maximize false to minimize score, true to maximize score
     * @param depth depth for searching
     * @return the move and its evaluation score
     */
    private MovePair optimizeScore(ChessBoard board, boolean maximize, int depth) {
        // calculate each option
        int optScore = maximize? Integer.MIN_VALUE: Integer.MAX_VALUE;
        Move optMove = null;
        for(Move move : board.getAllLegalMoves()) {
            board.submitMove(move);
            if(depth <= 0) {
                int score = board.evaluate();
                if((score < optScore) ^ maximize) {
                    optScore = score;
                    optMove = move;
                }
            } else {
                MovePair postEvaluation = optimizeScore(board, !maximize, depth - 1);
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
        ChessBoard boardClone = new ChessBoard(getBoard());
        boardClone.disableLogging();
        return optimizeScore(boardClone, getBoard().getSide(), SEARCHING_DEPTH).move;
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
