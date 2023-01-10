package utils;

/**
 * Interface for all things that can return a 'Move' object.
 * GUI can return move from human player.
 * ChessBot can return move automatically.
 */
public interface CanGetMove {
    Move getMove();
}
