package utils;

import java.util.ArrayList;

import javax.swing.JList;
import javax.swing.JOptionPane;

public class Main {
	/**
	 * Play a normal game with 2 players on a board
	 * @param board board
	 * @param gui GUI corresponding to the board
	 * @param moveSource sources of moving. An array with 2 elements, represents the two sides of players
	 */
	public static void normalGame(ChessBoard board, GUI gui, CanGetMove[] moveSource) {
		String whiteName = moveSource[0].getName();
		String blackName = moveSource[1].getName();
		board.enableLogging(whiteName, blackName);
		while(true) {
			Move move = moveSource[board.getSide()? 1: 0].getMove();
			board.submitMove(move);
			gui.applyMove(move);
			if(board.gameOver(board.getSide())>0) {
				//test if game over
				if(board.gameOver(board.getSide()) == 1) {
					GUI.popInfo("Game Over! " + ((board.getSide())? "Black": "White") + " Wins!");
					break;
				} else if(board.gameOver(board.getSide()) == 2) {
					GUI.popInfo("Game Over! Stalemate");
					break;
				}
				//            	board.restart();
				//            	gui.drawBoard();
			}
		}
	}

	public static void replayGame(ChessBoard board, GUI gui, String replayFileName) throws InterruptedException {
		board.setLoadingSimulation(true);
		ArrayList<Move> moves = board.simulatePlay(replayFileName);
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
    public static void main(String[] args) throws InterruptedException {
        JList<String> list = new JList<>(new String[]{"Play Game", "Simulate Game", "Train Bot"});
        while(list.getSelectedValue() == null) {
            JOptionPane.showInputDialog(null, list, "Choose the game mode", JOptionPane.QUESTION_MESSAGE);
        }
        ChessBoard board = new ChessBoard();
		GUI gui = new GUI(board);
        if(list.getSelectedValue().equals("Play Game")) {
			normalGame(board, gui, new GUI[] {gui, gui});
        } else if(list.getSelectedValue().equals("Simulate Game")) {
        	replayGame(board, gui, "./log.pgn");
        } else {
			//* USAGE:
			//* 1. remove comment of the following code
			//* 2. replace all YourChessBot with the class name of your own chess bot
			//// normalGame(board, gui, new YourChessBot[] {new YourChessBot(board), new YourChessBot(board)});
		}
    }
}
