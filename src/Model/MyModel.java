package Model;

import java.util.Observable;
import java.util.Observer;

import algorithms.mazeGenerators.AMazeGenerator;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.MyMazeGenerator;

public class MyModel extends Observable implements IModel{
    private AMazeGenerator mazeGenerator;
    private Maze maze;
    private int curRow;
    private int curCol;


    public MyModel() {
        mazeGenerator=new MyMazeGenerator();
        maze = null;
        curRow =0;
        curCol =0;
    }

    public void updateCharacterLocation(int direction)
    {
        /*
            direction = 1 -> Up
            direction = 2 -> Down
            direction = 3 -> Left
            direction = 4 -> Right
            direction = 5 -> Up-Left
            direction = 6 -> Up-Right
            direction = 7 -> Down-Right
            direction = 8 -> Down-Left
         */
        int newCol=curCol;
        int newRow=curRow;
        switch(direction)
        {
            case 1: //Up
                if(curRow!=0)
                    newRow=curRow-1;
                break;

            case 2: //Down
                if(curRow!=maze.getLength()-1)
                    newRow=curRow+1;
                break;
            case 3: //Left
                if(curCol!=0)
                    newCol=curCol-1;
                break;
            case 4: //Right
                if(curCol!=maze.getWidth()-1)
                    newCol=curCol+1;
                break;
            case 5://Left-top
                if(curCol!=0 && curRow!=0)
                    if(maze.getValue(curRow,curCol-1)==0 || maze.getValue(curRow-1,curCol)==0){
                        newCol=curCol-1;
                        newRow=curRow-1;
                }
                break;
            case 6://Right-top
                if(curRow!=0 && curCol!=maze.getWidth()-1)
                    if(maze.getValue(curRow,curCol+1)==0 || maze.getValue(curRow-1,curCol)==0){
                        newCol=curCol+1;
                        newRow=curRow-1;
                }
                break;
            case 7: // Right-bottom
                if(curRow!=maze.getLength()-1 && curCol!=maze.getWidth()-1)
                    if(maze.getValue(curRow,curCol+1)==0 || maze.getValue(curRow+1,curCol)==0){
                        newCol=curCol+1;
                        newRow=curRow+1;
                }
                break;
            case 8://Left-Bottom
                if(curCol!=0 && curRow!=maze.getLength()-1 )
                    if(maze.getValue(curRow,curCol-1)==0 || maze.getValue(curRow+1,curCol)==0){
                        newCol=curCol-1;
                        newRow=curRow+1;
                    }
                break;
            default:
                newCol=curCol;
                newRow=curRow;
        }
        if((newCol!=curCol || newRow!=curRow)&& maze.getValue(newRow,newCol)==0) // Position changed and Legal move
            {
                curRow=newRow;
                curCol=newCol;
                setChanged();
                notifyObservers();
            }
    }


    public int getCurRow() {
        return curRow;
    }

    public int getCurCol() {
        return curCol;
    }

    @Override
    public void assignObserver(Observer o) {
        this.addObserver(o);
    }

    @Override
    public void solveMaze(Maze maze) {
        //Solving maze
        setChanged();
        notifyObservers();
    }

    @Override
    public void getSolution() {
        //return this.solution;
    }


    public void generateRandomMaze(int row, int col)
    {
        Maze newMaze=mazeGenerator.generate(row,col);
        this.maze = newMaze;
        this.curRow=maze.getStartPosition().getRowIndex();
        this.curCol=maze.getStartPosition().getColumnIndex();
        setChanged();
        notifyObservers();
    }

    public Maze getMaze() {
        return maze;
    }
}
