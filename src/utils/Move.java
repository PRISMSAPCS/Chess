package utils;

public class Move {
    private Piece piece;
    private int[] start;
    private int[] end;
    private boolean capture;   // if this piece captures another piece at the end position

    public Move(Piece piece, int[] start, int[] end) {
        this.piece = piece;
        this.start = start;
        this.end = end;
        this.capture = false;
    }
    public Move(Piece piece, int[] start, int[] end, boolean capture) {
        this.piece = piece;
        this.start = start;
        this.end = end;
        this.capture = capture;
    }
    public Move(Piece piece, int startX, int startY, int endX, int endY) {
        this.piece = piece;
        this.start = new int[] {startX, startY};
        this.end = new int[] {endX, endY};
        this.capture = false;
    }

    public Piece getPiece() {
        return piece;
    }

    public int[] getStart() {
        return start;
    }

    public int[] getEnd() {
        return end;
    }

    public boolean getCapture() {
        return capture;
    }
}
