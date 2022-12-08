package utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.ArrayList;

public class GUI {
    public static final Color WHITE_GRID_COLOR = new Color(240, 240, 235);
    public static final Color BLACK_GRID_COLOR = new Color(35, 75, 50);
    public static final Color SELECTED_GRID_COLOR = new Color(255, 220, 0);
    public static final Color ALLOWED_GRID_COLOR = new Color(41, 169, 169);
    public static final double INTERPOLATE_RATIO = 0.5;

    public static Color linearInterpolate(Color c1, Color c2, double ratio) {
        return new Color(
                (int) (c1.getRed() * ratio + c2.getRed() * (1 - ratio)),
                (int) (c1.getGreen() * ratio + c2.getGreen() * (1 - ratio)),
                (int) (c1.getBlue() * ratio + c2.getBlue() * (1 - ratio)));
    }

    private JFrame window;
    private JPanel boardPanel;
    private JPanel[][] backgroundPanel; // black-and-white-alternating blocks in background
    private ChessBoard board;
    private Semaphore getMoveSem = new Semaphore(1); // used for getmove to wait for user input
    Pair firSelectedPos; // the position of the currently selected piece
    Pair secSelectedPos; // the position of the second selected piece, the first one is the position of
                         // piece, second position is the target position
    Map<Pair, Move> currentAllowedMove; // all grids that changed color after selecting a piece

    /**
     * Initialize GUI class.
     * @author mqcreaple
     * @param board The initial chess board.
     */
    public GUI(ChessBoard board) {
        try {
            getMoveSem.acquire();
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.board = board;
        this.currentAllowedMove = new HashMap<>();
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
                setOrigBack(new Pair(i, j));
                backgroundPanel[i][j].addMouseListener(new PieceSelectedListener(new Pair(i, j)));
                if (board.getBoard()[i][j] != null) {
                    // set corresponding image to label on index (i, j)
                    ImagePanel chessLabel = new ImagePanel("resource/" + board.getBoard()[i][j].getIconFile());
                    //// chessLabel.setBackground(curCorlor);
                    chessLabel.setOpaque(false);
                    backgroundPanel[i][j].add(chessLabel);
                }
                this.boardPanel.add(backgroundPanel[i][j], ChessBoard.HEIGHT - i - 1, j);
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
        Pair start = move.getStart();
        Pair end = move.getEnd();
        Pair capture = move.getCapture();
        if(capture != null) {
            backgroundPanel[capture.first][capture.second].removeAll();
        }
        backgroundPanel[end.first][end.second].add(backgroundPanel[start.first][start.second].getComponent(0));
        backgroundPanel[start.first][start.second].removeAll();
    }

    /**
     * @author tzyt
     * 
     * wait until the user selects a piece and a target position, then return the move
     * @return the move that the user selected
     */
    public Move getMove() {
        try {
            getMoveSem.acquire();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Move move = currentAllowedMove.get(secSelectedPos);
        // reset color
        for (Pair pos : currentAllowedMove.keySet()) {
            setOrigBack(pos);
        }
        setOrigBack(firSelectedPos, secSelectedPos);
        firSelectedPos = null;
        secSelectedPos = null;
        currentAllowedMove.clear();
        return move;
    }

    /**
     * piece selected listener (action listener class), when a piece is selected, it
     * will be highlighted, and when it is selected again, it will be deselected
     * 
     * @author tzyt, mqcreaple
     */
    private class PieceSelectedListener implements MouseListener {
        private Pair pos;

        public PieceSelectedListener(Pair pos) {
            this.pos = pos;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (board.getBoard()[pos.first][pos.second] != null && firSelectedPos == null) {
                // if nothing is currently selected, select the piece, and highlight it

                firSelectedPos = pos;
                backgroundPanel[pos.first][pos.second].setBackground(SELECTED_GRID_COLOR);
                //// List<Move> legalMoves = List.of(
                ////         new Move(new Pawn(false), pos.first, pos.second, pos.first + 1, pos.second),
                ////         new Move(new Pawn(false), pos.first, pos.second, pos.first + 2, pos.second));
                //// for (Move move : legalMoves) {
                for (Move move : board.getLegalMoves(pos.first, pos.second)) {
                    Pair end = move.getEnd();
                    backgroundPanel[end.first][end.second].setBackground(
                            linearInterpolate(backgroundPanel[end.first][end.second].getBackground(), ALLOWED_GRID_COLOR,
                                    INTERPOLATE_RATIO));
                    currentAllowedMove.put(end, move);
                }
                validateAndRepaint(backgroundPanel[pos.first][pos.second]);

            } else if (firSelectedPos != null && firSelectedPos == pos) {
                // if select the same thing, deselect it
                setOrigBack(firSelectedPos);
                for (Pair p : currentAllowedMove.keySet()) {
                    setOrigBack(p);
                }
                firSelectedPos = null;
                currentAllowedMove.clear();
            } else {
                // if select a different thing, check if it is a valid move
                if (currentAllowedMove.containsKey(pos)) {
                    secSelectedPos = pos;
                    getMoveSem.release();
                } else {
                    popInfo("Not a valid move");
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

    private Color getBoardOrigColor(Pair x) {
        return ((x.first + x.second) % 2 == 0) ? BLACK_GRID_COLOR : WHITE_GRID_COLOR;
    }

    private void setOrigBack(Pair... arr) {
        for (Pair x : arr)
            backgroundPanel[x.first][x.second].setBackground(getBoardOrigColor(x));
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
