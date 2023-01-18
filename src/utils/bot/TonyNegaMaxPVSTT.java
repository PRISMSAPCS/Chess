package utils.bot;

import utils.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

public class TonyNegaMaxPVSTT extends ChessBot {
    public record MoveScore(Move move, int score) {
    }

    public TonyZobrist hashFunc = new TonyZobrist();

    private class BoardHashCodeWrapper {

        public ChessBoard b;
        public long zcode;

        public BoardHashCodeWrapper(ChessBoard b) {
            zcode = hashFunc.getHash(b);
        }

        @Override
        public int hashCode() {
            return (int) zcode;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null)
                return false;
            if (obj == this)
                return true;
            if (!(obj instanceof BoardHashCodeWrapper))
                return false;
            return this.zcode == ((BoardHashCodeWrapper) obj).zcode;
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

    private boolean isWhite;

    private final int SEARCH_DEP_NORM = 6;
    private final int SEARCH_DEP_HIGH = 6;
    private float SEARCH_DEP_HIGH_RATIO = 0.3f;
    private long startTime;
    private final long TIME_LIMIT = 5000;
    private AtomicInteger[] nodeSearched = new AtomicInteger[SEARCH_DEP_HIGH];
    private int totZeroWindowSearch = 0, totFullSearch = 0;
    private int totSearch = 0, totCacheHit = 0;
    private Hashtable<BoardHashCodeWrapper, TTEntry> transposTable = new Hashtable<>();
    private final int MAX_THREADS = Runtime.getRuntime().availableProcessors() - 2;
    private ExecutorService thPool = Executors.newFixedThreadPool(MAX_THREADS);

    public TonyNegaMaxPVSTT(ChessBoard board, boolean side) {
        super(board);
        this.isWhite = side;
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
            else if (e.type == NodeType.UPPER)
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

        int remainPiece = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board.getBoard(new Pair(i, j)) != null)
                    remainPiece++;
            }
        }

        // mapping from [0, 28] to [0.4, 1]
        SEARCH_DEP_HIGH_RATIO = (float) (0.3 + 0.7 * (28 - remainPiece) / 28.0);
        System.out.println("remain piece: " + remainPiece + " ratio: " + SEARCH_DEP_HIGH_RATIO);

        totFullSearch = 0;
        totZeroWindowSearch = 0;
        for (int i = 0; i < nodeSearched.length; i++) {
            nodeSearched[i] = new AtomicInteger(0);
        }
        ArrayList<Future<MoveScore>> moveRets = new ArrayList<>();
        for (int i = 0; i < MAX_THREADS; i++) {
            moveRets.add(
                    thPool.submit(() -> {
                        MoveScore tmp = negaMax(isWhite ? 1 : -1, 0, Integer.MIN_VALUE / 2, Integer.MAX_VALUE / 2,
                                new ChessBoard(board));
                        MoveScore nega = new MoveScore(tmp.move, tmp.score);
                        return nega;
                    }));
        }

        Move bestMove = null;
        int bestScore = Integer.MIN_VALUE;
        try {
            for (Future<MoveScore> f : moveRets) {
                MoveScore ms = f.get();
                if (ms.score > bestScore) {
                    bestScore = ms.score;
                    bestMove = ms.move;
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        // print time used
        System.out.println((isWhite ? "white" : "black") + " spent " + (System.currentTimeMillis() - startTime) + "ms");
        printNodeSearched();
        return bestMove;
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

        // check if there are promotion moves, if so, change it to queen
        for (Move m : ret) {
            if (m instanceof PromotionMove) {
                ((PromotionMove) m).setPromoteTo(new Queen(curSide));
            }
        }

        return ret;
    }

    private int selectDepOnChance() {
        if (Math.random() < SEARCH_DEP_HIGH_RATIO)
            return SEARCH_DEP_HIGH;
        return SEARCH_DEP_NORM;
    }

    private MoveScore negaMax(int color, int dep, int alpha, int beta, ChessBoard b) {
        if (dep >= selectDepOnChance() || System.currentTimeMillis() - startTime > TIME_LIMIT) {
            // System.out.println("color " + color);
            return new MoveScore(null, b.evaluate() * color);
        }
        totSearch++;
        MoveScore probeRet = null;
        if ((probeRet = probeTT(b, dep, alpha, beta)) != null) {
            totCacheHit++;
            return probeRet;
        }

        nodeSearched[dep].incrementAndGet();

        Move bestMove = null;
        int maxScore = Integer.MIN_VALUE;

        ArrayList<Move> moves = getMovesInOwnSide(b, color == 1);
        sortMoves(moves, b);
        // shuffle
        // Collections.shuffle(moves);
        boolean betaCutoff = false;
        int curScore = Integer.MIN_VALUE;
        int alphaOrig = alpha;
        for (int i = 0; i < moves.size(); i++) {
            // pvs implemented
            Move m = moves.get(i);
            b.submitMove(m);
            if (i == 0) {
                curScore = -negaMax(-color, dep + 1, -beta, -alpha, b).score;
                bestMove = m;
            } else {
                // null window at first
                curScore = -negaMax(-color, dep + 1, -(alpha + 1), -alpha, b).score;

                totZeroWindowSearch++;
                if (alpha < curScore && curScore < beta) {
                    // full search
                    curScore = -negaMax(-color, dep + 1, -beta, -curScore, b).score;
                    totFullSearch++;
                }

            }
            b.undoMove();
            bestMove = curScore > maxScore ? m : bestMove;
            maxScore = Math.max(maxScore, curScore);
            alpha = Math.max(alpha, maxScore);
            if (alpha >= beta) {
                betaCutoff = true;
                break;
            }
        }
        MoveScore ret = new MoveScore(bestMove, maxScore);
        NodeType type;
        if (betaCutoff) {
            type = NodeType.LOWER;
        } else if (curScore <= alphaOrig) {
            type = NodeType.UPPER;
        } else {
            type = NodeType.EXACT;
        }

        // store transposition table
        TTEntry entry = new TTEntry(ret, type, dep);
        transposTable.put(new BoardHashCodeWrapper(b), entry);
        return ret;   
    }

    private void printNodeSearched() {
        int tot = 0;
        for (int i = 0; i < nodeSearched.length; i++) {
            tot += nodeSearched[i].get();
            System.out.println("depth: " + i + " : " + (float) nodeSearched[i].get() / (float) MAX_THREADS);
        }
        System.out.println("tot search ratio: " + (double) totFullSearch / totZeroWindowSearch + " full search = "
                + totFullSearch + " zero window search = " + totZeroWindowSearch);
        System.out.println("cache hit rate: " + (double) totCacheHit / totSearch + " cache hit = " + totCacheHit
                + " total search = " + totSearch);
        System.out.println("total: " + tot);
    }
}