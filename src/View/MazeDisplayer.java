package View;

import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.util.Pair;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;

public class MazeDisplayer extends Canvas {

    private Maze maze;
    private int row_player =0;
    private int col_player =0;
    private Position start;
    private Position goal;
    private boolean isSolved=false;
    private boolean showSol=false;
    private LinkedList<Pair<Integer,Integer>> solution;
    StringProperty imageFileNameWall = new SimpleStringProperty();
    StringProperty imageFileNamePlayer = new SimpleStringProperty();
    StringProperty imageFileNameStart = new SimpleStringProperty();
    StringProperty imageFileNameGoal = new SimpleStringProperty();



    public boolean isSolved() {
        return isSolved;
    }

    public void setSolved(boolean solved) {
        isSolved = solved;
    }

    public boolean isShowSol() {
        return showSol;
    }

    public void setShowSol(boolean showSol) {
        this.showSol = showSol;
    }

    public LinkedList<Pair<Integer,Integer>> getSolution() {
        return solution;
    }

    public void setStart(Position start) {
        this.start = start;
    }

    public void setGoal(Position goal) {
        this.goal = goal;
    }

    public void setSolution(LinkedList<Pair<Integer,Integer>> solution) {
        this.solution = solution;
    }

    public String getImageFileNameStart() {
        return imageFileNameStart.get();
    }

    public void setImageFileNameStart(String imageFileNameStart) {
        this.imageFileNameStart.set(imageFileNameStart);
    }

    public String getImageFileNameGoal() {
        return imageFileNameGoal.get();
    }

    public void setImageFileNameGoal(String imageFileNameGoal) {
        this.imageFileNameGoal.set(imageFileNameGoal);
    }

    public String getImageFileNameWall() {
        return imageFileNameWall.get();
    }

    public void setImageFileNameWall(String imageFileNameWall) {
        this.imageFileNameWall.set(imageFileNameWall);
    }

    public String getImageFileNamePlayer() {
        return imageFileNamePlayer.get();
    }

    public void setImageFileNamePlayer(String imageFileNamePlayer) {
        this.imageFileNamePlayer.set(imageFileNamePlayer);
    }

    public int getRow_player() {
        return row_player;
    }

    public int getCol_player() {
        return col_player;
    }

    public void set_player_position(int row, int col){
        this.row_player = row;
        this.col_player = col;
        draw();

    }


    public void drawMaze(Maze maze)
    {
        if(maze!=null) {
            this.maze = maze;
            this.start = maze.getStartPosition();
            this.goal = maze.getGoalPosition();
            row_player=start.getRowIndex();
            col_player=start.getColumnIndex();
            draw();
        }
    }

    public void draw()
    {
        if( maze!=null)
        {
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            int row = maze.getLength();
            int col = maze.getWidth();
            double cellHeight = canvasHeight/row;
            double cellWidth = canvasWidth/col;
            GraphicsContext graphicsContext = getGraphicsContext2D();
            graphicsContext.clearRect(0,0,canvasWidth,canvasHeight);
            drawStartEnd(graphicsContext,cellHeight,cellWidth);
            graphicsContext.setFill(Color.RED);
            double w,h;
            //Draw Maze
            Image wallImage = null;
            try {
                wallImage = new Image(new FileInputStream(getImageFileNameWall()));
            } catch (FileNotFoundException e) {
                System.out.println("There is no file....");
            }
            int[][] mazeGrid=maze.getMaze();
            for(int i=0;i<row;i++)
            {
                for(int j=0;j<col;j++)
                {
                    if(mazeGrid[i][j] == 1) // Wall
                    {
                        h = i * cellHeight;
                        w = j * cellWidth;
                        if (wallImage == null){
                            graphicsContext.fillRect(w,h,cellWidth,cellHeight);
                        }else{
                            graphicsContext.drawImage(wallImage,w,h,cellWidth,cellHeight);
                        }
                    }

                }
            }

            double h_player = getRow_player() * cellHeight;
            double w_player = getCol_player() * cellWidth;
            Image playerImage = null;
            try {
                playerImage = new Image(new FileInputStream(getImageFileNamePlayer()));
            } catch (FileNotFoundException e) {
                System.out.println("There is no Image player....");
            }
            graphicsContext.drawImage(playerImage,w_player,h_player,cellWidth,cellHeight);

        }
    }

    private void drawStartEnd(GraphicsContext graphicsContext,double cellHeight,double cellWidth){
        if(start!=null &&goal!=null) {

            Image startImage = null;
            try {
                startImage = new Image(new FileInputStream(getImageFileNameStart()));
            } catch (FileNotFoundException e) {
                System.out.println("There is no Image for start....");
            }
            graphicsContext.drawImage(startImage,start.getColumnIndex() * cellWidth,
                    start.getRowIndex() * cellHeight, cellWidth, cellHeight);
            Image goalImage = null;
            try {
                goalImage = new Image(new FileInputStream(getImageFileNameGoal()));
            } catch (FileNotFoundException e) {
                System.out.println("There is no Image for goal position....");
            }
            graphicsContext.drawImage(goalImage, goal.getColumnIndex() * cellWidth,
                    goal.getRowIndex() * cellHeight, cellWidth, cellHeight);
        }
    }
}
