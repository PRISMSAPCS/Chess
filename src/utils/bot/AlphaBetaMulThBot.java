package utils.bot;

import utils.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.ArrayList;
import java.util.Collections;

public class AlphaBetaMulThBot extends ChessBot {
    private ArrayList<Move> getMovesInOwnSide(ChessBoard b, boolean curSide) {
        ArrayList<Move> ret = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (b.getBoard()[i][j] == null || b.getBoard()[i][j].getColor() != curSide)
                    continue;
                ret.addAll(b.getLegalMoves(i, j, true));
            }
        }

        // Collections.shuffle(ret);

        return ret;
    }

    /**
     * 
     * @param dep           max dep to search
     * @param mulThInterval the interval of layer to start a new thread, if start
     *                      threads too quickly
     * @param bestMaxerAns  alpha
     * @param bestMinerAns  beta
     * @param movRets       all possible moves and their scores (pruned by
     *                      alpha-beta)
     * @param b             the board
     * 
     */
    private Move maximizeOpponentMin(int dep, int mulThInterval, int bestMaxerAns, int bestMinerAns,
            ArrayList<MoveScore> movRets, Move prevMov, ChessBoard b) {
        // maxer -> alpha, miner -> beta

        if (dep == MAX_SEARCH_DEP || System.currentTimeMillis() - startTime > TIME_LIMIT) {
            // System.out.println("returned null because" + (dep == 0 ? " dep == 0" : " time
            // limit reached"));
            synchronized (movRets) {
                movRets.add(new MoveScore(prevMov, b.evaluate()));
            }
            return null;
        }
        nodeSearched[dep].incrementAndGet();

        int maxScore = Integer.MIN_VALUE;
        Move bestMove = null;
        ArrayList<MoveScore> nexMovRets = new ArrayList<>();
        int cantmov = 0;
        int ansReceived = 0; // number of answer received from sub-thread
        int expectAns = 0;
        for (Move curMove : getMovesInOwnSide(b, true)) {
            
            synchronized (nexMovRets) {
                // check the result provided by the newly created thread
                for (MoveScore ret : nexMovRets) {
                    ansReceived++;
                    bestMaxerAns = Math.max(bestMaxerAns, ret.score);
                    bestMove = ret.score > maxScore ? ret.move : bestMove;
                    maxScore = Math.max(maxScore, ret.score);
                }
                nexMovRets.clear();
            }
            cantmov++;
            if (bestMinerAns <= bestMaxerAns)
                break;
            expectAns++;
            b.submitMove(curMove);
            if (activeThs() < THREAD_NUM && mulThInterval >= minMulThInterval) {
                // create a new thread
                final int _bestMaxerAns = bestMaxerAns;
                tPool.execute(() -> {
                    minimizeOpponentMax(dep + 1, 0, _bestMaxerAns, bestMinerAns, nexMovRets, curMove,
                            new ChessBoard(b));
                });
            } else {
                minimizeOpponentMax(dep + 1, mulThInterval + 1, bestMaxerAns, bestMinerAns, nexMovRets, curMove, b);
            }

            b.undoMove();
            // the previous layer of the tree is minimizing layer
            // and if the previous layer already got some answer that is very small
            // (besetAnsMiner)
            // there is no need to search further because the father not gonna choose it
            // (bestAnsMaxer is only going to be bigger)
            // anyway

        }

        // in case some sub-threads haven't finish calculating
        while (ansReceived < expectAns) {
            // sleep for a wh5le to wait for the sub-threads to finish
            // wait for 100ns
            try {
                Thread.sleep(0, 100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            synchronized (nexMovRets) {
                for (MoveScore ret : nexMovRets) {
                    ansReceived++;
                    bestMove = ret.score > maxScore ? ret.move : bestMove;
                    maxScore = Math.max(maxScore, ret.score);
                }
                nexMovRets.clear();
            }
        }

        synchronized (movRets) {
            movRets.add(new MoveScore(prevMov, maxScore));
        }

        // if (bestMove == null)
        //     System.out.println("bestMove is null " + "cant mov: " + cantmov + " exp ans: " + expectAns + " ans rec: "
        //             + ansReceived + " dep: " + dep);

        return bestMove;
    }

    private Move minimizeOpponentMax(int dep, int mulThInterval, int bestMaxerAns, int bestMinerAns,
            ArrayList<MoveScore> movRets, Move prevMov, ChessBoard b) {

        if (dep == MAX_SEARCH_DEP || System.currentTimeMillis() - startTime > TIME_LIMIT) {
            int eval = b.evaluate();
            synchronized (movRets) {
                movRets.add(new MoveScore(prevMov, eval));
                // the previous move made this eval result
            }
            return null;
        }
        nodeSearched[dep].incrementAndGet();
        int minScore = Integer.MAX_VALUE;
        Move bestMove = null;
        ArrayList<MoveScore> nexMovRets = new ArrayList<>();
        int ansReceived = 0;
        int expectAns = 0;
        for (Move curMove : getMovesInOwnSide(b, false)) {
            synchronized (nexMovRets) {
                // check the result provided by the newly created thread
                for (MoveScore ret : nexMovRets) {
                    bestMinerAns = Math.min(bestMinerAns, ret.score);
                    bestMove = ret.score < minScore ? ret.move : bestMove;
                    minScore = Math.min(minScore, ret.score);
                    ansReceived++;
                }
                nexMovRets.clear();
            }
            if (bestMinerAns <= 0)
                break;
            expectAns++;
            b.submitMove(curMove);
            if (activeThs() < THREAD_NUM && mulThInterval >= minMulThInterval) {
                // create a new thread
                final int _bestMinerAns = bestMinerAns;
                tPool.execute(() -> {
                    maximizeOpponentMin(dep + 1, 0, bestMaxerAns, _bestMinerAns, nexMovRets, curMove,
                            new ChessBoard(b));
                });
            } else {
                maximizeOpponentMin(dep + 1, mulThInterval + 1, bestMaxerAns, bestMinerAns, nexMovRets, curMove, b);
            }

            b.undoMove();
        }

        synchronized (movRets) {
            movRets.add(new MoveScore(prevMov, minScore));
        }

        // in case some sub-threads haven't finish calculating
        while (ansReceived < expectAns) {
            // sleep for a while to wait for the sub-threads to finish
            // wait for 500ns
            try {
                Thread.sleep(0, 100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (nexMovRets) {
                for (MoveScore ret : nexMovRets) {
                    ansReceived++;
                    bestMove = ret.score < minScore ? ret.move : bestMove;
                    minScore = Math.min(minScore, ret.score);
                }
                nexMovRets.clear();
            }
        }

        // if (bestMove == null)
        //     System.out.println("bestMove is null maxer");
        return bestMove;
    }

    @Override
    public String getName() {
        return "tony's AlphaBetaMultiThread";
    }

    @Override
    public Move getMove() {
        // init node searched
        for (int i = 0; i < nodeSearched.length; i++) {
            nodeSearched[i] = new AtomicInteger(0);
        }

        startTime = System.currentTimeMillis();
        board = new ChessBoard(getBoard());
        side = board.getSide();
        ArrayList<MoveScore> movRets = new ArrayList<>();
        Move ret;
        if (side)
            ret = maximizeOpponentMin(0, 114, Integer.MIN_VALUE, Integer.MAX_VALUE, movRets,
                    null, board);
        else
            ret = minimizeOpponentMax(0, 114, Integer.MIN_VALUE, Integer.MAX_VALUE, movRets,
                    null, board);
        System.out.println(
                (side ? "white" : "black") + " spent " + (System.currentTimeMillis() - startTime) + "ms");
        printNodeSearched();
        return ret;
    }

    public AlphaBetaMulThBot(ChessBoard board) {
        super(board);
    }

    synchronized private int activeThs() {
        return ((ThreadPoolExecutor) tPool).getActiveCount();
    }

    private void printNodeSearched() {
        int tot = 0;
        for (int i = 0; i < nodeSearched.length; i++) {
            tot += nodeSearched[i].get();
            System.out.println("depth: " + i + " : " + nodeSearched[i].get());
        }
        System.out.println("total: " + tot);
    }

    private ChessBoard board;
    private boolean side;
    private final int MAX_SEARCH_DEP = 6;
    private long startTime;
    private final long TIME_LIMIT = 1000;
    private static final int THREAD_NUM = Runtime.getRuntime().availableProcessors();
    private ExecutorService tPool = Executors.newFixedThreadPool(THREAD_NUM);
    private int minMulThInterval = 100; // difference in layer to apply multi-threading, 0 means immediately
    private AtomicInteger[] nodeSearched = new AtomicInteger[MAX_SEARCH_DEP];

    public record MoveScore(Move move, int score) {
    }
}
