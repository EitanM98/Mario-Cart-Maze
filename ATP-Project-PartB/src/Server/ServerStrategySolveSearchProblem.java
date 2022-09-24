package Server;

import algorithms.mazeGenerators.*;
import algorithms.search.*;

import java.io.*;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


public class ServerStrategySolveSearchProblem implements IServerStrategy {
    private String solutionDirPath;
    private volatile static AtomicInteger solutionId;
    private ConcurrentHashMap<String ,String> mazeMap;

    public ServerStrategySolveSearchProblem() {
        solutionDirPath= System.getProperty("java.io.tmpdir");
        mazeMap=new ConcurrentHashMap<>();
        solutionId=new AtomicInteger(1);
    }

    public ConcurrentHashMap<String, String> getMazeMap() {
        return mazeMap;
    }

    public String getTempDirectoryPath() {
        return solutionDirPath;
    }

    /**
     * @param inFromClient gets from the client a Maze to solve
     * @param outToClient Writes to the clients OutputStream the Solution of the maze
     */
    @Override
    public void applyStrategy(InputStream inFromClient, OutputStream outToClient) {
        try {
            ObjectInputStream fromClient = new ObjectInputStream(inFromClient);
            ObjectOutputStream toClient = new ObjectOutputStream(outToClient);
            try {
                Maze maze=(Maze) fromClient.readObject();
                String solFile=checkIfSolved(maze);
                Solution solution;
                if(solFile!=null){
                    String solPath=solutionDirPath+"\\"+solFile;
                    ObjectInputStream ois=new ObjectInputStream(new FileInputStream(solPath));
                    solution=(Solution)ois.readObject();
                }
                else {
                    SearchableMaze searchableMaze = new SearchableMaze(maze);
                    ISearchingAlgorithm searcher=getMazeSearchingAlgorithmProp();
                    solution = searcher.solve(searchableMaze);
                    saveSolution(maze,solution);
                }
                toClient.writeObject(solution);
                toClient.flush();
            }catch (ClassNotFoundException e){
                e.printStackTrace();
            }
            finally {
                fromClient.close();
                toClient.close();
                inFromClient.close();
                outToClient.close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ASearchingAlgorithm getMazeSearchingAlgorithmProp(){
        String value=Configurations.getInstance().getProperty("mazeSearchingAlgorithm");
        switch (value){
            case "DepthFirstSearch":
                return new DepthFirstSearch();
            case "BreadthFirstSearch":
                return new BreadthFirstSearch();
            default://We have chosen BestFirstSearch to be default algorithm
                return new BestFirstSearch();
        }
    }

    /**
     * @param clientMaze gets Maze to solve from the client. The ConcurrentHashMap is ThreadSafe.
     * @return returns the file name that contains the solution if found, null otherwise
     */
    private String checkIfSolved(Maze clientMaze){
            return mazeMap.get(Arrays.toString(clientMaze.toByteArray()));
    }

    /**
     * Method add the new maze and its solution to the hashmap and creates a separate file for each
     * in the solution Directory. The ConcurrentHashMap is ThreadSafe.
     * @param clientMaze gets the Maze that was solved
     * @param sol gets the maze's Solution
     */
    private void saveSolution(Maze clientMaze,Solution sol){
            addMazeToDB(clientMaze);
            addSolToDB(sol);
            solutionId.incrementAndGet();
    }

    /**
     * Method adds the maze to the hashMap and creates a file in the solution directory.
     * @param clientMaze a Maze to add to the solutions DB
     */
    private void addMazeToDB(Maze clientMaze){
        String value="Sol"+solutionId;
        String key=Arrays.toString(clientMaze.toByteArray());
        mazeMap.put(key,value);
        //Writing the maze to a file named Maze<solId>
        String mazePath=solutionDirPath+"\\Maze"+solutionId;
        writeObjectToFile(clientMaze,mazePath);
    }

    /**
     * Method adds the solution to the hashMap and creates a file in the solution directory.
     * @param sol a Solution to add to the solutions DB
     */
    private void addSolToDB(Solution sol){
        String solPath=solutionDirPath+"\\Sol"+solutionId;
        writeObjectToFile(sol,solPath);
    }

    /**
     * Helper method to write an object to a file
     * @param obj Object
     * @param filePath String
     */
    private void writeObjectToFile(Object obj,String filePath){
        try {
            FileOutputStream file=new FileOutputStream(filePath);
            ObjectOutputStream oos=new ObjectOutputStream(file);
            oos.writeObject(obj);
            file.close();
            oos.flush();
            oos.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

}

