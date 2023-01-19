package utils.bot.DanielBotClasses;

import static utils.bot.DanielBotClasses.BitBoardMoveGeneration.generateMoves;
import static utils.bot.DanielBotClasses.BitBoardMoveGeneration.makeMove;

import java.util.ArrayList;

import utils.Bishop;
import utils.ChessBoard;
import utils.King;
import utils.Knight;
import utils.Move;
import utils.Pair;
import utils.Pawn;
import utils.Queen;
import utils.Rook;

public class BitBoardConsts {
	public static final int a8 = 0;
	public static final int b8 = 1;
	public static final int c8 = 2;
	public static final int d8 = 3;
	public static final int e8 = 4;
	public static final int f8 = 5;
	public static final int g8 = 6;
	public static final int h8 = 7;
	public static final int a7 = 8;
	public static final int b7 = 9;
	public static final int c7 = 10;
	public static final int d7 = 11;
	public static final int e7 = 12;
	public static final int f7 = 13;
	public static final int g7 = 14;
	public static final int h7 = 15;
	public static final int a6 = 16;
	public static final int b6 = 17;
	public static final int c6 = 18;
	public static final int d6 = 19;
	public static final int e6 = 20;
	public static final int f6 = 21;
	public static final int g6 = 22;
	public static final int h6 = 23;
	public static final int a5 = 24;
	public static final int b5 = 25;
	public static final int c5 = 26;
	public static final int d5 = 27;
	public static final int e5 = 28;
	public static final int f5 = 29;
	public static final int g5 = 30;
	public static final int h5 = 31;
	public static final int a4 = 32;
	public static final int b4 = 33;
	public static final int c4 = 34;
	public static final int d4 = 35;
	public static final int e4 = 36;
	public static final int f4 = 37;
	public static final int g4 = 38;
	public static final int h4 = 39;
	public static final int a3 = 40;
	public static final int b3 = 41;
	public static final int c3 = 42;
	public static final int d3 = 43;
	public static final int e3 = 44;
	public static final int f3 = 45;
	public static final int g3 = 46;
	public static final int h3 = 47;
	public static final int a2 = 48;
	public static final int b2 = 49;
	public static final int c2 = 50;
	public static final int d2 = 51;
	public static final int e2 = 52;
	public static final int f2 = 53;
	public static final int g2 = 54;
	public static final int h2 = 55;
	public static final int a1 = 56;
	public static final int b1 = 57;
	public static final int c1 = 58;
	public static final int d1 = 59;
	public static final int e1 = 60;
	public static final int f1 = 61;
	public static final int g1 = 62;
	public static final int h1 = 63;
	public static final int no_sq = 64;
	
	public static final int P = 0;
	public static final int N = 1;
	public static final int B = 2;
	public static final int R = 3;
	public static final int Q = 4;
	public static final int K = 5;
	public static final int p = 6;
	public static final int n = 7;
	public static final int b = 8;
	public static final int r = 9;
	public static final int q = 10;
	public static final int k = 11;
	
	public static final int wk = 1;
	public static final int wq = 2;
	public static final int bk = 4;
	public static final int bq = 8;
	
	public static final int white = 0;
	public static final int black = 1;
	public static final int both = 2;
	
	public static final int rook = 0;
	public static final int bishop = 1;
	
	public static final long not_a_file = -72340172838076674L;
	public static final long not_ab_file = -217020518514230020L;
	public static final long not_h_file = 9187201950435737471L;
	public static final long not_hg_file = 4557430888798830399L;
	
	public static final String squareToCoordinates[] = {
			"a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8",
			"a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7",
			"a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6",
			"a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5",
			"a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4",
			"a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3",
			"a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2",
			"a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1"
			};
	
	public static final char asciiPieces[] = {'P', 'N', 'B', 'R', 'Q', 'K', 'p', 'n', 'b', 'r', 'q', 'k'};
	public static final int charPieces[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 1, 0, 0, 4, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8, 0, 0, 0, 0, 0, 0, 0, 0, 11, 0, 0, 7, 0, 6, 10, 9 };
	public static final char unicodePieces[] = {'P', 'N', 'B', 'R', 'Q', 'K', 'p', 'n', 'b', 'r', 'q', 'k'};
	
	public static final String emptyBoard = "8/8/8/8/8/8/8/8 w - - ";
	public static final String startPosition = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1 ";
	public static final String trickyPosition = "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1 ";
	public static final String killerPosition = "rnbqkb1r/pp1p1pPp/8/2p1pP2/1P1P4/3P3P/P1P1P3/RNBQKBNR w KQkq e6 0 1";
	public static final String cmkPosition = "r2q1rk1/ppp2ppp/2n1bn2/2b1p3/3pP3/3P1NPP/PPP1NPB1/R1BQ1RK1 b - - 0 9 ";
	
	public static final int allMoves = 0;
	public static final int capturesOnly = 1;
	
	public static final int castlingRights[] = {
		     7, 15, 15, 15,  3, 15, 15, 11,
		    15, 15, 15, 15, 15, 15, 15, 15,
		    15, 15, 15, 15, 15, 15, 15, 15,
		    15, 15, 15, 15, 15, 15, 15, 15,
		    15, 15, 15, 15, 15, 15, 15, 15,
		    15, 15, 15, 15, 15, 15, 15, 15,
		    15, 15, 15, 15, 15, 15, 15, 15,
		    13, 15, 15, 15, 12, 15, 15, 14
		};
	
	public static final int materialScore[] = {100, 320, 330, 500, 900, 10000, -100, -320, -330, -500, -900, -10000};
	
	// bishop relevant occupancy bit count for every square on board
	public static final int bishopRelevantBits[] = {
	    6, 5, 5, 5, 5, 5, 5, 6, 
	    5, 5, 5, 5, 5, 5, 5, 5, 
	    5, 5, 7, 7, 7, 7, 5, 5, 
	    5, 5, 7, 9, 9, 7, 5, 5, 
	    5, 5, 7, 9, 9, 7, 5, 5, 
	    5, 5, 7, 7, 7, 7, 5, 5, 
	    5, 5, 5, 5, 5, 5, 5, 5, 
	    6, 5, 5, 5, 5, 5, 5, 6
	};

	// rook relevant occupancy bit count for every square on board
	public static final int rookRelevantBits[] = {
	    12, 11, 11, 11, 11, 11, 11, 12, 
	    11, 10, 10, 10, 10, 10, 10, 11, 
	    11, 10, 10, 10, 10, 10, 10, 11, 
	    11, 10, 10, 10, 10, 10, 10, 11, 
	    11, 10, 10, 10, 10, 10, 10, 11, 
	    11, 10, 10, 10, 10, 10, 10, 11, 
	    11, 10, 10, 10, 10, 10, 10, 11, 
	    12, 11, 11, 11, 11, 11, 11, 12
	};
	
	public static final long rookMagicNumbers[] = {
		    0x8A80104000800020L,
		    0x140002000100040L,
		    0x2801880A0017001L,
		    0x100081001000420L,
		    0x200020010080420L,
		    0x3001C0002010008L,
		    0x8480008002000100L,
		    0x2080088004402900L,
		    0x800098204000L,
		    0x2024401000200040L,
		    0x100802000801000L,
		    0x120800800801000L,
		    0x208808088000400L,
		    0x2802200800400L,
		    0x2200800100020080L,
		    0x801000060821100L,
		    0x80044006422000L,
		    0x100808020004000L,
		    0x12108A0010204200L,
		    0x140848010000802L,
		    0x481828014002800L,
		    0x8094004002004100L,
		    0x4010040010010802L,
		    0x20008806104L,
		    0x100400080208000L,
		    0x2040002120081000L,
		    0x21200680100081L,
		    0x20100080080080L,
		    0x2000A00200410L,
		    0x20080800400L,
		    0x80088400100102L,
		    0x80004600042881L,
		    0x4040008040800020L,
		    0x440003000200801L,
		    0x4200011004500L,
		    0x188020010100100L,
		    0x14800401802800L,
		    0x2080040080800200L,
		    0x124080204001001L,
		    0x200046502000484L,
		    0x480400080088020L,
		    0x1000422010034000L,
		    0x30200100110040L,
		    0x100021010009L,
		    0x2002080100110004L,
		    0x202008004008002L,
		    0x20020004010100L,
		    0x2048440040820001L,
		    0x101002200408200L,
		    0x40802000401080L,
		    0x4008142004410100L,
		    0x2060820C0120200L,
		    0x1001004080100L,
		    0x20C020080040080L,
		    0x2935610830022400L,
		    0x44440041009200L,
		    0x280001040802101L,
		    0x2100190040002085L,
		    0x80C0084100102001L,
		    0x4024081001000421L,
		    0x20030A0244872L,
		    0x12001008414402L,
		    0x2006104900A0804L,
		    0x1004081002402L
		};

		public static final long bishopMagicNumbers[] = {
		    0x40040844404084L,
		    0x2004208A004208L,
		    0x10190041080202L,
		    0x108060845042010L,
		    0x581104180800210L,
		    0x2112080446200010L,
		    0x1080820820060210L,
		    0x3C0808410220200L,
		    0x4050404440404L,
		    0x21001420088L,
		    0x24D0080801082102L,
		    0x1020A0A020400L,
		    0x40308200402L,
		    0x4011002100800L,
		    0x401484104104005L,
		    0x801010402020200L,
		    0x400210C3880100L,
		    0x404022024108200L,
		    0x810018200204102L,
		    0x4002801A02003L,
		    0x85040820080400L,
		    0x810102C808880400L,
		    0xE900410884800L,
		    0x8002020480840102L,
		    0x220200865090201L,
		    0x2010100A02021202L,
		    0x152048408022401L,
		    0x20080002081110L,
		    0x4001001021004000L,
		    0x800040400A011002L,
		    0xE4004081011002L,
		    0x1C004001012080L,
		    0x8004200962A00220L,
		    0x8422100208500202L,
		    0x2000402200300C08L,
		    0x8646020080080080L,
		    0x80020A0200100808L,
		    0x2010004880111000L,
		    0x623000A080011400L,
		    0x42008C0340209202L,
		    0x209188240001000L,
		    0x400408A884001800L,
		    0x110400A6080400L,
		    0x1840060A44020800L,
		    0x90080104000041L,
		    0x201011000808101L,
		    0x1A2208080504F080L,
		    0x8012020600211212L,
		    0x500861011240000L,
		    0x180806108200800L,
		    0x4000020E01040044L,
		    0x300000261044000AL,
		    0x802241102020002L,
		    0x20906061210001L,
		    0x5A84841004010310L,
		    0x4010801011C04L,
		    0xA010109502200L,
		    0x4A02012000L,
		    0x500201010098B028L,
		    0x8040002811040900L,
		    0x28000010020204L,
		    0x6000020202D0240L,
		    0x8918844842082200L,
		    0x4010011029020020L
		};
		
		// initializes fen string "r3k2r/p1ppppb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R b KQkq - 0 1 " for chessboard speed testing
//		ChessBoard test = new ChessBoard();
//    	long start = System.currentTimeMillis();
//    	for (int counter = 0; counter < 100000; counter++) {
//    		ArrayList<Move> moves = test.getAllLegalMoves();
//    		for (Move x : moves) {
//    			test.submitMove(x);
//    			test.undoMove();
//    		}
//    	}
//    	System.out.printf("time taken: %d", System.currentTimeMillis() - start);
		
//		parseFen("r3k2r/p1ppppb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R b KQkq - 0 1 ");
//		
//		long start = System.currentTimeMillis();
//		for (int counter = 0; counter < 1000000; counter++) {
//			moves moveList = new moves();
//			
//			generateMoves(moveList, allMoves);
//			for (int i = 0; i < moveList.count; i++) {
//				if (!makeMove(moveList.moves[i])) { continue; }
//				takeBack();
//			}
//		}
//		
//		System.out.printf("time taken to execute: %d", System.currentTimeMillis() - start);
		
//		board[0][0] = new Rook(true);
//		board[0][4] = new King(true);
//		kingPos[1] = new Pair(0, 4);
//		board[0][7] = new Rook(true);
//		board[1][0] = new Pawn(true);
//		board[1][1] = new Pawn(true);
//		board[1][2] = new Pawn(true);
//		board[1][3] = new Bishop(true);
//		board[1][4] = new Bishop(true);
//		board[1][5] = new Pawn(true);
//		board[1][6] = new Pawn(true);
//		board[1][7] = new Pawn(true);
//		board[2][2] = new Knight(true);
//		board[2][5] = new Queen(true);
//		board[2][7] = new Pawn(false);
//		board[3][1] = new Pawn(false);
//		board[3][4] = new Pawn(true);
//		board[4][3] = new Pawn(true);
//		board[4][4] = new Knight(true);
//		board[5][0] = new Bishop(false);
//		board[5][1] = new Knight(false);
//		board[5][4] = new Pawn(false);
//		board[5][5] = new Knight(false);
//		board[5][6] = new Pawn(false);
//		board[6][0] = new Pawn(false);
//		board[6][2] = new Pawn(false);
//		board[6][3] = new Pawn(false);
//		board[6][4] = new Pawn(false);
//		board[6][5] = new Pawn(false);
//		board[6][6] = new Bishop(false);
//		board[7][0] = new Rook(false);
//		board[7][4] = new King(false);
//		kingPos[0] = new Pair(7, 4);
//		board[7][7] = new Rook(false);
}