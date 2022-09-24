package Server;

import IO.MyCompressorOutputStream;
import algorithms.mazeGenerators.*;

import java.io.*;

public class ServerStrategyGenerateMaze implements IServerStrategy {
    @Override
    public void applyStrategy(InputStream inFromClient, OutputStream outToClient) {
        try {
            ObjectInputStream fromClient = new ObjectInputStream(inFromClient);
            ObjectOutputStream toClient = new ObjectOutputStream(outToClient);
            int[] dimensions=(int[]) fromClient.readObject();
            AMazeGenerator mazeGenerator = getMazeGeneratorProperty();
            Maze maze = mazeGenerator.generate(dimensions[0],dimensions[1]);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            OutputStream compressor = new MyCompressorOutputStream(output);
            compressor.write(maze.toByteArray());
            toClient.writeObject(output.toByteArray());
            compressor.flush();
            compressor.close();
            fromClient.close();
            toClient.flush();
            toClient.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private AMazeGenerator getMazeGeneratorProperty(){
        String value=Configurations.getInstance().getProperty("mazeGeneratingAlgorithm");
        switch (value){
            case "EmptyMazeGenerator":
                return new EmptyMazeGenerator();
            case "SimpleMazeGenerator":
                return new SimpleMazeGenerator();
            default:// MyMazeGenerator is the default Generator
                return new MyMazeGenerator();
        }
    }
}


