package utils.bot.KZBotResources;

import java.util.ArrayList;

import utils.Bishop;
import utils.ChessBoard;
import utils.King;
import utils.Knight;
import utils.Move;
import utils.Pawn;
import utils.Piece;
import utils.Queen;
import utils.Rook;

public class KZEval {

    static int pawnBoard[][]={ 
        { 0,  0,  0,  0,  0,  0,  0,  0},
        {50, 50, 50, 50, 50, 50, 50, 50},
        {10, 10, 20, 30, 30, 20, 10, 10},
        { 5,  5, 10, 25, 25, 10,  5,  5},
        { 0,  0,  0, 20, 20,  0,  0,  0},
        { 5, -5,-10,  0,  0,-10, -5,  5},
        { 5, 10, 10,-20,-20, 10, 10,  5},
        { 0,  0,  0,  0,  0,  0,  0,  0}};
    static int pawnBoardRev[][]={
        {0, 0, 0, 0, 0, 0, 0, 0},
        {5, 10, 10, -20, -20, 10, 10, 5},
        {5, -5, -10, 0, 0, -10, -5, 5},
        {0, 0, 0, 20, 20, 0, 0, 0},
        {5, 5, 10, 25, 25, 10, 5, 5},
        {10, 10, 20, 30, 30, 20, 10, 10},
        {50, 50, 50, 50, 50, 50, 50, 50},
        {0, 0, 0, 0, 0, 0, 0, 0}};
    
    static int rookBoard[][]={
        { 0,  0,  0,  0,  0,  0,  0,  0},
        { 5, 10, 10, 10, 10, 10, 10,  5},
        {-5,  0,  0,  0,  0,  0,  0, -5},
        {-5,  0,  0,  0,  0,  0,  0, -5},
        {-5,  0,  0,  0,  0,  0,  0, -5},
        {-5,  0,  0,  0,  0,  0,  0, -5},
        {-5,  0,  0,  0,  0,  0,  0, -5},
        { 0,  0,  0,  5,  5,  0,  0,  0}};
    static int rookBoardRev[][]={
        {0, 0, 0, 5, 5, 0, 0, 0},
        {-5, 0, 0, 0, 0, 0, 0, -5},
        {-5, 0, 0, 0, 0, 0, 0, -5},
        {-5, 0, 0, 0, 0, 0, 0, -5},
        {-5, 0, 0, 0, 0, 0, 0, -5},
        {-5, 0, 0, 0, 0, 0, 0, -5},
        {5, 10, 10, 10, 10, 10, 10, 5},
        {0, 0, 0, 0, 0, 0, 0, 0}};

    static int knightBoard[][]={
        {-50,-40,-30,-30,-30,-30,-40,-50},
        {-40,-20,  0,  0,  0,  0,-20,-40},
        {-30,  0, 10, 15, 15, 10,  0,-30},
        {-30,  5, 15, 20, 20, 15,  5,-30},
        {-30,  0, 15, 20, 20, 15,  0,-30},
        {-30,  5, 10, 15, 15, 10,  5,-30},
        {-40,-20,  0,  5,  5,  0,-20,-40},
        {-50,-40,-30,-30,-30,-30,-40,-50}};
    static int knightBoardRev[][]={
        {-50, -40, -30, -30, -30, -30, -40, -50},
        {-40, -20, 0, 5, 5, 0, -20, -40},
        {-30, 5, 10, 15, 15, 10, 5, -30},
        {-30, 0, 15, 20, 20, 15, 0, -30},
        {-30, 5, 15, 20, 20, 15, 5, -30},
        {-30, 0, 10, 15, 15, 10, 0, -30},
        {-40, -20, 0, 0, 0, 0, -20, -40},
        {-50, -40, -30, -30, -30, -30, -40, -50}};
    
    static int bishopBoard[][]={
        {-20,-10,-10,-10,-10,-10,-10,-20},
        {-10,  0,  0,  0,  0,  0,  0,-10},
        {-10,  0,  5, 10, 10,  5,  0,-10},
        {-10,  5,  5, 10, 10,  5,  5,-10},
        {-10,  0, 10, 10, 10, 10,  0,-10},
        {-10, 10, 10, 10, 10, 10, 10,-10},
        {-10,  5,  0,  0,  0,  0,  5,-10},
        {-20,-10,-10,-10,-10,-10,-10,-20}};
    static int bishopBoardRev[][]={
        {-20, -10, -10, -10, -10, -10, -10, -20},
        {-10, 5, 0, 0, 0, 0, 5, -10},
        {-10, 10, 10, 10, 10, 10, 10, -10},
        {-10, 0, 10, 10, 10, 10, 0, -10},
        {-10, 5, 5, 10, 10, 5, 5, -10},
        {-10, 0, 5, 10, 10, 5, 0, -10},
        {-10, 0, 0, 0, 0, 0, 0, -10},
        {-20, -10, -10, -10, -10, -10, -10, -20}};

    static int queenBoard[][]={
        {-20,-10,-10, -5, -5,-10,-10,-20},
        {-10,  0,  0,  0,  0,  0,  0,-10},
        {-10,  0,  5,  5,  5,  5,  0,-10},
        { -5,  0,  5,  5,  5,  5,  0, -5},
        {  0,  0,  5,  5,  5,  5,  0, -5},
        {-10,  5,  5,  5,  5,  5,  0,-10},
        {-10,  0,  5,  0,  0,  0,  0,-10},
        {-20,-10,-10, -5, -5,-10,-10,-20}};
    static int queenBoardRev[][]={
        {-20, -10, -10, -5, -5, -10, -10, -20},
        {-10, 0, 0, 0, 0, 5, 0, -10},
        {-10, 0, 5, 5, 5, 5, 5, -10},
        {-5, 0, 5, 5, 5, 5, 0, 0},
        {-5, 0, 5, 5, 5, 5, 0, -5},
        {-10, 0, 5, 5, 5, 5, 0, -10},
        {-10, 0, 0, 0, 0, 0, 0, -10},
        {-20, -10, -10, -5, -5, -10, -10, -20}};

    static int kingMidBoard[][]={
        {-30,-40,-40,-50,-50,-40,-40,-30},
        {-30,-40,-40,-50,-50,-40,-40,-30},
        {-30,-40,-40,-50,-50,-40,-40,-30},
        {-30,-40,-40,-50,-50,-40,-40,-30},
        {-20,-30,-30,-40,-40,-30,-30,-20},
        {-10,-20,-20,-20,-20,-20,-20,-10},
        { 20, 20,  0,  0,  0,  0, 20, 20},
        { 20, 30, 10,  0,  0, 10, 30, 20}};
    static int kingMidBoardRev[][]={
        {20, 30, 10, 0, 0, 10, 30, 20},
        {20, 20, 0, 0, 0, 0, 20, 20},
        {-10, -20, -20, -20, -20, -20, -20, -10},
        {-20, -30, -30, -40, -40, -30, -30, -20},
        {-30, -40, -40, -50, -50, -40, -40, -30},
        {-30, -40, -40, -50, -50, -40, -40, -30},
        {-30, -40, -40, -50, -50, -40, -40, -30},
        {-30, -40, -40, -50, -50, -40, -40, -30}};

    static int kingEndBoard[][]={
        {-50,-40,-30,-20,-20,-30,-40,-50},
        {-30,-20,-10,  0,  0,-10,-20,-30},
        {-30,-10, 20, 30, 30, 20,-10,-30},
        {-30,-10, 30, 40, 40, 30,-10,-30},
        {-30,-10, 30, 40, 40, 30,-10,-30},
        {-30,-10, 20, 30, 30, 20,-10,-30},
        {-30,-30,  0,  0,  0,  0,-30,-30},
        {-50,-30,-30,-30,-30,-30,-30,-50}};
    static int kingEndBoardRev[][]={
        {-50, -30, -30, -30, -30, -30, -30, -50},
        {-30, -30, 0, 0, 0, 0, -30, -30},
        {-30, -10, 20, 30, 30, 20, -10, -30},
        {-30, -10, 30, 40, 40, 30, -10, -30},
        {-30, -10, 30, 40, 40, 30, -10, -30},
        {-30, -10, 20, 30, 30, 20, -10, -30},
        {-30, -20, -10, 0, 0, -10, -20, -30},
        {-50, -40, -30, -20, -20, -30, -40, -50}};

    public static int eval(ChessBoard board, int depth){
        Piece[][] b = board.getBoard();
        boolean side = true;

        int counter = 0;
        int material = rateMaterial(b, side);
        counter += rateAttack(b);
        counter += rateMoveability(board, b, side);
        counter += ratePosition(board, b, material, !side);
        counter += material;

        material = rateMaterial(b, !side);
        counter -= rateAttack(b);
        counter -= rateMoveability(board, b, !side);
        counter -= ratePosition(board, b, material, !side);
        counter -= material;

        return counter * (1 + depth/10); //prioritizes higher depth
    }

    public static int rateMaterial(Piece[][] b, boolean side){
        int counter = 0;
        int bishopCounter = 0;
        for (Piece[] x : b) {
			for (Piece y : x) {
                if(y==null) continue;
                if(y.getColor() != side) continue;
                if(y instanceof Pawn){
                    counter += 100;
                    continue;
                }else if(y instanceof Bishop ){
                    bishopCounter += 1;
                    continue;
                }else if(y instanceof Knight){
                    counter += 300;
                    continue;
                }else if(y instanceof Rook){
                    counter += 500;
                    continue;
                }else if(y instanceof Queen){
                    counter += 900;
                    continue;
                }
            }
        }
        if(bishopCounter >= 2){
            counter += 300 * bishopCounter;
        }else{
            if(bishopCounter == 1) counter += 250;
        }
        return counter;
    }

    public static int rateAttack(Piece[][] b){
        return 0;
    }

    public static int moveNumber(ChessBoard board, Piece[][] b, boolean side){
        int allLegalMoves = 0;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (b[i][j] != null && b[i][j].getColor() == side) {
					ArrayList<Move> temp = board.getLegalMoves(i, j, true);
					allLegalMoves += temp.size();
					
				}
			}
		}

		return allLegalMoves;
    }


    public static int rateMoveability(ChessBoard board, Piece[][] b, boolean side){
        int counter = 0;
        int numOfMoves = moveNumber(board, b, side);
        counter = numOfMoves * 5;
        if(numOfMoves == 0){
            counter -= 20000;
        }
        return counter;

    }

    public static int ratePosition(ChessBoard board, Piece[][] b, int material, boolean side){
        int counter = 0;
        for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
                if (b[i][j] != null && b[i][j].getColor() == side) {
                    if(side){
                        Piece piece = b[i][j];
                        if (piece instanceof Pawn) {
                            counter += pawnBoard[i][j];
                        } else if (piece instanceof Knight) {
                            counter += knightBoard[i][j];
                        } else if (piece instanceof Bishop) {
                            counter += bishopBoard[i][j];
                        } else if (piece instanceof Rook) {
                            counter += rookBoard[i][j];
                        } else if (piece instanceof Queen) {
                            counter += queenBoard[i][j];
                        } else if (piece instanceof King) {
                            if (material>=1750) {
                                counter += kingMidBoard[i][j];
                                counter += board.getLegalMoves(i, j, true).size() * 10;
                            } else {
                                counter += kingEndBoard[i][j];
                                counter += board.getLegalMoves(i, j, true).size() * 30;
                            }
                        }
                    }else{
                        Piece piece = b[i][j];
                        if (piece instanceof Pawn) {
                            counter += pawnBoardRev[i][j];
                        } else if (piece instanceof Knight) {
                            counter += knightBoardRev[i][j];
                        } else if (piece instanceof Bishop) {
                            counter += bishopBoardRev[i][j];
                        } else if (piece instanceof Rook) {
                            counter += rookBoardRev[i][j];
                        } else if (piece instanceof Queen) {
                            counter += queenBoardRev[i][j];
                        } else if (piece instanceof King) {
                            if (material>=1750) {
                                counter += kingMidBoardRev[i][j];
                                counter += board.getLegalMoves(i, j, true).size() * 10;
                            } else {
                                counter += kingEndBoardRev[i][j];
                                counter += board.getLegalMoves(i, j, true).size() * 30;
                            }
                        }
                    }
                }
            }
        }
        return counter;
    }
    
}
