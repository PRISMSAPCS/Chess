package utils;
public class Pair {
    public int first;
    public int second;
    public Pair(int first, int second) {
        this.first = first;
        this.second = second;
    }
    
    public String toChessNote(){
        String s = "";
        s += (char)(second + 'a');
        s += (char)(first + '1');
        return s;
    }

    public char getCol(){
        return (char)(first + '1');
    }

    public char getRow(){
        return (char)(second + 'a');
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Pair)) {
            return false;
        }
        Pair p = (Pair) o;
        return p.first == first && p.second == second;
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }

    @Override
    public int hashCode() {
        return 19260817 * Integer.hashCode(first) + Integer.hashCode(second);
    }
}