package algorithms.mazeGenerators;

import java.util.Random;

public abstract class AMazeGenerator implements IMazeGenerator{

    /**
     * Method to measure in milliseconds how much time it took to generate the maze
     * @param rows
     * @param cols
     * @return time taken in milliseconds
     */
    @Override
    public long measureAlgorithmTimeMillis(int rows, int cols) {
        long start_time=System.currentTimeMillis();
        generate(rows,cols);
        long end_time=System.currentTimeMillis();
        return end_time-start_time;
    }

    /**
     * @param maze
     * @return a random starting position on one of the edges, different from end position
     */
    protected Position starting_point_generate(Maze maze) {
        Random rand=new Random();
        Position start_pos=new Position(0,0);
        //Keep generating positions if the starting position is the same as goal position
        do{
            int edge=rand.nextInt(4);
            Edge starting_edge=Edge.values()[edge];
            switch (starting_edge) {
                case Top -> start_pos.setCol(rand.nextInt(maze.getWidth()));
                case Left -> start_pos.setRow(rand.nextInt(maze.getLength()));
                case Right -> {
                    start_pos.setRow(rand.nextInt(maze.getLength()));
                    start_pos.setCol(maze.getWidth() - 1);
                }
                case Bottom -> {
                    start_pos.setRow(maze.getLength() - 1);
                    start_pos.setCol(rand.nextInt(maze.getWidth()));
                }
            }
        }while(start_pos.equals(maze.getGoalPosition()));
        return start_pos;
    }

    /**
     * @param maze
     * @return a random end position on one of the edges, different from start position
     */
    protected Position goal_point_generate(Maze maze) {
        Position goal_point=starting_point_generate(maze);
        //Keep generating positions if the starting position is the same as goal position
        while(goal_point.equals(maze.getStartPosition()))
            goal_point=starting_point_generate(maze);
        return goal_point;
    }
}
