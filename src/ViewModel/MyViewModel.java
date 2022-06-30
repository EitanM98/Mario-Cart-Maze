package ViewModel;

import Model.IModel;
import View.MyViewController;
import algorithms.mazeGenerators.Maze;
import javafx.scene.input.KeyEvent;
import java.util.Observable;
import java.util.Observer;

// The mediator between the Model and the View. Observes the model.
public class MyViewModel extends Observable implements Observer {

    private final IModel model;
    private Maze maze;
    private int[][] solPath;
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

    public int[][] getSolPath() {
        return solPath;
    }

    public void setMaze(Maze maze) {
        this.maze = maze;
    }

    public void setCurPosition(int curRow,int curCol) {
        this.curRow = curRow;
        this.curCol=curCol;
    }

    public void setSolPath(int[][] solPath) {
        this.solPath = solPath;
    }

//    The model has changed, the ViewModel needs to be updated
    @Override
    public void update(Observable o, Object arg) {
            if(o instanceof IModel) {
                String event = (String) arg;
                switch (event) {
                    case "Maze generated" , "Maze loaded" -> mazeGenerated();
                    case "Player moved" -> playerMoved();
                    case "Maze solved" -> mazeSolved();
                    case "Settings Changed"->settingsChanged();
                }
//                Updates the listeners (ViewController)
                setChanged();
                notifyObservers(event);
            }
    }

    private void settingsChanged() {
        this.maze=null;
        this.setSolPath(null);
    }


    private void mazeGenerated() {
        this.maze=model.getMaze();
        this.setCurPosition(maze.getStartPosition().getRowIndex(),maze.getStartPosition().getColumnIndex());
        this.model.solveMaze();
    }


    private void mazeSolved() {
        this.setSolPath(model.getSolution());
    }

    private void playerMoved() {
        this.setCurPosition(model.getCurRow(), model.getCurCol());
    }

    public boolean isSolved(){
        return this.getCurRow() == maze.getGoalPosition().getRowIndex() &&
                this.getCurCol() == maze.getGoalPosition().getColumnIndex();
    }

//    Validate input and forwards the data to the model
    public void generateMaze(String row_fromUser,String col_fromUser) {
        int rows=0;
        int cols=0;
        String message="";
        try {
            rows = Integer.parseInt(row_fromUser);
            cols = Integer.parseInt(col_fromUser);
            if(rows<=0 || cols<=0)
                message="Quantity of rows and cols must be positive integer,\n Please try again";
            if(rows>1000 || cols>1000)
                message="Too big maze !\nMaximum number of rows/cols is 1000";
        } catch (NumberFormatException e) {
            message="Only numeric characters allowed";
        }
        if(!message.equals("")){
            MyViewController.showErrorAlert(message,"Invalid Parameters");
        }
        else{
            this.model.generateRandomMaze(rows,cols);
        }
    }

//    Forwards to the model the keyEvent that happened after validation
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

//    Forwards to the model a player move by dragging the mouse
    public void movePlayer(double newX,double newY,double prevX,double prevY,
                           double mazeDisplayerCellWidth,double mazeDisplayerCellHeight){

        model.movePlayerByMouse(newX,newY,prevX,prevY,mazeDisplayerCellWidth,mazeDisplayerCellHeight);
    }


    public void solveMaze()
    {
        model.solveMaze();
    }

    public int[][] getSolution()
    {
        return model.getSolution();
    }

    public void loadMaze() {
        model.loadMaze();
    }

    public void saveMaze() {
        if(maze==null)
            MyViewController.showErrorAlert("Maze is empty\n Please generate a maze first ","Saving Error");
        else
            model.saveMaze();
    }

    public void exitGame() {
        model.exitGame();
    }

    public void updateSettings(String generatingAlgorithm, String searchingAlgorithm) {
        model.updateProperties(generatingAlgorithm,searchingAlgorithm);
    }
}
