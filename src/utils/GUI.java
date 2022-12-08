package utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

public class GUI {
    public static final Color WHITE_GRID_COLOR = new Color(240, 240, 235);
    public static final Color BLACK_GRID_COLOR = new Color(60, 60, 60);
    public static final Color SELECTED_GRID_COLOR = new Color(255, 220, 0);
    public static final Color ALLOWED_GRID_COLOR = new Color(155, 150, 39);
    public static final double INTERPOLATE_RATIO = 0.5;

    public static Color linearInterpolate(Color c1, Color c2, double ratio) {
        return new Color(
                (int) (c1.getRed() * ratio + c2.getRed() * (1 - ratio)),
                (int) (c1.getGreen() * ratio + c2.getGreen() * (1 - ratio)),
                (int) (c1.getBlue() * ratio + c2.getBlue() * (1 - ratio))
        );
    }

    private JFrame window;
    private JPanel boardPanel;
    private JPanel[][] backgroundPanel; // black-and-white-alternating blocks in background
    private ChessBoard board;
    private JButton submitButton;
    Pair curSelectedPos;                // the position of the currently selected piece
    List<Pair> currentAllowedMove;      // all grids that changed color after selecting a piece

    /**
     * Initialize GUI class.
     * 
     * @author mqcreaple
     * @param board The initial chess board.
     */
    public GUI(ChessBoard board) {
        this.board = board;
        this.currentAllowedMove = new ArrayList<>();
        this.window = new JFrame();
        this.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.window.setSize(600, 600);
        this.boardPanel = new JPanel();
        this.boardPanel.setLayout(new GridLayout(ChessBoard.WIDTH, ChessBoard.HEIGHT));
        this.backgroundPanel = new JPanel[8][8];

        // initialize each block of background and chess pieces
        for (int i = 0; i < ChessBoard.WIDTH; i++) {
            for (int j = 0; j < ChessBoard.HEIGHT; j++) {
                backgroundPanel[i][j] = new JPanel();
                backgroundPanel[i][j].setLayout(new BoxLayout(backgroundPanel[i][j], 0));
                // I'm assuming that boxlayout will take all the space, so the image is
                // displayed
                // but still not sure why flowLayout doesn't work when I overloaded
                // getPreferredSiz
                Color curColor = ((i + j) % 2 == 0) ? BLACK_GRID_COLOR : WHITE_GRID_COLOR;
                backgroundPanel[i][j].setBackground(curColor);
                backgroundPanel[i][j].addMouseListener(new PieceSelectedListener(new Pair(i, j)));
                if (board.getBoard()[i][j] != null) {
                    // set corresponding image to label on index (i, j)
                    ImagePanel chessLabel = new ImagePanel("resource/" + board.getBoard()[i][j].getIconFile());
                    //// chessLabel.setBackground(curCorlor);
                    chessLabel.setOpaque(false);
                    backgroundPanel[i][j].add(chessLabel);
                }
                this.boardPanel.add(backgroundPanel[i][j], i, j);
            }
        }
        this.window.add(this.boardPanel);
        this.window.setVisible(true);
    }

    /**
     * Apply a move to show it on the GUI.
     * This method would not check if the move is valid.
     * 
     * @author mqcreaple
     * @param move the move being performed
     */
    public void applyMove(Move move) {
        int[] start = move.getStart();
        int[] end = move.getEnd();
        int[] capture = move.getCapture();
        if(capture != null) {
            backgroundPanel[capture[0]][capture[1]].removeAll();
        }
        backgroundPanel[end[0]][end[1]].add(backgroundPanel[start[0]][start[1]].getComponent(0));
        backgroundPanel[start[0]][start[1]].removeAll();
    }

    //// public Move getMove() {
    ////    popInfo("please select a piece to move");

    //// }

    /**
     * piece selected listener (action listener class), when a piece is selected, it
     * will be highlighted, and when it is selected again, it will be deselected
     * @author tzyt, mqcreaple
     */
    private class PieceSelectedListener implements MouseListener {
        private Pair pos;
        public PieceSelectedListener(Pair pos) {
            this.pos = pos;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (board.getBoard()[pos.first][pos.second] != null) {
                // if nothing is currently selected, select the piece, and highlight it
                if (curSelectedPos == null) {
                    curSelectedPos = pos;
                    backgroundPanel[pos.first][pos.second].setBackground(SELECTED_GRID_COLOR);
                    for(Move move : board.getLegalMoves(pos.first, pos.second)) {
                        int[] end = move.getEnd();
                        backgroundPanel[end[0]][end[1]].setBackground(
                                linearInterpolate(backgroundPanel[end[0]][end[1]].getBackground(), ALLOWED_GRID_COLOR, INTERPOLATE_RATIO)
                        );
                        currentAllowedMove.add(new Pair(end[0], end[1]));
                    }
                    validateAndRepaint(backgroundPanel[pos.first][pos.second]);
                } else {
                    // TODO
                }
            }
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }

        public void mousePressed(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
        }

    }

    /**
     * helper function that updata all the components in the component tree (parent
     * components)
     * this will force swing to repaint
     * 
     * @param comp the component to be validated and repainted
     * @author tzyt
     */
    public static void validateAndRepaint(Component comp) {
        do {
            comp.validate();
            comp.repaint();
        } while ((comp = comp.getParent()) != null);
    }

    public static void popInfo(String str) {
        JOptionPane.showMessageDialog(null, str);
    }
}
