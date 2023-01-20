package utils.bot.DanielBotClasses.StockfishEval;

import static utils.bot.DanielBotClasses.BitBoardConsts.*;
import static utils.bot.DanielBotClasses.BitBoardChessBoard.*;
import static utils.bot.DanielBotClasses.BitBoardBitManipulation.*;
import static utils.bot.DanielBotClasses.BitBoardAttacks.*;
import static utils.bot.DanielBotClasses.BitBoardIO.*;
import static utils.bot.DanielBotClasses.StockfishEval.Global.*;
import static utils.bot.DanielBotClasses.StockfishEval.Attack.*;
import static utils.bot.DanielBotClasses.StockfishEval.Helpers.*;
import static utils.bot.DanielBotClasses.StockfishEval.Consts.*;

public class King {
	public static int pawnlessFlank() {
		int pawns[] = { 0, 0, 0, 0, 0, 0, 0, 0 };
		
		long bitboard = bitboards[P] | bitboards[p];
		
		while (bitboard != 0) {
			int square = getLS1BIndex(bitboard);
			
			pawns[square % 8] = 1;
			
			bitboard &= ~(1L << square);
		}
		
		int kx = getLS1BIndex(bitboards[k]) % 8;
		
		int sum = 0;
		
		if (kx == 0) sum = pawns[0] + pawns[1] + pawns[2];
		else if (kx < 3) sum = pawns[0] + pawns[1] + pawns[2] + pawns[3];
		else if (kx < 5) sum = pawns[2] + pawns[3] + pawns[4] + pawns[5];
		else if (kx < 7) sum = pawns[4] + pawns[5] + pawns[6] + pawns[7];
		else sum = pawns[5] + pawns[6] + pawns[7];
		
		return (sum == 0) ? 1 : 0;
	}
	
	public static int strengthSquare(int square) {
		int toReturn = 5;
		int kx = Math.min(6, Math.max(1, square % 8));
		
		for (int x = kx - 1; x <= kx + 1; x++) {
			int us = 0;
			for (int y = 7; y >= square / 8; y--) {
				if (getBit(bitboards[p], x + y * 8) != 0 && board(y + 1, x - 1) != P && board(y + 1, x + 1) != P) {
					us = y;
				}
			}
			
			toReturn += weakness[Math.min(x, 7 - x)][us];
		}
		
		return toReturn;
	}
	
	public static int stormSquare(int square, boolean endgame) {
		int midgamePoints = 0;
		int endgamePoints = 5;
		
		int kx = Math.min(6,  Math.max(1,  square % 8));
		
		for (int x = kx - 1; x <= kx + 1; x++) {
			int us = 0, them = 0;
			for (int y = 7; y >= square / 8; y--) {
				if (getBit(bitboards[p], x + y * 8) != 0 && board(y + 1, x - 1) != P && board(y + 1, x + 1) != P) {
					us = y;
				}
				
				if (getBit(bitboards[P], x + y * 8) != 0) {
					them = y;
				}
			}
			
			int f = Math.min(x, 7 - x);
			
			if (us > 0 && them == us + 1) {
				midgamePoints += blockedStorm[0][them];
				endgamePoints += blockedStorm[1][them];
			} else {
				midgamePoints += unblockedStorm[f][them];
			}
		}
		
		return (endgame) ? endgamePoints : midgamePoints;
	}
	
	public static int shelterStrength(int square) {
		int w = 0;
		int s = 1024;
		int tx = -1;
		
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				if (getBit(bitboards[k], x + y * 8) != 0 || ((castle & bk) != 0 && x == 6 && y == 0) || ((castle & bq) != 0 && x == 2 && y == 0)) {
					int w1 = strengthSquare(x + y * 8);
					int s1 = stormSquare(x + y * 8, false);
					
					if (s1 - w1 < s - w) {
						w = w1;
						s = s1;
						tx = Math.max(1, Math.min(6, x));
					}
				}
			}
		}
		
		if (square == -1) {
			return w;
		}
		
		if (tx != -1 && getBit(bitboards[p], square) != 0 && square % 8 >= tx - 1 && square % 8 <= tx + 1) {
			for (int y = square / 8 - 1; y >= 0; y--) {
				if (getBit(bitboards[p], square % 8 + y * 8) != 0) return 0;
				
				return 1;
			}
		}
		return 0;
	}
	
	public static int shelterStorm(int square) {
		int w = 0;
		int s = 1024;
		int tx = -1;
		
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				if (getBit(bitboards[k], x + y * 8) != 0 || ((castle & bk) != 0 && x == 6 && y == 0) || ((castle & bq) != 0 && x == 2 && y == 0)) {
					int w1 = strengthSquare(x + y * 8);
					int s1 = stormSquare(x + y * 8, false);
					
					if (s1 - w1 < s - w) {
						w = w1;
						s = s1;
						tx = Math.max(1, Math.min(6, x));
					}
				}
			}
		}
		
		if (square == -1) {
			return s;
		}
		
		if (tx != -1 && getBit(bitboards[P], square) != 0 && square % 8 >= tx - 1 && square % 8 <= tx + 1) {
			for (int y = square / 8 - 1; y >= 0; y--) {
				if (board(square % 8, y) == board(square % 8, square / 8)) return 0;
				
				return 1;
			}
		}
		return 0;
	}
	
	public static int kingPawnDistance(int square) {
		int toReturn = 6;
		int kingSquare = getLS1BIndex(bitboards[K]);
		int kx = kingSquare % 8, ky = kingSquare / 8;
		int px = -1, py = -1;
		
		long bitboard = bitboards[P];
		
		while (bitboard != 0) {
			int pawnSquare = getLS1BIndex(bitboard);
			
			int dist = Math.max(Math.abs(pawnSquare % 8 - kx), Math.abs(pawnSquare / 8 - ky));
			
			if (dist < toReturn) {
				px = pawnSquare % 8;
				py = pawnSquare / 8;
				toReturn = dist;
			}
			
			bitboard &= ~(1L << pawnSquare);
		}
		
		if (square == -1 || (square % 8 == px && square / 8 == py)) {
			return toReturn;
		}
		
		return 0;
	}
	
	public static int check(int square, int type) {
		if (square == -1) {
			int sum = 0;
			
			for (int i = 0; i < 64; i++) {
				sum += check(i, type);
			}
			
			return sum;
		}
		
		if (rookXRayAttack(square, -1) != 0 && (type == -1 || type == 2 || type == 4)) {
			long blocker = occupancies[both] & ~(bitboards[q]);
			
			long attacks = getRookAttacks(square, blocker);
			
			if ((attacks & bitboards[k]) != 0) {
				return 1;
			}
		}
		
		if (queenAttack(square, -1) != 0 && (type == -1 || type == 3)) {
			long blocker = occupancies[both] & ~(bitboards[q]);
			
			long attacks = getQueenAttacks(square, blocker);
			
			if ((attacks & bitboards[k]) != 0) {
				return 1;
			}
		}
		
		if (bishopXRayAttack(square, -1) != 0 && (type == -1 || type == 1 || type == 4)) {
			long blocker = occupancies[both] & ~(bitboards[q]);
			
			long attacks = getBishopAttacks(square, blocker);
			
			if ((attacks & bitboards[k]) != 0) {
				return 1;
			}
		}
		
		if (knightAttack(square, -1) != 0 && (type == -1 || type == 0 || type == 4)) {
			if ((knightAttacks[square] & bitboards[k]) != 0) {
				return 1;
			}
		}
		
		return 0;
	}
	
	public static int safeCheck(int square, int type) {
		if (square == -1) {
			int sum = 0;
			
			for (int i = 0; i < 64; i++) {
				sum += safeCheck(i, type);
			}
			
			return sum;
		}
		
		if (board(square) <= K && board(square) >= P) return 0;
		
		if (type == 3 && safeCheck(square, 2) != 0) return 0;
		if (type == 1 && safeCheck(square, 3) != 0) return 0;
		
		int weakSquare = weakSquares(square);
		int attacked = attack(square);
		
		changeColor();
		
		int x = square % 8;
		int y = square / 8;
		
		if ((attack(x + (7 - y) * 8) == 0)
			|| (weakSquare != 0 && attacked > 1)
			&& (type != 3 || queenAttack(x + (7 - y) * 8, -1) == 0)) return 1;
		
		return 0;
	}
	
	public static int kingAttackersCount(int square) {
		if (square == -1) {
			int sum = 0;
			
			for (int i = 0; i < 64; i++) {
				sum += kingAttackersCount(i);
			}
			
			return sum;
		}
		
		if (board(square) < 0 || board(square) > K) return 0;
		
		if (board(square) == P) {
			long pawnAttackBitboard = pawnAttacks[white][square];
			int sum = 0;
			
			while (pawnAttackBitboard != 0) {
				int pawnAttackSquare = getLS1BIndex(pawnAttackBitboard);
				
				if (kingRing(pawnAttackSquare, true) != 0) {
					sum++;
				}
				
				pawnAttackBitboard &= ~(1L << pawnAttackSquare);
			}
			
			return sum;
		}
		
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				int s2 = x + y * 8;
				
				if (kingRing(s2, false) != 0) {
					if (knightAttack(s2, square) + bishopXRayAttack(s2, square) + rookXRayAttack(s2, square) + queenAttack(s2, square) != 0) {
						return 1;
					}
				}
			}
		}
		
		return 0;
	}
	
	public static int kingAttackersWeight(int square) {
		if (square == -1) {
			int sum = 0;
			
			for (int i = 0; i < 64; i++) {
				sum += kingAttackersWeight(i);
			}
			
			return sum;
		}
		
		if (kingAttackersCount(square) != 0) {
			return kingAttackerWeight[board(square)];
		}
		
		return 0;
	}
	
	public static int weakSquares(int square) {
		return 0;
	}
}