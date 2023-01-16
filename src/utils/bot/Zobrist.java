package utils.bot;

import java.security.*;
import utils.*;

public class Zobrist {
    public static long rand64() {
        final SecureRandom random = new SecureRandom();
        long rand = random.nextLong();
        return rand;
    }

    long[][][][] zobrist = new long[2][6][8][8]; // 2 for black and white, 6 for 6 pieces, 8 for position
    long zobristEnPassant[][] = new long[8][8]; // 64 for 64 squares
    long zobristIsWhite;
    public Zobrist() {
        for (int i = 0; i < 2; i++)
            for (int j = 0; j < 6; j++)
                for (int k = 0; k < 8; k++)
                    for (int l = 0; l < 8; l++)
                        zobrist[i][j][k][l] = rand64();
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                zobristEnPassant[i][j] = rand64();
        zobristIsWhite = rand64();
    }

    public long getHash(ChessBoard board) {
        long ret = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board.getBoard(new Pair(i, j)) == null)
                    continue;
                Piece piece = board.getBoard(new Pair(i, j));
                if (piece instanceof Pawn)
                    ret ^= zobrist[piece.getColor() ? 1 : 0][0][i][j];
                else if (piece instanceof Knight)
                    ret ^= zobrist[piece.getColor() ? 1 : 0][1][i][j];
                else if (piece instanceof Bishop)
                    ret ^= zobrist[piece.getColor() ? 1 : 0][2][i][j];
                else if (piece instanceof Rook)
                    ret ^= zobrist[piece.getColor() ? 1 : 0][3][i][j];
                else if (piece instanceof Queen)
                    ret ^= zobrist[piece.getColor() ? 1 : 0][4][i][j];
                else if (piece instanceof King)
                    ret ^= zobrist[piece.getColor() ? 1 : 0][5][i][j];
                
                // enpassant
                // if (board.getEnPassant() != null)
                //     ret ^= zobristEnPassant[board.getEnPassant().first][board.getEnPassant().second];
            }
        }

        if (board.getSide())
            ret ^= zobristIsWhite;
        return ret;
    }
}
