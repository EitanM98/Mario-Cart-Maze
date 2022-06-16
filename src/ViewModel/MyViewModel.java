package ViewModel;

import Model.IModel;
import javafx.scene.input.KeyEvent;

import java.util.Observable;
import java.util.Observer;

public class MyViewModel extends Observable implements Observer {

    private IModel model;
    private int [][] maze;
    private int rowChar;
    private int colChar;


    public MyViewModel(IModel model) {
        this.model = model;
        this.model.assignObserver(this);
        this.maze = null;
    }


    public int[][] getMaze() {
        return maze;
    }


    public int getRowChar() {
        return rowChar;
    }

    public int getColChar() {
        return colChar;
    }

    @Override
    public void update(Observable o, Object arg) {
        if(o instanceof IModel)
        {
            if(maze == null)//generateMaze
            {
                this.maze = model.getMaze();
            }
            else {
                int[][] maze = model.getMaze();

                if (maze == this.maze)//Not generateMaze, Move character
                {
                    int rowChar = model.getRowChar();
                    int colChar = model.getColChar();
                    if(this.colChar == colChar && this.rowChar == rowChar)//Solve Maze
                    {
                        model.getSolution();
                    }
                    else//Update location
                    {
                        this.rowChar = rowChar;
                        this.colChar = colChar;
                    }


                }
                else//GenerateMaze
                {
                    this.maze = maze;
                }
            }

            setChanged();
            notifyObservers();
        }
    }


    public void generateMaze(int row,int col)
    {
        this.model.generateRandomMaze(row,col);
    }

    public void moveCharacter(KeyEvent keyEvent)
    {
        int direction = -1;

        switch (keyEvent.getCode()){
            case UP:
                direction = 1;
                break;
            case DOWN:
                direction = 2;
                break;
            case LEFT:
                direction = 3;
                break;
            case RIGHT:
                direction = 4;
                break;
        }

        model.updateCharacterLocation(direction);
    }

    public void solveMaze(int [][] maze)
    {
        model.solveMaze(maze);
    }

    public void getSolution()
    {
        model.getSolution();
    }
}
