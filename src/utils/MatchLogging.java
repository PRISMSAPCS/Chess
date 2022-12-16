package utils;

import java.util.Calendar;
import java.io.*;
import java.util.*;

public class MatchLogging {
    // portable game notation format

    public MatchLogging(String path, String whiteName, String blackName) {
        movCnt = 0;
        // get current date, and convert it to string, eg. 1145.14.19
        String date = Calendar.getInstance().getTime().toString();
        OutputStream os = null;
        try {

            File file = new File(path);
            os = new FileOutputStream(file); // output stream will create a new file if not exist
            out = new OutputStreamWriter(os);

        } catch (Exception e) {
            System.out.println("Error in opening file for MatchLogging");
            e.printStackTrace();
        }
        try {
            out.append("[Date \"" + date + "\"]\n");
            out.append("[White \"" + whiteName + "\"]\n");
            out.append("[Black \"" + blackName + "\"]\n");
            out.flush();
        } catch (Exception e) {
            System.out.println("Error in writing to file for MatchLogging");
            e.printStackTrace();
        }
    }

    void logMove(Move m, ChessBoard b) {

        boolean captured = b.getBoard(m.getEnd()) != null;
        // the ending position of the piece being captured
        try {
            if (movCnt % 2 == 0) {
                out.append((movCnt / 2 + 1) + ". ");
                // write round number
            }

            if (b.getBoard(m.getStart()) instanceof Pawn) {
                // if move a pawn, no need to specify the piece
                // if captured, need to specify the column of the pawn
                if (captured) {
                    out.append(m.getStart().getCol() + "x");
                }
                out.append(m.getEnd().toChessNote() + " ");
            } else {
                // if move a piece other than pawn, need to specify the piece
                // if captures, add x after the piece's original position
                out.append(pieceToChar.get(b.getBoard(m.getStart()).getClass()));
                out.append(m.getStart().toChessNote() + " ");
                if (captured) {
                    out.append("x");
                }
                out.append(m.getEnd().toChessNote() + " ");
            }
            out.flush();
        } catch (Exception e) {
            System.out.println("Error in writing to file for MatchLogging");
            e.printStackTrace();
        }
        movCnt++;

    }

    private int movCnt; // count of moves, not rounds
    private OutputStreamWriter out;
    static private HashMap<Class<?>, String> pieceToChar;

    static {
        pieceToChar = new HashMap<Class<?>, String>();
        pieceToChar.put(Pawn.class, "P");
        pieceToChar.put(Rook.class, "R");
        pieceToChar.put(Knight.class, "N");
        pieceToChar.put(Bishop.class, "B");
        pieceToChar.put(Queen.class, "Q");
        pieceToChar.put(King.class, "K");
    }
}
