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
import static utils.bot.DanielBotClasses.StockfishEval.Mobility.*;

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
				if (getBit(bitboards[p], x + y * 8) != 0 && board(x - 1, y + 1) != P && board(x + 1, y + 1) != P) {
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
				if (getBit(bitboards[p], x + y * 8) != 0 && board(x + 1, y - 1) != P && board(x + 1, y + 1) != P) {
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
		if (check(square, type) == 0) return 0;
		
		if (type == 3 && safeCheck(square, 2) != 0) return 0;
		if (type == 1 && safeCheck(square, 3) != 0) return 0;
		
		int weakSquare = weakSquares(square);
		int attacked = attack(square);
		
		changeColor();
		
		int x = square % 8;
		int y = square / 8;
		
		if ((attack(x + (7 - y) * 8) == 0)
			|| (weakSquare != 0 && attacked > 1)
			&& (type != 3 || queenAttack(x + (7 - y) * 8, -1) == 0)) { changeColor(); return 1; }
		
		changeColor();
		
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
	
	public static int kingAttacks(int square) {
		if (square == -1) {
			int sum = 0;
			
			for (int i = 0; i < 64; i++) {
				sum += kingAttacks(i);
			}
			
			return sum;
		}
		
		if (board(square) < N || board(square) > Q) return 0;
		if (kingAttackersCount(square) == 0) return 0;
		
		int kingSquare = getLS1BIndex(bitboards[k]);
		
		int kx = kingSquare % 8;
		int ky = kingSquare / 8;
		
		int toReturn = 0;
		
		for (int x = kx - 1; x <= kx + 1; x++) {
			for (int y = ky - 1; y <= ky + 1; y++) {
				int s2 = x + y * 8;
				
				if (x >= 0 && y >= 0 && x <= 7 && y <= 7 && (x != kx || y != ky)) {
					toReturn += knightAttack(s2, square);
					toReturn += bishopXRayAttack(s2, square);
					toReturn += rookXRayAttack(s2, square);
					toReturn += queenAttack(s2, square);
				}
			}
		}
		
		return toReturn;
	}
	
	public static int weakBonus(int square) {
		if (square == -1) {
			int sum = 0;
			
			for (int i = 0; i < 64; i++) {
				sum += weakBonus(i);
			}
			
			return sum;
		}
		
		if (weakSquares(square) == 0) return 0;
		if (kingRing(square, false) == 0) return 0;
		
		return 1;
	}
	
	public static int weakSquares(int square) {
		if (square == -1) {
			int sum = 0;
			
			for (int i = 0; i < 64; i++) {
				sum += weakSquares(i);
			}
			
			return sum;
		}
		
		if (attack(square) != 0) {
			changeColor();
			
			int attacked = attack(square % 8 + (7 - square / 8) * 8);
			
			if (attacked >= 2) { changeColor(); return 0; }
			if (attacked == 0) { changeColor(); return 1; }
			
			if (kingAttack(square % 8 + (7 - square / 8) * 8) != 0
				|| queenAttack(square % 8 + (7 - square / 8) * 8, -1) != 0) { changeColor(); return 1; }
			
			changeColor();
		}
		
		return 0;
	}
	
	public static int unsafeChecks(int square) {
		if (square == -1) {
			int sum = 0;
			
			for (int i = 0; i < 64; i++) {
				sum += unsafeChecks(i);
			}
			
			return sum;
		}
		
		if (check(square, 0) != 0 && safeCheck(-1, 0) == 0) return 1;
		if (check(square, 1) != 0 && safeCheck(-1, 1) == 0) return 1;
		if (check(square, 2) != 0 && safeCheck(-1, 2) == 0) return 1;
		
		return 0;
	}
	
	public static int knightDefender(int square) {
		if (square == -1) {
			int sum = 0;
			
			for (int i = 0; i < 64; i++) {
				sum += knightDefender(i);
			}
			
			return sum;
		}
		
		if (knightAttack(square, -1) != 0 && kingAttack(square) != 0) return 1;
		
		return 0;
	}
	
	public static int endgameShelter(int square) {
		int w = 0, s = 1024, e = 0;
		
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				if (getBit(bitboards[k], x + y * 8) != 0 || ((castle & bk) != 0 && x == 6 && y == 0) || ((castle & bq) != 0 && x == 2 && y == 0)) {
					int w1 = strengthSquare(x + y * 8);
					int s1 = stormSquare(x + y * 8, false);
					int e1 = stormSquare(x + y * 8, true);
					
					if (s1 - w1 < s - w) {
						w = w1;
						s = s1;
						e = e1;
					}
				}
			}
		}
		
		if (square == -1) return e;
		return 0;
	}
	
	public static int blockersForKing(int square) {
		if (square == -1) {
			changeColor();
			
			int sum = 0;
			
			for (int i = 0; i < 64; i++) {
				if (pinnedDirection(i) != 0) sum += 1;
			}
			
			changeColor();
			
			return sum;
		}
		
		changeColor();
		
		int result = pinnedDirection(square % 8 + (7 - square / 8) * 8);
		
		changeColor();
		
		return (result != 0) ? 1 : 0;
	}
	
	public static int flankAttack(int square) {
		if (square == -1) {
			int sum = 0;
			
			for (int i = 0; i < 64; i++) {
				sum += flankAttack(i);
			}
			
			return sum;
		}
		
		if (square / 8 > 4) return 0;
		
		int kingSquare = getLS1BIndex(bitboards[k]);
		
		int kx = kingSquare % 8;
		int ky = kingSquare / 8;
		
		int x = square % 8;
		
		if (kx == 0 && x > 2) return 0;
		if (kx < 3 && x > 3) return 0;
		if (kx >= 3 && kx < 5 && (x < 2 || x > 5)) return 0;
		if (kx >= 5 && x < 4) return 0;
		if (kx == 7 && x < 5) return 0;
		
		int a = attack(square);
		
		if (a == 0) return 0;
		return (a > 1) ? 2 : 1;
	}
	
	public static int flankDefense(int square) {
		if (square == -1) {
			int sum = 0;
			
			for (int i = 0; i < 64; i++) {
				sum += flankDefense(i);
			}
			
			return sum;
		}
		
		if (square / 8 > 4) return 0;
		
		int kingSquare = getLS1BIndex(bitboards[k]);
		
		int kx = kingSquare % 8;
		int ky = kingSquare / 8;
		
		int x = square % 8;
		
		if (kx == 0 && x > 2) return 0;
		if (kx < 3 && x > 3) return 0;
		if (kx >= 3 && kx < 5 && (x < 2 || x > 5)) return 0;
		if (kx >= 5 && x < 4) return 0;
		if (kx == 7 && x < 5) return 0;
		
		changeColor();
		int a = attack(square % 8 + (7 - square / 8) * 8);
		changeColor();
		
		return (a > 0) ? 1 : 0;
	}
	
	public static int kingDanger() {
		int count = kingAttackersCount(-1);
		int weight = kingAttackersWeight(-1);
		int kingAttacks = kingAttacks(-1);
		int weak = weakBonus(-1);
		int unsafeChecks = unsafeChecks(-1);
		int blockersForKing = blockersForKing(-1);
		int kingFlankAttack = flankAttack(-1);
		int kingFlankDefense = flankDefense(-1);
		int noQueen = (queenCount() > 0) ? 0 : 1;
		int mobilityMG = mobilityMG(-1);
		
		changeColor();
		int knightDefender = (knightDefender(-1) > 0) ? 1 : 0;
		mobilityMG -= mobilityMG(-1);
		changeColor();
		
		int toReturn = count * weight
					+ 69 * kingAttacks
					+ 185 * weak
					- 100 * knightDefender
					+ 148 * unsafeChecks
					+ 98 * blockersForKing
					- 4 * kingFlankDefense
					+ 3 * kingFlankAttack * kingFlankAttack / 8
					- 873 * noQueen
					- 6 * (shelterStrength(-1) - shelterStorm(-1)) / 8
					+ mobilityMG
					+ 37
					+ (int) (772 * Math.min(safeCheck(-1, 3), 1.45))
					+ (int) (1084 * Math.min(safeCheck(-1, 2), 1.75))
					+ (int) (645 * Math.min(safeCheck(-1, 1), 1.5))
					+ (int) (792 * Math.min(safeCheck(1, 0), 1.62));
		
		if (toReturn > 100) return toReturn;
		
		return 0;			
	}
	
	public static int kingMG() {
		int toReturn = 0;
		int kd = kingDanger();
		
		toReturn -= shelterStrength(-1);
		toReturn += shelterStorm(-1);
		toReturn += (kd * kd / 4096);
		toReturn += 8 * flankAttack(-1);
		toReturn += 17 * pawnlessFlank();
		
		return toReturn;
	}
	
	public static int kingEG() {
		int toReturn = 0;
		
		toReturn -= 16 * kingPawnDistance(-1);
		toReturn += endgameShelter(-1);
		toReturn += 95 * pawnlessFlank();
		toReturn += (kingDanger() / 16);
		
		return toReturn;
	}
}