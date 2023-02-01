package utils.bot.KZBotResources;

public class flipEval {

    static int pawnBoard[][]={ 
        {-50,-40,-30,-20,-20,-30,-40,-50},
        {-30,-20,-10,  0,  0,-10,-20,-30},
        {-30,-10, 20, 30, 30, 20,-10,-30},
        {-30,-10, 30, 40, 40, 30,-10,-30},
        {-30,-10, 30, 40, 40, 30,-10,-30},
        {-30,-10, 20, 30, 30, 20,-10,-30},
        {-30,-30,  0,  0,  0,  0,-30,-30},
        {-50,-30,-30,-30,-30,-30,-30,-50}};
    public static void main(String[] args){
        for(int y = 7; y > -1; y--){
            System.out.print("{");
            for(int x = 7; x > -1; x--){
                System.out.print(pawnBoard[y][x]);
                if(x != 0) System.out.print(", "); else continue;
            }
            System.out.println("},");
        }
    }
}
