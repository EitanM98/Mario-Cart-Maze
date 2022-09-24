package Server;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Server {
    private int port;
    private int listeningIntervalMS;
    private IServerStrategy strategy;
    private volatile boolean stop;
    private ThreadPoolExecutor threadPool;
    private int numOfThreads;

    public Server(int port, int listeningIntervalMS, IServerStrategy strategy) {
        this.port = port;
        this.listeningIntervalMS = listeningIntervalMS;
        this.strategy = strategy;
        try {
            numOfThreads=Integer.parseInt(Configurations.getInstance().getProperty("threadPoolSize"));
        }
        catch (NumberFormatException e){
            numOfThreads=5;//default value
        }
        threadPool=(ThreadPoolExecutor) Executors.newFixedThreadPool(numOfThreads);
    }


    public void stop(){
        stop = true;
    }

    public void start() {
        Thread serverThread=new Thread(()->startServer());
        serverThread.start();
    }


    public void startServer() {
        try {
            stop=false;
            ServerSocket serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(listeningIntervalMS);
            while (!stop) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    threadPool.submit(() -> {
                        handleClient(clientSocket);
                    });
                } catch (SocketTimeoutException e) {
                    System.out.println("Server timed out");;
                }
            }
            serverSocket.close();
            threadPool.shutdownNow();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void handleClient(Socket clientSocket) {
        try {
            strategy.applyStrategy(clientSocket.getInputStream(), clientSocket.getOutputStream());
            clientSocket.close();
//            this.stop(); //Should check if it'll work with multithreaded
        } catch (IOException e){
            e.printStackTrace();
        }
    }

}
