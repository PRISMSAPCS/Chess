package utils;

public class Move {
    private Piece piece;
    private Pair start;
    private Pair end;
    private Piece piece2;
    private Pair start2;
    private Pair end2;
    private Pair capture;   // position of the piece being captured, optional

    public Move(Piece piece, Pair start, Pair end) {
        this.piece = piece;
        this.start = start;
        this.end = end;
        this.capture = null;
    }
    public Move(Piece piece, Pair start, Pair end, Pair capture) {
        this.piece = piece;
        this.start = start;
        this.end = end;
        this.capture = capture;
    }
    public Move(Piece piece, int startX, int startY, int endX, int endY) {
        this.piece = piece;
        this.start = new Pair(startX, startY);
        this.end = new Pair(endX, endY);
        this.capture = null;
    }

    public Move(Piece piece, int startX, int startY, int endX, int endY, Piece piece2, int startX2, int startY2, int endX2, int endY2) {
        this.piece = piece;
        this.start = new Pair(startX, startY);
        this.end = new Pair(endX, endY);
        this.piece2 = piece2;
        this.start2 = new Pair(startX2, startY2);
        this.end2 = new Pair(endX2, endY2);
        this.capture = null;
    }

    public Move(Piece piece, int startX, int startY, int endX, int endY, int captureX, int captureY) {
        this.piece = piece;
        this.start = new Pair(startX, startY);
        this.end = new Pair(endX, endY);
        this.capture = new Pair(captureX, captureY);
    }

    public Piece getPiece() {
        return piece;
    }

    public Pair getStart() {
        return start;
    }

    public Pair getEnd() {
        return end;
    }

    public Piece getPiece2() {
        return piece2;
    }

    public Pair getStart2() {
        return start2;
    }

    public Pair getEnd2() {
        return end2;
    }

    public Pair getCapture() {
        return capture;
    }

    @Override
    public String toString() {
        return piece + " from " + start.first + ", " + start.second + " to " + end.first + ", " + end.second;
    }
}
