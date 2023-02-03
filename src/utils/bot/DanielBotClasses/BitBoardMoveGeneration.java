package utils.bot.DanielBotClasses;

import static utils.bot.DanielBotClasses.BitBoardChessBoard.*;
import static utils.bot.DanielBotClasses.BitBoardConsts.*;
import static utils.bot.DanielBotClasses.BitBoardBitManipulation.*;
import static utils.bot.DanielBotClasses.BitBoardZobrist.*;
import static utils.bot.DanielBotClasses.BitBoardIO.*;
import static utils.bot.DanielBotClasses.BitBoardRepetition.*;
import static utils.bot.DanielBotClasses.BitBoardAttacks.*;
import static utils.bot.DanielBotClasses.BitBoardSearch.*;

import java.util.Arrays;

// stuff to do with move generation
public class BitBoardMoveGeneration {
	// adds a move to a move list
	public static void addMove(moves moveList, int move) {
		moveList.moves[moveList.count] = move;
		moveList.count++;
	}
	
	// checks if a square is attacked. mostly used for checking checks
	public static boolean isSquareAttacked(int square, int side) {
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
	
	// generates pseudo-legal moves (ignores pins)
	public static void generateMoves(moves moveList) {
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
								addMove(moveList, encodeMove(sourceSquare, targetSquare, piece, Q, 0, 0, 0, 0));
	                            addMove(moveList, encodeMove(sourceSquare, targetSquare, piece, R, 0, 0, 0, 0));
	                            addMove(moveList, encodeMove(sourceSquare, targetSquare, piece, B, 0, 0, 0, 0));
	                            addMove(moveList, encodeMove(sourceSquare, targetSquare, piece, N, 0, 0, 0, 0));
								
							} else {
								// one square
								addMove(moveList, encodeMove(sourceSquare, targetSquare, piece, 0, 0, 0, 0, 0));

								
								// two squares
								if (sourceSquare >= a2 && sourceSquare <= h2 && getBit(occupancies[both], targetSquare - 8) == 0) {
	                                addMove(moveList, encodeMove(sourceSquare, targetSquare - 8, piece, 0, 0, 1, 0, 0));
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
	                            addMove(moveList, encodeMove(sourceSquare, targetSquare, piece, Q, 1, 0, 0, 0));
	                            addMove(moveList, encodeMove(sourceSquare, targetSquare, piece, R, 1, 0, 0, 0));
	                            addMove(moveList, encodeMove(sourceSquare, targetSquare, piece, B, 1, 0, 0, 0));
	                            addMove(moveList, encodeMove(sourceSquare, targetSquare, piece, N, 1, 0, 0, 0));
							} else { // one square move
								// pawn capture
	                            addMove(moveList, encodeMove(sourceSquare, targetSquare, piece, 0, 1, 0, 0, 0));
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
	                            addMove(moveList, encodeMove(sourceSquare, targetEnPassant, piece, 0, 1, 0, 1, 0));
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
	                            addMove(moveList, encodeMove(e1, g1, piece, 0, 0, 0, 0, 1));
							}
						}
					}
					
					if ((castle & wq) != 0) {
						// make sure there are no pieces between king and rook
						if (getBit(occupancies[both], d1) == 0 && getBit(occupancies[both], c1) == 0 && getBit(occupancies[both], b1) == 0) {
							if (!isSquareAttacked(e1, black) && !isSquareAttacked(d1, black)) {
								// queenside castle
	                            addMove(moveList, encodeMove(e1, c1, piece, 0, 0, 0, 0, 1));
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
								addMove(moveList, encodeMove(sourceSquare, targetSquare, piece, q, 0, 0, 0, 0));
	                            addMove(moveList, encodeMove(sourceSquare, targetSquare, piece, r, 0, 0, 0, 0));
	                            addMove(moveList, encodeMove(sourceSquare, targetSquare, piece, b, 0, 0, 0, 0));
	                            addMove(moveList, encodeMove(sourceSquare, targetSquare, piece, n, 0, 0, 0, 0));								
							} else {
								// one square
	                            addMove(moveList, encodeMove(sourceSquare, targetSquare, piece, 0, 0, 0, 0, 0));
								
								// two squares
								if (sourceSquare >= a7 && sourceSquare <= h7 && getBit(occupancies[both], targetSquare + 8) == 0) {
	                                addMove(moveList, encodeMove(sourceSquare, targetSquare + 8, piece, 0, 0, 1, 0, 0));
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
	                            addMove(moveList, encodeMove(sourceSquare, targetSquare, piece, q, 1, 0, 0, 0));
	                            addMove(moveList, encodeMove(sourceSquare, targetSquare, piece, r, 1, 0, 0, 0));
	                            addMove(moveList, encodeMove(sourceSquare, targetSquare, piece, b, 1, 0, 0, 0));
	                            addMove(moveList, encodeMove(sourceSquare, targetSquare, piece, n, 1, 0, 0, 0));
							} else { // one square move
								// pawn capture
	                            addMove(moveList, encodeMove(sourceSquare, targetSquare, piece, 0, 1, 0, 0, 0));
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
	                            addMove(moveList, encodeMove(sourceSquare, targetEnPassant, piece, 0, 1, 0, 1, 0));
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
	                            addMove(moveList, encodeMove(e8, g8, piece, 0, 0, 0, 0, 1));
							}
						}
					}
					
					if ((castle & bq) != 0) {
						// make sure there are no pieces between king and rook
						if (getBit(occupancies[both], d8) == 0 && getBit(occupancies[both], c8) == 0 && getBit(occupancies[both], b8) == 0) {
							if (!isSquareAttacked(e8, white) && !isSquareAttacked(d8, white)) {
								// queenside castle
	                            addMove(moveList, encodeMove(e8, c8, piece, 0, 0, 0, 0, 1));
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
	                        addMove(moveList, encodeMove(sourceSquare, targetSquare, piece, 0, 0, 0, 0, 0));
						} else {
							// capture move
	                        addMove(moveList, encodeMove(sourceSquare, targetSquare, piece, 0, 1, 0, 0, 0));
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
	                        addMove(moveList, encodeMove(sourceSquare, targetSquare, piece, 0, 0, 0, 0, 0));
						} else {
							// capture move
	                        addMove(moveList, encodeMove(sourceSquare, targetSquare, piece, 0, 1, 0, 0, 0));
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
	                        addMove(moveList, encodeMove(sourceSquare, targetSquare, piece, 0, 0, 0, 0, 0));
						} else {
							// capture move
	                        addMove(moveList, encodeMove(sourceSquare, targetSquare, piece, 0, 1, 0, 0, 0));
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
	                        addMove(moveList, encodeMove(sourceSquare, targetSquare, piece, 0, 0, 0, 0, 0));
						} else {
							// capture move
	                        addMove(moveList, encodeMove(sourceSquare, targetSquare, piece, 0, 1, 0, 0, 0));
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
	                        addMove(moveList, encodeMove(sourceSquare, targetSquare, piece, 0, 0, 0, 0, 0));
						} else {
							// capture move
	                        addMove(moveList, encodeMove(sourceSquare, targetSquare, piece, 0, 1, 0, 0, 0));
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
	public static boolean makeMove(int move, int flag) {
		// preserve board state
        copyBoard();
        
        moveRule++;
        
        // parse move
        int sourceSquare = getMoveSource(move);
        int targetSquare = getMoveTarget(move);
        int piece = getMovePiece(move);
        int promoted = getMovePromoted(move);
        int capture = getMoveCapture(move);
        int doublePush = getMoveDouble(move);
        int enPassantLocal = getMoveEnPassant(move);
        int castling = getMoveCastling(move);
        
        boolean inCheck = isSquareAttacked((side == white) ? getLS1BIndex(bitboards[K]) : getLS1BIndex(bitboards[k]), side ^ 1);
        
        // move piece
        bitboards[piece] |= (1L << targetSquare);
        bitboards[piece] &= ~(1L << sourceSquare);
        
        hashKey ^= pieceKeys[piece][sourceSquare];
        hashKey ^= pieceKeys[piece][targetSquare];
        
        if (piece == P || piece == p) {
        	moveRule = 0;
        }
        
        // handle captures
        if (capture != 0) {
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
        if (enPassantLocal != 0) {
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
        
        if (doublePush != 0) {
        	if (side == white) {
        		enPassant = targetSquare + 8;
        		
        	} else {
        		enPassant = targetSquare - 8;
        	}
        	
        	hashKey ^= enPassantKeys[enPassant];
        }
        
        if (castling != 0) {
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

//        // evading checks
//        if (flag == nonQuietOnly && inCheck && ply < 10) {
//        	return true;
//        }
//        
//        // giving checks
//        if (flag == nonQuietOnly && isSquareAttacked((side == white) ? getLS1BIndex(bitboards[K]) : getLS1BIndex(bitboards[k]), side ^ 1) && ply < 10) {
//        	return true;
//        }
        
        // if it's not check related and there's no captures or promotions, it's a quiet move
        if (flag == nonQuietOnly && capture == 0 && promoted == 0 && enPassantLocal == 0) {
        	takeBack();
        	return false;
        }
        
        return true;
	}
}