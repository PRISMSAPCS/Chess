package utils;

import javax.swing.*;
import java.awt.*;

public class GUI {
    private JFrame window;
    private JPanel boardPanel;
    private JLabel[][] chessLabel;
    private ChessBoard board;

    public GUI(ChessBoard board) {
        this.board = board;
        this.window = new JFrame();
        this.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.window.setSize(600, 600);
        this.boardPanel = new JPanel();
        this.boardPanel.setLayout(new GridLayout(ChessBoard.WIDTH, ChessBoard.HEIGHT));
        this.chessLabel = new JLabel[8][8];
        for(int i = 0; i < ChessBoard.WIDTH; i++) {
            for(int j = 0; j < ChessBoard.HEIGHT; j++) {
                chessLabel[i][j] = new JLabel((board.getBoard()[i][j] == null)? "": board.getBoard()[i][j].getIcon());
                chessLabel[i][j].setFont(new Font(Font.SANS_SERIF, Font.BOLD, 30));
                this.boardPanel.add(chessLabel[i][j], i, j);
            }
        }
        this.window.add(this.boardPanel);
        this.window.setVisible(true);
    }
}
