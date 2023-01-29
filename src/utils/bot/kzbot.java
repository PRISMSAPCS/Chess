package utils.bot;

import utils.ChessBoard;
import utils.Move;
import utils.Piece;
import java.util.ArrayList;
import java.util.Random;


public class kzbot extends ChessBot{
    Random rand = new Random();
    static private int MIN = -100000;
    static private int MAX = 100000;
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

    private thing minimax(ChessBoard bo, int d){

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
        }

        Move bestMove = null;
        try{
            bestMove = moveArray.get(hiscoreIndex);
        }catch(Exception e){
            return null;
            //bestMove = null;
        }
        return new thing(bestMove, hiscore);
    }
    

    public thing minimax1(int depth, Boolean maxing, ChessBoard b, int alpha, int beta, Move m){
        if(m!=null){
            if(m.getPiece2() != null){
                if(m.getPiece2().getColor()) return new thing(m, -100000);
                else return new thing(m, 100000);
            }
        }

        if(depth == 4){
            return new thing(m, b.evaluate());
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
                int val = a.v;

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
                int val = a.v;
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


    public Move getMove(){
        Move finalMove = null;
        ChessBoard b1 = new ChessBoard(super.getBoard());
        thing d = minimax1(0, side, b1, MIN, MAX, null);
        finalMove = d.m;
        return finalMove;
    }

}
