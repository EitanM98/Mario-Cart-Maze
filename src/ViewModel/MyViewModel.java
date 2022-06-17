package ViewModel;

import Model.IModel;
import algorithms.mazeGenerators.Maze;
import javafx.scene.input.KeyEvent;
import javafx.util.Pair;

import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

public class MyViewModel extends Observable implements Observer {

    private IModel model;
    private Maze maze;
    private LinkedList<Pair<Integer,Integer>> solPath;
    private int curRow;
    private int curCol;


    public MyViewModel(IModel model) {
        this.model = model;
        this.model.assignObserver(this);
        this.maze = null;
        this.solPath=null;
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

    public LinkedList<Pair<Integer, Integer>> getSolPath() {
        return solPath;
    }


    //    @Override
//    public void update(Observable o, Object arg) {
//        if(o instanceof IModel)
//        {
//            if(maze == null)//generateMaze
//            {
//                this.maze = model.getMaze();
//            }
//            else {
//                Maze maze = model.getMaze();
//
//                if (maze == this.maze)//Not generateMaze, Move character
//                {
//                    int rowChar = model.getCurRow();
//                    int colChar = model.getCurCol();
//                    if(this.curCol == colChar && this.curRow == rowChar)//Solve Maze
//                    {
//                        model.getSolution();
//                    }
//                    else//Update location
//                    {
//                        this.curRow = rowChar;
//                        this.curCol = colChar;
//                    }
//
//
//                }
//                else//GenerateMaze
//                {
//                    this.maze = maze;
//                }
//            }
//
//            setChanged();
//            notifyObservers();
//        }
//    }

        @Override
    public void update(Observable o, Object arg) {
            if(o instanceof IModel)
            {
                setChanged();
                notifyObservers(arg);
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

    public void movePlayer (KeyEvent keyEvent)
    {
        int direction = switch (keyEvent.getCode()) {
            case NUMPAD8, UP -> 1;
            case NUMPAD2,DOWN -> 2;
            case NUMPAD4,LEFT -> 3;
            case NUMPAD6,RIGHT -> 4;
            case NUMPAD7 -> 5;
            case NUMPAD9 -> 6;
            case NUMPAD3 -> 7;
            case NUMPAD1 -> 8;
            default -> -1;
        };

        model.updatePlayerPosition(direction);
    }

    public void solveMaze(Maze maze)
    {
        model.solveMaze();
    }

    public LinkedList<Pair<Integer,Integer>> getSolution()
    {
        return model.getSolution();
    }

//    public void saveMaze() {
//        this.model.saveMaze();
//    }
//
//    public void loadMaze() {
//        this.model.loadMaze();
//    }
//
//    public void exitProgram() {
//        model.exitProgram();
//    }
}
