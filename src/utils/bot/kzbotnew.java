package utils.bot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import utils.ChessBoard;
import utils.Move;
import utils.bot.TonyNegaMaxPVSTT.MoveScore;
import utils.bot.KZBotResources.KZEval;



public class kzbotnew extends ChessBot{
    Random rand = new Random();
    static private int MIN = -1000000;
    static private int MAX = 1000000;
    static private int maxDepth = 15;
    private long start = 0;
    private int properDepth = 3;
    private int amountUnder = 0;
    private int amountOver = 0;
    class thing{
        public Move m;
        public int v;
        public thing(Move m1, int v1){
            m = m1;
            v = v1;
        }
    }

    private int depth = 2;
    private Boolean side = null;

    public kzbotnew(ChessBoard b, Boolean s){
        super(b);
        side = s;
    }
    
    public String getName(){
        return "KZChessBotNew";
    }

    private ArrayList<Move> getAllLegal(ChessBoard bo){
        return bo.getAllLegalMoves();
    }

    public Move breadthSearchThreadDistributor(boolean side, ChessBoard b){
        Move bestMove = null;
        int bestNum = 0;
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() -2);
        List<Future<Integer>> futures = new ArrayList<>();
    }


    public Move getMove(){
        Move finalMove = null;
        start = System.currentTimeMillis();
        ChessBoard b1 = new ChessBoard(super.getBoard());
        System.out.println(b1.evaluate());
        //thing d = minimax1(0, side, b1, MIN, MAX, null);
        Move bestMove = minimaxMultiThreadDistributer(this.side, b1);
        if(System.currentTimeMillis() - start < 500){
            amountUnder++;
            amountOver = 0;
        }
        if(amountUnder > 1){
            amountUnder = 0;
            properDepth++;
        }
        if(System.currentTimeMillis() - start >= 4990){
            amountOver++;
            amountUnder = 0;
        }
        if(amountOver > 1 && properDepth > 2){
            amountOver = 0;
            properDepth--;
        }
        //finalMove = d.m;
        finalMove = bestMove;
        return finalMove;
    }

}
