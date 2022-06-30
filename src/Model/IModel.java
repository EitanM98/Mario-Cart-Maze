package Model;

import algorithms.mazeGenerators.Maze;
import java.util.Observer;

public interface IModel {
    void generateRandomMaze(int row, int col);
    Maze getMaze();
    void updatePlayerPosition(int direction);
    int getCurRow();
    int getCurCol();
    void assignObserver(Observer o);
    void solveMaze();
    int[][] getSolution();
    void updateProperties(String mazeGenerator,String searchingAlgorithm);
    void loadMaze();

    void saveMaze();

    void exitGame();

    void movePlayerByMouse(double newX, double newY, double prevX, double prevY, double mazeDisplayerCellWidth, double mazeDisplayerCellHeight);
}
