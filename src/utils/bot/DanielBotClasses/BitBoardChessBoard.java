package utils.bot.DanielBotClasses;

import static utils.bot.DanielBotClasses.BitBoardConsts.*;
import static utils.bot.DanielBotClasses.BitBoardAttacks.*;
import static utils.bot.DanielBotClasses.BitBoardEvaluation.*;
import static utils.bot.DanielBotClasses.BitBoardBitManipulation.*;
import static utils.bot.DanielBotClasses.BitBoardZobrist.*;

import java.util.Arrays;

public class BitBoardChessBoard {	
	// bitboards contains 12 bitboards - one for each chess piece and color
	public long bitboards[] = new long[12];
	
	// occupancies contains 3 bitboards which give the location of all pieces - white, black, and both
	public long occupancies[] = new long[3];
	
	public int side = -1;
	public int enPassant = no_sq;
	public int castle;
	public long hashKey;
	
	public int moveRule = 0;
	public int totalMoves = 0;
	
	// basically a giant stack that stores past versions of the board for when we undo moves
	public long[][] pastBitBoards = new long[1000][12];
	public long[][] pastOccupancies = new long[1000][3];
	public byte[] pastSides = new byte[1000];
	public byte[] pastEnPassant = new byte[1000];
	public byte[] pastCastle = new byte[1000];
	public long[] pastHashKey = new long[1000];
	public int[] pastMoveRule = new int[1000];
	public int index = 0;
	
	// normal constructor
	public BitBoardChessBoard() {
		
	}
	
	// copy constructor
	public BitBoardChessBoard(BitBoardChessBoard other) {
		bitboards = other.bitboards.clone();
		occupancies = other.occupancies.clone();
		
		side = other.side;
		enPassant = other.enPassant;
		castle = other.castle;
		hashKey = other.hashKey;
		
		moveRule = other.moveRule;
		totalMoves = other.totalMoves;
		
		repetitionTable = other.repetitionTable.clone();
		pastZobrists = other.pastZobrists.clone();
		zobristIndex = other.zobristIndex;
	}
	
	// loads a fen string
	public void parseFen(String fen) {
		clearHistory();
		clearRepetitionTable();
		
		// reset bitboards and occupancies
		Arrays.fill(bitboards, 0);
		Arrays.fill(occupancies, 0);
		castle = 0;
		
		int fenIndex = 0;
		
		// loading all the pieces on the board (this is the main part of the fen string)
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
		
		// loading the side
		side = (fen.charAt(fenIndex) == 'w') ? white : black;
		
		fenIndex += 2;
		
		// loading castling rights
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
			
			fenIndex += 2;
		} else {
			fenIndex++;
			enPassant = no_sq;
		}
		
		// load the move rule
		moveRule = Integer.parseInt((fen.substring(index + fenIndex + 1).split(" ", 2)[0]));

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
		
		// generate a new hash key for incremental updating
		hashKey = generateHashKey(this);
		
		addPosition();
	}
	
	public int getPieceAtSquare(int source) {
		for (int bbPiece = side * 6; bbPiece < 6 + side * 6; bbPiece++) { if (getBit(bitboards[bbPiece], source) != 0) return bbPiece; }
		return 0;
	}
	
	// push entire state of board to stack
	public void copyBoard() {
		pastBitBoards[index] = bitboards.clone();
		pastOccupancies[index] = occupancies.clone();
		pastSides[index] = (byte) side;
		pastEnPassant[index] = (byte) enPassant;
		pastCastle[index] = (byte) castle;
		pastHashKey[index] = hashKey;
		pastMoveRule[index] = moveRule;
		
		index++;
	}
	
	// load board from stack, and reset repetition table by 1
	public void takeBack() {
		index--;
		
		removePosition();
		
		bitboards = pastBitBoards[index];
		occupancies = pastOccupancies[index];
		side = pastSides[index];
		enPassant = pastEnPassant[index];
		castle = pastCastle[index];
		hashKey = pastHashKey[index];
		moveRule = pastMoveRule[index];
	}
	
	// resets the stack
	public void clearHistory() {
		index = 0;
	}
	
	/**
	 * --------------------------------
	 * 
	 * 
	 * 		    Move Generation
	 * 
	 * 
	 * --------------------------------
	 */
	
	// adds a move to a move list
	public void addMove(moves moveList, short move) {
		moveList.moves[moveList.count] = move;
		moveList.count++;
	}
	
	// gets all attacks
	public long allAttacks(int side) {
		long toReturn = 0L;
		long bitboard = 0L;
		int square = 0;
		
		for (int bbPiece = P + side * 6; bbPiece <= K + side * 6; bbPiece++) {
			bitboard = bitboards[bbPiece];
			
			while (bitboard != 0) {
				square = getLS1BIndex(bitboard);
				
				switch (bbPiece) {
				case P:
					toReturn |= pawnAttacks[white][square];
					break;
				case p:
					toReturn |= pawnAttacks[black][square];
					break;
				case N:
				case n:
					toReturn |= knightAttacks[square];
					break;
				case B:
				case b:
					toReturn |= getBishopAttacks(square, occupancies[both]);
					break;
				case R:
				case r:
					toReturn |= getRookAttacks(square, occupancies[both]);
					break;
				case Q:
				case q:
					toReturn |= getQueenAttacks(square, occupancies[both]);
					break;
				case K:
				case k:
					toReturn |= kingAttacks[square];
					break;
				}
				
				bitboard &= ~(1L << square);
			}
		}
		
		return toReturn;
	}
	
	// checks if a square is attacked. mostly used for checking checks
	public boolean isSquareAttacked(int square, int side) {
		// white pawn attack
		if ((side == white) && ((pawnAttacks[black][square] & bitboards[P]) != 0)) return true;
		
		// black pawn attack
		if ((side == black) && ((pawnAttacks[white][square] & bitboards[p]) != 0)) return true;
		
		// knight attack
		if ((knightAttacks[square] & ((side == white) ? bitboards[N] : bitboards[n])) != 0) return true;
		
		// slider attacks
		if ((getBishopAttacks(square, occupancies[both]) & ((side == white) ? bitboards[B] : bitboards[b])) != 0) return true;
		if ((getRookAttacks(square, occupancies[both]) & ((side == white) ? bitboards[R] : bitboards[r])) != 0) return true;
		if ((getQueenAttacks(square, occupancies[both]) & ((side == white) ? bitboards[Q] : bitboards[q])) != 0) return true;
		
		// king attack
		if ((kingAttacks[square] & ((side == white) ? bitboards[K] : bitboards[k])) != 0) return true;
		
		// no attack (default)
		return false;
	}
	
	// returns a bitboard containing all the pieces that attack a square
	public long attacksTo(int square) {
		long toReturn = 0L;
		
		toReturn |= (pawnAttacks[white][square] & bitboards[p]);
		toReturn |= (pawnAttacks[black][square] & bitboards[P]);
		toReturn |= knightAttacks[square] & (bitboards[N] | bitboards[n]);
		toReturn |= kingAttacks[square] & (bitboards[K] | bitboards[k]);
		
		toReturn |= getBishopAttacks(square, occupancies[both]) & (bitboards[B] | bitboards[b]);
		toReturn |= getRookAttacks(square, occupancies[both]) & (bitboards[R] | bitboards[r]);
		toReturn |= getQueenAttacks(square, occupancies[both]) & (bitboards[Q] | bitboards[q]);
		
		return toReturn;
	}
	
	public long attacksToSliders(int square, long occ) {
		long toReturn = 0L;
		
		toReturn |= getBishopAttacks(square, occ) & (occ & (bitboards[B] | bitboards[b]));
		toReturn |= getRookAttacks(square, occ) & (occ & (bitboards[R] | bitboards[r]));
		toReturn |= getQueenAttacks(square, occ) & (occ & (bitboards[Q] | bitboards[q]));
		
		return toReturn;
	}
	
	public long getLeastValuablePiece(long attadef, int side, int[] piece) {
		for (piece[0] = P + side * 6; piece[0] <= K + side * 6; piece[0]++) {
			long subset = attadef & bitboards[piece[0]];
			if (subset != 0) {
				return subset;
			}
		}
		
		return 0;
	}
	
	// static exchange evaluation
	public short SEE(int sourceSquare, int targetSquare) {
		int targetPiece = P;
		
		int startPiece, endPiece;
		
		if (side == white) { startPiece = p; endPiece = k; }
		else { startPiece = P; endPiece = K; }
		
		for (int bbPiece = startPiece; bbPiece <= endPiece; bbPiece++) {
			if (getBit(bitboards[bbPiece], targetSquare) != 0) {
				targetPiece = bbPiece;
			}
		}
		
		short gain[] = new short[32];
		short d = 0;
		long mayXRay = bitboards[B] | bitboards[b] | bitboards[R] | bitboards[r] | bitboards[Q] | bitboards[q];
		long fromSet = 1L << sourceSquare;
		long occ = occupancies[both];
		long attadef = attacksTo(targetSquare);
		gain[d] = (short) Math.abs(materialScore[0][targetPiece]);
		
		int[] aPiece = new int[1];
		getLeastValuablePiece(attadef, d & 1, aPiece);
		
		do {
			d++;
			gain[d] = (short) (Math.abs(materialScore[0][aPiece[0]]) - gain[d - 1]);
			if (Math.max(-gain[d - 1], gain[d]) < 0) break;
			attadef ^= fromSet;
			occ ^= fromSet;
			if ((fromSet & mayXRay) != 0) {
				attadef |= attacksToSliders(targetSquare, occ);
			}
			fromSet = getLeastValuablePiece(attadef, d & 1, aPiece);
		} while (fromSet != 0);
		
		while (--d != 0) {
			gain[d - 1] = (short) -Math.max(-gain[d - 1], gain[d]);
		}
		
		
		
		return gain[0];
	}
	
	// generates pseudo-legal moves (ignores pins)
	public void generateMoves(moves moveList) {
		int sourceSquare, targetSquare;
		
		long bitboard, attacks;
		
		for (int piece = P; piece <= k; piece++) {
			bitboard = bitboards[piece];
			
			//generate white pawn and castling moves
			if (side == white) {
				// get white pawns
				if (piece == P) {
					// loop over entire bitboard
					while (bitboard != 0) {
						sourceSquare = getLS1BIndex(bitboard);
						targetSquare = sourceSquare - 8;
						
						// generate quiet pawn moves
						if (targetSquare >= a8 && getBit(occupancies[both], targetSquare) == 0) {
							// pawn promotion
							if (sourceSquare >= a7 && sourceSquare <= h7) {
								// pawn promotes
								addMove(moveList, encodeMove(sourceSquare, targetSquare, 0b1011));
	                            addMove(moveList, encodeMove(sourceSquare, targetSquare, 0b1010));
	                            addMove(moveList, encodeMove(sourceSquare, targetSquare, 0b1001));
	                            addMove(moveList, encodeMove(sourceSquare, targetSquare, 0b1000));
								
							} else {
								// one square
								addMove(moveList, encodeMove(sourceSquare, targetSquare, 0b0000));

								
								// two squares
								if (sourceSquare >= a2 && sourceSquare <= h2 && getBit(occupancies[both], targetSquare - 8) == 0) {
	                                addMove(moveList, encodeMove(sourceSquare, targetSquare - 8, 0b0001));
								}
							}
						}
						
						// init pawn attack bitboard
						attacks = pawnAttacks[side][sourceSquare] & occupancies[black];
						
						// generate pawn captures
						while (attacks != 0) {
							targetSquare = getLS1BIndex(attacks);
							
							// pawn promotion
							if (sourceSquare >= a7 && sourceSquare <= h7) {
								// pawn promotion capture
	                            addMove(moveList, encodeMove(sourceSquare, targetSquare, 0b1111));
	                            addMove(moveList, encodeMove(sourceSquare, targetSquare, 0b1110));
	                            addMove(moveList, encodeMove(sourceSquare, targetSquare, 0b1101));
	                            addMove(moveList, encodeMove(sourceSquare, targetSquare, 0b1100));
							} else { // one square move
								// pawn capture
	                            addMove(moveList, encodeMove(sourceSquare, targetSquare, 0b0100));
							}
							
							attacks &= ~(1L << (targetSquare));
						}
						
						// generate enPassant captures
						if (enPassant != no_sq) {
							long enPassantAttacks = pawnAttacks[side][sourceSquare] & (1L << enPassant);
							
							// if en passant is available
							if (enPassantAttacks != 0) {
								int targetEnPassant = getLS1BIndex(enPassantAttacks);
								
								// en passant capture
	                            addMove(moveList, encodeMove(sourceSquare, targetEnPassant, 0b0101));
							}
						}
						
						// pop bit
						bitboard &= ~(1L << (sourceSquare));
					}
				}
				
				// castling moves
				if (piece == K) {
					// king side castling is available
					if ((castle & wk) != 0) {
						// make sure there are no pieces between king and rook
						if (getBit(occupancies[both], f1) == 0 && getBit(occupancies[both], g1) == 0) {
							if (!isSquareAttacked(e1, black) && !isSquareAttacked(f1, black)) {
								// kingside castle
	                            addMove(moveList, encodeMove(e1, g1, 0b0010));
							}
						}
					}
					
					if ((castle & wq) != 0) {
						// make sure there are no pieces between king and rook
						if (getBit(occupancies[both], d1) == 0 && getBit(occupancies[both], c1) == 0 && getBit(occupancies[both], b1) == 0) {
							if (!isSquareAttacked(e1, black) && !isSquareAttacked(d1, black)) {
								// queenside castle
	                            addMove(moveList, encodeMove(e1, c1, 0b0010));
							}
						}
					}
				}
			} else { // generate black pawn and castling moves
				// get black pawns
				if (piece == p) {
					// loop over entire bitboard
					while (bitboard != 0) {
						sourceSquare = getLS1BIndex(bitboard);
						targetSquare = sourceSquare + 8;
						
						// generate quiet pawn moves
						if (targetSquare <= h1 && getBit(occupancies[both], targetSquare) == 0) {
							// pawn promotion
							if (sourceSquare >= a2 && sourceSquare <= h2) {
								// pawn promotes
								addMove(moveList, encodeMove(sourceSquare, targetSquare, 0b1011));
	                            addMove(moveList, encodeMove(sourceSquare, targetSquare, 0b1010));
	                            addMove(moveList, encodeMove(sourceSquare, targetSquare, 0b1001));
	                            addMove(moveList, encodeMove(sourceSquare, targetSquare, 0b1000));							
							} else {
								// one square
								addMove(moveList, encodeMove(sourceSquare, targetSquare, 0b0000));
								
								// two squares
								if (sourceSquare >= a7 && sourceSquare <= h7 && getBit(occupancies[both], targetSquare + 8) == 0) {
	                                addMove(moveList, encodeMove(sourceSquare, targetSquare + 8, 0b0001));
								}
							}
						}
						
						// init pawn attack bitboard
						attacks = pawnAttacks[side][sourceSquare] & occupancies[white];
						
						// generate pawn captures
						while (attacks != 0) {
							targetSquare = getLS1BIndex(attacks);
							
							// pawn promotion
							if (sourceSquare >= a2 && sourceSquare <= h2) {
								// pawn promotion capture
	                            addMove(moveList, encodeMove(sourceSquare, targetSquare, 0b1111));
	                            addMove(moveList, encodeMove(sourceSquare, targetSquare, 0b1110));
	                            addMove(moveList, encodeMove(sourceSquare, targetSquare, 0b1101));
	                            addMove(moveList, encodeMove(sourceSquare, targetSquare, 0b1100));
							} else { // one square move
								// pawn capture
	                            addMove(moveList, encodeMove(sourceSquare, targetSquare, 0b0100));
							}
							
							attacks &= ~(1L << (targetSquare));
						}
						
						// generate enPassant captures
						if (enPassant != no_sq) {
							long enPassantAttacks = pawnAttacks[side][sourceSquare] & (1L << enPassant);
							
							// if en passant is available
							if (enPassantAttacks != 0) {
								int targetEnPassant = getLS1BIndex(enPassantAttacks);

								// en passant capture
	                            addMove(moveList, encodeMove(sourceSquare, targetEnPassant, 0b0101));
							}
						}
						
						// pop bit
						bitboard &= ~(1L << (sourceSquare));
					}
				}
				
				// castling moves
				if (piece == k) {
					// king side castling is available
					if ((castle & bk) != 0) {
						// make sure there are no pieces between king and rook
						if (getBit(occupancies[both], f8) == 0 && getBit(occupancies[both], g8) == 0) {
							if (!isSquareAttacked(e8, white) && !isSquareAttacked(f8, white)) {
								// kingside castle
	                            addMove(moveList, encodeMove(e8, g8, 0b0010));
							}
						}
					}
					
					if ((castle & bq) != 0) {
						// make sure there are no pieces between king and rook
						if (getBit(occupancies[both], d8) == 0 && getBit(occupancies[both], c8) == 0 && getBit(occupancies[both], b8) == 0) {
							if (!isSquareAttacked(e8, white) && !isSquareAttacked(d8, white)) {
								// queenside castle
	                            addMove(moveList, encodeMove(e8, c8, 0b0010));
							}
						}
					}
				}
			}
			
			// generate knight moves
			if ((side == white) ? piece == N : piece == n) {
				// loop over bitboard
				while (bitboard != 0) {
					sourceSquare = getLS1BIndex(bitboard);
					
					// initialize attacks array - ignore squares which already have friendly pieces
					attacks = knightAttacks[sourceSquare] & ((side == white) ? ~occupancies[white] : ~occupancies[black]);
					
					// loop over target squares
					while (attacks != 0) {
						targetSquare = getLS1BIndex(attacks);
						
						// quiet moves
						if (getBit(((side == white) ? occupancies[black] : occupancies[white]), targetSquare) == 0) {
							// quiet piece move
	                        addMove(moveList, encodeMove(sourceSquare, targetSquare, 0b0000));
						} else {
							// capture move
	                        addMove(moveList, encodeMove(sourceSquare, targetSquare, 0b0100));
						}
						
						// pop bit
						attacks &= ~(1L << (targetSquare));
					}
					
					bitboard &= ~(1L << (sourceSquare));
				}
			}
			
			// generate bishop moves
			if ((side == white) ? piece == B : piece == b) {
				// loop over bitboard
				while (bitboard != 0) {
					sourceSquare = getLS1BIndex(bitboard);
					
					// initialize attacks array - ignore squares which already have friendly pieces
					attacks = getBishopAttacks(sourceSquare, occupancies[both]) & ((side == white) ? ~occupancies[white] : ~occupancies[black]);
					
					// loop over target squares
					while (attacks != 0) {
						targetSquare = getLS1BIndex(attacks);
						
						// quiet moves
						if (getBit(((side == white) ? occupancies[black] : occupancies[white]), targetSquare) == 0) {
							// quiet piece move
	                        addMove(moveList, encodeMove(sourceSquare, targetSquare, 0b0000));
						} else {
							// capture move
	                        addMove(moveList, encodeMove(sourceSquare, targetSquare, 0b0100));
						}
						
						// pop bit
						attacks &= ~(1L << (targetSquare));
					}
					
					bitboard &= ~(1L << (sourceSquare));
				}
			}
			
			// generate rook moves
			if ((side == white) ? piece == R : piece == r) {
				// loop over bitboard
				while (bitboard != 0) {
					sourceSquare = getLS1BIndex(bitboard);
					
					// initialize attacks array - ignore squares which already have friendly pieces
					attacks = getRookAttacks(sourceSquare, occupancies[both]) & ((side == white) ? ~occupancies[white] : ~occupancies[black]);
					
					// loop over target squares
					while (attacks != 0) {
						targetSquare = getLS1BIndex(attacks);
						
						// quiet moves
						if (getBit(((side == white) ? occupancies[black] : occupancies[white]), targetSquare) == 0) {
							// quiet piece move
	                        addMove(moveList, encodeMove(sourceSquare, targetSquare, 0b0000));
						} else {
							// capture move
	                        addMove(moveList, encodeMove(sourceSquare, targetSquare, 0b0100));
						}
						
						// pop bit
						attacks &= ~(1L << (targetSquare));
					}
					
					bitboard &= ~(1L << (sourceSquare));
				}
			}
			
			// generate queen moves
			if ((side == white) ? piece == Q : piece == q) {
				// loop over bitboard
				while (bitboard != 0) {
					sourceSquare = getLS1BIndex(bitboard);
					
					// initialize attacks array - ignore squares which already have friendly pieces
					attacks = getQueenAttacks(sourceSquare, occupancies[both]) & ((side == white) ? ~occupancies[white] : ~occupancies[black]);

					// loop over target squares
					while (attacks != 0) {
						targetSquare = getLS1BIndex(attacks);
						
						// quiet moves
						if (getBit(((side == white) ? occupancies[black] : occupancies[white]), targetSquare) == 0) {
							// quiet piece move
	                        addMove(moveList, encodeMove(sourceSquare, targetSquare, 0b0000));
						} else {
							// capture move
	                        addMove(moveList, encodeMove(sourceSquare, targetSquare, 0b0100));
						}
						
						// pop bit
						attacks &= ~(1L << (targetSquare));
					}
					
					bitboard &= ~(1L << (sourceSquare));
				}
			}
			
			// generate king moves
			if ((side == white) ? piece == K : piece == k) {
				// loop over bitboard
				while (bitboard != 0) {
					sourceSquare = getLS1BIndex(bitboard);
					
					// initialize attacks array - ignore squares which already have friendly pieces
					attacks = kingAttacks[sourceSquare] & ((side == white) ? ~occupancies[white] : ~occupancies[black]);
					
					// loop over target squares
					while (attacks != 0) {
						targetSquare = getLS1BIndex(attacks);
						
						// quiet moves
						if (getBit(((side == white) ? occupancies[black] : occupancies[white]), targetSquare) == 0) {
							// quiet piece move
	                        addMove(moveList, encodeMove(sourceSquare, targetSquare, 0b0000));
						} else {
							// capture move
	                        addMove(moveList, encodeMove(sourceSquare, targetSquare, 0b0100));
						}
						
						// pop bit
						attacks &= ~(1L << (targetSquare));
					}
					
					bitboard &= ~(1L << (sourceSquare));
				}
			}
		}
	}
	
	// makes the move on the board with a flag denoting all moves, or captures only for quiescence search
	public boolean makeMove(int move, int flag) {
		// preserve board state
        copyBoard();
        
        moveRule++;
        
        // parse move
        int sourceSquare = getMoveSource(move);
        int targetSquare = getMoveTarget(move);
        int piece = getPieceAtSquare(sourceSquare);
        int promoted = getMovePromoted(move);
        boolean capture = getMoveCapture(move);
        boolean doublePush = getMoveDouble(move);
        boolean enPassantLocal = getMoveEnPassant(move);
        boolean castling = getMoveCastling(move);
        
        if (promoted != 0) promoted += side * 6;
        
        // move piece
        bitboards[piece] |= (1L << targetSquare);
        bitboards[piece] &= ~(1L << sourceSquare);
        
        hashKey ^= pieceKeys[piece][sourceSquare];
        hashKey ^= pieceKeys[piece][targetSquare];
        
        if (piece == P || piece == p) {
        	moveRule = 0;
        }
        
        // handle captures
        if (capture) {
        	moveRule = 0;
        	
        	int startPiece = -1, endPiece = -1;
        	
        	if (side == white) { // white to move, capture black pieces
        		startPiece = p;
        		endPiece = k;
        	} else { // black to move, capture white pieces
        		startPiece = P;
        		endPiece = K;
        	}
        	
        	for (int bbPiece = startPiece; bbPiece < endPiece; bbPiece++) {
        		if (getBit(bitboards[bbPiece], targetSquare) != 0) { // captured piece was found
        			// pop!
        			bitboards[bbPiece] &= ~(1L << targetSquare);
        			
        			hashKey ^= pieceKeys[bbPiece][targetSquare];
        			
        			break;
        		}
        	}
        }
        
        // handle promotions
        if (promoted != 0) {
        	// erase pawn from target square
        	bitboards[(side == white) ? P : p] &= ~(1L << targetSquare);
        	
        	hashKey ^= pieceKeys[(side == white) ? P : p][targetSquare];
        	        	
        	// add promoted piece
        	bitboards[promoted] |= (1L << targetSquare);
        	hashKey ^= pieceKeys[promoted][targetSquare];
        }
        
        // handle en passant
        if (enPassantLocal) {
        	moveRule = 0;
        	
        	// erase the pawn
        	if (side == white) {
        		bitboards[p] &= ~(1L << (targetSquare + 8));
        		hashKey ^= pieceKeys[p][targetSquare + 8];
        	} else {
        		bitboards[P] &= ~(1L << (targetSquare - 8));
        		hashKey ^= pieceKeys[P][targetSquare - 8];
        	}
        }
        
        // handle en passant variable
        if (enPassant != no_sq) hashKey ^= enPassantKeys[enPassant];
        
        enPassant = no_sq;
        
        if (doublePush) {
        	if (side == white) {
        		enPassant = targetSquare + 8;
        		
        	} else {
        		enPassant = targetSquare - 8;
        	}
        	
        	hashKey ^= enPassantKeys[enPassant];
        }
        
        if (castling) {
        	switch (targetSquare) {
        	case g1: // white castles king side
        		bitboards[R] |= (1L << f1);
                bitboards[R] &= ~(1L << h1);
                
                hashKey ^= pieceKeys[R][f1];
                hashKey ^= pieceKeys[R][h1];
                break;
        	case c1: // white castles queen side
        		bitboards[R] |= (1L << d1);
        		bitboards[R] &= ~(1L << a1);
        		
        		hashKey ^= pieceKeys[R][d1];
        		hashKey ^= pieceKeys[R][a1];
        		break;
        	case g8: // black castles king side
        		bitboards[r] |= (1L << f8);
        		bitboards[r] &= ~(1L << h8);
        		
        		hashKey ^= pieceKeys[r][f8];
        		hashKey ^= pieceKeys[r][h8];
        		break;
        	case c8: // black castles queen side
        		bitboards[r] |= (1L << d8);
        		bitboards[r] &= ~(1L << a8);
        		
        		hashKey ^= pieceKeys[r][d8];
        		hashKey ^= pieceKeys[r][a8];
        		break;
        	}
        }
        
        // update castling rights
        hashKey ^= castleKeys[castle];
        castle &= castlingRights[sourceSquare];
        castle &= castlingRights[targetSquare];
        hashKey ^= castleKeys[castle];
        
        // reset occupancies
        Arrays.fill(occupancies, 0);
        
        // recreate occupancy bitboards
        for (int bbPiece = P; bbPiece <= K; bbPiece++) {
        	occupancies[white] |= bitboards[bbPiece];
        }
        
        for (int bbPiece = p; bbPiece <= k; bbPiece++) {
        	occupancies[black] |= bitboards[bbPiece];
        }
        
        occupancies[both] |= occupancies[white];
        occupancies[both] |= occupancies[black];
        
        // switch side
        side ^= 1;
        hashKey ^= sideKey;
        
        addPosition();
        
        // check if king is in check
        if (isSquareAttacked((side == white) ? getLS1BIndex(bitboards[k]) : getLS1BIndex(bitboards[K]), side)) {
        	takeBack();
        	return false;
        }

        if (flag == nonQuietOnly && !capture && promoted == 0 && !enPassantLocal) {
        	takeBack();
        	return false;
        }
        
        return true;
	}
	
	/**
	 * --------------------------------
	 * 
	 * 
	 * 		    Evaluation
	 * 
	 * 
	 * --------------------------------
	 */
	
	// gets the phase of the game, based on how much material is still on the board
	public int getGamePhaseScore() {
		int whitePieceScores = 0, blackPieceScores = 0;
		
		for (int piece = N; piece <= Q; piece++) {
			whitePieceScores += countBits(bitboards[piece]) * materialScore[opening][piece];
		}
		
		for (int piece = n; piece <= q; piece++) {
			blackPieceScores += countBits(bitboards[piece]) * -materialScore[opening][piece];
		}
		
		return whitePieceScores + blackPieceScores;
	}
	
	// weak squares
	public long getWeakSquares() {		
		long enemyDefense = allAttacks(side ^ 1);
		long ourAttacks = allAttacks(side);
		
		long enemyKingDefense = kingAttacks[getLS1BIndex(bitboards[k - side * 6])];
		long enemyQueenDefense = getQueenAttacks(getLS1BIndex(bitboards[q - side * 6]), occupancies[both]);
		
		enemyDefense &= ~enemyKingDefense;
		enemyDefense &= ~enemyQueenDefense;
		enemyDefense |= enemyKingDefense & enemyQueenDefense;
		
		return (~enemyDefense) & ourAttacks;
	}
	
	// checks if a player is drawing due to lack of mating material
	public boolean drawing(int side) {
		// if pawns or queen exists, mate can occur
		if (bitboards[P + side * 6] != 0) return false;
		if (bitboards[Q + side * 6] != 0) return false;
		
		int knightsUs = countBits(bitboards[N + side * 6]);
		int bishopsUs = countBits(bitboards[B + side * 6]);
		int rooksUs = countBits(bitboards[R + side * 6]);
		int minorPiecesUs = knightsUs + bishopsUs;
		int allPiecesUs = minorPiecesUs + rooksUs;
		
		// no need to worry about their heavy pieces, or pawns. if we're losing, eval function won't consider our lack of mating material
		int knightsThem = countBits(bitboards[n - side * 6]);
		int bishopsThem = countBits(bitboards[b - side * 6]);
		int minorPiecesThem = knightsThem + bishopsThem;
		
		// first, consider no rooks
		if (rooksUs == 0) { 
			// no pieces on the board
			if (allPiecesUs == 0 && minorPiecesThem == 0) return true;
			
			// one piece each is a draw
			if (minorPiecesUs <= 1 && minorPiecesThem <= 1) return true;
			
			// two knights (or less) is a draw
			if (bishopsUs == 0 && knightsUs <= 2 && minorPiecesThem <= 1) return true;
			
			// bishop pair only
			if (bishopsUs == 2 && allPiecesUs == 2) {
				// bishop pair wins against knight pair
				if (knightsThem <= 2 && bishopsThem == 0) return false;
				
				// bishop pair draws against any bishop
				if (bishopsThem != 0) return true;
			}
			
			// two minor pieces against one draws (if no bishop pair)
			if (bishopsUs <= 2 && minorPiecesUs == 2 && minorPiecesThem == 1) return true; 
		}
		
		// only one rook
		if (rooksUs == 1 && allPiecesUs == 1) {
			// rook vs one minor piece is a draw, unless there are tactics.
			// tactics will be detected in the search
			if (minorPiecesThem == 1) return true;
		}
		
		// default to not a draw
		return false;
	}
	
	// basic eval function
	public short evaluate() {
		int gamePhaseScore = getGamePhaseScore();
		
		int gamePhase = -1;
		
		// determine phase of the game based on game phase score
		if (gamePhaseScore > openingPhaseScore) gamePhase = opening;
		else if (gamePhaseScore < endgamePhaseScore) gamePhase = endgame;
		else gamePhase = midgame;
		
		// split up score into 3 categories for interpolation
		int score = 0;
		int scoreOpening = 0;
		int scoreEndgame = 0;
		
		long bitboard = 0;
		
		int piece, square;
		
		int doubledPawns = 0;
		
		// record attack pressure for both sides
		int whiteAttackPressure = 0;
		int blackAttackPressure = 0;
		
		// record # of attackers for both sides
		int whiteAttackers = 0;
		int blackAttackers = 0;
		
		// king positions
		int whiteKingSquare = getLS1BIndex(bitboards[K]);
		int blackKingSquare = getLS1BIndex(bitboards[k]);
		
		long attacks;
		long kingRingAttacks;
		
		// weak squares that we attack that are only defended by enemy queen or king
		
		
		// we do some stuff to find all the squares that white pawns attack, and all the squares that black pawns attack
		// we do this for mobility - pieces that can only move to the squares defended by pawns really can't move at all
		long whitePawnAttacks = 0L;
		long blackPawnAttacks = 0L;
		
		long whitePawnBitboard = bitboards[P];
		long blackPawnBitboard = bitboards[p];
		
		while (whitePawnBitboard != 0) {
			int pawnSquare = getLS1BIndex(whitePawnBitboard);
			
			whitePawnAttacks |= pawnAttacks[white][pawnSquare];
			
			whitePawnBitboard &= ~(1L << pawnSquare);
		}
		
		while (blackPawnBitboard != 0) {
			int pawnSquare = getLS1BIndex(blackPawnBitboard);
			
			blackPawnAttacks |= pawnAttacks[black][pawnSquare];
			
			blackPawnBitboard &= ~(1L << pawnSquare);
		}
		
		for (int bbPiece = P; bbPiece <= k; bbPiece++) {
			bitboard = bitboards[bbPiece];
			
			while (bitboard != 0) {
				piece = bbPiece;
				square = getLS1BIndex(bitboard);
				
				scoreOpening += materialScore[opening][piece];
				scoreEndgame += materialScore[endgame][piece];
				
				switch (piece) {
				case P: 
					scoreOpening += positionalScore[opening][PAWN][square];
					scoreEndgame += positionalScore[endgame][PAWN][square];
					
					// doubled pawns
					doubledPawns = countBits(bitboards[P] & fileMasks[square]);
					if (doubledPawns > 1) {
						scoreOpening += (doubledPawns - 1) * doubledPawnPenaltyOpening;
						scoreEndgame += (doubledPawns - 1) * doubledPawnPenaltyEndgame;
					}
					
					// isolated pawns
					if ((bitboards[P] & isolatedMasks[square]) == 0) {
						scoreOpening += isolatedPawnPenaltyOpening;
						scoreEndgame += isolatedPawnPenaltyEndgame;
					}
					
					// passed pawn bonus
					if ((passedMasks[white][square] & bitboards[p]) == 0) {
						scoreOpening += passedPawnBonus[getRank[square]];
						scoreEndgame += passedPawnBonus[getRank[square]];
					}
					break;
				case N:
					scoreOpening += positionalScore[opening][KNIGHT][square];
					scoreEndgame += positionalScore[endgame][KNIGHT][square];
					
					attacks = knightAttacks[square];
					kingRingAttacks = attacks & kingRingMasks[blackKingSquare];
					
					// added score for knight mobility
					scoreOpening += (countBits(attacks & ~blackPawnAttacks) - knightUnit) * knightMobilityOpening;
					scoreEndgame += (countBits(attacks & ~blackPawnAttacks) - knightUnit) * knightMobilityEndgame;
					
					if (kingRingAttacks != 0) {
						whiteAttackers++;
						whiteAttackPressure += countBits(kingRingAttacks) * minorPieceAttackUnit;
					}
					break;
				case B:
					scoreOpening += positionalScore[opening][BISHOP][square];
					scoreEndgame += positionalScore[endgame][BISHOP][square];
					
					attacks = getBishopAttacks(square, occupancies[both] & ~(bitboards[Q]));
					kingRingAttacks = attacks & kingRingMasks[blackKingSquare];
					
					// added score for bishop mobility
					scoreOpening += (countBits(attacks & ~blackPawnAttacks) - bishopUnit) * bishopMobilityOpening;
					scoreEndgame += (countBits(attacks & ~blackPawnAttacks) - bishopUnit) * bishopMobilityEndgame;
					
					if (kingRingAttacks != 0) {
						whiteAttackers++;
						whiteAttackPressure += countBits(kingRingAttacks) * minorPieceAttackUnit;
					}
					break;
				case R:
					scoreOpening += positionalScore[opening][ROOK][square];
					scoreEndgame += positionalScore[endgame][ROOK][square];
					
					// bonus for semiopen and open files
					if ((bitboards[P] & fileMasks[square]) == 0) {
						score += semiOpenFileScore;
					}
					
					if (((bitboards[P] | bitboards[p]) & fileMasks[square]) == 0) {
						score += openFileScore;
					}
					
					attacks = getRookAttacks(square, occupancies[both] & ~(bitboards[R] | bitboards[Q]));
					kingRingAttacks = attacks & kingRingMasks[blackKingSquare];
					
					// added score for rook mobility
					scoreOpening += (countBits(attacks & ~blackPawnAttacks) - rookUnit) * rookMobilityOpening;
					scoreEndgame += (countBits(attacks & ~blackPawnAttacks) - rookUnit) * rookMobilityEndgame;
					
					if (kingRingAttacks != 0) {
						whiteAttackers++;
						whiteAttackPressure += countBits(kingRingAttacks) * rookAttackUnit;
					}
					break;
				case Q:
					scoreOpening += positionalScore[opening][QUEEN][square];
					scoreEndgame += positionalScore[endgame][QUEEN][square];
					
					attacks = getQueenAttacks(square, occupancies[both] & ~(bitboards[R] | bitboards[Q]));
					kingRingAttacks = attacks & kingRingMasks[blackKingSquare];
					
					// added score for queen mobility
					scoreOpening += (countBits(attacks & ~blackPawnAttacks) - queenUnit) * queenMobilityOpening;
					scoreEndgame += (countBits(attacks & ~blackPawnAttacks) - queenUnit) * queenMobilityEndgame;
					
					if (kingRingAttacks != 0) {
						whiteAttackers++;
						whiteAttackPressure += countBits(kingRingAttacks) * queenAttackUnit;
					}
					break;
				case K:
					scoreOpening += positionalScore[opening][KING][square];
					scoreEndgame += positionalScore[endgame][KING][square];
					
					// penalty for being on a semiopen or open file
					if ((bitboards[P] & fileMasks[square]) == 0) {
						scoreOpening -= semiOpenFileScore;
					}
					
					if (((bitboards[P] | bitboards[p]) & fileMasks[square]) == 0) {
						scoreOpening -= openFileScore;
					}
					
					// king safety bonus
					scoreOpening += countBits(kingAttacks[square] & occupancies[white]) * kingShieldBonus;
					scoreEndgame += countBits(kingAttacks[square] & occupancies[white]) * kingShieldBonus;
					
					break;
				
				// everything below is the same thing, but for black. so we invert the sign
				case p:
					scoreOpening -= positionalScore[opening][PAWN][mirrorScore[square]];
					scoreEndgame -= positionalScore[endgame][PAWN][mirrorScore[square]];

					doubledPawns = countBits(bitboards[p] & fileMasks[square]);
					if (doubledPawns > 1) {
						scoreOpening -= doubledPawns * doubledPawnPenaltyOpening;
						scoreEndgame -= doubledPawns * doubledPawnPenaltyEndgame;
					}
					
					if ((bitboards[p] & isolatedMasks[square]) == 0) {
						scoreOpening -= isolatedPawnPenaltyOpening;
						scoreEndgame -= isolatedPawnPenaltyEndgame;
					}
					
					if ((passedMasks[black][square] & bitboards[P]) == 0) {
						scoreOpening -= passedPawnBonus[getRank[mirrorScore[square]]];
						scoreEndgame -= passedPawnBonus[getRank[mirrorScore[square]]];
					}
					break;
				case n:
					scoreOpening -= positionalScore[opening][KNIGHT][mirrorScore[square]];
					scoreEndgame -= positionalScore[endgame][KNIGHT][mirrorScore[square]];
					
					attacks = knightAttacks[square];
					kingRingAttacks = attacks & kingRingMasks[whiteKingSquare];
					
					scoreOpening -= (countBits(attacks & ~whitePawnAttacks) - knightUnit) * knightMobilityOpening;
					scoreEndgame -= (countBits(attacks & ~whitePawnAttacks) - knightUnit) * knightMobilityEndgame;
					
					if (kingRingAttacks != 0) {
						blackAttackers++;
						blackAttackPressure += countBits(kingRingAttacks) * minorPieceAttackUnit;
					}
					break;
				case b:
					scoreOpening -= positionalScore[opening][BISHOP][mirrorScore[square]];
					scoreEndgame -= positionalScore[endgame][BISHOP][mirrorScore[square]];
					
					attacks = getBishopAttacks(square, occupancies[both] & ~(bitboards[q]));
					kingRingAttacks = attacks & kingRingMasks[whiteKingSquare];
					
					scoreOpening -= (countBits(attacks & ~whitePawnAttacks) - bishopUnit) * bishopMobilityOpening;
					scoreEndgame -= (countBits(attacks & ~whitePawnAttacks) - bishopUnit) * bishopMobilityEndgame;
					
					if (kingRingAttacks != 0) {
						blackAttackers++;
						blackAttackPressure += countBits(kingRingAttacks) * minorPieceAttackUnit;
					}
					break;
				case r:
					scoreOpening -= positionalScore[opening][ROOK][mirrorScore[square]];
					scoreEndgame -= positionalScore[endgame][ROOK][mirrorScore[square]];
					
					if ((bitboards[p] & fileMasks[square]) == 0) {
						score -= semiOpenFileScore;
					}
					
					if (((bitboards[P] | bitboards[p]) & fileMasks[square]) == 0) {
						score -= openFileScore;
					}
					
					attacks = getRookAttacks(square, occupancies[both] & ~(bitboards[r] | bitboards[q]));
					kingRingAttacks = attacks & kingRingMasks[whiteKingSquare];
					
					scoreOpening -= (countBits(attacks & ~whitePawnAttacks) - rookUnit) * rookMobilityOpening;
					scoreEndgame -= (countBits(attacks & ~whitePawnAttacks) - rookUnit) * rookMobilityEndgame;
					
					if (kingRingAttacks != 0) {
						blackAttackers++;
						blackAttackPressure += countBits(kingRingAttacks) * rookAttackUnit;
					}
					break;
				case q:
					scoreOpening -= positionalScore[opening][QUEEN][mirrorScore[square]];
					scoreEndgame -= positionalScore[endgame][QUEEN][mirrorScore[square]];
					
					attacks = getQueenAttacks(square, occupancies[both] & ~(bitboards[r] | bitboards[q]));
					kingRingAttacks = attacks & kingRingMasks[whiteKingSquare];
					
					scoreOpening -= (countBits(attacks & ~whitePawnAttacks) - queenUnit) * queenMobilityOpening;
					scoreEndgame -= (countBits(attacks & ~whitePawnAttacks) - queenUnit) * queenMobilityEndgame;
					
					if (kingRingAttacks != 0) {
						blackAttackers++;
						blackAttackPressure += countBits(kingRingAttacks) * queenAttackUnit;
					}
					break;
				case k:
					scoreOpening -= positionalScore[opening][KING][mirrorScore[square]];
					scoreEndgame -= positionalScore[endgame][KING][mirrorScore[square]];
					
					if ((bitboards[p] & fileMasks[square]) == 0) {
						scoreOpening += semiOpenFileScore;
					}
					
					if (((bitboards[P] | bitboards[p]) & fileMasks[square]) == 0) {
						scoreOpening += openFileScore;
					}
					
					// king safety bonus
					scoreOpening -= countBits(kingAttacks[square] & occupancies[black]) * kingShieldBonus;
					scoreEndgame -= countBits(kingAttacks[square] & occupancies[black]) * kingShieldBonus;
					
					break;
				}
				
				bitboard &= ~(1L << (square));
			}
		}
		
		// add king attack bonus
		scoreOpening += scaleAttacks(whiteAttackPressure, whiteAttackers);
		scoreOpening -= scaleAttacks(blackAttackPressure, blackAttackers);
		
		// interpolate scores. this is called tapered eval
		if (gamePhase == midgame) {
			score += (scoreOpening * openingPhaseScore + scoreEndgame * (openingPhaseScore - gamePhaseScore)) / openingPhaseScore;
		} else if (gamePhase == opening) {
			score += scoreOpening;
		} else if (gamePhase == endgame) {
			score += scoreEndgame;
		}
		
		score += mopUpEval();
		
		// white is winning, but lacks sufficient material to mate
		if (score > 0 && drawing(white)) score /= 16;
		
		// black is winning, but lacks sufficient material to mate
		if (score < 0 && drawing(black)) score /= 16;
		
		// add tempo bonus
		score += (side == white) ? tempo : -tempo;
		
		// since we use negamax, return in the perspective of the side to play
		return (short) ((side == white) ? score : -score);
	}
	
	// scales attack bonus based on # of attackers
	public int scaleAttacks(int attackValue, int numberOfAttackers) {
		switch (numberOfAttackers) {
		case 0: return 0;
		case 1: return 0;
		case 2: return attackValue;
		case 3: return attackValue * 4 / 3;
		case 4: return attackValue * 3 / 2;
		default: return attackValue * 2;
		}
	}
	
	// gets how much material one side has
	public int materialScore(int side) {
		int materialScores = 0;
		
		if (side == white) {
			for (int piece = P; piece <= Q; piece++) {
				materialScores += countBits(bitboards[piece]) * materialScore[opening][piece];
			}
		} else {
			for (int piece = p; piece <= q; piece++) {
				materialScores += countBits(bitboards[piece]) * -materialScore[opening][piece];
			}
		}
		
		return materialScores;
	}
	
	public double mopUpEval() {
		int mopUpScore = 0;
		
		int whiteMaterialScore = materialScore(white);
		int blackMaterialScore = materialScore(black);
		
		double multiplier = (double) (whiteMaterialScore - blackMaterialScore) / ((whiteMaterialScore + blackMaterialScore) * (whiteMaterialScore + blackMaterialScore)) * 1000;
		
		if (multiplier > 2) multiplier = 2;
		
		int friendlyKingRank = -1, friendlyKingFile = -1;
		int enemyKingRank = -1, enemyKingFile = -1;
		
		if (multiplier > 0) {
			int friendlySquare = getLS1BIndex(bitboards[K]);
			int enemySquare = getLS1BIndex(bitboards[k]);
			
			friendlyKingRank = friendlySquare / 8;
			friendlyKingFile = friendlySquare % 8;
			
			enemyKingRank = enemySquare / 8;
			enemyKingFile = enemySquare % 8;
		} else {
			int friendlySquare = getLS1BIndex(bitboards[k]);
			int enemySquare = getLS1BIndex(bitboards[K]);
			
			friendlyKingRank = friendlySquare / 8;
			friendlyKingFile = friendlySquare % 8;
			
			enemyKingRank = enemySquare / 8;
			enemyKingFile = enemySquare % 8;
		}
		
		int enemyKingDstFromCenter = Math.max(3 - enemyKingRank, enemyKingRank - 4) + Math.max(3 - enemyKingFile, enemyKingFile - 4);
		int kingDistance = Math.abs(friendlyKingRank - enemyKingRank) + Math.abs(friendlyKingFile - enemyKingFile);
		
		mopUpScore += enemyKingDstFromCenter * 10;
		mopUpScore += (14 - kingDistance) * 4;
		
		return mopUpScore * multiplier;
	}
	
	/**
	 * --------------------------------
	 * 
	 * 
	 * 		  Checking Repetition
	 * 
	 * 
	 * --------------------------------
	 */
	
	// a dedicated hash table for repetition checking. it takes the last 16 bits of the zobrist key as a key
	byte repetitionTable[] = new byte[65536]; // 2 ^ 16
	
	// we still actually need to store the past zobrist keys, since there will be collisions
	// but we won't have to iterate over the whole thing all the time
	long pastZobrists[] = new long[1000];
	
	// instead of using an arraylist, which is slow, we just keep an index
	int zobristIndex = 0;
	
	// add a position to the hash table and past zobrists
	public void addPosition() {
		repetitionTable[(int) (hashKey >>> 48)]++;
		pastZobrists[zobristIndex] = hashKey;
		zobristIndex++;
	}
	
	// remove a position from the hash table and past zobrists
	public void removePosition() {
		repetitionTable[(int) (hashKey >>> 48)]--;
		zobristIndex--;
	}
	
	// check if a position has been repeated twice before
	public boolean positionRepeated() {
		// at least one repetition in the hash table
		if (repetitionTable[(int) (hashKey >>> 48)] >= 1) {
			// so now we manually loop over the past zobrists table, and check all occurences
			int occurrences = 0;
			for (int i = 0; i < zobristIndex; i++) {
				if (pastZobrists[i] == hashKey) occurrences++;
			}
			
			// 3fold
			if (occurrences >= 3) return true;
		}
		
		// no 3fold
		return false;
	}
	
	// clear the table for when we load a fen, or start a new game (UCI)
	public void clearRepetitionTable() {
		zobristIndex = 0;
	}
}