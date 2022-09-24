package algorithms.mazeGenerators;

import java.util.Random;

public class SimpleMazeGenerator extends AMazeGenerator{

    /**
     * @param rows
     * @param cols
     * @return a simple Maze that posses one solution
     */
    @Override
    public Maze generate(int rows, int cols) {
        Maze myMaze = new Maze(rows, cols);
        maze_init(myMaze);
        myMaze.setStart_pos(starting_point_generate(myMaze));
        myMaze.setGoal_pos(goal_point_generate(myMaze));
        // Making sure starting and end points are accessible (0)
        myMaze.setValue(myMaze.getStartPosition(),0);
        myMaze.setValue(myMaze.getGoalPosition(),0);
        generate_path(myMaze);
        return myMaze;
    }

    /**
     * method updates the maze with rows of 0 and rows of 1
     * @param myMaze
     */
    private void maze_init(Maze myMaze){
        int value;
        for(int row=0; row<myMaze.getLength();row++){
            // Odd rows will get 1 value, while Even rows will get 0 value
            value=(row%2==0)?0:1;
            for (int col=0;col< myMaze.getWidth();col++)
                myMaze.getMaze()[row][col] = value;
        }
    }

    /**
     * Method is "Paving" the path to create a possible solution, updating the maze.
     * @param myMaze
     */
    private void generate_path(Maze myMaze){
        Random rnd=new Random();
        // Create one open cell (0) in each row of walls (1)
        for(int row=1;row<myMaze.getLength();row+=2){
            myMaze.getMaze()[row][rnd.nextInt(myMaze.getWidth())]=0;
        }
    }
}
