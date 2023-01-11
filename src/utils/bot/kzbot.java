package utils.bot;

import utils.ChessBoard;
import utils.Move;
import utils.Piece;

public class kzbot extends ChessBot{

    public kzbot(ChessBoard b){
        super(b);
    }
    
    public String getName(){
        return "KZChessBot";
    }
    public Move getMove(){
        Move finalMove = null;
        ChessBoard b1 = new ChessBoard(super.getBoard());
        
        for (Piece[] row : b1.getBoard()){
            for (Piece p : row){
                if(p.getColor() == b1.getSide()){

                }
            }
        }
        return finalMove;
    }

}
