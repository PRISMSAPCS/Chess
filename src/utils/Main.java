package utils;

import java.util.ArrayList;

import javax.swing.JList;
import javax.swing.JOptionPane;

import utils.bot.*;

public class Main {
	/**
	 * Play a normal game with 2 players on a board
	 * @param board board
	 * @param gui GUI corresponding to the board
	 * @param moveSource sources of moving. An array with 2 elements, represents the two sides of players
	 */
	public static void normalGame(ChessBoard board, GUI gui, CanGetMove[] moveSource) {
		String whiteName = moveSource[1].getName();
		String blackName = moveSource[0].getName();
		board.enableLogging(whiteName, blackName);
		while(true) {
			System.out.println("--------------------" + (board.getSide() ? "White": "Black") + "'s turn --------------------");
			long startTime = System.currentTimeMillis();
			Move move = moveSource[board.getSide()? 1: 0].getMove();
			long endTime = System.currentTimeMillis();
			System.out.println("Time used: " + (endTime - startTime) + "ms");
			board.submitMove(move);
			gui.applyMove(move);
			
			if(board.gameOver(board.getSide())>0) {
				//test if game over
				if(board.gameOver(board.getSide()) == 1) {
					GUI.popInfo("Game Over! " + ((board.getSide())? "Black": "White") + " Wins!");
					break;
				} else if(board.gameOver(board.getSide()) == 2) {
					GUI.popInfo("Game Over! Draw");
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
					GUI.popInfo("Game Over! Draw");
				}
				//board.restart();
				//gui.drawBoard();
			}
			moves.remove(0);
		}
	}
    public static void main(String[] args) throws InterruptedException {
    	Eval.flip();
        JList<String> list = new JList<>(new String[]{"Play Game", "Simulate Game", "Train Bot", "Play Against Bot As White", "Play Against Bot As Black"});
        while(list.getSelectedValue() == null) {
            JOptionPane.showInputDialog(null, list, "Choose the game mode", JOptionPane.QUESTION_MESSAGE);
        }
        ChessBoard board = new ChessBoard();
        if(list.getSelectedValue().equals("Play Game")) {
			GUI gui = new GUI(board, false);
			normalGame(board, gui, new GUI[] {gui, gui});
        } else if(list.getSelectedValue().equals("Simulate Game")) {
			GUI gui = new GUI(board, false);
        	replayGame(board, gui, "./log.pgn");
        } else if (list.getSelectedValue().equals("Train Bot")){
			//* USAGE:
			//* 1. remove comment of the following code
			//* 2. replace all YourChessBot with the class name of your own chess bot
			//// normalGame(board, gui, new YourChessBot[] {new YourChessBot(board), new YourChessBot(board)});
			GUI gui = new GUI(board, true);
<<<<<<< HEAD
			normalGame(board, gui, new CanGetMove[] {new DanielBot(board), new TonyNegaMaxPVSTT(board, true)});
=======
			normalGame(board, gui, new CanGetMove[] {new DanielBot(board), new GraydenBot(board, 2)});
>>>>>>> aacce56cb9cafd55c35b598451098e26c169ca0b
		} else if (list.getSelectedValue().equals("Play Against Bot As White")){
			GUI gui = new GUI(board, false);
			normalGame(board, gui, new CanGetMove[] {new DanielBot(board), gui});
		} else if (list.getSelectedValue().equals("Play Against Bot As Black")){
			GUI gui = new GUI(board, false);
			normalGame(board, gui, new CanGetMove[] {gui, new DanielBot(board)});
		}
    }
}
