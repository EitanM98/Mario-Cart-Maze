package Model;

import algorithms.mazeGenerators.Maze;
import javafx.util.Pair;

import java.util.LinkedList;
import java.util.Observer;

public interface IModel {
    public void generateRandomMaze(int row, int col);
    public Maze getMaze();
    public int[][] getMazeGrid();
    public void updatePlayerPosition(int direction);
    public int getCurRow();
    public int getCurCol();
    public int getStartRow();
    public int getStartCol();
    public int getGoalRow();
    public int getGoalCol();
    public void assignObserver(Observer o);
    public void solveMaze();
    public int[][] getSolution();
}
