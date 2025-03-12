
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * AI bot "Boblise" for Othello, implemented using minimax algorithm
 * 
 * @group Boblise 
 * @author Hannah Camilla Andersen and Turið Højgaard
 */
public class OthelloAIBoblise implements IOthelloAI {
    
    /**
     * Calculates the optimal move for Boblise given current game state.
     * @param s The current state of the game in which it should be Boblises turn.
     * @return The optimal position where Boblise wants to put its token.
     */
    @Override
    public Position decideMove(GameState s){
        MoveUtility bestMove = maxValue(s, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        System.out.println("Boblise decided to place token at: " + bestMove.getMove() + " with utility value: " + bestMove.getUtility());
        return bestMove.getMove();
    }

    /**
    * Returns the move with the highest utility value
    * @param s The current state of the game in which it should be Boblises turn.
    * @param alpha The best utility value for Max found so far
    * @param beta The best utility value for Min found so far
    * @return The move with the highest utility value
    */
    private MoveUtility maxValue(GameState s, double alpha, double beta) {
        MoveUtility bestMove = new MoveUtility(Double.NEGATIVE_INFINITY, null); 
        
        if (isTerminal(s)) {
            System.out.println("Max (white) reached terminal state");
            bestMove.setUtility(utility(s));
            return bestMove;
        }

        //System.out.println("Max (white) has legal moves: " + actions(s));
        for(Position a : actions(s)) {
            //System.out.println("Max (white) evaluating move: " + a);
            MoveUtility currentMove = minValue(result(s, a), alpha, beta);

            if (currentMove.getUtility() > bestMove.getUtility()) {
                bestMove.setUtility(currentMove.getUtility());
                bestMove.setMove(a);
                alpha = Math.max(alpha, bestMove.getUtility());
            }
            if (bestMove.getUtility() >= beta) {
                System.out.println("Max (white) made beta cut, returning move: " + bestMove.getMove() + " with utility value: " + bestMove.getUtility());
                return bestMove;
            }
        }

        System.out.println("Max (white) returning move: " + bestMove.getMove() + " with utility value: " + bestMove.getUtility());
        return bestMove;
    }

    /**
     * Returns the move with the lowest utility value
     * 
     * @param s The current state of the game in which it should be Boblises turn.
     * @param alpha The best utility value for Max found so far
     * @param beta The best utility value for Min found so far
     * @return The move with the lowest utility value
     */
    private MoveUtility minValue(GameState s, double alpha, double beta) {
        MoveUtility bestMove = new MoveUtility(Double.POSITIVE_INFINITY, null); 
        
        if (isTerminal(s)) {
            System.out.println("Min (black) reached terminal state");
            bestMove.setUtility(utility(s));
            return bestMove;
        }

        //System.out.println("Min (black) has legal moves: " + actions(s));
        for(Position a : actions(s)) {
            //System.out.println("Min (black) evaluating move: " + a);
            MoveUtility currentMove = maxValue(result(s, a), alpha, beta);

            if (currentMove.getUtility() < bestMove.getUtility()) {
                bestMove.setUtility(currentMove.getUtility());
                bestMove.setMove(a);
                beta = Math.min(beta, bestMove.getUtility());
            }
            if (bestMove.getUtility() <= alpha) {
                System.out.println("Min (black) made alpha cut, returning move: " + bestMove.getMove() + " with utility value: " + bestMove.getUtility());
                return bestMove;
            }
        }

        System.out.println("Min (black) returning move: " + bestMove.getMove() + " with utility value: " + bestMove.getUtility());
        return bestMove;
    }

    /**
     * Returns true if the given state is terminal
     * @param s The current state of the game
     * @return True if the given state is terminal (no more possible moves)
     */
    private boolean isTerminal(GameState s) {
        return s.isFinished() || actions(s).isEmpty();
    }

    /**
     * Returns the utility value of the given state
     * The utility value is the number of white tokens (Boblises tokens)
     * @param s The current state of the game
     * @return The utility value of the given state
     */
    private int utility(GameState s) {
        return s.countTokens()[1];
    }

    /**
     * Returns a list of all possible unique moves in the given state
     * @param s The current state of the game
     * @return A list of all possible legal moves in the given state
     */
    private ArrayList<Position> actions(GameState s) {
        Set<Position> uniqueMoves = new HashSet<>(s.legalMoves());
        return new ArrayList<>(uniqueMoves);
    }

    /**
     * Returns the state that results from applying the given move to the given state
     * @param s The current state of the game
     * @param a The move to apply to the given state
     * @return The state that results from applying the given move to the given state
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
}


/* STEPS
 * Minimax algorithm - Done
 * Minimax with alpha-beta pruning - Done
 * Heuritic-Alpha-Beta-Search
 */