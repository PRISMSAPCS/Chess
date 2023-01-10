package utils.bot;

import utils.CanGetMove;
import utils.ChessBoard;

/***
 !!! WARNING: DON'T WRITE YOUR CODE IN THIS CLASS!!!
 ***/
public abstract class ChessBot implements CanGetMove {
    public abstract String getName();

    private boolean side;      // Side of this chess bot. white = true; black = false.
    private ChessBoard board;  // the chess board associates with this chess bot
    public ChessBot(boolean side, ChessBoard board) {
        this.side = side;
        this.board = board;
    }

    public boolean getSide() {
        return this.side;
    }
    public ChessBoard getBoard() {
        return this.board;
    }
}
