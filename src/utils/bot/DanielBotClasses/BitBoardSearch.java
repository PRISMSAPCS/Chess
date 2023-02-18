package utils.bot.DanielBotClasses;

import static utils.bot.DanielBotClasses.BitBoardEvaluation.*;
import static utils.bot.DanielBotClasses.BitBoardPerformanceTesting.*;
import static utils.bot.DanielBotClasses.BitBoardSettings.*;
import static utils.bot.DanielBotClasses.BitBoardTranspositionTable.*;
import static utils.bot.DanielBotClasses.BitBoardConsts.*;
import static utils.bot.DanielBotClasses.BitBoardChessBoard.*;
import static utils.bot.DanielBotClasses.BitBoardIO.*;
import static utils.bot.DanielBotClasses.BitBoardBitManipulation.*;
import static utils.bot.DanielBotClasses.BitBoardZobrist.*;

import java.util.ArrayList;
import java.util.concurrent.*;

import utils.ChessBoard;
import utils.bot.TonyNegaMaxPVSTT.MoveScore;

import static utils.bot.DanielBotClasses.BitBoardBook.*;

// searches moves
public class BitBoardSearch {
	// global chess board instance to represent the base state of the board
	public static BitBoardChessBoard bbBoard = null;
	
	static final int maxPly = maxDepth;
	
	// for Late Move Reduction
	static final int fullDepthMoves = 4;
	static final int reductionLimit = 3;
	
	// for iterative deepening with time limit
	static long startTime;
	static boolean keepSearching;
	
//	public static int ply = 0;
//	
//	// killer and history moves heuristic
//	public static int killerMoves[][] = new int[2][maxPly];
//	public static int historyMoves[][] = new int[12][maxPly];
//	
//	// triangular principle variation table
//	public static int pvLength[] = new int[maxPly];
//	public static short pvTable[][] = new short[100][maxPly];
//	
//	// scoring and following PV nodes for move ordering
//	static boolean scorePV = false;
//	static boolean followPV = false;
//	
//	// diagnostic
//	static int transpositions;
	
	// for transposition table, adjusting score
	public static int sideMultiplier;
	
	// for multithreading
	private final static int MAX_THREADS = Math.min(Runtime.getRuntime().availableProcessors() - 1, 1);
    public static ExecutorService thPool = Executors.newFixedThreadPool(MAX_THREADS);
	
	// search driver which does book moves and iterative deepening
//	public static int searchPosition() {
//		startTime = System.currentTimeMillis();
//		
//		// book move stuff
//		if (useBook) {
//			int bookMove = getBookMove();
//			
//			if (bookMove != -1) {
//				bbBoard.makeMove(bookMove, allMoves);
//				if (useUCIIO) {
//					System.out.print("bestmove ");
//					printMove(bookMove);
//					System.out.println();
//				}
//				return bookMove;
//			}
//		}
//		
////		// initiate stuff
//		int score = 0;
////		nodes = 0;
////		transpositions = 0;
////		
//		keepSearching = true;
////		
//		sideMultiplier = (bbBoard.side == white) ? 1 : -1;
////		
////		// reset stuff
////		killerMoves = new int[2][maxPly];
////		historyMoves = new int[12][maxPly];
////		pvLength = new int[maxPly];
////		pvTable = new short[maxPly][maxPly];
////		scorePV = false;
////		followPV = false;
//		
//		// for printing
//		int bestMove = 0;
//		int finalScore = 0;
//		int maxDepthSearched = 0;
//		
//		// iterative deepening
//		for (int currentDepth = 1; currentDepth <= maxDepth; currentDepth++) {
//			long oldNodes = nodes;
//			
//			// aspiration search
//			if (currentDepth == 1) {
//				score = negamax(-32000, 32000, currentDepth);
//			} else {
//				int alpha = score - 50, beta = score + 50;
//				
//				score = negamax(alpha, beta, currentDepth);
//				
//				if (score <= alpha || score >= beta) {
//					score = negamax(-32000, 32000, currentDepth);
//				}
//			}
//			
//			// we finished the search all the way to the end
//			if (keepSearching) {
//				// for diagnostics
//				finalScore = score;
//				maxDepthSearched = currentDepth;
//				bestMove = pvTable[0][0];
//				
//				// enable following PV
//				followPV = true;
//				
//				// special IO for uci, more pretty when not using UCI
//				if (useUCIIO) {
//					System.out.printf("info score cp %d depth %d nodes %d pv ", score, currentDepth, nodes);
//				} else {
//					System.out.printf("Depth: %d\tEval: %d \tNodes: %d\tPV: ", currentDepth, score, nodes- oldNodes);
//				}
//				
//				// print principal variation
//				for (int i = 0; i < pvLength[0]; i++) {
//					printMove(pvTable[0][i]);
//					System.out.print(" ");
//				}
//				
//				System.out.println();
//				
//				// we found a mate, no need to continue searching
//				if (score >= mateScoreThreshold || score <= -mateScoreThreshold) break;
//			} else { // we did not finish searching due to the time limit
//				break;
//			}
//		}
//		
//		ply = 0;
//		
//		// if we're using UCI, print standard. if we're not, print pretty
//		if (!useUCIIO) {
//			System.out.printf("\nDepth: %d\nEvaluation: %d\nNodes: %d\nTranspositions: %d\n\n", maxDepthSearched, finalScore, nodes, transpositions);
//		}
//
//		if (useUCIIO) {
//			System.out.print("bestmove ");
//			printMove(bestMove);
//			System.out.println();
//		}
//		return bestMove;
//	}
	
	public static short searchPosition() {
		keepSearching = true;
		
		
		
		// book move stuff
		if (useBook) {
			int bookMove = getBookMove();
			
			if (bookMove != -1) {
				bbBoard.makeMove(bookMove, allMoves);
				if (useUCIIO) {
					System.out.print("bestmove ");
					printMove(bookMove);
					System.out.println();
				}
				return (short) bookMove;
			}
		}
		
		startTime = System.currentTimeMillis();

	    short bestMove = 0;
		short finalScore = -32750;
		int nodes = 0;
		boolean inAspiration = false;
		int maxDepthSearched = 0;
		int alpha;
		int beta;
		int transpositions = 0;
		sideMultiplier = (bbBoard.side == white) ? 1 : -1;
		
		for (int depth = 1; depth < maxDepth; depth++) {
			ArrayList<Future<ThreadInformation>> futureMoveRets = new ArrayList<>();
			
			if (inAspiration) {
				alpha = finalScore - 50;
				beta = finalScore + 50;
			} else {
				alpha = -32000;
				beta = 32000;
			}
			
			for (int i = 0; i < MAX_THREADS; i++) {
				final int fdep = depth;
				final int adjust = i % 2;
				final int a = alpha;
				final int b = beta;
				futureMoveRets.add(
						thPool.submit(() -> {
							ThreadInformation ti = new ThreadInformation();
	                        ti.score = negamax(a, b, fdep, ti);
	                        return ti;
	                    }));
			}
			
			ArrayList<ThreadInformation> moveRets = new ArrayList<>();
			
			int oldNodes = nodes;
			
			try {
				for (Future<ThreadInformation> f : futureMoveRets) {
					ThreadInformation ms = f.get();
					
					moveRets.add(ms);
					nodes += ms.nodes;
					transpositions += ms.transpositions;
				}
			} catch (Exception e) { e.printStackTrace(); }
			
			if (moveRets.get(0).score <= alpha || moveRets.get(0).score >= beta) {
				inAspiration = false;
				depth--;
				continue;
			}
			
			if (keepSearching) {
				bestMove = moveRets.get(0).pvTable[0][0];
				finalScore = moveRets.get(0).score;
				inAspiration = true;
				maxDepthSearched = depth;
				
				System.out.printf("Depth: %d\tEval: %d \tNodes: %d\tPV: ", depth, finalScore, nodes - oldNodes);
				
				for (int i = 0; i < moveRets.get(0).pvLength[0]; i++) {
					printMove(moveRets.get(0).pvTable[0][i]);
					System.out.print(" ");
				}
				
				System.out.println();
				
				// search for at least 500 milliseconds, and if we find a mate, break out
				if ((System.currentTimeMillis() - startTime >= 500) && (finalScore >= mateScoreThreshold || finalScore <= -mateScoreThreshold)) break;
			} else {
				break;
			}
		}
		
		System.out.printf("\nDepth: %d\nEvaluation: %d\nNodes: %d\nTranspositions: %d\n\n", maxDepthSearched, finalScore, nodes, transpositions);
		
		return bestMove;
	}
	
	// standard negamax with alpha beta pruning. and like, a bunch of other crap
	public static short negamax(int alpha, int beta, int depth, ThreadInformation t) {
		// if time limit is reached, leave
		if (!keepSearching || (((nodes & 4096) == 0) && System.currentTimeMillis() - startTime >= (timeLimit - timeLimitMargin))) {
			keepSearching = false;
			
			return 0;
		}
		
		//ThreadInformation t = ti.get();
		BitBoardChessBoard board = t.board;

		// set ply for pv table
		t.pvLength[t.ply] = t.ply;
		
		if (depth > 0 && (board.positionRepeated() || board.moveRule >= 50)) {
			return 0;
		}
		
		// we have reached max ply so some of our arrays are overflowing, break out
		if (t.ply >= maxPly) {
			return board.evaluate();
		}
		
		// is king in check
		boolean inCheck = board.isSquareAttacked((board.side == white) ? getLS1BIndex(board.bitboards[K]) : getLS1BIndex(board.bitboards[k]), board.side ^ 1);
		
		// check extension
		if (inCheck) {
			depth++;
		}
		
		// for hash
		int hashFlag = hashFlagAlpha;
		
		// is a principal variation node
		boolean isPVNode = beta - alpha > 1;
		
		// read from transposition table, and only use TT if it's not a pv node
		if (t.ply != 0 && !isPVNode) {
			short ttVal = readHashEntry(alpha, beta, depth, board.hashKey, t.ply);
			if (ttVal != noHashEntry) {
					t.transpositions++;
					return (short) ttVal;
			}
		}
		
		// found principal variation (for Principal Variation Search)
		boolean foundPV = false;
		
		// if depth is 0, we run quiescence
		if (depth <= 0) {
			// run quiescence search
			return quiescence(alpha, beta, t);
		}
		
		t.nodes++;
		
		// legal moves counter
		int legalMoves = 0;
		
		// get static evaluation score
		short staticEval = board.evaluate();
		
		// static null move pruning
		if (depth < 3 && !isPVNode && !inCheck && beta > -mateScoreThreshold) {
			int evalMargin = 120 * depth;
			
			if (staticEval - evalMargin >= beta) {
				return (short) (staticEval - evalMargin);
			}
		}
		
		// razoring
		if (!isPVNode && !inCheck && depth <= 3) {
			short score = (short) (staticEval + 125);
			short newScore = 0;
			
			if (score < beta) {
				if (depth == 1) {
					newScore = quiescence(alpha, beta, t);
					
					return (newScore > score) ? newScore : score;
				}
				
				score += 175;
				
				if (score < beta && depth <= 3) {
					newScore = quiescence(alpha, beta, t);
					
					if (newScore < beta) {
						return (newScore > score) ? newScore : score;
					}
				}
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
		if (depth >= 3 && !inCheck && t.ply != 0) {
			if ((board.bitboards[N] | board.bitboards[B] | board.bitboards[R] | board.bitboards[Q]
				| board.bitboards[n] | board.bitboards[b] | board.bitboards[r] | board.bitboards[q]) != 0) {
				board.copyBoard();
				
				// depth reduction
				short R = 3;
				int numPieces = 0;
				if (depth > 7) {
					for (int bbPiece = N + board.side * 6; bbPiece <= Q + board.side * 6; bbPiece++) {
						numPieces += countBits(board.bitboards[bbPiece]);
					}
					if (numPieces >= 2) R++;
				}
								
				t.ply++;
				
				// switch the side, giving them a free move
				board.side ^= 1;
				board.hashKey ^= sideKey;
				
				if (board.enPassant != no_sq) {
					board.hashKey ^= enPassantKeys[board.enPassant];
				}
				board.enPassant = no_sq;
				
				// check for beta cutoffs
				int bound = beta - 10;
				int score = -negamax(-bound, -bound + 1, depth - R - 1, t);
				
				t.ply--;
				
				board.addPosition(); // adjust for repetition table
				board.takeBack();
				
				if (score >= bound) {
					return (short) beta;
				}
				
				if (!keepSearching) return 0;
			}
		}
		
		// generate moves
		moves moveList = new moves();
		
		int probCutBeta = beta + 100;
		board.generateMoves(moveList);
		if (t.followPV) enablePVScoring(moveList, t);
		sortMoves(moveList, alpha, beta, depth, t);
		
		if (!isPVNode
			&& depth > 4) {
			for (int count = 0; count < moveList.count; count++) {
				if (!board.makeMove(moveList.moves[count], allMoves)) {
					continue;
				}
				
				t.ply++;
				
				int score = -quiescence(-probCutBeta, -probCutBeta + 1, t);
				
				if (score >= probCutBeta) {
					score = -negamax(-probCutBeta, -probCutBeta + 1, depth - 3, t);
				}
				
				t.ply--;
				board.takeBack();
				
				if (score >= probCutBeta) {
					return (short) score; 
				}
			}
		}
		
		short bestMoveInThisPosition = noHashEntry;
		
		// number of moves searched (for Late Move Reduction)
		int movesSearched = 0;
		
		// loop over all moves
		for (int count = 0; count < moveList.count; count++) {
			t.ply++;
						
			// only make a legal move (because we're using pseudolegal move generation)
			if (!board.makeMove(moveList.moves[count], allMoves)) {
				t.ply--;
				continue;
			}
			
			// increment legal moves
			legalMoves++;
			
			// get score
			int score = 0;
			
			// principal variation search
			/**
			 * If we find a possible good move, we use the Principal Variation Search to check if there are any moves that are better.
			 * We set alpha and beta to be right next to each other, making the PVS very very fast. PVS will return if we fail low
			 * or fail high. Since we believe that our move is good, we believe that each PVS will fail low. However, if it fails
			 * high, we have to conduct a re-search.
			 * 
			 * But, because of move ordering, we can assume that we will have a relatively low amount of re-searches, improving
			 * our performance.
			 */
			if (foundPV) {
				score = -negamax(-alpha - 1, -alpha, depth -1, t);
				
				if ((score > alpha) && (score < beta)) {
					score = -negamax(-beta, -alpha, depth -1, t);
				}
			} else {
				// no late move reduction, normal search
				if (movesSearched == 0) {
					score = -negamax(-beta, -alpha, depth -1, t);
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
						score = -negamax(-alpha - 1, -alpha, depth - 2, t);
					} else {
						score = alpha + 1;
					}
					
					if (score > alpha) {
						score = -negamax(-alpha - 1, -alpha, depth - 1, t);
						
						if ((score > alpha) && (score < beta)) {
							score = -negamax(-beta, -alpha, depth - 1, t);
						}
					}
				}
				
			}
			
			t.ply--;
						
			// return to previous board state
			board.takeBack();
						
			movesSearched++;
			
			// new best move
			if (score > alpha) {
				bestMoveInThisPosition = moveList.moves[count];
				
				// history moves heuristic
				if (!getMoveCapture(moveList.moves[count])) {
					t.historyMoves[board.getPieceAtSquare(getMoveSource(moveList.moves[count]))][getMoveTarget(moveList.moves[count])] += depth;
				}
				
				alpha = score;
				hashFlag = hashFlagExact;
				
				foundPV = true;
				
				// write PV move
				t.pvTable[t.ply][t.ply] = moveList.moves[count];
				
				// copy move from deeper ply into current ply's line
				for (int nextPly = t.ply + 1; nextPly < t.pvLength[t.ply + 1]; nextPly++) {
					t.pvTable[t.ply][nextPly] = t.pvTable[t.ply + 1][nextPly];
				}
				
				t.pvLength[t.ply] = t.pvLength[t.ply + 1];
				
				// beta cutoff
				if (score >= beta) {
					writeHashEntry((short) beta, depth, hashFlagBeta, moveList.moves[count], board.hashKey, t.ply);
					
					if (!getMoveCapture(moveList.moves[count])) {
						t.killerMoves[1][t.ply] = t.killerMoves[0][t.ply];
						t.killerMoves[0][t.ply] = moveList.moves[count];
					}
					
					return (short) beta;
				}
			}
		}
		
		// no legal moves
		if (legalMoves == 0) {
			// mate
			if (inCheck) {
				// mating score is larger the smaller the ply is
				return (short) -(mateScore - t.ply);
			} else {
				return 0;
			}
		}
		
		writeHashEntry((short) alpha, depth, hashFlag, bestMoveInThisPosition, board.hashKey, t.ply);
		
		return (short) alpha;
	}
	
	// only search captures, to limit horizon effect
	public static short quiescence(int alpha, int beta, ThreadInformation t) {
		// time limit reached
		if (!keepSearching || (((nodes & 4096) == 0) && System.currentTimeMillis() - startTime >= (timeLimit - timeLimitMargin))) {
			keepSearching = false;
			
			return 0;
		}
		
		BitBoardChessBoard board = t.board;
		
		t.nodes++;

		short evaluation = board.evaluate();
		
		// we have reached our max ply, so we just leave
		if (t.ply >= maxPly) {
			return evaluation;
		}
		
		// beta cutoff
		if (evaluation >= beta) {
			return (short) beta;
		}
		
		// delta pruning
		int bigDelta = 1075;
		
		// increase bigDelta if pawn might be able to promote
		if ((board.bitboards[P + board.side * 6] & rankMasks[(board.side == white) ? a7 : a2]) != 0) bigDelta += 875;
		
		if (evaluation < alpha - bigDelta) {
			return (short) alpha;
		}
		
		// found better move
		if (evaluation > alpha) {
			alpha = evaluation;
		}
	
		// generate moves
		moves moveList = new moves();
		
		board.generateMoves(moveList);
		sortMoves(moveList, alpha, beta, 0, t);
		
		// loop over moves
		for (int count = 0; count < moveList.count; count++) {
			t.ply++;
			
			// only make a legal move
			if (!board.makeMove(moveList.moves[count], nonQuietOnly)) {
				t.ply--;
				continue;
			}
			
			// get score
			int score = -quiescence(-beta, -alpha, t);

			t.ply--;
			
			// return to previous board state
			board.takeBack();
			
			// new best move
			if (score > alpha) {
				alpha = score;
				
				// beta cutoff
				if (score >= beta) {
					return (short) beta;
				}
			}
		}
		
		return (short) alpha;
	}
	
	public static void enablePVScoring(moves moveList, ThreadInformation t) {
		// disable by default, in case we do not find a pv node
		t.followPV = false;
		
		for (int count = 0; count < moveList.count; count++) {
			if (t.pvTable[0][t.ply] == moveList.moves[count]) {
				// enable pv stuff
				t.scorePV = true;
				t.followPV = true;
			}
		}
	}
	
	// score a move for move ordering
	public static int scoreMove(short move, ThreadInformation t) {
		BitBoardChessBoard board = t.board;
		
		// if it's the best move in the PV table, score it ultra super mega high
		if (t.scorePV) {
			if (t.pvTable[0][t.ply] == move) {
				t.scorePV = false;
				return 30000;
			}
		}
		
		// if it's the best recommended move in the transposition table, score it super high
		if (move == getStoredMove(board.hashKey)) {
			return 20000;
		}
		
		// if it's a capture, score it high
		if (getMoveCapture(move)) {
			int targetPiece = P;
			
			int startPiece, endPiece;
			
			if (board.side == white) { startPiece = p; endPiece = k; }
			else { startPiece = P; endPiece = K; }
			
			for (int bbPiece = startPiece; bbPiece <= endPiece; bbPiece++) {
				if (getBit(board.bitboards[bbPiece], getMoveTarget(move)) != 0) {
					targetPiece = bbPiece;
				}
			}
			
			// score it higher or less higher based on how valuable the victim is, and how valuable the attacker is
			return mvv_lva[board.getPieceAtSquare(getMoveSource(move))][targetPiece] + 10000;
		} else {
			// if it's a killer move, score it kinda high
			if (t.killerMoves[0][t.ply] == move) {
				return 9000;
			} else if (t.killerMoves[1][t.ply] == move) {
				return 8000;
			} else { // if not, default to history moves
				return t.historyMoves[board.getPieceAtSquare(getMoveSource(move))][getMoveTarget(move)];
			}
		}
	}
	
	// sort moves for move ordering
	public static void sortMoves(moves moveList, int alpha, int beta, int depth, ThreadInformation t) {
		int moveScores[] = new int[moveList.count];
		
		boolean useIID = true;
		
		for (int count = 0; count < moveList.count; count++) {
			moveScores[count] = scoreMove(moveList.moves[count], t);
			if (moveScores[count] >= 20000) useIID = false;
		}
		
		if (useIID && depth > 5 && beta - alpha > 1) {
			negamax(alpha, beta, depth - 2, t);
			short move = t.pvTable[t.ply][t.ply];
			for (int i = 0; i < moveList.count; i++) {
				if (moveList.moves[i] == move) {
					moveScores[i] = 20000;
				}
			}
		}
		
		// lazy O(n^2) sort
		for (int currentMove = 0; currentMove < moveList.count; currentMove++) {
			for (int nextMove = 0; nextMove < moveList.count; nextMove++) {
				if (moveScores[currentMove] > moveScores[nextMove]) {
					int tempScore = moveScores[currentMove];
					moveScores[currentMove] = moveScores[nextMove];
					moveScores[nextMove] = tempScore;
					
					short tempMove = moveList.moves[currentMove];
					moveList.moves[currentMove] = moveList.moves[nextMove];
					moveList.moves[nextMove] = tempMove;
				}
			}
		}
	}
}