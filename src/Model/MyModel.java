package Model;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

import View.MyViewController;
import algorithms.search.*;
import Server.Server;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import Server.*;
import Client.*;
import IO.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;


public class MyModel extends Observable implements IModel{
    private Maze maze;
    private Position startPos;
    private Position goalPos;
    private int curRow;
    private int curCol;
    private Solution solution;
    private Server mazeGeneratingServer;
    private Server solveSearchProblemServer;

    private static final Logger LOG = LogManager.getLogger();


    public MyModel() {
        maze = null;
        solution=null;
        curRow =0;
        curCol =0;
        mazeGeneratingServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
        solveSearchProblemServer = new Server(5401,1000, new ServerStrategySolveSearchProblem());
        mazeGeneratingServer.start();
        solveSearchProblemServer.start();
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


    /**
     * each row in the array is [row_number,col_number] of each step in the solution path
     * @return maze solution as 2 dimensional array
     */
    @Override
    public int[][] getSolution() {
        ArrayList<AState> mazeSolution = solution.getSolutionPath();
        int[][] solutionPath=new int[mazeSolution.size()][2];
        for (int i = 0; i < mazeSolution.size(); i++) {
            AState state=mazeSolution.get(i);
            if(state instanceof MazeState){
                solutionPath[i][0]=((MazeState) state).getPosition().getRowIndex();
                solutionPath[i][1]=((MazeState) state).getPosition().getColumnIndex();
            }
        }
        return solutionPath;
    }

    @Override
    public void loadMaze() {
        FileChooser fileWindow=new FileChooser();
        fileWindow.setTitle("Load Mario Maze");
        fileWindow.setInitialFileName("myMaze.mk_maze");
        fileWindow.getExtensionFilters().add( new FileChooser.ExtensionFilter("Maze Files (*.maze)","*.maze"));
        fileWindow.setInitialDirectory(new File("./src/resources"));
        File file = fileWindow.showOpenDialog(new Stage());
        if (file != null) {
            try {
                ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file));
                Maze maze = (Maze) objectInputStream.readObject();
                objectInputStream.close();
                this.maze = maze;
                startPos = maze.getStartPosition();
                goalPos = maze.getGoalPosition();
                curCol=startPos.getColumnIndex();
                curRow=startPos.getRowIndex();
                this.setChanged();
                this.notifyObservers("Maze loaded");
            } catch (Exception e){
                LOG.error("Maze loading failed");
                MyViewController.showErrorAlert("Maze loading failed \n Please check your file","Failed loading");
            }
        }
    }


    @Override
    public void saveMaze() {
        FileChooser fileWindow = new FileChooser();
        fileWindow.setTitle("Save Mario Maze");
        fileWindow.setInitialFileName("myMaze.maze");
        fileWindow.setInitialDirectory(new File("./src/resources"));
        fileWindow.getExtensionFilters().add( new FileChooser.ExtensionFilter("Maze Files","*.maze"));
        File mazeFile = fileWindow.showSaveDialog(null);
        if (mazeFile != null) {
            try {
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(mazeFile));
                objectOutputStream.writeObject(this.maze);
                LOG.info("Maze was successfully saved");
                MyViewController.showInfoAlert("Successfully saved","Saved");
            } catch (IOException e) {
                LOG.error("Failed saving the maze");
                MyViewController.showErrorAlert("Wrong file format\n couldn't save the maze ","Saving Error");
            }
        }

    }

    @Override
    public void exitGame() {
        mazeGeneratingServer.stop();
        solveSearchProblemServer.stop();
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
            LOG.info("Maze with "+rows+" rows and "+cols+" cols was successfully generated for "+InetAddress.getLocalHost()+ "address");
        } catch (UnknownHostException e) {
            LOG.debug("Maze couldn't be generated");
            e.printStackTrace();
        }
        setChanged();
        notifyObservers("Maze generated");
    }

    @Override
    public void movePlayerByMouse(double newX, double newY, double prevX, double prevY, double mazeDisplayerCellWidth, double mazeDisplayerCellHeight) {
        int direction=0;
        boolean right = false,left=false,up=false,down=false;
        if(Math.abs(newX-prevX)>=mazeDisplayerCellWidth/2){
            if(newX>prevX)
                right=true;
            else
                left=true;
        }
        if(Math.abs(newY-prevY)>=mazeDisplayerCellHeight/2){
            if(newY>prevY)
                down=true;
            else
                up=true;
        }
        if(right) {
            if (up)
                direction = 6;
            else if (down)
                direction = 7;
            else
                direction=4;
        } else if(left){
            if (up)
                direction = 5;
            else if (down)
                direction = 8;
            else
                direction=3;
        } else if (up){
            direction=1;
        }else if(down){
            direction=2;
        }
        updatePlayerPosition(direction);
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
                if(curRow>0)
                    newRow=curRow-1;
                break;

            case 2: //Down
                if(curRow<maze.getLength()-1)
                    newRow=curRow+1;
                break;
            case 3: //Left
                if(curCol>0)
                    newCol=curCol-1;
                break;
            case 4: //Right
                if(curCol<maze.getWidth()-1)
                    newCol=curCol+1;
                break;
            case 5://Left-top
                if(curCol>0 && curRow>0)
                    if(maze.getValue(curRow,curCol-1)==0 || maze.getValue(curRow-1,curCol)==0){
                        newCol=curCol-1;
                        newRow=curRow-1;
                    }
                break;
            case 6://Right-top
                if(curRow>0 && curCol<maze.getWidth()-1)
                    if(maze.getValue(curRow,curCol+1)==0 || maze.getValue(curRow-1,curCol)==0){
                        newCol=curCol+1;
                        newRow=curRow-1;
                    }
                break;
            case 7: // Right-bottom
                if(curRow<maze.getLength()-1 && curCol<maze.getWidth()-1)
                    if(maze.getValue(curRow,curCol+1)==0 || maze.getValue(curRow+1,curCol)==0){
                        newCol=curCol+1;
                        newRow=curRow+1;
                    }
                break;
            case 8://Left-Bottom
                if(curCol>0 && curRow<maze.getLength()-1 )
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
            notifyObservers("Invalid move");
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
                        } catch (Exception e) {
                            LOG.error("Error solving while solving a maze");
                            e.printStackTrace();
                        }
                    }
                });
                client.communicateWithServer();
                LOG.info("A maze was solved for "+InetAddress.getLocalHost()+" the solution has "+solution.getSolutionPath().size()+" moves.");
            } catch (UnknownHostException e) {
                LOG.error("Error solving while solving a maze");
                e.printStackTrace();
            }
            setChanged();
            notifyObservers("Maze solved");
        }
    }

    public void updateProperties(String mazeGenerator,String searchingAlgorithm){
        Configurations config=Configurations.getInstance();
        if(mazeGenerator.compareTo("Simple Generator")==0)
            mazeGenerator="SimpleMazeGenerator";
        else
            mazeGenerator="MyMazeGenerator";
        switch (searchingAlgorithm) {
            case "Depth First Search" -> searchingAlgorithm = "DepthFirstSearch";
            case "Breadth First Search" -> searchingAlgorithm = "BreadthFirstSearch";
            //We have chosen BestFirstSearch to be default algorithm
            default -> searchingAlgorithm = "BestFirstSearch";
        }
        config.AddOrUpdateProperty("mazeGeneratingAlgorithm",mazeGenerator);
        config.AddOrUpdateProperty("mazeSearchingAlgorithm",searchingAlgorithm);
        restartSevers();
        setChanged();
        notifyObservers("Settings Changed");
    }

    private void restartSevers() {
        mazeGeneratingServer.stop();
        solveSearchProblemServer.stop();
        try {
            Thread.sleep(1000);
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
        mazeGeneratingServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
        solveSearchProblemServer = new Server(5401,1000, new ServerStrategySolveSearchProblem());
        solveSearchProblemServer.start();
        mazeGeneratingServer.start();
        startPos=null;
        goalPos=null;
        maze = null;
        solution=null;
        curRow = 0;
        curCol=0;
    }

}
