package utils;

public class PromotionMove extends Move {
    private Piece promoteTo;

    public PromotionMove(Piece piece, Pair start, Pair end) {
        super(piece, start, end);
    }

    public PromotionMove(Piece piece, Pair start, Pair end, Pair capture) {
        super(piece, start, end, capture);
    }

    public PromotionMove(Piece piece, int startX, int startY, int endX, int endY) {
        super(piece, startX, startY, endX, endY);
    }

    public PromotionMove(Piece piece, int startX, int startY, int endX, int endY, Piece promoteTo) {
        super(piece, startX, startY, endX, endY);
    	this.setPromoteTo(promoteTo);
    }
    
    public PromotionMove(Piece piece, int startX, int startY, int endX, int endY, int captureX, int captureY) {
        super(piece, startX, startY, endX, endY, captureX, captureY);
    }

    public Piece getPromoteTo() {
        return promoteTo;
    }

    public void setPromoteTo(Piece promoteTo) {
        this.promoteTo = promoteTo;
    }
}
