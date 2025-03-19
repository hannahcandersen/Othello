
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.io.FileWriter;
import java.io.IOException;

/**
 * AI bot "Boblise" for Othello, implemented using minimax algorithm
 * 
 * @group Boblise 
 * @authors Hannah Camilla Andersen and Turið Højgaard
 */
public class OthelloAIBoblise implements IOthelloAI {

    private static int moveCount = 1; 
    private static final String TIMES = "time_to_find_move.txt";
    private static final int MAX_DEPTH = 7; 
    
    /**
     * Calculates the optimal move for Boblise given current game state
     * Writes the time it took to find the move to file "time_to_find_move.txt"
     * @param s The current state of the game in which it should be Boblises turn.
     */
    @Override
    public Position decideMove(GameState s){
        long startTime = System.currentTimeMillis();
        MoveUtility bestMove = maxValue(s, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0);
        long endTime = System.currentTimeMillis();
        //System.out.println("Boblise decided to place token at: " + bestMove.getMove() + " with utility value: " + bestMove.getUtility());

        writeTimeToFile(endTime - startTime);
        moveCount++;

        return bestMove.getMove();
    }

    /**
    * Returns the move with the highest utility value
    * Corresponds to the best possible move for Max (white)
    * @param s The current state of the game in which it should be Boblises turn.
    * @param alpha The best utility value for Max found so far
    * @param beta The best utility value for Min found so far
    */
    private MoveUtility maxValue(GameState s, double alpha, double beta, int depth) {
        MoveUtility bestMove = new MoveUtility(Double.NEGATIVE_INFINITY, null); 
        
        if (isCutOff(s, depth)) {
            bestMove.setUtility(eval(s));
            return bestMove;
        }

        for(Position a : actions(s)) {
            MoveUtility currentMove = minValue(result(s, a), alpha, beta, depth + 1);

            if (currentMove.getUtility() > bestMove.getUtility()) {
                bestMove.setUtility(currentMove.getUtility());
                bestMove.setMove(a);
                alpha = Math.max(alpha, bestMove.getUtility());
            }
            if (bestMove.getUtility() >= beta) {
                return bestMove;
            }
        }

        return bestMove;
    }

    /**
     * Returns the move with the lowest utility value
     * Corresponds to the best possible move for Min (black)
     * @param s The current state of the game in which it should be Boblises turn.
     * @param alpha The best utility value for Max found so far
     * @param beta The best utility value for Min found so far
     */
    private MoveUtility minValue(GameState s, double alpha, double beta, int depth) {
        MoveUtility bestMove = new MoveUtility(Double.POSITIVE_INFINITY, null); 
        
        if (isCutOff(s, depth)) {
            bestMove.setUtility(eval(s));
            return bestMove;
        }

        for(Position a : actions(s)) {
            MoveUtility currentMove = maxValue(result(s, a), alpha, beta, depth + 1);

            if (currentMove.getUtility() < bestMove.getUtility()) {
                bestMove.setUtility(currentMove.getUtility());
                bestMove.setMove(a);
                beta = Math.min(beta, bestMove.getUtility());
            }
            if (bestMove.getUtility() <= alpha) {
                return bestMove;
            }
        }

        return bestMove;
    }

    /**
     * Returns true if the given state is terminal or the depth of the search tree has reached MAX_DEPTH
     * @param s     The current state of the game
     * @param depth The current depth of the search tree
     */
    private boolean isCutOff(GameState s, int depth) {
        return isTerminal(s) || depth >= MAX_DEPTH;
    }

    /**
     * Returns true if the given state is terminal
     * Happens when the game is finished or there are no more legal moves for either player
     * @param s The current state of the game
     */
    private boolean isTerminal(GameState s) {
        return s.isFinished() || actions(s).isEmpty();
    }

    /**
     * Returns the heuristic utility value of the given state
     * For terminal states, returns the utility value
     * For non-terminal states, returns an estimated utility value based on these heuristics:
     ** Number of tokens for Max (white) - Weight: 0.7
     ** Number of corners for Max (white) - Weight: 0.2
     ** Number of available moves for Max (white) - Weight: 0.1
     ** Normalized to ensure that the estimated utility is between 0 and the total number of positions on the board, to be comparable to utility values
     * @param s The current state of the game
     */
    private double eval(GameState s) {
        if (isTerminal(s)) {
            return utility(s);
        } else {
            int size = s.getBoard().length;
            int totalPositions = size * size;

            int maxTokens = s.countTokens()[1];         // White tokens
            int maxCorners = countCorners(s)[1];        // White corners
            int maxAvailableMoves = actions(s).size();  // White available moves
            
            double normMaxTokens = (double) maxTokens / totalPositions;
            double normMaxCorners = (double) maxCorners / 4.0; // Always 4 corners
            double normMaxAvailableMoves = (double) maxAvailableMoves / totalPositions;
            
            double eval = (0.7 * normMaxTokens + 0.2 * normMaxCorners + 0.1 * normMaxAvailableMoves) * totalPositions;
            // Other things that could be considered: edges, ratio of black/white, spreading out of the tokens
            // For 8x8 board, returns a value between 0 and 64

            return eval;
        }
    }

    /**
     * Returns the utility value of the given state
     * The utility value is counted as the number of white tokens (Max's tokens)
     * @param s The current state of the game
     */
    private int utility(GameState s) {
        return s.countTokens()[1];
    }

    /**
     * Returns a list of all possible unique moves in the given state
     * @param s The current state of the game
     */
    private ArrayList<Position> actions(GameState s) {
        Set<Position> uniqueMoves = new HashSet<>(s.legalMoves());
        return new ArrayList<>(uniqueMoves);
    }

    /**
     * Returns the state that results from applying the given move to the given state
     * @param s The current state of the game
     * @param a The move to apply to the given state
     */
    private GameState result(GameState s, Position a) {
        GameState newState = new GameState(s.getBoard(), s.getPlayerInTurn());
        if (newState.insertToken(a)) {
            return newState;
        } else {
            System.out.println("Error: Illegal move");
            return s; // Should never happen as this is only called with legal moves
        }
    }

    /**
     * Counts tokens in the corner of the player 1 (black) and player 2 (white), respectively, and returns an array
     * @param s The current state of the game
     */
    private int[] countCorners(GameState s) {
        int whiteCorners = 0;
        int blackCorners = 0;
        int[][] board = s.getBoard();
        int size = board.length;

        // Upper left corner
        if (board[0][0] == 1) {
            blackCorners++;
        } else if (board[0][0] == 2) {
            whiteCorners++;
        }

        // Upper right corner
        if (board[0][size - 1] == 1) {
            blackCorners++;
        } else if (board[0][size - 1] == 2) {
            whiteCorners++;
        }

        // Lower left corner
        if (board[size - 1][0] == 1) {
            blackCorners++;
        } else if (board[size - 1][0] == 2) {
            whiteCorners++;
        }

        // Lower right corner
        if (board[size - 1][size - 1] == 1) {
            blackCorners++;
        } else if (board[size - 1][size - 1] == 2) {
            whiteCorners++;
        }

        return new int[] {blackCorners, whiteCorners};

    }    

    /**
     * Writes the time it took to find the move to file TIMES
     * For debugging and report purposes
     * @param time
     */
    private void writeTimeToFile(long time) {
        try {
            FileWriter writer = new FileWriter(TIMES, true);
            if (moveCount == 1) {
                writer.write("Boblise starts new game :) \n"); 
            }
            writer.write("Time to decide move " + moveCount + ": " + time + " ms (" + (time / 1000) + " s)\n");
            writer.close();
        } catch (IOException e) {
            System.out.println("Error writing time to file");
            e.printStackTrace();
        }
    }
}
