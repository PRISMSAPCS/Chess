package utils.bot;

import utils.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class AlphaBetaPVSTT extends ChessBot {
    public record MoveScore(Move move, int score) {
    }

    public Zobrist hashFunc = new Zobrist();

    private class BoardHashCodeWrapper {

        public ChessBoard b;
        public long zcode;

        public BoardHashCodeWrapper(ChessBoard b) {
            zcode = hashFunc.getHash(b);
        }

        @Override
        public int hashCode() {
            return (int)zcode;
        }

        @Override
        public boolean equals(Object obj){
            if (obj == null) return false;
            if (obj == this) return true;
            if (!(obj instanceof BoardHashCodeWrapper)) return false;
            return this.zcode == ((BoardHashCodeWrapper)obj).zcode;
        }
    }

    private record TTEntry(MoveScore movScore, NodeType type, int depth) {
    }

    private enum NodeType {
        EXACT, LOWER, UPPER;
        /*
         * lowe bound: used to record cut-nodes
         * cut-nodes are the nodes that beta-cutoff is performed
         * beta-cutoff means the value is higher than beta, so there is no need to
         * search
         * this lower bound means, at this point, all the value is always going to be
         * higher than beta, but we stoped searching
         *
         * there are also alpha-cut off in minmax search, however, in negamax, this is
         * the same as beta-cutoff
         * 
         * upper bound: used to record all nodes
         * meaning that the nodes failed to get a better value, so the value is always
         * going to be lower than alpha
         */
    }

    public static void sortMoves(ArrayList<Move> moves, ChessBoard b) {
        Collections.sort(moves, (Move m1, Move m2) -> {
            int m1Score = m1.getCapture() != null ? pieceScore(b.getBoard(m1.getCapture())) : 0;
            int m2Score = m2.getCapture() != null ? pieceScore(b.getBoard(m2.getCapture())) : 0;
            return m2Score - m1Score;
        });
    }

    public static int pieceScore(Piece p) {
        if (p == null)
            return 0;
        if (p instanceof Pawn)
            return 1;
        if (p instanceof Knight)
            return 3;
        if (p instanceof Bishop)
            return 3;
        if (p instanceof Rook)
            return 5;
        if (p instanceof Queen)
            return 9;
        if (p instanceof King)
            return 100;
        return 0;
    }

    private ChessBoard board;

    private boolean side;

    private final int MAX_SEARCH_DEP = 4;
    private long startTime;
    private final long TIME_LIMIT = 5000;
    private AtomicInteger[] nodeSearched = new AtomicInteger[MAX_SEARCH_DEP];
    private int totZeroWindowSearch = 0, totFullSearch = 0;
    private HashMap<BoardHashCodeWrapper, TTEntry> transposTable = new HashMap<>();

    public AlphaBetaPVSTT(ChessBoard board, boolean side) {
        super(board);
        this.side = side;
    }

    private MoveScore probeTT(ChessBoard b, int depth, int alpha, int beta) {
        TTEntry e = transposTable.get(new BoardHashCodeWrapper(b));
        if (e == null)
            return null;
        if (e.depth <= depth) {
            // means e have more remianing depth until search end
            // this is because e.depth means the current depth of e
            if (e.type == NodeType.EXACT)
                return e.movScore;
            else if (e.type == NodeType.LOWER)
                alpha = Math.max(alpha, e.movScore.score);
            else if (e.type == NodeType.UPPER && e.movScore.score >= beta)
                beta = Math.min(beta, e.movScore.score);
        }
        // if getting cut off, return it
        if (alpha >= beta)
            return e.movScore;
        return null;
    }

    @Override
    public String getName() {
        return "tony's AlphabetaPVSTT";
    }

    @Override
    public Move getMove() {
        startTime = System.currentTimeMillis();
        board = new ChessBoard(getBoard());
        for (int i = 0; i < MAX_SEARCH_DEP; i++) {
            nodeSearched[i] = new AtomicInteger(0);
        }
        MoveScore bestMove = search(side, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, board);
        // print time used
        System.out.println((side ? "white" : "black") + " spent " + (System.currentTimeMillis() - startTime) + "ms");
        printNodeSearched();
        return bestMove.move;
    }

    private ArrayList<Move> getMovesInOwnSide(ChessBoard b, boolean curSide) {
        ArrayList<Move> ret = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (b.getBoard()[i][j] == null || b.getBoard()[i][j].getColor() != curSide)
                    continue;
                ret.addAll(b.getLegalMoves(i, j, true));
            }
        }
        return ret;
    }

    private MoveScore search(boolean isWhite, int dep, int alpha, int beta, ChessBoard b) {
        if (dep == MAX_SEARCH_DEP || System.currentTimeMillis() - startTime > TIME_LIMIT) {
            MoveScore ret = new MoveScore(null, b.evaluate());

            return ret;
        }
        nodeSearched[dep].incrementAndGet();
        // uses principle variation search
        MoveScore probRet;
        // if ((probRet = probeTT(b, dep, alpha, beta)) != null) {
        //     return probRet;
        // }

        int origAlpha = alpha, origBeta = beta;

        ArrayList<Move> moves = getMovesInOwnSide(b, isWhite);
        sortMoves(moves, b);
        // for the first move (principle variation)
        Move bestMov = null;
        int bestScore = isWhite ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        for (int i = 0; i < moves.size(); i++) {
            b.submitMove(moves.get(i));
            if (i == 0) {
                // is principle variation
                bestScore = search(!isWhite, dep + 1, alpha, beta, b).score;
                bestMov = moves.get(i);
            } else {
                if (isWhite) {
                    // do a zero window search first
                    // beta = alpha + 1
                    totZeroWindowSearch++;
                    int score = search(!isWhite, dep + 1, alpha, alpha + 1, b).score;
                    // if the child-node beats alpha, but not beta, do a full search
                    if (alpha < score && score < beta) {
                        score = search(!isWhite, dep + 1, score, beta, b).score;
                        totFullSearch++;
                    }
                    bestMov = score > bestScore ? moves.get(i) : bestMov;
                    bestScore = Math.max(bestScore, score);
                    alpha = Math.max(alpha, bestScore);
                } else {
                    // do a zero window search first
                    // alpha = beta - 1
                    totZeroWindowSearch++;
                    int score = search(!isWhite, dep + 1, beta - 1, beta, b).score;
                    // if the child-node beats beta
                    if (alpha < score && score < beta) {
                        score = search(!isWhite, dep + 1, alpha, score, b).score;

                        totFullSearch++;
                    }
                    bestMov = score < bestScore ? moves.get(i) : bestMov;
                    bestScore = Math.min(bestScore, score);
                    beta = Math.min(beta, bestScore);
                }
            }
            b.undoMove();
            // update alpha or beta, check if cut off happend
            if (alpha >= beta) {
                break;
            }
        }
        // update transposition table

        // if it is miner, a cut off is performed of the best answer is lower than beta
        MoveScore ret = new MoveScore(bestMov, bestScore);
        if (!side && bestScore < beta){
            transposTable.put(new BoardHashCodeWrapper(b), new TTEntry(ret, NodeType.UPPER,  dep));
        } else if (side && bestScore > alpha){
            transposTable.put(new BoardHashCodeWrapper(b), new TTEntry(ret, NodeType.LOWER,  dep));
        } else {
            transposTable.put(new BoardHashCodeWrapper(b), new TTEntry(ret, NodeType.EXACT, dep));
        }
        return ret;
    }

    private void printNodeSearched() {
        int tot = 0;
        for (int i = 0; i < nodeSearched.length; i++) {
            tot += nodeSearched[i].get();
            System.out.println("depth: " + i + " : " + nodeSearched[i].get());
        }
        System.out.println("tot search ratio: " + (double) totFullSearch / totZeroWindowSearch);
        System.out.println("total: " + tot);
    }
}
