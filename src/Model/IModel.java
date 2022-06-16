package Model;

import algorithms.mazeGenerators.Maze;

import java.util.Observer;

public interface IModel {
    public void generateRandomMaze(int row, int col);
    public Maze getMaze();
    public void updateCharacterLocation(int direction);
    public int getCurRow();
    public int getCurCol();
    public void assignObserver(Observer o);
    public void solveMaze(Maze maze);
    public void getSolution();
}
