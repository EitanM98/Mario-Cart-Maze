package algorithms.mazeGenerators;

import java.util.Random;

public class EmptyMazeGenerator extends AMazeGenerator{

    /**
     * @param rows
     * @param cols
     * @return Maze with no walls only zero values
     */
    @Override
    public Maze generate(int rows, int cols) {
        Maze maze= new Maze(rows,cols);
        maze.setStart_pos(starting_point_generate(maze));
        maze.setGoal_pos(goal_point_generate(maze));
        return maze;
    }
}
