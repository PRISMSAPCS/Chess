package utils.bot.KZBotResources;

import utils.ChessBoard;
import utils.Piece;
import utils.*;

public class KZEval {
    public int eval(ChessBoard b){
        int counter = 0;
        Piece[][] board = b.getBoard();
        counter += rateAttack(board);
        counter += rateMaterial(board);
        counter += rateMoveability(board);
        counter += ratePosition(board);
        return counter;
    }

    public int rateMaterial(Piece[][] b){
        int counter = 0;
        int mult = 0;
        int bishopCounterWhite = 0;
        int bishopCounterBlack = 0;

        for (Piece[] x : b) {
			for (Piece y : x) {
                if(y.getColor()) mult = 1; else mult = -1;
                if(y instanceof Pawn){
                    counter += 100 * mult;
                    continue;
                }else if(y instanceof Bishop ){
                    switch(mult){
                        case -1: bishopCounterBlack += 1;
                            break;
                        case 1: bishopCounterWhite += 1;
                            break;
                    }
                    continue;
                }else if(y instanceof Knight){
                    counter += 300 * mult;
                    continue;
                }else if(y instanceof Rook){
                    counter += 500 * mult;
                    continue;
                }else if(y instanceof Queen){
                    counter += 900 * mult;
                    continue;
                }
            }
        }

        if(bishopCounterBlack >= 2){
            counter -= 300 * bishopCounterBlack;
        }else{
            if(bishopCounterBlack == 1) counter -= 250;
        }
        if(bishopCounterWhite >= 2){
            counter += 300 * bishopCounterWhite;
        }else{
            if(bishopCounterWhite == 1) counter += 250;
        }

        return counter;
    }

    public int rateAttack(Piece[][] b){
        return 0;
    }

    public int rateMoveability(Piece[][] b){
        return 0;
    }

    public int ratePosition(Piece[][] b){
        return 0;
    }
    
}
