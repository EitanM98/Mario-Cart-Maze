package Model;

import algorithms.mazeGenerators.Maze;
import javafx.util.Pair;

import java.util.LinkedList;
import java.util.Observer;

public interface IModel {
    void generateRandomMaze(int row, int col);
    Maze getMaze();
    int[][] getMazeGrid();
    void updatePlayerPosition(int direction);
    int getCurRow();
    int getCurCol();
    int getStartRow();
    int getStartCol();
    int getGoalRow();
    int getGoalCol();
    void assignObserver(Observer o);
    void solveMaze();
    int[][] getSolution();
    void updateProperties(String mazeGenerator,String searchingAlgorithm);
    void loadMaze();

    void saveMaze();

    void exitGame();
}
