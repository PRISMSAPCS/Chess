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
        }
    }
}
