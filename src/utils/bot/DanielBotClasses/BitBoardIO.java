package utils.bot.DanielBotClasses;

import static utils.bot.DanielBotClasses.BitBoardBitManipulation.*;
import static utils.bot.DanielBotClasses.BitBoardChessBoard.*;
import static utils.bot.DanielBotClasses.BitBoardConsts.*;
import static utils.bot.DanielBotClasses.BitBoardMoveGeneration.*;
import static utils.bot.DanielBotClasses.BitBoardZobrist.*;

// basic IO functions that make debugging a lot easier
public class BitBoardIO {
	// print a bitboard
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
	
	// print the whole chess board
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
	    System.out.printf("     Hash Key: %x\n\n", generateHashKey());
	}
	
	// print a move (for UCI purposes)
	public static void printMove(int move) {
		System.out.printf("%s%s%c", squareToCoordinates[getMoveSource(move)], squareToCoordinates[getMoveTarget(move)], (getMovePromoted(move) != 0) ? asciiPieces[getMovePromoted(move)] : ' ');
	}


	// print a move list
	public static void printMoveList(moves moveList) {
		System.out.printf("\n    move    piece   capture   double    enpass    castling\n\n");
	    
	    // loop over moves within a move list
	    for (int moveCount = 0; moveCount < moveList.count; moveCount++) {
	        // init move
	    	int move = moveList.moves[moveCount];
	        System.out.printf("    %s%s%c   %c       %d         %d         %d         %d\n", squareToCoordinates[getMoveSource(move)],
                                                                                  squareToCoordinates[getMoveTarget(move)],
                                                                                  (getMovePromoted(move) != 0) ? asciiPieces[getMovePromoted(move)] : ' ',
                                                                                  asciiPieces[getMovePiece(move)],
                                                                                  getMoveCapture(move) ? 1 : 0,
                                                                                  getMoveDouble(move) ? 1 : 0,
                                                                                  getMoveEnPassant(move) ? 1 : 0,
                                                                                  getMoveCastling(move) ? 1 : 0);
	    }
	    
	    // print total number of moves
        System.out.printf("\n\n    Total number of moves: %d\n\n", moveList.count);
	}
}