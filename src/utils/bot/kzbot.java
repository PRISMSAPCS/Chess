package utils.bot;

import utils.ChessBoard;
import utils.Move;
import utils.Piece;
import java.util.ArrayList;
import java.util.Random;


public class kzbot extends ChessBot{
    Random rand = new Random();
    class thing{
        public Move m;
        public int v;
        public thing(Move m1, int v1){
            m = m1;
            v = v1;
        }
    }

    private static int depth = 4;

    public kzbot(ChessBoard b){
        super(b);
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

    private thing getBestMove(ChessBoard bo, int d){

        ArrayList<Move> moveArray = new ArrayList<>();
        moveArray = getAllLegal(bo);

        int hiscoreIndex = 0;
        int hiscore = 0;
        int temp = 0;
        int s = 0;

        thing recursed = new thing(null, 0);

        for(int i =0; i<moveArray.size(); i++){
            ChessBoard b2 = new ChessBoard(bo);

            if(b2.getSide()){
                s = -1;
            }else{
                s = 1;
                //multiplier for side
            }

            b2.submitMove(moveArray.get(i));

            if(d > 1){
                //temp = (b2.evaluate() * s) - getEnemyBestMove(b2, d-1).v;
                recursed = getBestMove(b2, d-1);
                if (recursed == null){
                    continue;
                }
                temp = recursed.v;
            }else{
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
    
    public Move getMove(){
        Move finalMove = null;
        ChessBoard b1 = new ChessBoard(super.getBoard());
        
        finalMove = getBestMove(b1, depth).m;
        return finalMove;
    }

}
