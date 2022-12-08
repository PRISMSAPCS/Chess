package utils;

public class Move {
    private Piece piece;
    private Pair start;
    private Pair end;
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

    public Pair getCapture() {
        return capture;
    }

    @Override
    public String toString() {
        return piece + " from " + start.first + ", " + start.second + " to " + end.first + ", " + end.second;
    }
}
