package utils.bot.DanielBotClasses.StockfishEval;

import static utils.bot.DanielBotClasses.BitBoardConsts.*;
import static utils.bot.DanielBotClasses.BitBoardChessBoard.*;
import static utils.bot.DanielBotClasses.BitBoardBitManipulation.*;
import static utils.bot.DanielBotClasses.BitBoardAttacks.*;
import static utils.bot.DanielBotClasses.BitBoardIO.*;
import static utils.bot.DanielBotClasses.StockfishEval.Global.*;

public class Attack {
	// get direction that a piece is pinned
	public static int pinnedDirection(int square) {
		if (square == -1) {
			// get king location
			int location = getLS1BIndex(bitboards[K]);
			
			// iterate over only queen attacks from location, which saves time
			long bitboard = getQueenAttacks(location, 0L);
			
			int sum = 0;
			
			while (bitboard != 0) {
				int queenAttackSquare = getLS1BIndex(bitboard);
				
				// sum
				sum += pinnedDirection(queenAttackSquare);
				
				// pop bit
				bitboard &= ~(1L << (queenAttackSquare));
			}
			
			return sum;
		}
		
		if (board(square) == -1) return 0; // no piece at location
		int color = 1;
		if (board(square) >= p) color = -1;
		
		int x = square % 8;
		int y = square / 8;
		
		for (int i = 0; i < 8; i++) {
			// 8 directions around king
			int ix = (i + ((i > 3) ? 1 : 0)) % 3 - 1;
			int iy = ((i + ((i > 3) ? 1 : 0)) / 3) - 1;
			
			boolean king = false;
			
			for (int distance = 1; distance < 8; distance++) {
				int piece = board(x + distance * ix, y + distance * iy);
				if (piece == K) king = true;
				if (piece != -1) break;
			}
						
			if (king) {
				for (int distance = 1; distance < 8; distance++) {
					int piece = board(x - distance * ix, y - distance * iy);
					
					if (piece == q || (piece == b && (ix * iy != 0)) || (piece == r && (ix * iy == 0))) {
						return Math.abs(ix + iy * 3) * color;
					}
					
					if (piece != -1) break;
				}
			}
		}
		
		return 0;
	}
	
	// count # of knight attackers ON square, coming FROM square2 (if set)
	public static int knightAttack(int square, int square2) {
		if (square == -1) {
			long knightAttackBitboard = 0L;
			long knightLocations = bitboards[N];
			
			int sum = 0;
			
			while (knightLocations != 0) {
				int knightSquare = getLS1BIndex(knightLocations);
				
				// add to list of knight attacks
				knightAttackBitboard |= knightAttacks[knightSquare];
				
				// pop knight location
				knightLocations &= ~(1L << (knightSquare));
			}
						
			while (knightAttackBitboard != 0) {
				int knightAttackSquare = getLS1BIndex(knightAttackBitboard);
				
				sum += knightAttack(knightAttackSquare, -1);
				
				knightAttackBitboard &= ~(1L << (knightAttackSquare));
			}
			
			return sum;
		}
		
		int toReturn = 0;
		
		long knightCanReach = knightAttacks[square] & bitboards[N];

		while (knightCanReach != 0) {
			int knightReachSquare = getLS1BIndex(knightCanReach);
			
			if (getBit(bitboards[N], knightReachSquare) != 0 && ((square2 == -1) || (square2 == knightReachSquare)) && (pinned(knightReachSquare) == 0)) {
				toReturn++;
			}
			
			knightCanReach &= ~(1L << (knightReachSquare));
		}
		
		return toReturn;
	}
	
	// count # of bishop attackers ON square, coming FROM square2 (if set), XRays through queens
	public static int bishopXRayAttack(int square, int square2) {
		if (square == -1) {
			long bishopAttackBitboard = 0L;
			long bishopLocations = bitboards[B];
			
			int sum = 0;
			
			while (bishopLocations != 0) {
				int bishopSquare = getLS1BIndex(bishopLocations);
				
				// add to list of bishop attacks
				bishopAttackBitboard |= getBishopAttacks(bishopSquare, 0L);
				
				// pop bishop location
				bishopLocations &= ~(1L << (bishopSquare));
			}
									
			while (bishopAttackBitboard != 0) {
				int bishopAttackSquare = getLS1BIndex(bishopAttackBitboard);
				
				sum += bishopXRayAttack(bishopAttackSquare, -1);
				
				bishopAttackBitboard &= ~(1L << (bishopAttackSquare));
			}
			
			return sum;
		}
		
		int toReturn = 0;
		
		long blocker = occupancies[both];
		
		blocker &= ~(bitboards[Q] | bitboards[q]); // remove both queens from blocker array
		
		long bishopCanReach = getBishopAttacks(square, blocker) & bitboards[B];
		
		while (bishopCanReach != 0) {
			int bishopReachSquare = getLS1BIndex(bishopCanReach);
			
			if (getBit(bitboards[B], bishopReachSquare) != 0 && ((square2 == -1) || (square2 == bishopReachSquare))) {
				int direction = pinnedDirection(bishopReachSquare);
				
				int startX = square % 8;
				int startY = square / 8;
				
				int endX = bishopReachSquare % 8;
				int endY = bishopReachSquare / 8;
				
				int dX = endX - startX;
				int dY = endY - startY;
				
				if (direction == 0 || (direction == 4 && dX == dY) || (direction == 2 && dX == dY * -1)) {
					toReturn++;
				}
			}
			
			bishopCanReach &= ~(1L << bishopReachSquare);
		}
		
		return toReturn;
	}
	
	// count # of rook attackers ON square, coming FROM square2 (if set), XRays through queens and same side rooks
	public static int rookXRayAttack(int square, int square2) {
		if (square == -1) {
			long rookAttackBitboard = 0L;
			long rookLocations = bitboards[R];
			
			int sum = 0;
			
			while (rookLocations != 0) {
				int rookSquare = getLS1BIndex(rookLocations);
				
				// add to list of rook attacks
				rookAttackBitboard |= getRookAttacks(rookSquare, 0L);
				
				// pop rook location
				rookLocations &= ~(1L << (rookSquare));
			}
						
			while (rookAttackBitboard != 0) {
				int rookAttackSquare = getLS1BIndex(rookAttackBitboard);
				
				sum += rookXRayAttack(rookAttackSquare, -1);
				
				rookAttackBitboard &= ~(1L << (rookAttackSquare));
			}
			
			return sum;
		}
		
		int toReturn = 0;
		
		long blocker = occupancies[both];
		
		blocker &= ~(bitboards[Q] | bitboards[q] | bitboards[R]); // remove both queens and our rooks from blocker array
		long rookCanReach = getRookAttacks(square, blocker) & bitboards[R];
		
		while (rookCanReach != 0) {
			int rookReachSquare = getLS1BIndex(rookCanReach);
			
			if (getBit(bitboards[R], rookReachSquare) != 0 && ((square2 == -1) || (square2 == rookReachSquare))) {
				int direction = pinnedDirection(rookReachSquare);
				
				int startX = square % 8;
				int startY = square / 8;
				
				int endX = rookReachSquare % 8;
				int endY = rookReachSquare / 8;
				
				int dX = endX - startX;
				int dY = endY - startY;
				
				if (direction == 0 || (direction == 1 && dX == 0) || (direction == 3 && dY == 0)) {
					toReturn++;
				}
			}
			
			rookCanReach &= ~(1L << rookReachSquare);
		}
		
		return toReturn;
	}
	
	// count # of queen attackers ON square, coming FROM square2 (if set)
	public static int queenAttack(int square, int square2) {
		if (square == -1) {
			long queenAttackBitboard = 0L;
			long queenLocations = bitboards[Q];
			
			int sum = 0;
			
			while (queenLocations != 0) {
				int queenSquare = getLS1BIndex(queenLocations);
				
				// add to list of queen attacks
				queenAttackBitboard |= getQueenAttacks(queenSquare, 0L);
				
				// pop queen location
				queenLocations &= ~(1L << (queenSquare));
			}
			
			while (queenAttackBitboard != 0) {
				int queenAttackSquare = getLS1BIndex(queenAttackBitboard);
				
				sum += queenAttack(queenAttackSquare, -1);
				
				queenAttackBitboard &= ~(1L << (queenAttackSquare));
			}
			
			return sum;
		}
		
		int toReturn = 0;
		
		long queenCanReach = getQueenAttacks(square, occupancies[both]) & bitboards[Q];
		
		while (queenCanReach != 0) {
			int queenReachSquare = getLS1BIndex(queenCanReach);
			
			if (getBit(bitboards[Q], queenReachSquare) != 0 && ((square2 == -1) || (square2 == queenReachSquare))) {
				int direction = pinnedDirection(queenReachSquare);

				int startX = square % 8;
				int startY = square / 8;
				
				int endX = queenReachSquare % 8;
				int endY = queenReachSquare / 8;
				
				int dX = endX - startX;
				int dY = endY - startY;
				
				if (direction == 0 || (direction == 1 && dX == 0) || (direction == 3 && dY == 0) || (direction == 4 && dX == dY) || (direction == 2 && dX == dY * -1)) {
					toReturn++;
				}
			}
			
			queenCanReach &= ~(1L << queenReachSquare);
		}
		
		return toReturn;
	}
	
	// count # of pawn attackers ON square, pins and en passant ignored
	public static int pawnAttack(int square) {
		if (square == -1) {
			long pawnLocations = bitboards[P];
			
			int sum = 0;
			
			while (pawnLocations != 0) {
				int pawnSquare = getLS1BIndex(pawnLocations);
				
				// count pawn attacks
				sum += countBits(pawnAttacks[white][pawnSquare]);
				
				// pop pawn location
				pawnLocations &= ~(1L << (pawnSquare));
			}
			
			return sum;
		}
		
		int toReturn = 0;
		
		long pawnCanReach = pawnAttacks[black][square] & bitboards[P]; // get pawn attacks from opposite side to find what can attack that square
		
		while (pawnCanReach != 0) {
			int pawnReachSquare = getLS1BIndex(pawnCanReach);
			
			if (getBit(bitboards[P], pawnReachSquare) != 0) {
				toReturn++;
			}
			
			pawnCanReach &= ~(1L << pawnReachSquare);
		}
		
		return toReturn;
	}
	
	// count # of king attackers ON square, pins and en passant ignored
	public static int kingAttack(int square) {
		if (square == -1) {
			long kingLocation = bitboards[K];
			
			int kingSquare = getLS1BIndex(kingLocation);
			
			// count king attacks
			return countBits(kingAttacks[kingSquare]);
		}
		
		long kingCanReach = kingAttacks[square] & bitboards[K]; // get pawn attacks from opposite side to find what can attack that square
		
		while (kingCanReach != 0) {
			int kingReachSquare = getLS1BIndex(kingCanReach);
			
			if (getBit(bitboards[K], kingReachSquare) != 0) {
				return 1;
			}
			
			kingCanReach &= ~(1L << kingReachSquare);
		}
		
		return 0;
	}
	
	// count # of attackers ON square
	public static int attack(int square) {
		int toReturn = 0;
		
		toReturn += pawnAttack(square);
		toReturn += kingAttack(square);
		toReturn += knightAttack(square, -1);
		toReturn += bishopXRayAttack(square, -1);
		toReturn += rookXRayAttack(square, -1);
		toReturn += queenAttack(square, -1);
		
		return toReturn;
	}
	
	// count # of queen attackers ON square, considering only diagonals
	public static int queenDiagonalAttack(int square, int square2) {
		if (square == -1) {
			long queenAttackBitboard = 0L;
			long queenLocations = bitboards[Q];
			
			int sum = 0;
			
			while (queenLocations != 0) {
				int queenSquare = getLS1BIndex(queenLocations);
				
				// add to list of queen attacks
				queenAttackBitboard |= getBishopAttacks(queenSquare, 0L); // getBishopAttacks because we are ONLY considering DIAGONALS
				
				// pop queen location
				queenLocations &= ~(1L << (queenSquare));
			}
									
			while (queenAttackBitboard != 0) {
				int queenAttackSquare = getLS1BIndex(queenAttackBitboard);
				
				sum += queenDiagonalAttack(queenAttackSquare, -1);
				
				queenAttackBitboard &= ~(1L << (queenAttackSquare));
			}
			
			return sum;
		}
		
		int toReturn = 0;
		
		long queenCanReach = getBishopAttacks(square, occupancies[both]) & bitboards[Q]; // remember, ONLY DIAGONALS
		
		while (queenCanReach != 0) {
			int queenReachSquare = getLS1BIndex(queenCanReach);
			
			if (getBit(bitboards[Q], queenReachSquare) != 0 && ((square2 == -1) || (square2 == queenReachSquare))) {
				int direction = pinnedDirection(queenReachSquare);
				
				int startX = square % 8;
				int startY = square / 8;
				
				int endX = queenReachSquare % 8;
				int endY = queenReachSquare / 8;
				
				int dX = endX - startX;
				int dY = endY - startY;
				
				if (direction == 0 || (direction == 4 && dX == dY) || (direction == 2 && dX == dY * -1) || (direction == 4 && dX == dY) || (direction == 2 && dX == dY * -1)) {
					toReturn++;
				}
			}
			
			queenCanReach &= ~(1L << queenReachSquare);
		}
		
		return toReturn;
	}
	
	// check if a piece is pinned
	public static int pinned(int square) {
		if (board(square) >= p) return 0;
		
		return (pinnedDirection(square) != 0) ? 1 : 0;
	}
}