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



public class kzbot extends ChessBot{
    Random rand = new Random();
    static private int MIN = -1000000;
    static private int MAX = 1000000;
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

    public kzbot(ChessBoard b, Boolean s){
        super(b);
        side = s;
    }
    
    public String getName(){
        return "KZChessBot";
    }

    private ArrayList<Move> getAllLegal(ChessBoard bo){
        /*Piece[][] pl = bo.getBoard();
        ArrayList<Move> moveA = new ArrayList<>();

        for (int y=0; y<8; y++){
            for (int x=0; x<8; x++){  
                if(pl[y][x]!=null && pl[y][x].getColor() == bo.getSide()){
                    moveA.addAll(bo.getLegalMoves(y, x, true));
                }
            }
        }
        System.out.println(moveA.size());*/
        return bo.getAllLegalMoves();
    }

    /*private thing minimax(ChessBoard bo, int d){

            ArrayList<Move> moveArray = new ArrayList<>();
            moveArray = getAllLegal(bo);

        int hiscoreIndex = 0;
        int hiscore = 0;
        int temp = 0;
        int s = 0;

        thing recursed = new thing(null, 0);

        for(int i =0; i<moveArray.size(); i++){
            ChessBoard b2 = new ChessBoard(bo);

            b2.submitMove(moveArray.get(i));

            if(d > 1){
                //temp = (b2.evaluate() * s) - getEnemyBestMove(b2, d-1).v;
                recursed = minimax(b2, d-1);
                if (recursed == null){
                    continue;
                }
                temp = recursed.v;
            }else{

                if(b2.getSide()){
                    s = -1;
                }else{
                    s = 1;
                    //multiplier for side
                }
                temp = b2.evaluate() * s;
            }

            if((temp > hiscore)){ 
                hiscore = temp;
                hiscoreIndex = i;
            }/*else if(temp == hiscore && rand.nextInt(3)==1){//slight randomization here, quick fix, needs to be revamped.
                hiscoreIndex = i;
            }*/
        /*}

        Move bestMove = null;
        try{
            bestMove = moveArray.get(hiscoreIndex);
        }catch(Exception e){
            return null;
            //bestMove = null;
        }
        return new thing(bestMove, hiscore);
    }*/

    public thing minimax1(int depth, Boolean maxing, ChessBoard b, int alpha, int beta, Move m){

        if(System.currentTimeMillis() - start > 4990){
            return null;
        }

        if(m!=null){
            if(m.getPiece2() != null){
                if(m.getPiece2().getColor()) return new thing(m, 100000); 
                else return new thing(m, -100000);
            }
        }
        int modifier = 0;

        /*if(depth == 1){
            modifier = KZEval.eval(b, maxing) * -10;
        }*/

        if(depth == properDepth){

            int evaluation = KZEval.eval(b, depth);
            return new thing(m, /*b.evaluate()*/ evaluation);
        }

        ArrayList<Move> allLegal = b.getAllLegalMoves();
        if(allLegal.size()==0){
            return null;
        }
        Move m1;
        Move b1 = null; 

        if(maxing){
            int best = MIN;
            for(int i=0; i<allLegal.size(); i++){
                m1 = allLegal.get(i);
                ChessBoard b2 = new ChessBoard(b);
                b2.submitMove(m1);
                thing a = minimax1(depth+1, false, b2, alpha, beta, m1);

                if(a==null){
                    continue;
                }
                int val = a.v + modifier;

                best = Math.max(best, val);
                if(best == val){
                    b1 = m1;
                }
                alpha = Math.max(alpha, best);

                if(beta <= alpha){
                    break;
                }
            }
            return new thing(b1, best);
        }else{
            int best = MAX;
            for(int i=0; i<allLegal.size(); i++){
                m1 = allLegal.get(i);
                ChessBoard b2 = new ChessBoard(b);
                b2.submitMove(m1);
                thing a = minimax1(depth+1, true, b2, alpha, beta, m1);
                if(a==null){
                    continue;
                }
                int val = a.v + modifier;
                best = Math.min(best,val);
                if(best == val){
                    b1 = m1;
                }
                beta = Math.min(beta,best);

                if(beta <= alpha){
                    break;
                }
            }
            return new thing(b1, best);
        }
        
    }

    public Move minimaxMultiThreadDistributer(boolean side, ChessBoard b){
        Move bestMove = null;
        int bestNum = 0;
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() -2);
        List<Future<thing>> futures = new ArrayList<>();
        ArrayList<Move> allLegal = b.getAllLegalMoves();
        for(int i = 0; i < allLegal.size(); i++){
            ChessBoard b2 = new ChessBoard(b);
            Move m1 = allLegal.get(i);
            b2.submitMove(m1);
            futures.add(
                executor.submit(() -> {
                    thing tmp = minimax1(1, !side, b2, MIN, MAX, m1);
                    return tmp;
            }));
        }
        
        ArrayList<thing> moves = new ArrayList<>();

        try {
                for (Future<thing> f : futures) {
                    thing move = f.get();
                    moves.add(move);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

        if(side){
            bestNum = MIN;
            for(thing m : moves){
                if(m == null){
                    continue;
                }
                if (m.v > bestNum){
                    bestMove = m.m;
                    bestNum = m.v;
                }
            }
        }else if(!side){
            bestNum = MAX;
            for(thing m : moves){
                if(m == null){
                    continue;
                }
                if (m.v < bestNum){
                    bestMove = m.m;
                    bestNum = m.v;
                }
            }
        }


        return bestMove;

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
