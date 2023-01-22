package utils.bot.DanielBotClasses;

import static utils.bot.DanielBotClasses.BitBoardMoveGeneration.*;
import static utils.bot.DanielBotClasses.BitBoardConsts.*;
import static utils.bot.DanielBotClasses.BitBoardBitManipulation.*;
import static utils.bot.DanielBotClasses.BitBoardChessBoard.*;
import static utils.bot.DanielBotClasses.BitBoardIO.*;
import static utils.bot.DanielBotClasses.BitBoardSearch.*;
import static utils.bot.DanielBotClasses.BitBoardRepetition.*;

import java.util.Scanner;

public class BitBoardUCI {
	public static int parseMove(String moveString) {
		moves moveList = new moves();
		
		generateMoves(moveList);
		
		int sourceSquare = (moveString.charAt(0) - 'a') + (8 - (moveString.charAt(1) - '0')) * 8;
		int targetSquare = (moveString.charAt(2) - 'a') + (8 - (moveString.charAt(3) - '0')) * 8;
				
		for (int moveCount = 0; moveCount < moveList.count; moveCount++) {
			int move = moveList.moves[moveCount];
			
			if (sourceSquare == getMoveSource(move) && targetSquare == getMoveTarget(move)) {
				int promotedPiece = getMovePromoted(move);
				
				if (promotedPiece != 0) {
					// promoted to queen
	                if ((promotedPiece == Q || promotedPiece == q) && moveString.charAt(4) == 'q')
	                    // return legal move
	                	return move;

	                // promoted to rook
	                else if ((promotedPiece == R || promotedPiece == r) && moveString.charAt(4) == 'r')
	                    // return legal move
	                    return move;
	                
	                // promoted to bishop
	                else if ((promotedPiece == B || promotedPiece == b) && moveString.charAt(4) == 'b')
	                    // return legal move
	                    return move;
	                
	                // promoted to knight
	                else if ((promotedPiece == N || promotedPiece == n) && moveString.charAt(4) == 'n')
	                    // return legal move
	                    return move;
	                
	                // continue the loop on possible wrong promotions (e.g. "e7e8f")
	                continue;
				}
				
				return move;
			}
		}
		
		return 0;
	}
	
	public static void parsePosition(String command) {
		int index = 9;
		
		if (command.contains("startpos")) {
			parseFen(startPosition);
		} else {
			index = command.indexOf("fen");
			
			if (index == -1) {
				parseFen(startPosition);
			} else {
				index += 4;
				
				parseFen(command.substring(index));
			}
		}
		
		index = command.indexOf("moves");
		
		if (index != -1) {
			index += 6;
			command = command.substring(index);
			String splitMoves[] = command.split(" ");
			
			for (String x : splitMoves) {
				int move = parseMove(x);
				
				if (move == 0) break;
								
				makeMove(move, allMoves);
			}
		}
		
		clearHistory();
	}
	
	public static void parseGo(String command) {
		int depth = -1;
		
		int index = command.indexOf("depth");
		
		if (index != -1) {
			depth = Integer.parseInt((command.substring(index + 6).split(" ", 2)[0]));
		} else {
			depth = 6;
		}
		
		searchPosition();
	}
	
	public static void uciLoop() {
		Scanner input = new Scanner(System.in);
		String command = "";
		
		while (true) {
			command = input.nextLine();
			
			if (command.equals("uci")) {
				System.out.println("id name DanielBot");
				System.out.println("id author Daniel Zeng");
				System.out.println("uciok");
			} else if (command.equals("isready")) {
				System.out.println("readyok");
			} else if (command.startsWith("position")) {
				parsePosition(command);
			} else if (command.startsWith("go")) {
				parseGo(command);
			} else if (command.startsWith("ucinewgame")) {
				parsePosition("position startpos");
			} else if (command.equals("quit")) {
				break;
			}
		}
	}
}