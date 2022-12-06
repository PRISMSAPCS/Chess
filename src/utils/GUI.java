package utils;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class GUI {
    private JFrame window;
    private JPanel boardPanel;
    private JPanel[][] backgroundPanel; // black-and-white-alternating blocks in background
    private ImagePanel[][] chessLabel; // pieces laying over background panels
    private ChessBoard board;

    /**
     * Initialize GUI class.
     * 
     * @author mqcreaple
     * @param board The initial chess board.
     */
    public GUI(ChessBoard board) {
        this.board = board;
        this.window = new JFrame();
        this.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.window.setSize(600, 600);
        this.boardPanel = new JPanel();
        this.boardPanel.setLayout(new GridLayout(ChessBoard.WIDTH, ChessBoard.HEIGHT));
        this.chessLabel = new ImagePanel[8][8];
        this.backgroundPanel = new JPanel[8][8];

        // initialize each block of background and chess pieces
        for (int i = 0; i < ChessBoard.WIDTH; i++) {
            for (int j = 0; j < ChessBoard.HEIGHT; j++) {
                backgroundPanel[i][j] = new JPanel();
                backgroundPanel[i][j].setLayout(new BoxLayout(backgroundPanel[i][j], 0));
                // i'm assuming that boxlayout will take all the space, so the image is displayed
                // but still not sure why flowLayout doesn't work when I overloaded getPreferredSize
                backgroundPanel[i][j]
                        .setBackground(((i + j) % 2 == 0) ? new Color(65, 65, 65) : new Color(220, 220, 220));

                if (board.getBoard()[i][j] != null) {
                    // set corresponding image to label on index (i, j)
                    chessLabel[i][j] = new ImagePanel("resource/" + board.getBoard()[i][j].getIconFile());
                    backgroundPanel[i][j].add(chessLabel[i][j]);
                }
                this.boardPanel.add(backgroundPanel[i][j], i, j);
            }
        }
        this.window.add(this.boardPanel);
        this.window.setVisible(true);
    }

    public static void validateNrepaint(Component comp) {
        do {
            comp.validate();
            comp.repaint();
        } while ((comp = comp.getParent()) != null);
    }
}
