package utils.bot;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.Scanner;
import utils.*;
import static utils.bot.DanielBotClasses.BitBoardBitManipulation.*;
import static utils.bot.DanielBotClasses.BitBoardConsts.*;
import static utils.bot.DanielBotClasses.BitBoard.*;


import java.util.Arrays;


public class DanielBot extends ChessBot {
	class Entry {
		public final long key;
		public final int value;
		public final Move move;
		public final byte depth;
		public final byte nodeType;
		
		public Entry(long Key, int Value, byte Depth, byte NodeType, Move Move) {
			key = Key;
			value = Value;
			move = Move;
			depth = Depth;
			nodeType = NodeType;
		}
	}
	static final int Exact = 0;
	static final int LowerBound = 1;
	static final int UpperBound = 2;
	static final int lookupFailed = -25783;
	
	static final boolean useIterativeDeepening = true;
	static final int depth = 4;
	static final boolean useFixedDepthSearch = false;
	static final int timeLimit = 5000;
	static final int mateScore = 50000;
	static final boolean infiniteBook = false;
	static final int bookLimit = 10;
	static final String bookFile = "final.pgn";
	
	Entry[] entries;
	
	int posCounter;
	int transpositionCounter;
	int depthSearched;
	
	long startTime;
	ChessBoard boardCopy;
	
	Move bestMoveThisIteration;
	int bestEvalThisIteration;
	Move bestMove;
	int bestEval;
	
	
	boolean finishedSearching;
	private boolean inBook;
	public DanielBot(ChessBoard board) {
		super(board);
		inBook = true;
		//entries = new Entry[64000];
		initAll();
	}

	@Override
	public Move getMove() {
//		if (inBook && bookMove() && (infiniteBook || super.getBoard().getPreviousMoves().size() < bookLimit)) {
//			return bestMove;
//		} else {
//			inBook = false;
//		}
		
		Move move = convertIntToMove(getBitBoardMove(super.getBoard()));
		return move;
//		startSearch();
//		System.out.print("Depth: ");
//		System.out.println(depthSearched);
//		System.out.print("Positions evaluated: ");
//		System.out.println(posCounter);
//		System.out.print("Transpositions: ");
//		System.out.println(transpositionCounter);
//		System.out.print("Evaluation: ");
//		System.out.println(bestEval);
//		return bestMove;
	}
	
	private void startSearch() {		
		finishedSearching = true;
		
		bestMove = bestMoveThisIteration = null;
		bestEval = bestEvalThisIteration = 0;
		depthSearched = 0;
		posCounter = 0;
		transpositionCounter = 0;
		startTime = System.currentTimeMillis();
		
		boardCopy = new ChessBoard(super.getBoard());
		
		if (useIterativeDeepening) {
			int targetDepth = (useFixedDepthSearch) ? depth : Integer.MAX_VALUE;
			for (int searchDepth = 1; searchDepth < targetDepth; searchDepth++) {
				searchMoves(searchDepth, 0, -100000, 100000);
				if (!finishedSearching) {
					break;
				} else {
					depthSearched = searchDepth;
					bestMove = bestMoveThisIteration;
					bestEval = bestEvalThisIteration;
					if (bestEval > 40000 || bestEval < -40000) {
						break;
					}
				}
			}
		} else {
			searchMoves(depth, 0, -100000, 100000);
			bestMove = bestMoveThisIteration;
			bestEval = bestEvalThisIteration; 
		}
	}
	
	private int searchMoves(int depth, int plyFromRoot, int alpha, int beta) {
		posCounter++;
		if (!finishedSearching || System.currentTimeMillis() - startTime >= timeLimit) {
			finishedSearching = false;
			return 0;
		}
		
		if (plyFromRoot > 0) {
			// detect draw by repetition
			int occurrences = 0;
			for (long x : boardCopy.getPreviousZobrists()) {
				if (boardCopy.getZobristKey() == x) {
					occurrences++;
				}
			}
			
			for (long x : super.getBoard().getPreviousZobrists()) {
				if (boardCopy.getZobristKey() == x) {
					occurrences++;
				}
			}
			
			if (occurrences >= 2) return 0;
			if (boardCopy.getMoveRule() >= 50) return 0;
			
			alpha = Math.max(alpha, -mateScore + plyFromRoot);
			beta = Math.min(beta, mateScore - plyFromRoot);
			if (alpha >= beta) {
				return alpha;
			}
		}
		
		int ttVal = lookupEvaluationTT(depth, plyFromRoot, alpha, beta);
		if (ttVal != lookupFailed) {
			transpositionCounter++;
			int occurrences = 0;
			for (long x : boardCopy.getPreviousZobrists()) {
				if (boardCopy.getZobristKey() == x) {
					occurrences++;
				}
			}
			
			for (long x : super.getBoard().getPreviousZobrists()) {
				if (boardCopy.getZobristKey() == x) {
					occurrences++;
				}
			}
			
			if (occurrences >= 2) return 0;
			if (boardCopy.getMoveRule() >= 50) return 0;
			if (plyFromRoot == 0) {
				bestMoveThisIteration = entries[indexTT()].move;
				bestEvalThisIteration = entries[indexTT()].value;
			}
			
			return ttVal;
		}
		
		if (depth == 0) return quietSearch(alpha, beta);
		ArrayList<Move> legalMoves = orderMoves(boardCopy.getAllLegalMoves(), true);
		
		// no legal moves
		if (legalMoves.isEmpty()) {
			if (boardCopy.checked(boardCopy.getSide())) { // no legal moves and checked? you're mated
				return -1 * (mateScore - plyFromRoot);
			}
			
			// no legal moves and not checked? stalemate
			return 0;
		}
		
		int evalType = UpperBound;
		Move bestMoveInThisPosition = null;
		
		for (Move m : legalMoves) {
			boardCopy.submitMove(m);
			int eval = searchMoves(depth - 1, plyFromRoot + 1, beta * -1, alpha * -1) * -1;
			boardCopy.undoMove();
			
			if (eval >= beta) {
				storeEvaluationTT(depth, plyFromRoot, beta, LowerBound, m);
				return beta;
			}

			if (eval > alpha) {	
				evalType = Exact;
				bestMoveInThisPosition = m;
				
				alpha = eval;
				if (plyFromRoot == 0) {
					bestMoveThisIteration = m;
					bestEvalThisIteration = eval;
				}
			}
		}
		
		storeEvaluationTT(depth, plyFromRoot, alpha, evalType, bestMoveInThisPosition);
		
		return alpha;
	}
	
	private int quietSearch(int alpha, int beta) {
		int eval = DanielEval.evaluate(boardCopy.getBoard()) * (boardCopy.getSide() ? 1 : -1);
		if (eval >= beta) {
			return beta;
		}
		
		if (eval > alpha) {
			alpha = eval;
		}
		
		ArrayList<Move> legalMoves = orderMoves(pruneNonCaptures(boardCopy.getAllLegalMoves()), false);
		for (Move m : legalMoves) {
			boardCopy.submitMove(m);
			eval = quietSearch(beta * -1, alpha * -1) * -1;
			boardCopy.undoMove();
			
			if (eval >= beta) {
				return beta;
			}
			
			if (eval > alpha) {
				alpha = eval;
			}
		}
		
		return alpha;
	}
	
	private ArrayList<Move> pruneNonCaptures(ArrayList<Move> moves) {
		ArrayList<Move> temp = new ArrayList<Move>();
		for (Move x : moves) {
			if (x.getCapture() != null) {
				temp.add(x);
			} else if (boardCopy.getBoard()[x.getEnd().first][x.getEnd().second] != null) {
				temp.add(x);
			}
		}
		
		return temp;
	}
	
	private ArrayList<Move> orderMoves(ArrayList<Move> moves, boolean useTT) {
		ArrayList<int[]> indexPair = new ArrayList<int[]>();
		for (int i = 0; i < moves.size(); i++) {
			Move x = moves.get(i);
			int score = 0;
			Piece movePiece = x.getPiece();
			Piece capturePiece = boardCopy.getBoard((x.getCapture() != null) ? x.getCapture() : x.getEnd());
			Move hashMove = null;
			
			
			if (useTT) {
				Entry entry = entries[indexTT()];
				if (entry != null) {
					hashMove = entries[indexTT()].move;
				}
			}
			
			if (capturePiece != null) {
				score = 10 * getPieceValue(capturePiece) - getPieceValue(movePiece);
			}
			
			if (movePiece instanceof Pawn) {
				if (x instanceof PromotionMove) {
					score += 900;
				}
			} else {
				try {
				if (boardCopy.getBoard(new Pair(x.getEnd().first + 1, x.getEnd().second + ((boardCopy.getSide()) ? 1 : -1))) instanceof Pawn) {
					score -= 350;
				}
				} catch (Exception e) {
					
				}
				try {
				if (boardCopy.getBoard(new Pair(x.getEnd().first - 1, x.getEnd().second + ((boardCopy.getSide()) ? -1 : 1))) instanceof Pawn) {
					score -= 350;
				}
				} catch (Exception e) {
					
				}
			}
			
			if (hashMove != null && hashMove.getStart().equals(x.getStart()) && hashMove.getEnd().equals(x.getEnd())) {
				score += 10000;
			}
			
			int[] toAdd = {i, score};
			indexPair.add(toAdd);
		}
		
		Collections.sort(indexPair, new scoreSort());
		
		ArrayList<Move> toReturn = new ArrayList<Move>();
		for (int[] x : indexPair) {
			toReturn.add(moves.get(x[0]));
		}
		
		return toReturn;
	}
	
	private boolean bookMove() {
		ArrayList<Move> moves = super.getBoard().getPreviousMoves();
		ChessBoard b = new ChessBoard();
		String PGNString = "";

		for (Move x : moves) {
	        boolean captured = b.getBoard(x.getEnd()) != null;
			if (x.getEnd2() != null) {
				if (x.getEnd().second == 2) {
            		PGNString += "O-O-O";
            	} else {
            		PGNString += "O-O";
            	}
			} else if (x instanceof PromotionMove) {
				if (captured) {
                	PGNString += x.getStart().getCol() + "x";
                }
                PGNString += x.getEnd().toChessNote() + "=Q";
			} else if (x.getPiece() instanceof Pawn) {
				if (captured || x.getCapture() != null) {
                    PGNString += x.getStart().getCol() + "x";
                }
                PGNString += x.getEnd().toChessNote();
			} else {
				if (x.getPiece() instanceof Knight) PGNString += "N";
				if (x.getPiece() instanceof Bishop) PGNString += "B";
				if (x.getPiece() instanceof Rook) PGNString += "R";
				if (x.getPiece() instanceof Queen) PGNString += "Q";
				if (x.getPiece() instanceof King) PGNString += "K";
				
				char rankReference = ' ';
				char fileReference = ' ';
				
				for (int i = 0; i < 8; i++) {
					for (int j = 0; j < 8; j++) {
						if (i != x.getStart().first || j != x.getStart().second) {
							Piece toCheck = b.getBoard()[i][j];
							if (toCheck == null) continue;
							if (toCheck.getClass().equals(x.getPiece().getClass()) && toCheck.getColor() == x.getPiece().getColor()) {
								ArrayList<int[]> moveset = toCheck.getMoveSet(b.getBoard(), i, j);
								for (int[] finalSquare : moveset) {
									if ((new Pair(finalSquare[0], finalSquare[1])).equals(x.getEnd())) {
										if (i == x.getStart().first) {
											fileReference = x.getStart().getCol();
										} else if (j == x.getStart().second) {
											rankReference = x.getStart().getRow();
										} else {
											fileReference = x.getStart().getCol();
										}
									}
								}
							}
						}
					}
				}
				
				if (fileReference != ' ') PGNString += Character.toString(fileReference);
				if (rankReference != ' ') PGNString += Character.toString(rankReference);

                if (captured) PGNString += "x";
                
                PGNString += x.getEnd().toChessNote();
			}
			
			b.submitMove(x);
			PGNString += " ";
		}
		if (PGNString.length() > 0) {
			PGNString = PGNString.substring(0, PGNString.length() - 1);
		}
		
		File file = new File("src//utils//bot//DanielBotResources//" + bookFile);
		ArrayList<String> possibleContinuations = new ArrayList<String>();
		
		try {
			Scanner scanner = new Scanner(file);
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				
				if (line.length() <= PGNString.length()) continue;
				
				if (line.substring(0, PGNString.length()).equals(PGNString)) {
					if (PGNString.length() > 0) { 
						line = line.substring(PGNString.length() + 1);
					}
					possibleContinuations.add(line.split(" ", 2)[0]);
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (possibleContinuations.isEmpty()) return false;
		
		int rnd = new Random().nextInt(possibleContinuations.size());
		String result = possibleContinuations.get(rnd);

		Move move = null;
		
		if (Character.isLowerCase(result.charAt(0))) { // pawn move
			if (result.length() == 2) { // normal pawn move
				int moveFile = result.charAt(0) - 'a';
				int moveRank = result.charAt(1) - '1';
				if (moveRank == 3 && b.getSide() == true) { // checking if it was a double move or not
					if (b.getBoard()[2][moveFile] instanceof Pawn) {
						move = new Move(b.getBoard()[2][moveFile], 2, moveFile, 3, moveFile);
					} else {
						move = new Move(b.getBoard()[1][moveFile], 1, moveFile, 3, moveFile);
					}
				} else if (moveRank == 4 && b.getSide() == false) {
					if (b.getBoard()[5][moveFile] instanceof Pawn) {
						move = new Move(b.getBoard()[5][moveFile], 5, moveFile, 4, moveFile);
					} else {
						move = new Move(b.getBoard()[6][moveFile], 6, moveFile, 4, moveFile);
					}
				} else { // not a double move
					move = new Move(b.getBoard()[moveRank - (b.getSide() ? 1 : -1)][moveFile], moveRank - (b.getSide() ? 1 : -1), moveFile, moveRank, moveFile);
				}
			} else if (result.charAt(1) == 'x') { // pawn captured something else
				int startFile = result.charAt(0) - 'a';
				int endFile = result.charAt(2) - 'a';
				int endRank = result.charAt(3) - '1';
				int startRank = endRank - (b.getSide() ? 1 : -1);
				if (result.length() == 6) { // pawn capture promotion (exc6=Q is 6 characters)
					char piecePromote = result.charAt(5);
					Piece toPromoteTo = null;
					switch (piecePromote) {
					case 'N':
						toPromoteTo = new Knight(b.getSide());
						break;
					case 'B':
						toPromoteTo = new Bishop(b.getSide());
						break;
					case 'R':
						toPromoteTo = new Rook(b.getSide());
						break;
					case 'Q':
						toPromoteTo = new Queen(b.getSide());
						break;
					}
					
					move = new PromotionMove(b.getBoard()[startRank][startFile], startRank, startFile, endRank, endFile, toPromoteTo);
				} else if (b.getBoard()[endRank][endFile] == null) { // nothing where we captured? must be en passant
					move = new Move(b.getBoard()[startRank][startFile], startRank, startFile, endRank, endFile, endRank - (b.getSide() ? 1 : -1), endFile);
				} else { // normal pawn capture
					move = new Move(b.getBoard()[startRank][startFile], startRank, startFile, endRank, endFile);
				}
			} else if (result.charAt(2) == '=') { // promotion
				int moveFile = result.charAt(0) - 'a';
				int endRank = result.charAt(1) - '1';
				int startRank = endRank - (b.getSide() ? 1 : -1);
				char piecePromote = result.charAt(3);
				Piece toPromoteTo = null;
				
				switch (piecePromote) {
				case 'N':
					toPromoteTo = new Knight(b.getSide());
					break;
				case 'B':
					toPromoteTo = new Bishop(b.getSide());
					break;
				case 'R':
					toPromoteTo = new Rook(b.getSide());
					break;
				case 'Q':
					toPromoteTo = new Queen(b.getSide());
					break;
				}
				
				move = new PromotionMove(b.getBoard()[startRank][moveFile], startRank, moveFile, endRank, moveFile, toPromoteTo);
			}
		} else if (result.charAt(0) == 'O') { // castling
			if (result.equals("O-O")) {
				if (b.getSide()) {
					move = new Move(b.getBoard()[0][4], 0, 4, 0, 6, b.getBoard()[0][7], 0, 7, 0, 5);
				} else {
					move = new Move(b.getBoard()[7][4], 7, 4, 7, 6, b.getBoard()[7][7], 7, 7, 7, 5);
				}
			} else {
				if (b.getSide()) {
					move = new Move(b.getBoard()[0][4], 0, 4, 0, 2, b.getBoard()[0][0], 0, 0, 0, 3);
				} else {
					move = new Move(b.getBoard()[7][4], 7, 4, 7, 2, b.getBoard()[7][0], 7, 0, 7, 3);
				}
			}
		} else { // piece move
			result = result.replace("x", "");
			Piece referencePiece = null;
			
			switch (result.charAt(0)) {
			case 'N':
				referencePiece = new Knight(b.getSide());
				break;
			case 'B':
				referencePiece = new Bishop(b.getSide());
				break;
			case 'R':
				referencePiece = new Rook(b.getSide());
				break;
			case 'Q':
				referencePiece = new Queen(b.getSide());
				break;
			case 'K':
				referencePiece = new King(b.getSide());
				break;
			}
			
			int referenceFile = -1;
			int referenceRank = -1;
			int startFile = -1;
			int startRank = -1;
			int endFile = result.charAt(result.length() - 2) - 'a';
			int endRank = result.charAt(result.length() - 1) - '1';
			
			if (result.length() == 4) {
				if (Character.isDigit(result.charAt(1))) {
					referenceRank = result.charAt(1) - '1';
				} else {
					referenceFile = result.charAt(1) - 'a';
				}
			} else if (result.length() == 5) {
				referenceFile = result.charAt(1) - 'a';
				referenceRank = result.charAt(2) - '1';
			}
			
			if (referenceFile != -1 && referenceRank != -1) {
				startFile = referenceFile;
				startRank = referenceRank;
			} else if (referenceFile != -1) {
				for (int i = 0; i < 8; i++) {
					Piece possiblePiece = b.getBoard()[i][referenceFile];
					if (possiblePiece != null && possiblePiece.getClass().equals(referencePiece.getClass()) && possiblePiece.getColor() == b.getSide()) {
						ArrayList<Move> moveSet = b.getLegalMoves(i, referenceFile, false);
						for (Move pos : moveSet) {
							if (pos.getEnd().equals(new Pair(endRank, endFile))) {
								startFile = referenceFile;
								startRank = i;
							}
						}
					}
				}
			} else if (referenceRank != -1) {
				for (int i = 0; i < 8; i++) {
					Piece possiblePiece = b.getBoard()[referenceRank][i];
					if (possiblePiece != null && possiblePiece.getClass().equals(referencePiece.getClass()) && possiblePiece.getColor() == b.getSide()) {
						ArrayList<Move> moveSet = b.getLegalMoves(referenceRank, i, false);
						for (Move pos : moveSet) {
							if (pos.getEnd().equals(new Pair(endRank, endFile))) {
								startFile = i;
								startRank = referenceRank;
							}
						}
					}
				}
			} else {
				for (int i = 0; i < 8; i++) {
					for (int j = 0; j < 8; j++) {
						Piece possiblePiece = b.getBoard()[i][j];
						if (possiblePiece != null && possiblePiece.getClass().equals(referencePiece.getClass()) && possiblePiece.getColor() == b.getSide()) {
							ArrayList<Move> moveSet = b.getLegalMoves(i, j, false);
							for (Move pos : moveSet) {
								if (pos.getEnd().equals(new Pair(endRank, endFile))) {
									startFile = j;
									startRank = i;
								}
							}
						}
					}
				}
			}
			move = new Move(b.getBoard()[startRank][startFile], startRank, startFile, endRank, endFile);
		}
		
		bestMove = move;
		return true;
	}
	
	private void clearTT() {
		for (int i = 0; i < entries.length; i++) {
			entries[i] = null;
		}
	}
	
	private int indexTT() {
		return Math.abs((int) (boardCopy.getZobristKey() % 64000));
	}
	
	public int lookupEvaluationTT(int depth, int plyFromRoot, int alpha, int beta) {
		Entry entry = entries[indexTT()];
		if (entry != null && entry.key == boardCopy.getZobristKey()) {
			if (entry.depth >= depth) {
				int score = correctMateScoreForRetrieval(entry.value, plyFromRoot);
				if (entry.nodeType == Exact) {
					return score;
				}
				
				if (entry.nodeType == UpperBound && score <= alpha) {
					return score;
				}
				
				if (entry.nodeType == LowerBound && score >= beta) {
					return score;
				}
			}
		}
		
		return lookupFailed;
	}
	
	public void storeEvaluationTT(int depth, int numPlySearched, int eval, int evalType, Move move) {
		entries[indexTT()] = new Entry(boardCopy.getZobristKey(), correctMateScoreForStorage(eval, numPlySearched), (byte) depth, (byte) evalType, move);
	}
	
	public int correctMateScoreForStorage(int score, int numPlySearched) {
		if (score > 30000 || score < -30000) {
			return (score * (int) Math.signum(score) + numPlySearched) * (int) Math.signum(score);
		}
		
		return score;
	}
	
	public int correctMateScoreForRetrieval(int score, int numPlySearched) {
		if (score > 30000 || score < -30000) {
			return (score * (int) Math.signum(score) - numPlySearched) * (int) Math.signum(score);
		}
		
		return score;
	}
	
	private String generateFen(ChessBoard board) {
		String fenString = "";
		Piece[][] pieceArray = board.getBoard();
		for (int r = 7; r >= 0; r--) {
			for (int c = 0; c < 8; c++) {
				Piece piece = pieceArray[r][c];
				if (piece == null) {
					if (fenString.length() != 0 && Character.isDigit(fenString.charAt(fenString.length() - 1))) {
						int numberEmpty = fenString.charAt(fenString.length() - 1) - '0' + 1;
						fenString = fenString.substring(0, fenString.length() - 1);
						fenString += numberEmpty;
					} else {
						fenString += "1";
					}
				} else {
					if (piece instanceof King) {
						if (piece.getColor()) { fenString += "K"; }
						else { fenString += 'k'; }
					} else if (piece instanceof Queen) {
						if (piece.getColor()) { fenString += "Q"; }
						else { fenString += 'q'; }
					} else if (piece instanceof Bishop) {
						if (piece.getColor()) { fenString += "B"; }
						else { fenString += 'b'; }
					} else if (piece instanceof Knight) {
						if (piece.getColor()) { fenString += "N"; }
						else { fenString += 'n'; }
					} else if (piece instanceof Rook) {
						if (piece.getColor()) { fenString += "R"; }
						else { fenString += 'r'; }
					} else if (piece instanceof Pawn) {
						if (piece.getColor()) { fenString += "P"; }
						else { fenString += 'p'; }
					}
				}
			}
			fenString += "/";
		}
		
		fenString = fenString.substring(0, fenString.length() - 1);
		
		fenString += " ";
		
		fenString += board.getSide() ? "w" : "b";
		
		fenString += " ";
		
		boolean[] castling = board.castlingRights();
		
		if (castling[2]) { fenString += "K"; }		
		if (castling[3]) { fenString += "Q"; }		
		if (castling[0]) { fenString += "k"; }		
		if (castling[1]) { fenString += "q"; }
		
		if (fenString.charAt(fenString.length() - 1) == ' ') { fenString += "-"; }
		
		fenString += " ";
		
		if (board.getEnPassant().second != -1) {
			Pair enPassant = board.getEnPassant();
			Pair newPair = new Pair(enPassant.first, enPassant.second);
			
			if (enPassant.first == 4) newPair.first = 3;
			if (enPassant.first == 5) newPair.first = 6;
			
			fenString += newPair.toChessNote();
		} else {
			fenString += "-";
		}
		
		fenString += " ";
		
		fenString += board.getMoveRule();
		
		fenString += " ";
		
		fenString += board.getPreviousMoves().size();
		
		return fenString;
	}
	
	private Move convertIntToMove(int move) {
		int sourceSquare = getMoveSource(move);
        int targetSquare = getMoveTarget(move);
        int promoted = getMovePromoted(move);
        int enPassant = getMoveEnPassant(move);
        int castling = getMoveCastling(move);
        
        if (castling != 0) {
    	if (targetSquare == 62) return new Move(super.getBoard().getBoard()[0][4], 0, 4, 0, 6, super.getBoard().getBoard()[0][7], 0, 7, 0, 5);
    	if (targetSquare == 58) return new Move(super.getBoard().getBoard()[0][4], 0, 4, 0, 2, super.getBoard().getBoard()[0][0], 0, 0, 0, 3);
    	if (targetSquare == 6) return new Move(super.getBoard().getBoard()[7][4], 7, 4, 7, 6, super.getBoard().getBoard()[7][7], 7, 7, 7, 5);
    	if (targetSquare == 2) return new Move(super.getBoard().getBoard()[7][4], 7, 4, 7, 2, super.getBoard().getBoard()[7][0], 7, 0, 7, 3);
    	}
        
        int sourceRank = 7 - sourceSquare / 8;
        int sourceFile = sourceSquare % 8;
        Pair start = new Pair(sourceRank, sourceFile);
        
        int targetRank = 7 - targetSquare / 8;
        int targetFile = targetSquare % 8;
        Pair end = new Pair(targetRank, targetFile);
        
        Piece piece = super.getBoard().getBoard(start);
        
    	if (enPassant != 0) {
    		Pair captureSquare = new Pair((sourceRank + targetRank) / 2, targetFile);
    		return new Move(piece, start, end, captureSquare);
    	}
        
    	if (promoted != 0) {
    		Piece promoteTo = null;
    		switch (promoted) {
    		case N: promoteTo = new Knight(true); break;
    		case B: promoteTo = new Bishop(true); break;
    		case R: promoteTo = new Rook(true); break;
    		case Q: promoteTo = new Queen(true); break;
    		case n: promoteTo = new Knight(false); break;
    		case b: promoteTo = new Bishop(false); break;
    		case r: promoteTo = new Rook(false); break;
    		case q: promoteTo = new Queen(false); break;
    		}
    		return new PromotionMove(piece, start.first, start.second, end.first, end.second, promoteTo);
    	}
    	
    	return new Move(piece, start, end);
	}
	
	private int getPieceValue(Piece piece) {
		if (piece instanceof King) {
			return 1000;
		} else if (piece instanceof Queen) {
			return 900;
		} else if (piece instanceof Bishop) {
			return 330;
		} else if (piece instanceof Knight) {
			return 320;
		} else if (piece instanceof Rook) {
			return 500;
		} else if (piece instanceof Pawn) {
			return 100;
		}
		
		return 0;
	}
	
	class scoreSort implements Comparator<int[]> {
		@Override
		public int compare(int[] o1, int[] o2) {
			// TODO Auto-generated method stub
			return o2[1] - o1[1];
		}
	}
	
	public String getName() {
		// TODO Auto-generated method stub
		return "DanielBot";
	}
	
//	private int miniMax(int depth, boolean maximizingPlayer, boolean setBestMove) { // first minimax
//	if (depth == 0) return boardCopy.evaluate();
//	ArrayList<Move> legalMoves = boardCopy.getAllLegalMoves();
//	if (maximizingPlayer) {
//		int max = -1000000;
//		for (Move x : legalMoves) {
//			boardCopy.submitMove(x);
//			int eval = miniMax(depth - 1, false, false) * evalMult;
//			if (max <= eval) {
//				max = eval;
//				if (setBestMove) { bestMove = x; }
//			}
//			
//			boardCopy.undoMove();
//		}
//		
//		return max * evalMult;
//	} else {
//		int min = 1000000;
//		for (Move x : legalMoves) {
//			boardCopy.submitMove(x);
//			int eval = miniMax(depth - 1, true, false) * evalMult;
//			if (min >= eval) {
//				min = eval;
//			}
//			
//			boardCopy.undoMove();
//		}
//		
//		return min * evalMult;
//	}
//}
//
//	private int miniMax(int depth, int plyFromRoot, int alpha, int beta, boolean maximizingPlayer, boolean setBestMove) { // second minimax
//	if (System.currentTimeMillis() - startTime >= 5000) {
//		finishedSearching = false;
//		return 0;
//	}
//	posCounter++;
//	int occurrences = 0;
//	for (long x : boardCopy.getPreviousZobrists()) {
//		if (boardCopy.getZobristKey() == x) {
//			occurrences++;
//		}
//	}
//	
//	for (long x : super.getBoard().getPreviousZobrists()) {
//		if (boardCopy.getZobristKey() == x) {
//			occurrences++;
//		}
//	}
//	if (occurrences >= 2) return 0;
//	if (boardCopy.getMoveRule() >= 50) return 0;
//	int ttVal = lookupEvaluationTT(depth, plyFromRoot, alpha, beta);
//	if (ttVal != lookupFailed) {
//		if (plyFromRoot != 0) {
//			transpositionCounter++;
//			return ttVal;
//		}
//	}
//	if (depth == 0) return quietSearch(alpha, beta);
//	ArrayList<Move> legalMoves = orderMoves(boardCopy.getAllLegalMoves());
//	if (legalMoves.isEmpty()) {
//		if (boardCopy.checked(boardCopy.getSide())) {
//			return ((boardCopy.getSide()) ? -1 : 1) * (50000 - plyFromRoot); 
//		}
//		return 0;
//	}
//	
//	Move localBestMove = null;
//	
//	if (maximizingPlayer) {
//		int max = -1000000;
//		for (Move x : legalMoves) {
//			boardCopy.submitMove(x);
//			int eval = miniMax(depth - 1, plyFromRoot + 1, alpha, beta, false, false) * evalMult;
//			if (finishedSearching == false) return 0;
//			boardCopy.undoMove();
//			if (max < eval) {
//				max = eval;
//				localBestMove = x;
//				if (setBestMove) { bestMove = x; }
//			}
//			
//			if (max > beta) {
//				storeEvaluationTT(depth, plyFromRoot, max * evalMult, LowerBound, x);
//				return beta * evalMult;
//			}
//			alpha = Math.max(max, alpha);
//		}
//		
//		storeEvaluationTT(depth, plyFromRoot, Math.max(max, alpha) * evalMult, (max == alpha) ? Exact : UpperBound, localBestMove);
//		
//		return max * evalMult;
//	} else {
//		int min = 1000000;
//		for (Move x : legalMoves) {
//			boardCopy.submitMove(x);
//			int eval = miniMax(depth - 1, plyFromRoot + 1, alpha, beta, true, false) * evalMult;
//			if (finishedSearching == false) return 0;
//			boardCopy.undoMove();
//			if (min > eval) {
//				min = eval;
//				localBestMove = x;
//			}
//			
//			if (min < alpha) {
//				storeEvaluationTT(depth, plyFromRoot, min * evalMult, UpperBound, x);
//				return alpha * evalMult;
//			}
//			beta = Math.min(min, beta);
//		}
//		
//		storeEvaluationTT(depth, plyFromRoot, Math.min(min, beta) * evalMult, (min == beta) ? Exact : LowerBound, localBestMove);
//		
//		return min * evalMult;
//	}
//}
//	startTime = System.currentTimeMillis();
//	
//	boardCopy = new ChessBoard(super.getBoard());
//	posCounter = 0;
//	transpositionCounter = 0;
//	int depth = 2;
//	Move toReturn = null;
//	int finalEval = 0;
//	while (System.currentTimeMillis() - startTime < 5000) {
//		finishedSearching = true;
//		clearTT();
//		int eval = miniMax(depth, 0, -100000, 100000, true, true);
//		if (finishedSearching) {
//			toReturn = bestMove;
//			finalEval = eval;
//		}
//		depth += 2;
//	}
//	System.out.print("Depth (ply): ");
//	System.out.println(depth - 2);
//	System.out.print("Positions reached: ");
//	System.out.println(posCounter);
//	System.out.print("Transpositions: ");
//	System.out.println(transpositionCounter);
//	System.out.print("Evaluation: ");
//	System.out.println(finalEval);
//	return toReturn;

}
