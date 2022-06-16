package ViewModel;

import Model.IModel;
import algorithms.mazeGenerators.Maze;
import javafx.scene.input.KeyEvent;

import java.util.Observable;
import java.util.Observer;

public class MyViewModel extends Observable implements Observer {

    private IModel model;
    private Maze maze;
    private int curRow;
    private int curCol;


    public MyViewModel(IModel model) {
        this.model = model;
        this.model.assignObserver(this);
        this.maze = null;
    }


    public Maze getMaze() {
        return maze;
    }


    public int getCurRow() {
        return curRow;
    }

    public int getCurCol() {
        return curCol;
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
                Maze maze = model.getMaze();

                if (maze == this.maze)//Not generateMaze, Move character
                {
                    int rowChar = model.getCurRow();
                    int colChar = model.getCurCol();
                    if(this.curCol == colChar && this.curRow == rowChar)//Solve Maze
                    {
                        model.getSolution();
                    }
                    else//Update location
                    {
                        this.curRow = rowChar;
                        this.curCol = colChar;
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


    public void generateMaze(String row_fromUser,String col_fromUser) throws Exception {
        int rows=0;
        int cols=0;
        String message="";
        try {
            rows = Integer.parseInt(row_fromUser);
            cols = Integer.parseInt(col_fromUser);
            if(rows<=0 || cols<=0)
                message="Quantity of rows and cols must be positive integer,\n Please try again";
        } catch (NumberFormatException e) {
            message="Only numeric characters allowed";
        }
        if(!message.equals("")){
            throw new Exception(message);
        }
        this.model.generateRandomMaze(rows,cols);
    }

    public void moveCharacter(KeyEvent keyEvent)
    {
        int direction = -1;

        switch (keyEvent.getCode()){
            case NUMPAD8:
                direction = 1;
                break;
            case NUMPAD2:
                direction = 2;
                break;
            case NUMPAD4:
                direction = 3;
                break;
            case NUMPAD6:
                direction = 4;
                break;
            case NUMPAD7:
                direction=5;
                break;
            case NUMPAD9:
                direction=6;
                break;
            case NUMPAD3:
                direction=7;
                break;
            case NUMPAD1:
                direction=8;
                break;

        }

        model.updateCharacterLocation(direction);
    }

    public void solveMaze(Maze maze)
    {
        model.solveMaze(maze);
    }

    public void getSolution()
    {
        model.getSolution();
    }
}
