package utils;

import java.util.ArrayList;

import javax.swing.JList;
import javax.swing.JOptionPane;

public class Main {
    public static void main(String[] args) throws InterruptedException{
        JList<String> list = new JList<>(new String[]{"Play Game", "Simulate Game"});
        while(list.getSelectedValue() == null) {
            JOptionPane.showInputDialog(null, list, "Choose the game mode", JOptionPane.QUESTION_MESSAGE);
        }
        ChessBoard board = new ChessBoard();
        GUI gui = new GUI(board);
        if(list.getSelectedValue() == "Play Game") {
            board.enableLogging();
	        while(true) {
	
	            Move move = gui.getMove();
	            board.submitMove(move);
	            gui.applyMove(move);
	            if(board.gameOver(board.getSide())>0) {
	                //test if game over
	                if(board.gameOver(board.getSide()) == 1) {
	                    GUI.popInfo("Game Over! " + ((board.getSide())? "Black": "White") + " Wins!");
	                } else if(board.gameOver(board.getSide()) == 2) {
	                    GUI.popInfo("Game Over! Stalemate");
	                }
	//            	board.restart();
	//            	gui.drawBoard();
	            }
	        }
        } else {
        	board.setLoadingSimulation(true);
        	ArrayList<Move> moves = board.simulatePlay("./log.pgn");
	    	board.restart();
	    	board.setLoadingSimulation(false);
	    	gui.drawBoard();
            gui.enableButton(true);
	        while(moves.isEmpty() == false) {
            	board.setProceed(false);
	            while(board.getProceed() == false) {
	            	Thread.sleep(50);
	            }
	            Move move = moves.get(0);
	            board.submitMove(move);
	            gui.applyMove(move);
	            if(board.gameOver(board.getSide())>0) {
	                //test if game over
	                if(board.gameOver(board.getSide()) == 1) {
	                    GUI.popInfo("Game Over! " + ((board.getSide())? "Black": "White") + " Win!");
	                } else if(board.gameOver(board.getSide()) == 2) {
	                    GUI.popInfo("Game Over! Stalemate");
	                }
	            	//board.restart();
	            	//gui.drawBoard();
	            }
	            moves.remove(0);
	        }
        }
    }
}
