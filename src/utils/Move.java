package utils;

public class Move {
    private Piece piece;
    private int[] start;
    private int[] end;

    public Move(Piece piece, int[] start, int[] end) {
        this.piece = piece;
        this.start = start;
        this.end = end;
    }
    public Move(Piece piece, int startX, int startY, int endX, int endY) {
        this.piece = piece;
        this.start = new int[] {startX, startY};
        this.end = new int[] {endX, endY};
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
}
