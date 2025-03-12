
import java.util.ArrayList;

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
        MoveUtility moveUtility = maxValue(s);
        return moveUtility.getMove();
    }

    /**
    * Returns the move with the highest utility value
    * @param s The current state of the game in which it should be Boblises turn.
    * @return The move with the highest utility value
    */
    private MoveUtility maxValue(GameState s) {
        MoveUtility bestMove = new MoveUtility(Double.NEGATIVE_INFINITY, null); 
        
        if (isTerminal(s)) {
            bestMove.setUtility(utility(s));
            return bestMove;
        }

        for(Position a : actions(s)) {
            MoveUtility currentMove = minValue(result(s, a));

            if (currentMove.getUtility() > bestMove.getUtility()) {
                bestMove.setUtility(currentMove.getUtility());
                bestMove.setMove(a);
            }
        }

        return bestMove;
    }

    /**
     * Returns the move with the lowest utility value
     * @param s The current state of the game in which it should be Boblises turn.
     * @return The move with the lowest utility value
     */
    private MoveUtility minValue(GameState s) {
        MoveUtility bestMove = new MoveUtility(Double.POSITIVE_INFINITY, null); 
        
        if (isTerminal(s)) {
            bestMove.setUtility(utility(s));
            return bestMove;
        }

        for(Position a : actions(s)) {
            MoveUtility currentMove = maxValue(result(s, a));

            if (currentMove.getUtility() < bestMove.getUtility()) {
                bestMove.setUtility(currentMove.getUtility());
                bestMove.setMove(a);
            }
        }

        return bestMove;
    }

    /**
     * Returns true if the given state is terminal
     * @param s The current state of the game
     * @return True if the given state is terminal (no more possible moves)
     */
    private boolean isTerminal(GameState s) {
        return s.isFinished();
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
     * Returns a list of all possible moves in the given state
     * @param s The current state of the game
     * @return A list of all possible legal moves in the given state
     */
    private ArrayList<Position> actions(GameState s) {
        return s.legalMoves();
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
 * Minimax algorithm
 * Minimax with alpha-beta pruning
 * Heuritic-Alpha-Beta-Search
 */