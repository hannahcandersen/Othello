
import java.util.ArrayList;

public class OthelloAIBoblise implements IOthelloAI {
    
    @Override
    public Position decideMove(GameState s){
        MoveUtility moveUtility = maxValue(s);
        return moveUtility.getMove();
    }

    // Returns the move with the highest utility value
    public MoveUtility maxValue(GameState s) {
        double v = Double.NEGATIVE_INFINITY;
        MoveUtility moveUtility = new MoveUtility(0, null); //consider changing utility to something else
        
        if (isTerminal(s)) {
            int util = utility(s, s.getPlayerInTurn());
            moveUtility.setUtility(util);
            return moveUtility;
        }

        for(Position a : actions(s)) {
            moveUtility = minValue(result(s, a));

            if (moveUtility.getUtility() > v) {
                v = moveUtility.getUtility();
                moveUtility.setMove(a);
            }
        }

        return moveUtility;
    }

    // Returns the move with the lowest utility value
    public MoveUtility minValue(GameState s) {
        double v = Double.POSITIVE_INFINITY;
        MoveUtility moveUtility = new MoveUtility(0, null); //consider changing utility to something else
        
        if (isTerminal(s)) {
            int util = utility(s, s.getPlayerInTurn());
            moveUtility.setUtility(util);
            return moveUtility;
        }

        for(Position a : actions(s)) {
            moveUtility = maxValue(result(s, a));

            if (moveUtility.getUtility() < v) {
                v = moveUtility.getUtility();
                moveUtility.setMove(a);
            }
        }

        return moveUtility;
    }

    // Returns true if the given state is terminal, and false otherwise
    public boolean isTerminal(GameState s) {
        return s.isFinished();
    }

    // Returns the utility of the given state - we count the number of white tokens, since our bot is white
    public int utility(GameState s, int player) {
        return s.countTokens()[1]; // Not sure what colour our Othello bot is
    }

    // Returns a list of all possible moves for the given state
    public ArrayList<Position> actions(GameState s) {
        return s.legalMoves();
    }

    public GameState result(GameState s, Position a) {
        if (s.insertToken(a)) {
            return s;
        } else {
            return null; // Should never happen
        }
    }

}


/* STEPS
 * Minimax algorithm
 * Minimax with alpha-beta pruning
 * Heuritic-Alpha-Beta-Search
 */