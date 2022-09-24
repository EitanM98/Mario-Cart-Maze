package algorithms.mazeGenerators;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MazeTest {

    @Test
    void toByteArray() {
        int[][] grid={{0,0,1,1},
                {1,0,1,1},
                {1,0,0,0},
                {1,1,1,0}};
        Maze maze=new Maze(4,4);
        maze.setMaze(grid);
        maze.setStart_pos(new Position(0,1));
        maze.setGoal_pos(new Position(2,3));
        byte[] bytes=maze.toByteArray();
        Maze newMaze=new Maze(bytes);
        assert(maze.getStartPosition().equals(newMaze.getStartPosition()));
        assert (maze.getGoalPosition().equals(newMaze.getGoalPosition()));
        for(int i=0;i<grid.length;i++)
            for(int j=0;j< grid[0].length;j++){
                assertEquals(grid[i][j],newMaze.getValue(i,j));
            }
    }
}