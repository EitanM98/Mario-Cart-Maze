package Model;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

import algorithms.search.AState;
import algorithms.search.MazeState;
import javafx.util.Pair;
import Server.Server;
import algorithms.mazeGenerators.AMazeGenerator;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.MyMazeGenerator;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import Server.*;
import Client.*;
import IO.*;

public class MyModel extends Observable implements IModel{
    private Maze maze;
    private Position startPos;
    private Position goalPos;
    private int curRow;
    private int curCol;
    private Solution solution;
    private Server mazeGeneratingServer;
    private Server solveSearchProblemServer;

    public MyModel() {
        maze = null;
        solution=null;
        curRow =0;
        curCol =0;
        mazeGeneratingServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
        solveSearchProblemServer = new Server(5401,1000, new ServerStrategySolveSearchProblem());
        solveSearchProblemServer.start();
        mazeGeneratingServer.start();
    }

    public int getCurRow() {
        return curRow;
    }

    public int getCurCol() {
        return curCol;
    }

    public int getStartRow(){
        return startPos.getRowIndex();
    }

    public int getStartCol(){
        return startPos.getColumnIndex();
    }

    public int getGoalRow(){
        return goalPos.getRowIndex();
    }

    public int getGoalCol(){
        return goalPos.getColumnIndex();
    }

    @Override
    public void assignObserver(Observer o) {
        this.addObserver(o);
    }


    @Override
    public LinkedList<Pair<Integer,Integer>> getSolution() {
        ArrayList<AState> mazeSolution = solution.getSolutionPath();
        LinkedList<Pair<Integer,Integer>> solutionPath=new LinkedList<>();
        for (int i = 0; i < mazeSolution.size(); i++) {
            AState state=mazeSolution.get(i);
            if(state instanceof MazeState){
                Integer row=(((MazeState) state).getPosition().getRowIndex());
                Integer col=(((MazeState) state).getPosition().getColumnIndex());
                Pair <Integer,Integer> position=new Pair(row,col);
                solutionPath.add(position);
            }
        }
        return solutionPath;
    }

    public Maze getMaze() {
        return maze;
    }

    public int[][] getMazeGrid() {
        return maze.getMaze();
    }


    public void generateRandomMaze(int rows, int cols)
    {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5400, new IClientStrategy() {
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        toServer.writeObject(new int[]{rows, cols});
                        toServer.flush();
                        byte[] compressedMaze = (byte[]) fromServer.readObject();
                        InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                        byte[] decompressedMaze = new byte[((rows * cols) + 18) ];
                        is.read(decompressedMaze);
                        maze = new Maze(decompressedMaze);
                        startPos = maze.getStartPosition();
                        goalPos = maze.getGoalPosition();
                        curCol=startPos.getColumnIndex();
                        curRow=startPos.getRowIndex();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            client.communicateWithServer();
//            initMazeTable();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        setChanged();
        notifyObservers("maze generated");
    }

    public void updatePlayerPosition(int direction)
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
            notifyObservers("Player moved");
        }
        else{
            setChanged();
            notifyObservers("Illegal move");
        }
    }

    @Override
    public void solveMaze() {
        if (this.maze != null) {
            try {
                Client client = new Client(InetAddress.getLocalHost(), 5401, new IClientStrategy() {
                    public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                        try {
                            ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                            ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                            toServer.flush();
                            toServer.writeObject(maze);
                            toServer.flush();
                            solution = (Solution) fromServer.readObject();
                            System.out.println(String.format("Solution steps: %s", solution));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                client.communicateWithServer();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            setChanged();
            notifyObservers("Maze solved");
        }
    }







}
