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

    private record TTEntry(MoveScore movScore, NodeType type, int remDep, long zobrist) {
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

    private final int MAX_SEARCH_DEP = 15;
    private volatile boolean finishedSearchingInTime = true;
    private long startTime;
    private final long TIME_LIMIT = 5000;
    private AtomicInteger[] nodeSearched = new AtomicInteger[MAX_SEARCH_DEP];
    private int totZeroWindowSearch = 0, totFullSearch = 0;
    private int totSearch = 0, totCacheHit = 0;
    private Hashtable<ChessBoard, TTEntry> transposTable = new Hashtable<>();
    private final int MAX_THREADS = Runtime.getRuntime().availableProcessors() - 1;
    private ExecutorService thPool = Executors.newFixedThreadPool(MAX_THREADS);

    public TonyNegaMaxPVSTT(ChessBoard board, boolean side) {
        super(board);
        this.isWhite = side;
    }

    private MoveScore probeTT(ChessBoard b, int remDep, int alpha, int beta) {
        TTEntry e = transposTable.get(b);
        if (e == null)
            return null;
        assert(e.zobrist == b.getZobristKey()) : "zobrist key not match";
        if (e.remDep >= remDep) {
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

    public Move multiThreadSearch() {
        Move bestMove = null;
        int bestScore = Integer.MIN_VALUE;
        
        for (int dep = 1; dep <= MAX_SEARCH_DEP; dep++) {
            // clear node searched
            for (int i = 0; i < dep; i++) {
                nodeSearched[i] = new AtomicInteger(0);
            }
            // System.out.println("dep " + dep + "executed");
            ArrayList<Future<MoveScore>> futureMoveRets = new ArrayList<>();
            finishedSearchingInTime = true;
            for (int i = 0; i < MAX_THREADS; i++) {
                final int fDep = dep;
                futureMoveRets.add(
                        thPool.submit(() -> {
                            MoveScore tmp = negaMax(isWhite ? 1 : -1, 0, fDep, Integer.MIN_VALUE / 2, Integer.MAX_VALUE / 2,
                                    new ChessBoard(board));
                            return tmp;
                        }));
            }


            ArrayList<MoveScore> moveRets = new ArrayList<>();
            try {
                for (Future<MoveScore> f : futureMoveRets) {
                    MoveScore ms = f.get();
                    moveRets.add(ms);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            // check if finished searching in this layer
            if (!finishedSearchingInTime) {
                System.out.println("tdep " + (dep - 1));
                break;
            }

            // get best move
            bestMove = null;
            bestScore = Integer.MIN_VALUE;
            for (MoveScore ms : moveRets) {
                if (ms.score > bestScore) {
                    bestScore = ms.score;
                    bestMove = ms.move;
                }
            }
            
        }
        return bestMove;
    }

    @Override
    public Move getMove() {
        startTime = System.currentTimeMillis();
        board = new ChessBoard(getBoard());

        totFullSearch = 0;
        totZeroWindowSearch = 0;
        Move bestMove;
        for (int i = 0; i < nodeSearched.length; i++) {
            nodeSearched[i] = new AtomicInteger(0);
        }

        bestMove = multiThreadSearch();

        // print time used
        printTurnInfo();
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

    private MoveScore negaMax(int color, int curDep, int remDep,int alpha, int beta, ChessBoard b) {
        boolean timeOut = System.currentTimeMillis() - startTime > TIME_LIMIT;
        // boolean timeOut = false;
        if (remDep == 0 || timeOut) {
            if (timeOut){
                finishedSearchingInTime = false;
            }
            return new MoveScore(null, b.evaluate() * color);
        }
        totSearch++;
        nodeSearched[curDep].incrementAndGet();
        MoveScore probeRet = null;
        if ((probeRet = probeTT(b, remDep, alpha, beta)) != null) {
            totCacheHit++;
            assert probeRet != null : "TT returned null value";
            return probeRet;
        }

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
                curScore = -negaMax(-color, curDep + 1, remDep - 1, -beta, -alpha, b).score;
                bestMove = m;
            } else {
                // null window at first
                curScore = -negaMax(-color, curDep + 1, remDep - 1, -(alpha + 1), -alpha, b).score;
                
                totZeroWindowSearch++;
                if (alpha < curScore && curScore < beta) {
                    // full search
                    curScore = -negaMax(-color, curDep + 1, remDep -1, -beta, -curScore, b).score;
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
            // this branch is too good to be true, the father tree will not choose this branch
            // however, it is possible for this to be better, so it is a lower bound
            type = NodeType.LOWER;
        } else if (curScore <= alphaOrig) {
            // didn't improve alpha at all, it is lower bound
            type = NodeType.UPPER;
        } else {
            type = NodeType.EXACT;
        }

        // store transposition table
        TTEntry entry = new TTEntry(ret, type, curDep, b.getZobristKey());
        transposTable.put(b, entry);
        assert ret != null : "bestmove returned is null";
        return ret;
    }

    private void printTurnInfo() {
        System.out.println("tot search ratio: " + (double) totFullSearch / totZeroWindowSearch + " full search = "
                + totFullSearch + " zero window search = " + totZeroWindowSearch);
        System.out.println("cache hit rate: " + (double) totCacheHit / totSearch + " cache hit = " + totCacheHit
                + " total search = " + totSearch);
        System.out.println("total: " + totSearch);
    }
}