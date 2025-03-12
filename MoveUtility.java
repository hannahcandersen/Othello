public class MoveUtility {
    double utility;
    Position move;

    public MoveUtility(double utility, Position move) {
        this.utility = utility;
        this.move = move;
    }

    public double getUtility() {
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
