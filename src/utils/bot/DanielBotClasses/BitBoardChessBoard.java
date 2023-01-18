package utils.bot.DanielBotClasses;

import static utils.bot.DanielBotClasses.BitBoardConsts.*;
import static utils.bot.DanielBotClasses.BitBoardBitManipulation.*;


import java.util.Arrays;

public class BitBoardChessBoard {
	public static long bitboards[] = new long[12];
	public static long occupancies[] = new long[3];
	
	public static int side = -1;
	
	public static int enPassant = no_sq;
	
	public static int castle;
	
	public static void parseFen(String fen) {
		// reset bitboards and occupancies
		Arrays.fill(bitboards, 0);
		Arrays.fill(occupancies, 0);
		
		int fenIndex = 0;
		
		for (int rank = 0; rank < 8; rank++) {
			for (int file = 0; file < 8; file++) {
				int square = rank * 8 + file;
				char c = fen.charAt(fenIndex);
				
				if (Character.isAlphabetic(c)) {
					// add piece to bitboard
					int piece = charPieces[c];
					bitboards[piece] |= (1L << square);
				} else if (Character.isDigit(c)) {
					// skip empty spaces
					int offset = c - '1';
					file += offset;
				} else if (c == '/') {
					// decrement file to account for "wasted" fen
					file--;
				}
				fenIndex++;
			}
		}
		
		fenIndex++;
		
		side = (fen.charAt(fenIndex) == 'w') ? white : black;
		
		fenIndex += 2;
		
		while (fen.charAt(fenIndex) != ' ') {
			switch (fen.charAt(fenIndex)) {
			case 'K': castle |= wk; break;
			case 'Q': castle |= wq; break;
			case 'k': castle |= bk; break;
			case 'q': castle |= bq; break;
			case '-': break;
			}
			
			fenIndex++;
		}
		
		fenIndex++;
		
		// set en passant
		if (fen.charAt(fenIndex) != '-') {
			int file = fen.charAt(fenIndex) - 'a';
			int rank = 8 - (fen.charAt(fenIndex + 1) - '0');
			
			enPassant = rank * 8 + file;
		} else {
			enPassant = no_sq;
		}
		
		// set white occupancies
		for (int piece = P; piece <= K; piece++) {
			occupancies[white] |= bitboards[piece];
		}
		
		// set black occupancies
		for (int piece = p; piece <= k; piece++) {
			occupancies[black] |= bitboards[piece];
		}
		
		// set both occupancies
		occupancies[both] |= occupancies[white];
		occupancies[both] |= occupancies[black];
	}
}