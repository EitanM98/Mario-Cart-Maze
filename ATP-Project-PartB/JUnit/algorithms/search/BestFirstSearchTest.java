package algorithms.search;

import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class BestFirstSearchTest {
    private BestFirstSearch bfs=new BestFirstSearch();



    @Test
    void getName() {
        assertEquals("BestFirstSearch",bfs.getName());
    }

    @Test
    void test4x4Maze(){
        int[][] grid={{0,0,1,1},
                      {1,0,1,0},
                      {1,0,0,0},
                      {1,1,1,0}};
        Maze maze=new Maze(4,4);
        maze.setMaze(grid);
        SearchableMaze searchMaze=new SearchableMaze(maze);
        AState goalState=bfs.search(searchMaze);
        assertEquals(45,goalState.getCost());
        bfs=new BestFirstSearch();
        Solution solution=bfs.solve(searchMaze);
        assertEquals(4,solution.getSolutionPath().size());
    }

    @Test
    void testNoSoloution(){
        int[][] grid={{0,0,1,1},
                      {1,1,1,0},
                      {1,0,0,0},
                      {1,1,1,0}};
        Maze maze=new Maze(4,4);
        maze.setMaze(grid);
        SearchableMaze noSolMaze=new SearchableMaze(maze);
        Solution solution=bfs.solve(noSolMaze);
        assertEquals(null,solution);
    }

    @Test
    void testWrongParameters(){
        //Should generate a default empty maze with 5x5 size and [0,0] as start and [4,4] as end
        SearchableMaze noSolMaze=new SearchableMaze(new Maze(-1,-1));
        AState goalState=bfs.search(noSolMaze);
        assertEquals(60,goalState.getCost());
    }

    @Test
    void testEvaluatedNodes(){
        int[][] grid={{0,0,1,1},
                {1,0,1,1},
                {1,0,0,0},
                {1,1,1,0}};
        Maze maze=new Maze(4,4);
        maze.setMaze(grid);
        SearchableMaze searchMaze=new SearchableMaze(maze);
        bfs.solve(searchMaze);
        assertEquals(6,bfs.getNumberOfNodesEvaluated());
    }
}