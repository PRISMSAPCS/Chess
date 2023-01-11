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
        Piece[][] pl = b1.getBoard();
        moveArray

        for (int y=0; y<pl.length; y++){
            for (int x=0; x<pl[y].length; y++){
                if(pl[y][x].getColor() == b1.getSide()){

                }
            }
        }
        return finalMove;
    }

}
