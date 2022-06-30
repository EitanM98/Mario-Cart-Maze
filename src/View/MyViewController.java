package View;

import ViewModel.MyViewModel;
import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.transform.Scale;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

//Our frontend class, observes ViewModel (the mediator)- MVVM Architecture
public class MyViewController implements Initializable,Observer,IView {
//    FXML Objects
    @FXML
    public Pane mazeDisplayerPane;
    @FXML
    public MazeDisplayer mazeDisplayer;
    @FXML
    public Label lbl_Invalid_move;
    @FXML
    public Button btn_newGame = new Button();
    @FXML
    public Button btn_showSolution = new Button();
    @FXML
    public Button btn_giveHint = new Button();
    @FXML
    public BorderPane mazeDisplayerBoarderPane;

    private MyViewModel viewModel;
    int hint_index = 1;
    private HostServices hostServices;
    private static GenerateMazeController generateMazeController=null;
    AtomicInteger zoomIns=new AtomicInteger(0);

    //Sounds section:
    public static boolean isSoundOn = true;
    public static boolean isMusicOn = true;
    //Sounds paths
    static String hintSoundPath = "./src/resources/Sounds/boing_sound.wav";
    static String bgMusicPath = "./src/resources/Sounds/gameMusic.mp3";
    static String errorSoundPath = "./src/resources/Sounds/oh-No.mp3";
    static String winnerSoundPath = "./src/resources/Sounds/We_Are_The_Champions.mp3";

    //Sound Files
    static File hintfile = new File(hintSoundPath);
    static File bgMusicfile = new File(bgMusicPath);
    static File errorSoundFile = new File(errorSoundPath);
    static File winnerSoundFile = new File(winnerSoundPath);

    //Media
    static Media hintMedia = new Media(hintfile.toURI().toString());
    static Media bgMusicMedia = new Media(bgMusicfile.toURI().toString());
    static Media errorSoundMedia = new Media(errorSoundFile.toURI().toString());
    static Media winnerSoundMedia = new Media(winnerSoundFile.toURI().toString());

    //Media Players
    public static MediaPlayer hintSoundPlayer = new MediaPlayer(hintMedia);
    public static MediaPlayer bgMusicPlayer = new MediaPlayer(bgMusicMedia);
    public static MediaPlayer errorSoundPlayer = new MediaPlayer(errorSoundMedia);
    public static MediaPlayer winnerSoundPlayer = new MediaPlayer(winnerSoundMedia);

    //Mouse Events
    double mouseX;
    double mouseY;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        playBackgroundMusic();
        mazeDisplayer.heightProperty().bind(mazeDisplayerPane.heightProperty());
        mazeDisplayer.widthProperty().bind(mazeDisplayerPane.widthProperty());
    }

// Enables opening a link on the web
    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    public void setViewModel(MyViewModel viewModel) {
        this.viewModel = viewModel;
        viewModel.addObserver(this);
    }

    public MyViewModel getViewModel() {
        return viewModel;
    }

    public static void setIsSoundOn(boolean isSoundOn) {
        MyViewController.isSoundOn = isSoundOn;
    }

    public static void setIsMusicOn(boolean isMusicOn) {
        MyViewController.isMusicOn = isMusicOn;
    }

//    Allowing only one generateMaze window open every moment
    public static void setGenerateMazeController(GenerateMazeController generateMazeController) {
        MyViewController.generateMazeController = generateMazeController;
    }

// Generic Alerts:
    public static void showInfoAlert(String message, String title) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.setTitle(title);
        alert.show();
    }

    public static void showErrorAlert(String message, String title) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.setTitle(title);
        alert.show();
        playSound(errorSoundPlayer);
    }

    private void disableButtons(boolean bool) {
        btn_newGame.disableProperty().setValue(bool);
        btn_giveHint.disableProperty().setValue(bool);
        btn_showSolution.disableProperty().setValue(bool);
    }

//    Updates that a changed occurred in ViewModel
    public void update(Observable o, Object arg) {
        if (o instanceof MyViewModel) {

            String event = (String) arg;
            switch (event) {

                case "Maze generated", "Maze loaded" -> mazeGenerated();
                case "Maze solved" -> mazeSolved();
                case "Player moved" -> playerMoved();
                case "Invalid move" -> invalidMove();
                case "Settings Changed" -> settingsChanged();
            }
        }
    }

//    Updated Section:

//    Algorithms were changed, a restart is required. The game board is hiddens
    private void settingsChanged() {
        btn_giveHint.disableProperty().setValue(true);
        btn_showSolution.disableProperty().setValue(true);
        mazeDisplayerBoarderPane.setVisible(false);
        mazeDisplayerBoarderPane.setDisable(true);
        mazeDisplayer.draw();
    }

//    No restart is needed, minor changes are applied to the GUI
    public void updateViewSettings(String character, boolean sound, boolean bgMusic) {
        setIsMusicOn(bgMusic);
        setIsSoundOn(sound);
        playBackgroundMusic();
        mazeDisplayer.setCharacter(character);
        if (viewModel.getMaze() != null)
            mazeDisplayer.draw();
        mazeDisplayer.requestFocus();
    }

//    Shows for 2 seconds Invalid move error label
    private void invalidMove() {
        Runnable task = () -> {
            lbl_Invalid_move.setVisible(true);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lbl_Invalid_move.setVisible(false);
        };
        new Thread(task).start();

    }

//    Updates the game board and draws the new maze
    private void mazeGenerated() {
        if (generateMazeController!=null)
            generateMazeController.closeWindow();
        mazeDisplayerBoarderPane.setVisible(true);
        mazeDisplayerBoarderPane.setDisable(false);
        mazeDisplayer.hideSolution();
        mazeDisplayer.drawMaze(viewModel.getMaze());
        mazeDisplayer.setShowSol(false);
        disableButtons(false);
        hint_index = 1;
        mazeDisplayer.requestFocus();
    }


//  Updates player location on the board
    private void playerMoved() {
        mazeDisplayer.set_player_position(viewModel.getCurRow(), viewModel.getCurCol());
        if (viewModel.isSolved())
            playerWon();
        mazeDisplayer.requestFocus();
    }

    private void mazeSolved() {
        mazeDisplayer.setSolution(viewModel.getSolution());
        mazeDisplayer.requestFocus();
//        solutionIndex=viewModel.getSolution().length;
    }

//    Button Actions Section:

    //    Opens generateMazeController stage
    public void generateMaze() {
        if (generateMazeController==null){
            Parent root;
            try {
                disableButtons(true);
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("GenerateMaze.fxml"));
                root = fxmlLoader.load();
                Stage stage = new Stage();
                stage.setTitle("Generate Maze");
                stage.setScene(new Scene(root, 265, 464));
                GenerateMazeController view = fxmlLoader.getController();
                view.setViewModel(viewModel);
                setGenerateMazeController(view);
                stage.setOnCloseRequest(event -> {
                    disableButtons(false);
                    view.closeWindow();
                });
                stage.setResizable(false);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

//    Toggles the visibility of the solution
    public void showHideSolution() {
        if (viewModel.getSolPath() == null) {  //First use of hide or show soloution =Solve and Show Solution
            this.viewModel.solveMaze();
            hint_index = mazeDisplayer.getSolution().length;
            mazeDisplayer.showSolution();
        } else if (mazeDisplayer.isShowSol()) {     //Hide
            hint_index = 1;
            mazeDisplayer.hideSolution();
        } else { //Show solution
            hint_index = mazeDisplayer.getSolution().length;
            mazeDisplayer.showSolution();
        }
        mazeDisplayer.requestFocus();
    }

//    Shows one more step in the solution
    public void giveAHint() {
        if (!mazeDisplayer.isShowSol()) { //Solution isn't shown already
            if (mazeDisplayer.getSolution() == null) { //Not solved yet
                this.viewModel.solveMaze();
                hint_index = 1;
            }
            mazeDisplayer.requestHint(hint_index);
            hint_index++;
            playSound(hintSoundPlayer);
        }
        mazeDisplayer.requestFocus();
    }

    public void exitGameAlert() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to exit?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            exitGame();
        }
    }

    public void exitGame() {
        if (viewModel != null) {
            viewModel.exitGame();
        }
        System.exit(0);
    }

    public void saveMaze() {
        if (viewModel.getMaze() == null)
            showErrorAlert("The maze is empty \n Please generate a maze first", "Saving Error");
        else
            viewModel.saveMaze();
    }

    public void loadMaze() {
        this.viewModel.loadMaze();
    }

    //Opens player won stage
    private void playerWon() {
        Parent root;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PlayerWon.fxml"));
            root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Congratulations");
            stage.setScene(new Scene(root, 275, 333));
            WinnerStageController winnerWindow = fxmlLoader.getController();
            winnerWindow.setView(this);
            stage.setOnCloseRequest(event -> {
                winnerWindow.closeWindow();
                playBackgroundMusic();
            });
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        MyViewController.playSound(MyViewController.winnerSoundPlayer);
        //add here music and images
    }

    public void openMarioKartLink() {
        hostServices.showDocument("https://mariokarttour.com/en-US");
    }


    public void openAbout() {
            Parent root;
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(new File("src/resources/View/About.fxml").toURI().toURL());
                root = fxmlLoader.load();
                Stage stage = new Stage();
                stage.setTitle("About");
                stage.setScene(new Scene(root, 342, 452));
                AboutController view = fxmlLoader.getController();
                stage.setOnCloseRequest(event -> {
                    view.closeWindow();
                });
                stage.setResizable(false);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    public void openHelp() {
            Parent root;
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(new File("src/resources/View/Help.fxml").toURI().toURL());
                root = fxmlLoader.load();
                Stage stage = new Stage();
                stage.setTitle("Help");
                stage.setScene(new Scene(root, 808, 486));
                HelpController view = fxmlLoader.getController();
                stage.setOnCloseRequest(event -> {
                    view.closeWindow();
                });
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setResizable(false);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    public void openPreferences() {
        Parent root;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Preferences.fxml"));
            root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Preferences");
            stage.setScene(new Scene(root, 235, 400));
            PreferencesController prefController = fxmlLoader.getController();
            prefController.setViewController(this);
            stage.setOnCloseRequest(event -> {
                prefController.closeWindow();
            });
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    Sounds Section:

//    Enables sounds such as: winning sound, hint sound and error sound
public static void playSound(MediaPlayer sound) {
    if (isSoundOn) {
        if (MyViewController.isMusicOn) {
            MyViewController.bgMusicPlayer.stop();
        }
        sound.play();
        sound.setOnEndOfMedia(MyViewController::playBackgroundMusic);
        sound.setOnStopped(MyViewController::playBackgroundMusic);
        sound.seek(sound.getStartTime());
    }
}

    private static void playBackgroundMusic() {
        if (isMusicOn) { //Start
            bgMusicPlayer.play();
            bgMusicPlayer.setOnEndOfMedia(() -> {
                bgMusicPlayer.seek(bgMusicPlayer.getStartTime());
                bgMusicPlayer.play();
            });
        } else { //Stop
            bgMusicPlayer.stop();
            bgMusicPlayer.seek(bgMusicPlayer.getStartTime());
        }
    }


    //Mouse Events for dragging the player over the maze:
    private void setMouseXY(double x, double y) {
        mouseX = x;
        mouseY = y;
    }

    public void dragDetected(MouseEvent event) {
        setMouseXY(event.getX(), event.getY());
        mazeDisplayer.startFullDrag();
        mazeDisplayer.setOnMouseDragOver(this::mouseDragged);
        mazeDisplayer.setOnMouseDragReleased(this::mouseReleased);
    }

    public void mouseDragged(MouseEvent mouseEvent) {
        if (viewModel.getMaze() != null) {
            if (draggedLongEnough(mouseEvent)) {
                double curX = mouseX;
                double curY = mouseY;
                setMouseXY(mouseEvent.getX(), mouseEvent.getY());
                viewModel.movePlayer(mouseEvent.getX(), mouseEvent.getY(), curX, curY,
                        mazeDisplayer.getCellWidth(), mazeDisplayer.getCellHeight());
                setMouseXY(mouseEvent.getX(), mouseEvent.getY());
            }
        }
        mazeDisplayer.requestFocus();
    }

    //Return true if the mouse was dragged long enough
    private boolean draggedLongEnough(MouseEvent mouseEvent) {
        double newX = mouseEvent.getX();
        double newY = mouseEvent.getY();
        return (Math.abs(newX - this.mouseX) >= (mazeDisplayer.getCellWidth() / 2) || Math.abs(newY - this.mouseY) >= (mazeDisplayer.getCellHeight() / 2));
    }

    public void mouseReleased(MouseEvent mouseEvent) {
        setMouseXY(mouseEvent.getX(), mouseEvent.getY());
    }

// Resizing the main stage according to the window dimensions
    public void stageResized() {
        Scene scene = btn_newGame.getScene();
        Stage stage=(Stage) scene.getWindow();
        mazeDisplayer.heightProperty().bind(mazeDisplayerPane.heightProperty());
        mazeDisplayer.widthProperty().bind(mazeDisplayerPane.widthProperty());

        stage.maximizedProperty().addListener((ov, t, t1) -> mazeDisplayer.draw());
        stage.iconifiedProperty().addListener((ov, t, t1) -> mazeDisplayer.draw());

        scene.heightProperty().addListener((observable, oldValue, newValue) -> {
            mazeDisplayer.draw();
        });
        scene.widthProperty().addListener((observable, oldValue, newValue) -> {
            mazeDisplayer.draw();
        });
    }

//    Handles zoom in&out on the maze
    public void zoomMaze(ScrollEvent event){
            if (event.isControlDown()) {
                double zoomFactor = 1;
//                Limit zoom in and outs to 10
                    if (zoomIns.get()>-10 && event.getDeltaY() < 0) {
                        zoomFactor = 1/1.05;
                        zoomIns.decrementAndGet();
                    }
                    else if(zoomIns.get()<10 && event.getDeltaY() > 0) {
                        zoomIns.incrementAndGet();
                        zoomFactor=1.05;
                    }
                Scale newScale = new Scale();
                newScale.setPivotX(event.getX());
                newScale.setPivotY(event.getY());
                newScale.setX(zoomFactor);
                newScale.setY(zoomFactor);
                mazeDisplayer.getTransforms().add(newScale);
                event.consume();
            }
        }


    public void mouseClicked() {
        mazeDisplayer.requestFocus();
    }

    public void onKeyPressed(KeyEvent keyEvent) {

        viewModel.movePlayer(keyEvent);
        keyEvent.consume();

    }
}


