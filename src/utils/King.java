package utils;

public class King extends Piece {
    public King(boolean color) {
        super(color);
    }
    @Override
    public String getIconFile() {
        return getColor()? "k_w.png": "k_b.png";
    }
}
