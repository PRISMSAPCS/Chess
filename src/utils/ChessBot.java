package utils;
public interface ChessBot {

    // Returns the name of this Bot for display/logging
    public abstract String getBotName();

    // Function to generate moves for this chessbot
    // This function should return a legal move on the board in play
    // This function has 5 seconds to run then must return a move
    // You can use a simple timed loop to calc time:
    // long start = System.currentTimeMillis();
    // long end = start + 5 * 1000;
    // while (System.currentTimeMillis() < end) {
    // calculating a move
    // }
    // Or use threads/timeOuts/Scheduler/etc.
    public abstract Move getMove();

}