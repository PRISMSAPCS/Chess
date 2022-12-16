package utils;

public class Main {
    public static void main(String[] args){
        ChessBoard board = new ChessBoard();
        // board.enableLogging();
        GUI gui = new GUI(board);
        while(true) {

            Move move = gui.getMove();
            board.submitMove(move);
            gui.applyMove(move);
            if(board.gameOver(board.getSide())>0) {
                //test if game over
                if(board.gameOver(board.getSide()) == 1) {
                    GUI.popInfo("Game Over! " + ((board.getSide())? "Black": "White") + " Win!");
                } else if(board.gameOver(board.getSide()) == 2) {
                    GUI.popInfo("Game Over! Stalemate");
                }
            	board.restart();
            	gui.drawBoard();
            }
        }
    }
}
