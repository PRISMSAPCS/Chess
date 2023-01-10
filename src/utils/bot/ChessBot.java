package utils.bot;

import utils.CanGetMove;
import utils.ChessBoard;

/***
 !!! WARNING: DON'T WRITE YOUR CODE IN THIS CLASS!!!
 ***/
public abstract class ChessBot implements CanGetMove {
    public abstract String getName();

    private ChessBoard board;  // the chess board associates with this chess bot
    public ChessBot(ChessBoard board) {
        this.board = board;
    }

    public ChessBoard getBoard() {
        return this.board;
    }
}
