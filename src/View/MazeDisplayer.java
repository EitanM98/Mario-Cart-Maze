package View;

import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

// Canvas Controller to hold the game's board
public class MazeDisplayer extends Canvas {

//    Maze details
    private Maze maze;
    private int row_player = 0;
    private int col_player = 0;
    private Position start;
    private Position goal;
    private int[][] solution;

    private boolean showSol = false;
    private boolean hintRequested;
    private int hint_index;
//    Default character
    private static String mazeCharacter = "Mario";
//    Canvas dimensions
    private double cellHeight;
    private double cellWidth;
//    Images sources
    StringProperty imageFileNameWall = new SimpleStringProperty("./src/resources/Images/woodWall.jpg");
    StringProperty imageFileNamePlayer = new SimpleStringProperty("src/resources/Images/Mario.png");
    StringProperty imageFileNameStart = new SimpleStringProperty("src/resources/Images/Start-position.jpg");
    StringProperty imageFileNameGoal = new SimpleStringProperty("src/resources/Images/goal-position.jpg");
    StringProperty imageFileNameSoloutionStep = new SimpleStringProperty("src/resources/Images/Banana.png");

    public static String getMazeCharacter() {
        return mazeCharacter;
    }

    public void setCellHeight(double cellHeight) {
        this.cellHeight = cellHeight;
    }

    public void setCellWidth(double cellWidth) {
        this.cellWidth = cellWidth;
    }

    public boolean isHintRequested() {
        return hintRequested;
    }

    public boolean isShowSol() {
        return showSol;
    }

    public void setShowSol(boolean showSol) {
        this.showSol = showSol;
    }

    public int[][] getSolution() {
        return solution;
    }

    public void setStart(Position start) {
        this.start = start;
    }

    public void setGoal(Position goal) {
        this.goal = goal;
    }

    public void setSolution(int[][] solution) {
        this.solution = solution;
    }

    public void hideSolution() {
        this.setShowSol(false);
        this.hintRequested = false;
        draw();
    }

    public void showSolution() {
        this.setShowSol(true);
        this.hintRequested = false;
        draw();
    }


    public String getImageFileNameStart() {
        return imageFileNameStart.get();
    }

    public String getImageFileNameGoal() {
        return imageFileNameGoal.get();
    }

    public String getImageFileNameSoloutionStep() {
        return imageFileNameSoloutionStep.get();
    }

    public String getImageFileNameWall() {
        return imageFileNameWall.get();
    }

    public String getImageFileNamePlayer() {
        return imageFileNamePlayer.get();
    }

    public int getRow_player() {
        return row_player;
    }

    public int getCol_player() {
        return col_player;
    }

    public void set_player_position(int row, int col) {
        this.row_player = row;
        this.col_player = col;
        draw();
    }

    public double getCellHeight() {
        double canvasHeight = getHeight();
        int rows = maze.getLength();
        return ((canvasHeight) / rows);
    }

    public double getCellWidth() {
        double canvasWidth = getWidth();
        int cols = maze.getWidth();
        return ((canvasWidth) / cols);
    }

//    Show one more step of the solution
    public void requestHint(int cur_hint) {
        if (cur_hint > 0 && cur_hint < solution.length - 1) {
            this.hintRequested = true;
            hint_index = cur_hint;
            draw();
        }
        this.hintRequested = false;
    }

// Updates the details of the maze and draws it
    public void drawMaze(Maze maze) {
        if (maze != null) {
            this.maze = maze;
            this.start = maze.getStartPosition();
            this.goal = maze.getGoalPosition();
            row_player = start.getRowIndex();
            col_player = start.getColumnIndex();
            draw();
        }
    }

//    Drawing functionality of the game board
    public void draw() {
        if (maze != null) {
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            double cellHeight = getCellHeight();
            double cellWidth = getCellWidth();
            setCellHeight(cellHeight);
            setCellWidth(cellWidth);
//            Init a canvas
            GraphicsContext graphicsContext = getGraphicsContext2D();
            graphicsContext.clearRect(0, 0, canvasWidth, canvasHeight);
            graphicsContext.setStroke(Color.BLACK);
            graphicsContext.setLineWidth(1);
            drawStartEnd(graphicsContext, cellHeight, cellWidth);
            graphicsContext.setFill(Color.BROWN);
//            Draw components
            drawWalls(graphicsContext, cellHeight, cellWidth);
            if (isShowSol())
                drawSolution(graphicsContext, cellHeight, cellWidth, solution.length - 1); //Give all the solution
            else if (isHintRequested())
                drawSolution(graphicsContext, cellHeight, cellWidth, hint_index + 1); //Give hint till hint_index
            drawPlayer(graphicsContext, cellHeight, cellWidth);
        }
    }


    private void drawSolution(GraphicsContext graphicsContext, double cellHeight, double cellWidth, int last_step_index) {
        if (solution != null) {
            for (int i = 1; i < last_step_index; i++) { //Exclude start and goal positions
                double h = solution[i][0] * cellHeight;
                double w = solution[i][1] * cellWidth;
                Image solutionStepImage = null;
                try {
                    solutionStepImage = new Image(new FileInputStream(getImageFileNameSoloutionStep()));
                } catch (FileNotFoundException e) {
                    System.out.println("There is no Image solution step....");
                }
                graphicsContext.drawImage(solutionStepImage, w, h, cellWidth, cellHeight);
            }
        }
    }

    private void drawStartEnd(GraphicsContext graphicsContext, double cellHeight, double cellWidth) {
        if (start != null && goal != null) {

            Image startImage = null;
            try {
                startImage = new Image(new FileInputStream(getImageFileNameStart()));
            } catch (FileNotFoundException e) {
                System.out.println("There is no Image for start....");
            }
            graphicsContext.drawImage(startImage, start.getColumnIndex() * cellWidth,
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

    private void drawWalls(GraphicsContext graphicsContext, double cellHeight, double cellWidth) {
        double w, h;
        //Draw Maze
        Image wallImage = null;
        try {
            wallImage = new Image(new FileInputStream(getImageFileNameWall()));
        } catch (FileNotFoundException e) {
            System.out.println("There is no file....");
        }
        int[][] mazeGrid = maze.getMaze();
        for (int i = 0; i < maze.getLength(); i++) {
            for (int j = 0; j < maze.getWidth(); j++) {
                h = i * cellHeight;
                w = j * cellWidth;
                if (mazeGrid[i][j] == 1) // Wall
                {
                    if (wallImage == null) {
                        graphicsContext.fillRect(w, h, cellWidth, cellHeight);
                    } else {
                        graphicsContext.drawImage(wallImage, w, h, cellWidth, cellHeight);
                    }
                }

            }
        }
    }

    private void drawPlayer(GraphicsContext graphicsContext, double cellHeight, double cellWidth) {
        double h_player = getRow_player() * cellHeight;
        double w_player = getCol_player() * cellWidth;
        Image playerImage = null;
        try {
            playerImage = new Image(new FileInputStream(getImageFileNamePlayer()));
        } catch (FileNotFoundException e) {
            System.out.println("There is no Image player....");
        }
        graphicsContext.drawImage(playerImage, w_player, h_player, cellWidth, cellHeight);
    }

// If character property is changed, the character's image will be updated
    public void setCharacter(String character) {
        StringProperty path = null;
        switch (character) {
            case "Mario" -> {
                path = new SimpleStringProperty("src/resources/Images/Mario.png");
                mazeCharacter = "Mario";
            }
            case "Toad" -> {
                path = new SimpleStringProperty("src/resources/Images/Toad_img.jpg");
                mazeCharacter = "Toad";
            }
            case "Luigi" -> {
                path = new SimpleStringProperty("src/resources/Images/Luigi.png");
                mazeCharacter = "Luigi";
            }
        }
        if (path != null)
            imageFileNamePlayer = path;
    }

}
