package utils.bot.DanielBotClasses;

import static utils.bot.DanielBotClasses.BitBoardBitManipulation.getBit;
import static utils.bot.DanielBotClasses.BitBoardChessBoard.*;
import static utils.bot.DanielBotClasses.BitBoardConsts.*;
import static utils.bot.DanielBotClasses.BitBoardMoveGeneration.*;

public class BitBoardIO {
	public static void print(long bitboard) {
		for (int rank = 0; rank < 8; rank++) {
			for (int file = 0; file < 8; file++) {
				// print ranks
				if (file == 0)
					System.out.printf("  %d ", 8 - rank);
					
				System.out.printf(" %c", (getBit(bitboard, rank * 8 + file) != 0) ? '1' : '.');
			}
			
			System.out.println();
		}
		
		// print board files
		System.out.println("\n     a b c d e f g h");
		
		System.out.printf("       Bitboard: %d\n\n", bitboard);
	}

	public static void printAttackedSquares(int side) {
		for (int rank = 0; rank < 8; rank++) {
			for (int file = 0; file < 8; file++) {
				// print ranks
				if (file == 0)
					System.out.printf("  %d ", 8 - rank);
					
				System.out.printf(" %c", (isSquareAttacked(rank * 8 + file, side) == 1) ? '1' : '.');
			}
			
			System.out.println();
		}
		
		// print board files
		System.out.println("\n     a b c d e f g h");
	}
	
	public static void printBoard() {
	    // print offset
	    System.out.println();

	    // loop over board ranks
	    for (int rank = 0; rank < 8; rank++) {
	        // loop ober board files
	        for (int file = 0; file < 8; file++) {
	            // init square
	            int square = rank * 8 + file;
	            
	            // print ranks
	            if (file == 0) {
	                System.out.printf("  %d ", 8 - rank);
	            }
	            
	            // define piece variable
	            int piece = -1;
	            
	            // loop over all piece bitboards
	            for (int bb_piece = P; bb_piece <= k; bb_piece++) {
	                if (getBit(bitboards[bb_piece], square) != 0)
	                    piece = bb_piece;
	            }
	            
	            // print different piece set depending on OS
	            System.out.printf(" %c", (piece == -1) ? '.' : asciiPieces[piece]);
	        }
	        
	        // print new line every rank
	        System.out.println();
	    }
	    
	    // print board files
	    System.out.println("\n     a b c d e f g h\n");
	    
	    // print side to move
	    System.out.printf("     Side:     %s\n", (side == white) ? "white" : "black");
	    
	    // print enpassant square
	    System.out.printf("     Enpassant:   %s\n", (enPassant != no_sq) ? squareToCoordinates[enPassant] : "no");
	    
	    // print castling rights
	    System.out.printf("     Castling:  %c%c%c%c\n\n", ((castle & wk) != 0) ? 'K' : '-',
	                                           ((castle & wq) != 0) ? 'Q' : '-',
	                                           ((castle & bk) != 0) ? 'k' : '-',
	                                           ((castle & bq) != 0) ? 'q' : '-');
	}
}