package utils.bot.DanielBotClasses;

import static utils.bot.DanielBotClasses.BitBoardBitManipulation.*;
import static utils.bot.DanielBotClasses.BitBoardAttacks.*;
import static utils.bot.DanielBotClasses.BitBoardConsts.*;
import static utils.bot.DanielBotClasses.BitBoardChessBoard.*;
import static utils.bot.DanielBotClasses.BitBoardSearch.*;
import static utils.bot.DanielBotClasses.BitBoardSettings.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import utils.*;

// uses the polyglot format
// http://hgm.nubati.net/book_format.html is a good explanation for how the format works
public class BitBoardBook {
	static int bookSize;
	static RandomAccessFile bookFile;
	
	// initiates the file and book size
	public static void openBook() {
		try {
			bookFile = new RandomAccessFile("src//utils//bot//DanielBotResources//" + bookFilePath, "r");
			Path path = Paths.get("src//utils//bot//DanielBotResources//" + bookFilePath);
			bookSize = (int) (Files.size(path) / 16);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// generates the zobrist key for the current position, using polyglot's random numbers
	public static long getPolyglotZobrist() {
		long finalHash = 0L;
		
		for (int bbPiece = P; bbPiece <= k; bbPiece++) {
			long bitboard = bbBoard.bitboards[bbPiece];
			
			while (bitboard != 0) {
				int square = getLS1BIndex(bitboard);
				
				// polyglot's zobrist hashes only hash en passant if there's a pawn that can capture en passant, hence this complication
				if (bbBoard.side == white && bbPiece == P) {
					// generate bbBoard.enPassant captures
					if (bbBoard.enPassant != no_sq) {
						long enPassantAttacks = pawnAttacks[bbBoard.side][square] & (1L << bbBoard.enPassant);
						
						// if en passant is available
						if (enPassantAttacks != 0) {
							finalHash ^= Random64[772 + bbBoard.enPassant % 8];
						}
					}
				} else if (bbBoard.side == black && bbPiece == p) {
					// generate bbBoard.enPassant captures
					if (bbBoard.enPassant != no_sq) {
						long enPassantAttacks = pawnAttacks[bbBoard.side][square] & (1L << bbBoard.enPassant);
						
						// if en passant is available
						if (enPassantAttacks != 0) {
							finalHash ^= Random64[772 + bbBoard.enPassant % 8];
						}
					}
				}
				
				int rank = 7 - (square / 8);
				int file = square % 8;
				
				finalHash ^= Random64[polyglotPiece[bbPiece] * 64 + 8 * rank + file];
				
				bitboard &= ~(1L << square);
			}
		}
		
		if ((bbBoard.castle & wk) != 0) finalHash ^= Random64[768];
		if ((bbBoard.castle & wq) != 0) finalHash ^= Random64[769];
		if ((bbBoard.castle & bk) != 0) finalHash ^= Random64[770];
		if ((bbBoard.castle & bq) != 0) finalHash ^= Random64[771];
		
		if (bbBoard.side == white) {
			finalHash ^= Random64[780];
		}
		
		return finalHash;
	}
	
	// polyglot books are sorted in order of zobrist key, from lowest to highest. this uses binary search to find out key
	public static int findPos(long key) {
		int left, right, mid;
		
		PolyglotEntry polyglotEntry = null;
		
		left = 0;
		right = bookSize - 1;
		
		// standard binary search
		while (Long.compareUnsigned(left, right) < 0) {
			mid = (left + right) / 2;
			
			polyglotEntry = readEntry(mid);
			if (Long.compareUnsigned(key, polyglotEntry.key) <= 0) {
				right = mid;
			} else {
				left = mid + 1;
			}
		}
		
		polyglotEntry = readEntry(left);
		
		// check if entry's key matches our board's key
		return (polyglotEntry.key == key) ? left : bookSize;
	}
	
	// reads the nth entry in the file
	public static PolyglotEntry readEntry(int n) {
		try {
			bookFile.seek(n * 16);
			
			long key = bookFile.readLong();
			short move = bookFile.readShort();
			short count = bookFile.readShort();
						
			return new PolyglotEntry(key, move, count);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	// given the zobrist key, this function finds the move
	public static int bookMove(long key) {
		int firstPos;
		PolyglotEntry polyglotEntry = null;
		
		firstPos = findPos(key);
		
		// if we did not find the position in the book, we leave
		if (firstPos == bookSize) return -1;
		
		int bestMove = -1;
		int bestScore = 0;
		
		// because a position can have multiple moves, we have to choose one
		for (int pos = firstPos; pos < bookSize; pos++) {
			polyglotEntry = readEntry(pos);
			
			if (polyglotEntry.key != key) break;
			
			// chooses one randomly based off of weight
			if (!bestBookMoveOnly) {
				bestScore += polyglotEntry.count;
				if (Math.abs((int) (Math.random() * (bestScore))) < polyglotEntry.count) bestMove = polyglotEntry.move;
			} else { // chooses the one with the highest weight
				if (polyglotEntry.count > bestScore) {
					bestScore = polyglotEntry.count;
					bestMove = polyglotEntry.move;
				}
			}
		}
		
		return bestMove;
	}
	
	// gets a book move
	public static int getBookMove() {
		int move = bookMove(getPolyglotZobrist());
		
		// no move found, just break
		if (move == -1) return -1;
		
		// polyglot represents its moves differently than we do, so we have to do a bit of translation
		int endFile = 			(move & 0b000000000000111) >> 0;
		int endRank = 			(move & 0b000000000111000) >> 3;
		int startFile = 		(move & 0b000000111000000) >> 6;
		int startRank = 		(move & 0b000111000000000) >> 9;
		int promotionPiece = 	(move & 0b111000000000000) >> 12;
		
		int startSquare = (7 - startRank) * 8 + startFile;
		int endSquare = (7 - endRank) * 8 + endFile;
		int flag = 0;
		
		int piece = 0;

		// a bit of manual checking to see if the move is castling
		if (startSquare == e1 && endSquare == h1) {
			if (getBit(bbBoard.bitboards[K], e1) != 0 && getBit(bbBoard.occupancies[both], f1) == 0 && getBit(bbBoard.occupancies[both], g1) == 0) {
				endSquare = g1;
				flag = 0b0010;
			}
		}
		if (startSquare == e1 && endSquare == a1) {
			if (getBit(bbBoard.bitboards[K], e1) != 0 && getBit(bbBoard.occupancies[both], d1) == 0 && getBit(bbBoard.occupancies[both], c1) == 0 && getBit(bbBoard.occupancies[both], b1) == 0) {
				endSquare = c1;
				flag = 0b0010;
			}
		}
		if (startSquare == e8 && endSquare == h8) {
			if (getBit(bbBoard.bitboards[k], e8) != 0 && getBit(bbBoard.occupancies[both], f8) == 0 && getBit(bbBoard.occupancies[both], g8) == 0) {
				endSquare = g8;
				flag = 0b0010;
			}
		}
		if (startSquare == e8 && endSquare == a8) {
			if (getBit(bbBoard.bitboards[k], e8) != 0  && getBit(bbBoard.occupancies[both], d8) == 0 && getBit(bbBoard.occupancies[both], c8) == 0 && getBit(bbBoard.occupancies[both], b8) == 0) {
				endSquare = c8;
				flag = 0b0010;
			}
		}
		
		// checking for capture flag
		for (int bbPiece = P; bbPiece <= k; bbPiece++) {
			if (getBit(bbBoard.bitboards[bbPiece], startSquare) != 0) {
				piece = bbPiece;
			}
			
			if (getBit(bbBoard.bitboards[bbPiece], endSquare) != 0) {
				flag = 0b0100;
			}
		}
		
		if (piece == P || piece == p) {
			// checking pawn diagonal attacks for special capture flag en passant case
			if (startSquare == endSquare - 7) {
				if (getBit(bbBoard.bitboards[P], startSquare - 1) != 0) {
					flag = 0b0101;
				}
			} else if (startSquare == endSquare - 9) {
				if (getBit(bbBoard.bitboards[P], startSquare + 1) != 0) {
					flag = 0b0101;
				}
			} else if (startSquare == endSquare + 7) {
				if (getBit(bbBoard.bitboards[p], startSquare + 1) != 0) {
					flag = 0b0101;
				}
			} else if (startSquare == endSquare + 9) {
				if (getBit(bbBoard.bitboards[p], startSquare - 1) != 0) {
					flag = 0b0101;
				}
			}
			
			// checking for double push
			if (startSquare == endSquare + 16 || startSquare == endSquare - 16) {
				flag = 0b0001;
			}
		}
		
		// check promotion
		if (promotionPiece != 0) {
			// shift it left by 1
			promotionPiece -= N;
			
			// set promotion flag
			flag |= 0b1000;
			
			// set promotion piece
			flag |= promotionPiece;
		}
				
		return encodeMove(startSquare, endSquare, flag);
	}
}