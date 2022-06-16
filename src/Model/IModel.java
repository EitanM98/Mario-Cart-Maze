package Model;

import java.util.Observer;

public interface IModel {
    public void generateRandomMaze(int row, int col);
    public int[][] getMaze();
    public void updateCharacterLocation(int direction);
    public int getRowChar();
    public int getColChar();
    public void assignObserver(Observer o);
    public void solveMaze(int [][] maze);
    public void getSolution();
}
