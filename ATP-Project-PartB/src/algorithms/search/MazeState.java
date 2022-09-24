package algorithms.search;

import algorithms.mazeGenerators.Position;

public class MazeState extends AState{
    private Position position;


    public MazeState(Position position,AState discoveredFrom, double cost){
        super(discoveredFrom,cost);
        this.position=position;
    }

    public MazeState(Position pos){
        super(null,0);
        this.position=pos;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return this.position.toString();
    }

    /**
     * @param obj
     * @return true if the 2 mazeStates have the same Position, false otherwise
     */
    @Override
    public boolean equals(Object obj){
        if(!(obj instanceof MazeState))
            return false;
        return this.position.equals(((MazeState) obj).getPosition());
    }


    @Override
    public int hashCode() {
        return position.hashCode();
    }
}
