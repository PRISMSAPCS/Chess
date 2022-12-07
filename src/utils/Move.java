package utils;

public class Move {
    private Piece piece;
    private int[] start;
    private int[] end;
    /**
     * Position of the piece being captured in this move.
     * This field is optional.
     * Note: 'capture' field is not always equal to 'end' field because of the pawn's capture rule.
     */
    private int[] capture;

    public Move(Piece piece, int[] start, int[] end) {
        this.piece = piece;
        this.start = start;
        this.end = end;
        this.capture = null;
    }
    public Move(Piece piece, int[] start, int[] end, int[] capture) {
        this.piece = piece;
        this.start = start;
        this.end = end;
        this.capture = capture;
    }
    public Move(Piece piece, int startX, int startY, int endX, int endY) {
        this.piece = piece;
        this.start = new int[] {startX, startY};
        this.end = new int[] {endX, endY};
        this.capture = null;
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

    public int[] getCapture() {
        return capture;
    }
}
