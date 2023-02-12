package utils.bot.DanielBotClasses;

import static utils.bot.DanielBotClasses.BitBoardEvaluation.*;
import static utils.bot.DanielBotClasses.BitBoardPerformanceTesting.*;
import static utils.bot.DanielBotClasses.BitBoardSettings.*;
import static utils.bot.DanielBotClasses.BitBoardTranspositionTable.*;
import static utils.bot.DanielBotClasses.BitBoardMoveGeneration.*;
import static utils.bot.DanielBotClasses.BitBoardConsts.*;
import static utils.bot.DanielBotClasses.BitBoardChessBoard.*;
import static utils.bot.DanielBotClasses.BitBoardIO.*;
import static utils.bot.DanielBotClasses.BitBoardBitManipulation.*;
import static utils.bot.DanielBotClasses.BitBoardZobrist.*;
import static utils.bot.DanielBotClasses.BitBoardRepetition.*;
import static utils.bot.DanielBotClasses.BitBoardBook.*;

// searches moves
public class BitBoardSearch {	
	static final int maxPly = maxDepth;
	
	// for Late Move Reduction
	static final int fullDepthMoves = 4;
	static final int reductionLimit = 3;
	
	// for iterative deepening with time limit
	static long startTime;
	static boolean keepSearching;
	
	public static int ply = 0;
	
	// killer and history moves heuristic
	public static int killerMoves[][] = new int[2][maxPly];
	public static int historyMoves[][] = new int[12][maxPly];
	
	// triangular principle variation table
	public static int pvLength[] = new int[maxPly];
	public static int pvTable[][] = new int[100][maxPly];
	
	// scoring and following PV nodes for move ordering
	static boolean scorePV = false;
	static boolean followPV = false;
	
	// diagnostic
	static int transpositions;
	
	// for transposition table, adjusting score
	public static int sideMultiplier = (side == white) ? 1 : -1;
	
	// search driver which does book moves and iterative deepening
	public static int searchPosition() {
		startTime = System.currentTimeMillis();
		
		// book move stuff
		if (useBook) {
			int bookMove = getBookMove();
			
			if (bookMove != -1) {
				makeMove(bookMove, allMoves);
				if (useUCIIO) {
					System.out.print("bestmove ");
					printMove(bookMove);
					System.out.println();
				}
				return bookMove;
			}
		}
		
		// initiate stuff
		int score = 0;
		nodes = 0;
		transpositions = 0;
		
		keepSearching = true;
		
		// reset stuff
		killerMoves = new int[2][maxPly];
		historyMoves = new int[12][maxPly];
		pvLength = new int[maxPly];
		pvTable = new int[maxPly][maxPly];
		scorePV = false;
		followPV = false;
		
		// for printing
		int bestMove = 0;
		int finalScore = 0;
		int maxDepthSearched = 0;
		
		// iterative deepening
		for (int currentDepth = 1; currentDepth <= maxDepth; currentDepth++) {
			long oldNodes = nodes;
			
			// aspiration search
			if (currentDepth == 1) {
				score = negamax(-32000, 32000, currentDepth);
			} else {
				int alpha = score - 50, beta = score + 50;
				
				score = negamax(alpha, beta, currentDepth);
				
				if (score <= alpha || score >= beta) {
					score = negamax(-32000, 32000, currentDepth);
				}
			}
			
			// we finished the search all the way to the end
			if (keepSearching) {
				// for diagnostics
				finalScore = score;
				maxDepthSearched = currentDepth;
				bestMove = pvTable[0][0];
				
				// enable following PV
				followPV = true;
				
				// special IO for uci, more pretty when not using UCI
				if (useUCIIO) {
					System.out.printf("info score cp %d depth %d nodes %d pv ", score, currentDepth, nodes);
				} else {
					System.out.printf("Depth: %d\tEval: %d \tNodes: %d\tPV: ", currentDepth, score, nodes- oldNodes);
				}
				
				// print principal variation
				for (int i = 0; i < pvLength[0]; i++) {
					printMove(pvTable[0][i]);
					System.out.print(" ");
				}
				
				System.out.println();
				
				// we found a mate, no need to continue searching
				if (score >= mateScoreThreshold || score <= -mateScoreThreshold) break;
			} else { // we did not finish searching due to the time limit
				break;
			}
		}
		
		ply = 0;
		
		// if we're using UCI, print standard. if we're not, print pretty
		if (!useUCIIO) {
			System.out.printf("\nDepth: %d\nEvaluation: %d\nNodes: %d\nTranspositions: %d\n\n", maxDepthSearched, finalScore, nodes, transpositions);
		}

		if (useUCIIO) {
			System.out.print("bestmove ");
			printMove(bestMove);
			System.out.println();
		}
		return bestMove;
	}
	
	// standard negamax with alpha beta pruning. and like, a bunch of other crap
	public static int negamax(int alpha, int beta, int depth) {
		// if time limit is reached, leave
		if (!keepSearching || (((nodes & 4096) == 0) && System.currentTimeMillis() - startTime >= (timeLimit - timeLimitMargin))) {
			keepSearching = false;
			
			return 0;
		}
		
		// set ply for pv table
		pvLength[ply] = ply;
		
		if (depth > 0 && (positionRepeated() || moveRule >= 50)) {
			return 0;
		}
		
		// we have reached max ply so some of our arrays are overflowing, break out
		if (ply >= maxPly) {
			return evaluate();
		}
		
		// is king in check
		boolean inCheck = isSquareAttacked((side == white) ? getLS1BIndex(bitboards[K]) : getLS1BIndex(bitboards[k]), side ^ 1);
		
		// check extension
		if (inCheck) {
			depth++;
		}
		
		// for hash
		int hashFlag = hashFlagAlpha;
		
		// is a principal variation node
		boolean isPVNode = beta - alpha > 1;
		
		// read from transposition table, and only use TT if it's not a pv node
		if (ply != 0 && !isPVNode) {
			int ttVal = readHashEntry(alpha, beta, depth);
			if (ttVal != noHashEntry) {
				transpositions++;
				return ttVal;
			}
		}
		
		// found principal variation (for Principal Variation Search)
		boolean foundPV = false;
		
		// if depth is 0, we run quiescence
		if (depth == 0) {
			// run quiescence search
			return quiescence(alpha, beta);
		}
		
		nodes++;
		
		// legal moves counter
		int legalMoves = 0;
		
		// get static evaluation score
		int staticEval = evaluate();
		
		// static null move pruning
		if (depth < 3 && !isPVNode && !inCheck && beta > -mateScoreThreshold) {
			int evalMargin = 120 * depth;
			
			if (staticEval - evalMargin >= beta) {
				return staticEval - evalMargin;
			}
		}
		
		// Null Move Pruning
		/**
		 * Operates under the observation that, if the opponent getting a free move doesn't improve their position enough, we can prune
		 * the tree. For example: if we start with an equal position, and then evaluate down to a node where we are up a rook, we can try
		 * giving the opponent a free move. If we are just simply up a rook, and giving them a free move doesn't allow them to catch up,
		 * then our score is much above beta and we can prune the rest of the tree.
		 * 
		 * An analogy in the real world - an experienced fighter gives his opponent a free shot. If the opponent doesn't deal some damage
		 * to the fighter with a free shot, then he is just dead lost. Similarly, we can prune dead lost positions out of the tree.
		 */
		if (depth >= 3 && !inCheck && ply != 0) {
			if ((bitboards[N] | bitboards[B] | bitboards[R] | bitboards[Q]
				| bitboards[n] | bitboards[b] | bitboards[r] | bitboards[q]) != 0) {
				copyBoard();
				
				ply++;
				
				// switch the side, giving them a free move
				side ^= 1;
				hashKey ^= sideKey;
				
				if (enPassant != no_sq) {
					hashKey ^= enPassantKeys[enPassant];
				}
				enPassant = no_sq;
				
				
				// check for beta cutoffs
				int score = -negamax(-beta, -beta + 1, depth - 3);
				
				ply--;
				
				addPosition(); // adjust for repetition table
				takeBack();
				
				if (score >= beta) {
					return beta;
				}
				
				if (!keepSearching) return 0;
			}
		}
		
		// razoring
		if (!isPVNode && !inCheck && depth <= 3) {
			int score = staticEval + 125;
			int newScore = 0;
			
			if (score < beta) {
				if (depth == 1) {
					newScore = quiescence(alpha, beta);
					
					return (newScore > score) ? newScore : score;
				}
				
				score += 175;
				
				if (score < beta && depth <= 2) {
					newScore = quiescence(alpha, beta);
					
					if (newScore < beta) {
						return (newScore > score) ? newScore : score;
					}
				}
			}
		}
		
		// generate moves
		moves moveList = new moves();
		
		generateMoves(moveList);
		if (followPV) enablePVScoring(moveList);
		sortMoves(moveList);
		
		int bestMoveInThisPosition = noHashEntry;
		
		// number of moves searched (for Late Move Reduction)
		int movesSearched = 0;
		
		// loop over all moves
		for (int count = 0; count < moveList.count; count++) {
			ply++;
						
			// only make a legal move (because we're using pseudolegal move generation)
			if (!makeMove(moveList.moves[count], allMoves)) {
				ply--;
				continue;
			}
			
			// increment legal moves
			legalMoves++;
			
			// futility pruning
			/**
			 * Conditions:
			 * 1. not a PV node
			 * 2. not in check
			 * 3. move does not give check
			 * 4. depth <= 2
			 * 5. neither alpha nor beta is a mating value
			 */
			if (!isPVNode
				&& !inCheck
				&& !isSquareAttacked((side == white) ? getLS1BIndex(bitboards[K]) : getLS1BIndex(bitboards[k]), side ^ 1)
				&& !getMoveCapture(moveList.moves[count])
				&& depth <= 2
				&& !(Math.max(Math.abs(alpha), Math.abs(beta)) > mateScoreThreshold)) {
				staticEval = -evaluate();
				if (staticEval + depth * 120 <= alpha) {
					takeBack();
					ply--;
					continue;
				}
			}
			
			// get score
			int score = 0;
			
			// principal variation search
			/**
			 * If we find a possible good move, we use the Principle Variation Search to check if there are any moves that are better.
			 * We set alpha and beta to be right next to each other, making the PVS very very fast. PVS will return if we fail low
			 * or fail high. Since we believe that our move is good, we believe that each PVS will fail low. However, if it fails
			 * high, we have to conduct a re-search.
			 * 
			 * But, because of move ordering, we can assume that we will have a relatively low amount of re-searches, improving
			 * our performance.
			 */
			if (foundPV) {
				score = -negamax(-alpha - 1, -alpha, depth -1);
				
				if ((score > alpha) && (score < beta)) {
					score = -negamax(-beta, -alpha, depth -1);
				}
			} else {
				// no late move reduction, normal search
				if (movesSearched == 0) {
					score = -negamax(-beta, -alpha, depth -1);
				} else {
					// Late Move Reduction
					/**
					 * Operates under the observation that, with good move ordering, a beta cutoff will usually happen at the first node,
					 * or not at all. Thus, we only enter LMR if movesSearched is not 0. We apply LMR to non-forcing moves, which means that
					 * we don't do reduced depth searches for positions in check, or for moves that capture or promote. Then, if the move
					 * surprises us with a score above alpha, we do a full depth search.
					 */
					if (movesSearched >= fullDepthMoves
						&& depth >= reductionLimit
						&& !inCheck
						&& !getMoveCapture(moveList.moves[count])
						&& getMovePromoted(moveList.moves[count]) == 0) {
						score = -negamax(-alpha - 1, -alpha, depth - 2);
					} else {
						score = alpha + 1;
					}
					
					if (score > alpha) {
						score = -negamax(-alpha - 1, -alpha, depth - 1);
						
						if ((score > alpha) && (score < beta)) {
							score = -negamax(-beta, -alpha, depth - 1);
						}
					}
				}
				
			}
			
			ply--;
			
			// return to previous board state
			takeBack();
						
			movesSearched++;
			
			// new best move
			if (score > alpha) {
				bestMoveInThisPosition = moveList.moves[count];
				
				// history moves heuristic
				if (!getMoveCapture(moveList.moves[count])) {
					historyMoves[getMovePiece(moveList.moves[count])][getMoveTarget(moveList.moves[count])] += depth;
				}
				
				alpha = score;
				hashFlag = hashFlagExact;
				
				foundPV = true;
				
				// write PV move
				pvTable[ply][ply] = moveList.moves[count];
				
				// copy move from deeper ply into current ply's line
				for (int nextPly = ply + 1; nextPly < pvLength[ply + 1]; nextPly++) {
					pvTable[ply][nextPly] = pvTable[ply + 1][nextPly];
				}
				
				pvLength[ply] = pvLength[ply + 1];
				
				// beta cutoff
				if (score >= beta) {
					writeHashEntry(beta, depth, hashFlagBeta, moveList.moves[count]);
					
					if (!getMoveCapture(moveList.moves[count])) {
						killerMoves[1][ply] = killerMoves[0][ply];
						killerMoves[0][ply] = moveList.moves[count];
					}
					
					return beta;
				}
			}
		}
		
		// no legal moves
		if (legalMoves == 0) {
			// mate
			if (inCheck) {
				// mating score is larger the smaller the ply is
				return -(mateScore - ply);
			} else {
				return 0;
			}
		}
		
		writeHashEntry(alpha, depth, hashFlag, bestMoveInThisPosition);
		
		return alpha;
	}
	
	// only search captures, to limit horizon effect
	public static int quiescence(int alpha, int beta) {
		// time limit reached
		if (!keepSearching || (((nodes & 4096) == 0) && System.currentTimeMillis() - startTime >= (timeLimit - timeLimitMargin))) {
			keepSearching = false;
			
			return 0;
		}
		
		nodes++;

		int evaluation = evaluate();
		
		// we have reached our max ply, so we just leave
		if (ply >= maxPly) {
			return evaluation;
		}
		
		// beta cutoff
		if (evaluation >= beta) {
			return beta;
		}
		
		// delta pruning
		int bigDelta = 1125;
		
		// increase bigDelta if pawn might be able to promote
		if ((bitboards[P + side * 6] & rankMasks[(side == white) ? a7 : a2]) != 0) bigDelta += 775;
		
		if (evaluation < alpha - bigDelta) {
			return alpha;
		}
		
		// found better move
		if (evaluation > alpha) {
			alpha = evaluation;
		}
	
		// generate moves
		moves moveList = new moves();
		
		generateMoves(moveList);
		sortMoves(moveList);
		
		// loop over moves
		for (int count = 0; count < moveList.count; count++) {
			ply++;
			
			// only make a legal move
			if (!makeMove(moveList.moves[count], nonQuietOnly)) {
				ply--;
				continue;
			}
			
			// get score
			int score = -quiescence(-beta, -alpha);

			ply--;
			
			// return to previous board state
			takeBack();
			
			// new best move
			if (score > alpha) {
				alpha = score;
				
				// beta cutoff
				if (score >= beta) {
					return beta;
				}
			}
		}
		
		return alpha;
	}
	
	public static void enablePVScoring(moves moveList) {
		// disable by default, in case we do not find a pv node
		followPV = false;
		
		for (int count = 0; count < moveList.count; count++) {
			if (pvTable[0][ply] == moveList.moves[count]) {
				// enable pv stuff
				scorePV = true;
				followPV = true;
			}
		}
	}
	
	// score a move for move ordering
	public static int scoreMove(int move) {
		// if it's the best move in the PV table, score it ultra super mega high
		if (scorePV) {
			if (pvTable[0][ply] == move) {
				scorePV = false;
				return 30000;
			}
		}
		
		// if it's the best recommended move in the transposition table, score it super high
		if (move == getStoredMove()) {
			return 20000;
		}
		
		// if it's a capture, score it high
		if (getMoveCapture(move)) {
			int targetPiece = P;
			
			int startPiece, endPiece;
			
			if (side == white) { startPiece = p; endPiece = k; }
			else { startPiece = P; endPiece = K; }
			
			for (int bbPiece = startPiece; bbPiece <= endPiece; bbPiece++) {
				if (getBit(bitboards[bbPiece], getMoveTarget(move)) != 0) {
					targetPiece = bbPiece;
				}
			}
			
			// score it higher or less higher based on how valuable the victim is, and how valuable the attacker is
			return mvv_lva[getMovePiece(move)][targetPiece] + 10000;
		} else {
			// if it's a killer move, score it kinda high
			if (killerMoves[0][ply] == move) {
				return 9000;
			} else if (killerMoves[1][ply] == move) {
				return 8000;
			} else { // if not, default to history moves
				return historyMoves[getMovePiece(move)][getMoveTarget(move)];
			}
		}
	}
	
	// sort moves for move ordering
	public static void sortMoves(moves moveList) {
		int moveScores[] = new int[moveList.count];
		
		for (int count = 0; count < moveList.count; count++) {
			moveScores[count] = scoreMove(moveList.moves[count]);
		}
		
		// lazy O(n^2) sort
		for (int currentMove = 0; currentMove < moveList.count; currentMove++) {
			for (int nextMove = 0; nextMove < moveList.count; nextMove++) {
				if (moveScores[currentMove] > moveScores[nextMove]) {
					int tempScore = moveScores[currentMove];
					moveScores[currentMove] = moveScores[nextMove];
					moveScores[nextMove] = tempScore;
					
					int tempMove = moveList.moves[currentMove];
					moveList.moves[currentMove] = moveList.moves[nextMove];
					moveList.moves[nextMove] = tempMove;
				}
			}
		}
	}
}