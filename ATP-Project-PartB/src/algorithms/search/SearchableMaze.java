package algorithms.search;

import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;

import java.util.ArrayList;

public class SearchableMaze implements ISearchable {
    private Maze maze;


    public SearchableMaze(Maze maze) {
        this.maze = maze;
    }

    public Maze getMaze() {
        return maze;
    }

    @Override
    public AState getStartState() {
         MazeState state=new MazeState(maze.getStartPosition());
         return state;
    }

    @Override
    public AState getGoalState() {
        MazeState state=new MazeState(maze.getGoalPosition());
        return state;

    }

    /**
     *
     * @param astate MazeState current state of the maze
     * @return ArrayList<AState>, list of all possibleStates that can be reached from current state
     */
    @Override
    public ArrayList<AState> getAllPossibleStates(AState astate) {
        if (!(astate instanceof MazeState))
            return null;
        MazeState cur_state = (MazeState) astate;
        if (!maze.isLegalPosition(cur_state.getPosition()))
            return null;
        int row = cur_state.getPosition().getRowIndex();
        int col = cur_state.getPosition().getColumnIndex();
        ArrayList<AState> possibleStates = new ArrayList<>();
        //Orthogonal possibleStates adding (Clockwise starting top)
        addStateOrthogonal(possibleStates, cur_state, row - 1, col);
        addStateOrthogonal(possibleStates, cur_state, row, col + 1);
        addStateOrthogonal(possibleStates, cur_state, row + 1, col);
        addStateOrthogonal(possibleStates, cur_state, row, col - 1);
        //Diagonal possibleStates adding (Clockwise starting top left)
        addStateDiagonal(possibleStates, cur_state, row - 1, col - 1, row - 1, col, row, col - 1);
        addStateDiagonal(possibleStates, cur_state, row - 1, col + 1, row - 1, col, row, col + 1);
        addStateDiagonal(possibleStates, cur_state, row + 1, col + 1, row + 1, col, row, col + 1);
        addStateDiagonal(possibleStates, cur_state, row + 1, col - 1, row + 1, col, row, col - 1);
        return possibleStates;
    }

    /**
     * Adds the state (orthogonal-up,down,right,left) to the states list if it's valid
     * @param possibleStates list of states
     * @param parentState    current MazeState
     * @param row            possible state's row
     * @param col            possible state's col
     */
    private void addStateOrthogonal(ArrayList<AState> possibleStates, MazeState parentState, int row, int col) {
        if (maze.isLegalPosition(row, col)
                && maze.getValue(row, col) == 0)
        {
            //Cost of orthogonal move
            double orthogCost = 10;
            double new_cost = parentState.getCost() + orthogCost;
            Position statePos=new Position(row, col);
            AState new_state = new MazeState(statePos, parentState, new_cost);
            possibleStates.add(new_state);
        }
    }

    /**
     * Adds the state (Diagonals) to the possibleStates list if it's valid
     *  @param possibleStates list of possibleStates
     * @param parentState    current MazeState
     * @param row            possible state's row
     * @param col            possible state's col
     * @param pathARow       first possible path in diagonal to possible state, Row
     * @param pathACol       first possible path in diagonal to possible state, Col
     * @param pathBRow       second possible path in diagonal to possible state, Row
     * @param pathBCol       second possible path in diagonal to possible state, Col
     */
    private void addStateDiagonal(ArrayList<AState> possibleStates, MazeState parentState, int row, int col,
                                  int pathARow, int pathACol, int pathBRow, int pathBCol) {
        if (maze.isLegalPosition(row, col) && maze.getValue(row, col) == 0
                && (maze.getValue(pathARow, pathACol) == 0 || maze.getValue(pathBRow, pathBCol) == 0))
        {
            // Cost of diagonal move
            double diagCost = 15;
            double new_cost = parentState.getCost() + diagCost;
            Position statePos=new Position(row, col);
            AState new_state = new MazeState(statePos, parentState, new_cost);
            possibleStates.add(new_state);
        }
    }
}
