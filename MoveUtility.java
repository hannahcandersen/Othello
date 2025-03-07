public class MoveUtility {
    int utility;
    Position move;

    public MoveUtility(int utility, Position move) {
        this.utility = utility;
        this.move = move;
    }

    public int getUtility() {
        return utility;
    }

    public Position getMove() {
        return move;
    }

    public void setUtility(int utility) {
        this.utility = utility;
    }

    public void setMove(Position move) {
        this.move = move;
    }
}
