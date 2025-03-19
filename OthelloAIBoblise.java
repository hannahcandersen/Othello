
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.io.FileWriter;
import java.io.IOException;

/**
 * AI bot "Boblise" for Othello, implemented using minimax algorithm
 * 
 * @group Boblise 
 * @author Hannah Camilla Andersen and Turið Højgaard
 */
public class OthelloAIBoblise implements IOthelloAI {

    private static int moveCount = 1; 
    private static final String TIMES = "time_to_find_move.txt";
    private static final int MAX_DEPTH = 10; // TODO: Find optimal depth
    
    /**
     * Calculates the optimal move for Boblise given current game state
     * Writes the time it took to find the move to file "time_to_find_move.txt"
     * 
     * @param s The current state of the game in which it should be Boblises turn.
     */
    @Override
    public Position decideMove(GameState s){
        long startTime = System.currentTimeMillis();
        MoveUtility bestMove = maxValue(s, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0);
        long endTime = System.currentTimeMillis();
        System.out.println("Boblise decided to place token at: " + bestMove.getMove() + " with utility value: " + bestMove.getUtility());

        writeTimeToFile(endTime - startTime);
        moveCount++;

        return bestMove.getMove();
    }

    /**
    * Returns the move with the highest utility value
    * Corresponds to the best possible move for Max (white)
    *
    * @param s The current state of the game in which it should be Boblises turn.
    * @param alpha The best utility value for Max found so far
    * @param beta The best utility value for Min found so far
    */
    private MoveUtility maxValue(GameState s, double alpha, double beta, int depth) {
        MoveUtility bestMove = new MoveUtility(Double.NEGATIVE_INFINITY, null); 
        
        if (isCutOff(s, depth)) {
            //System.out.println("Max (white) reached terminal state or cut-off at depth: " + depth);
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
                //System.out.println("Max (white) made beta cut, returning move: " + bestMove.getMove() + " with utility value: " + bestMove.getUtility());
                return bestMove;
            }
        }

        //System.out.println("Max (white) returning move: " + bestMove.getMove() + " with utility value: " + bestMove.getUtility());
        return bestMove;
    }

    /**
     * Returns the move with the lowest utility value
     * Corresponds to the best possible move for Min (black)
     * 
     * @param s The current state of the game in which it should be Boblises turn.
     * @param alpha The best utility value for Max found so far
     * @param beta The best utility value for Min found so far
     */
    private MoveUtility minValue(GameState s, double alpha, double beta, int depth) {
        MoveUtility bestMove = new MoveUtility(Double.POSITIVE_INFINITY, null); 
        
        if (isCutOff(s, depth)) {
            System.out.println("Min (black) reached terminal state or cut-off at depth: " + depth);
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
                //System.out.println("Min (black) made alpha cut, returning move: " + bestMove.getMove() + " with utility value: " + bestMove.getUtility());
                return bestMove;
            }
        }

        //System.out.println("Min (black) returning move: " + bestMove.getMove() + " with utility value: " + bestMove.getUtility());
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
     * @return True if the given state is terminal 
     */
    private boolean isTerminal(GameState s) {
        return s.isFinished() || actions(s).isEmpty();
    }

    /**
     * Returns the heuristic utility value of the given state
     * For terminal states, returns the utility value
     * For non-terminal states, returns a heuristic value based on: // TODO
     * @param s The current state of the game
     */
    private double eval(GameState s) {
        if (isTerminal(s)) {
            return utility(s);
        } else {
            // Must be utility(loss) <= eval <= utility(win) to ensure it does not
            // overestimate compared to using clean utility as above
            // What it measures
            // Many whites
            // Few black
            // Many possible moves
            // Few possible moves for opponent
            // Many corners
            // Few corners for opponent
            // Many edges
            // Few edges for opponent
            // TODO: Find heuristic utility value
            return utility(s);
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
            return s; // Should never happen as the move is always legal
        }
    }

    /**
     * Writes the time it took to find the move to file "time_to_find_move.txt" 
     * For debugging and report purposes
     * @param time
     */
    private void writeTimeToFile(long time) {
        try {
            FileWriter writer = new FileWriter(TIMES, true);
            if (moveCount == 1) {
                writer.write("Boblise starts new game. Max depth is " + MAX_DEPTH + "\n"); // TODO: Remove last part
            }
            writer.write("Time to decide move " + moveCount + ": " + time + " ms (" + (time / 1000.0) + " s)\n");
            writer.close();
        } catch (IOException e) {
            System.out.println("Error writing time to file");
            e.printStackTrace();
        }
    }
}



/* STEPS
 * Minimax algorithm - Done
 * Minimax with alpha-beta pruning - Done
 * Heuritic-Alpha-Beta-Search
 */