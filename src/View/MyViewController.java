package View;

import ViewModel.MyViewModel;
import javafx.application.HostServices;
//import javafx.event.ActionEvent;
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

public class MyViewController implements Initializable,Observer,IView {

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
    //Controllers
    private static AboutController aboutController;
    private static GenerateMazeController newMazeController;
    private static HelpController helpController;
    private static WinnerStageController winnerController;

    //Sounds section:
    public static boolean isSoundOn = true;
    public static boolean isMusicOn = true;
    //Sounds paths
    static String hintSoundPath = "./Resources/Sounds/boing_sound.wav";
    static String bgMusicPath = "./Resources/Sounds/gameMusic.mp3";
    static String errorSoundPath = "./Resources/Sounds/oh-No.mp3";
    static String winnerSoundPath = "./Resources/Sounds/We_Are_The_Champions.mp3";


    //Files
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
        mazeDisplayer.heightProperty().bind(mazeDisplayerBoarderPane.heightProperty());
        mazeDisplayer.widthProperty().bind(mazeDisplayerBoarderPane.widthProperty());
    }

    public static void setHelpController(HelpController helpController) {
        MyViewController.helpController = helpController;
    }

    public static void setNewMazeController(GenerateMazeController newMazeController) {
        MyViewController.newMazeController = newMazeController;
    }

    public static void setAboutController(AboutController aboutController) {
        MyViewController.aboutController = aboutController;
    }

    public static void setWinnerController(WinnerStageController winnerController) {
        MyViewController.winnerController = winnerController;
    }

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

    public void generateMaze() {
        if (MyViewController.newMazeController == null) {
            Parent root;
            try {
                disableButtons(true);
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("GenerateMaze.fxml"));
                root = fxmlLoader.load();
                Stage stage = new Stage();
                stage.setTitle("Generate Maze");
                stage.setScene(new Scene(root, 265, 464));
                GenerateMazeController view = fxmlLoader.getController();
                MyViewController.setNewMazeController(view);
                view.setViewModel(viewModel);
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


    public void keyPressed(KeyEvent keyEvent) {

        viewModel.movePlayer(keyEvent);
        keyEvent.consume();

    }

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

    private void settingsChanged() {
        //TO-DO
        btn_giveHint.disableProperty().setValue(true);
        btn_showSolution.disableProperty().setValue(true);
        mazeDisplayer.draw();
//        mazeDisplayer.setVisible(false);
    }

    public void updateViewSettings(String character, boolean sound, boolean bgMusic) {
        setIsMusicOn(bgMusic);
        setIsSoundOn(sound);
        playBackgroundMusic();
        mazeDisplayer.setCharacter(character);
        if (viewModel.getMaze() != null)
            mazeDisplayer.draw();
        mazeDisplayer.requestFocus();
    }

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

    private void mazeGenerated() {
        if (winnerController != null)
            winnerController.closeWindow();
        if (newMazeController != null)
            newMazeController.closeWindow();
//        mazeDisplayer.setVisible(true);
        mazeDisplayer.setStart(viewModel.getMaze().getStartPosition());
        mazeDisplayer.setGoal(viewModel.getMaze().getGoalPosition());
        mazeDisplayer.drawMaze(viewModel.getMaze());
        mazeDisplayer.setShowSol(false);
        disableButtons(false);
        hint_index = 1;
        mazeDisplayer.requestFocus();
    }

    private void disableButtons(boolean bool) {
        btn_newGame.disableProperty().setValue(bool);
        btn_giveHint.disableProperty().setValue(bool);
        btn_showSolution.disableProperty().setValue(bool);
    }


    private void playerMoved() {
        mazeDisplayer.set_player_position(viewModel.getCurRow(), viewModel.getCurCol());
        if (viewModel.isSolved())
            playerWon();
        else if (winnerController != null)
            winnerController.closeWindow();
        mazeDisplayer.requestFocus();
    }


    private void playerWon() {
        Parent root;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PlayerWon.fxml"));
            root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Congratulations");
            stage.setScene(new Scene(root, 275, 333));
            WinnerStageController winnerWindow = fxmlLoader.getController();
            MyViewController.setWinnerController(winnerWindow);
            winnerWindow.setView(this);
            stage.setOnCloseRequest(event -> {
                winnerWindow.closeWindow();
                playBackgroundMusic();
            });
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        MyViewController.playSound(MyViewController.winnerSoundPlayer);
        //add here music and images
    }

    private void mazeSolved() {
        mazeDisplayer.setSolution(viewModel.getSolution());
        mazeDisplayer.requestFocus();
//        solutionIndex=viewModel.getSolution().length;
    }

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

    public void openMarioKartLink() {
        hostServices.showDocument("https://mariokarttour.com/en-US");
    }


    public void openAbout() {
        if (MyViewController.aboutController == null) {
            Parent root;
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(new File("src/resources/View/About.fxml").toURI().toURL());
                root = fxmlLoader.load();
                Stage stage = new Stage();
                stage.setTitle("About");
                stage.setScene(new Scene(root, 342, 452));
                AboutController view = fxmlLoader.getController();
                MyViewController.setAboutController(view);
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
    }

    public void openHelp() {
        if (MyViewController.helpController == null) {
            Parent root;
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(new File("src/resources/View/Help.fxml").toURI().toURL());
                root = fxmlLoader.load();
                Stage stage = new Stage();
                stage.setTitle("Help");
                stage.setScene(new Scene(root, 605, 388));
                HelpController view = fxmlLoader.getController();
                MyViewController.setHelpController(view);
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
    }

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

    public void openPreferences() {
        Parent root;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Preferences.fxml"));
            root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Preferences");
            stage.setScene(new Scene(root, 235, 400));
            PreferencesController prefController = fxmlLoader.getController();
//            prefController.initialize(null,null);
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

    //Mouse Events:

    private void setMouseXY(double x, double y) {
        mouseX = x;
        mouseY = y;
    }

    public void dragDetected(MouseEvent event) {
        setMouseXY(event.getX(), event.getY());
//        drag_detected = true;
        mazeDisplayer.startFullDrag();
        mazeDisplayer.setOnMouseDragOver(this::mouseDragged);
        mazeDisplayer.setOnMouseDragReleased(this::mouseReleased);
    }

    public void mouseDragged(MouseEvent mouseEvent) {
        if (viewModel.getMaze() != null) {
            if (draggedLongEnough(mouseEvent)) {
                double curX = mouseX;
                double curY = mouseY;
                viewModel.movePlayer(mouseEvent.getX(), mouseEvent.getY(), curX, curY,
                        mazeDisplayer.getCellHeight(), mazeDisplayer.getCellWidth());
                setMouseXY(mouseEvent.getX(), mouseEvent.getY());
            }
        }
        mazeDisplayer.requestFocus();
    }

    //Return true if the mouse was dragged long enough
    private boolean draggedLongEnough(MouseEvent mouseEvent) {
        double newX = mouseEvent.getX();
        double newY = mouseEvent.getY();
        return (Math.abs(newX - this.mouseX) >= (mazeDisplayer.getCellWidth() / 3) || Math.abs(newY - this.mouseY) >= (mazeDisplayer.getCellHeight() / 3));
    }


    public void mouseClicked(MouseEvent mouseEvent) {
//        setMouseXY(mouseEvent.getX(),mouseEvent.getY());
//        mazeDisplayer.requestFocus();
    }

    public void mouseReleased(MouseEvent mouseEvent) {
        setMouseXY(mouseEvent.getX(), mouseEvent.getY());
//        mouseEvent.consume();
    }

    public void stageResized() {
        Scene scene = btn_newGame.getScene();
        mazeDisplayer.heightProperty().bind(mazeDisplayerBoarderPane.heightProperty());
        mazeDisplayer.widthProperty().bind(mazeDisplayerBoarderPane.widthProperty());

        scene.heightProperty().addListener((observable, oldValue, newValue) -> {
            mazeDisplayer.draw();
        });
        scene.widthProperty().addListener((observable, oldValue, newValue) -> {
            mazeDisplayer.draw();
        });
    }

//    public void zoomMaze(ScrollEvent scrollEvent) {
//        double deltaY = scrollEvent.getDeltaY();
//        if (scrollEvent.isControlDown()) {
//            double zoomFactor = 1.1;
//            if (deltaY < 0) {
//                zoomFactor = 0.90;
//            }
//            Scale newScale = new Scale();
//            newScale.setX(mazeDisplayer.getScaleX() * zoomFactor);
//            newScale.setY(mazeDisplayer.getScaleY() * zoomFactor);
//            newScale.setPivotX(mazeDisplayer.getScaleX());
//            newScale.setPivotY(mazeDisplayer.getScaleY());
//            mazeDisplayer.getTransforms().add(newScale);
//            mazeDisplayerBoarderPane.set
//            mazeDisplayerScrollPane.setContent(mazeDisplayer);
//            scrollEvent.consume();
//        }
//    }
//        public void zoomMaze(ScrollEvent scrollEvent) {
//            double deltaY = scrollEvent.getDeltaY();
//            if (scrollEvent.isControlDown()) {
//                if (deltaY < 0)
//                    mazeDisplayer.zoomOut();
//                else
//                    mazeDisplayer.zoomIn();
//            }
//        }
    public void zoomMaze(ScrollEvent event) {
        if(event.isControlDown()) {
            double delta = 1.1;

//        double scaleX = mazeDisplayer.getScaleX();
            double scaleY = mazeDisplayer.getScaleY();
//        double oldScaleX = scaleX;
            double oldScaleY = scaleY;

//        if (event.getDeltaX() < 0)
//            scaleX /= delta;
//        else
//            scaleX *= delta;
            if (event.getDeltaY() < 0)
                scaleY /= delta;
            else
                scaleY *= delta;
//        scaleX=clamp( scaleX, MazeDisplayer.minScale,MazeDisplayer.maxScale);
            scaleY = isInScale(scaleY, MazeDisplayer.minScale, MazeDisplayer.maxScale);

//        double fx = (scaleX / oldScaleX)-1;
            double fy = (scaleY / oldScaleY) - 1;

            double dx = (event.getSceneX() - (mazeDisplayer.getBoundsInParent().getWidth() / 2 + mazeDisplayer.getBoundsInParent().getMinX()));
            double dy = (event.getSceneY() - (mazeDisplayer.getBoundsInParent().getHeight() / 2 + mazeDisplayer.getBoundsInParent().getMinY()));

            mazeDisplayer.setScaleX(scaleY);
            mazeDisplayer.setScaleY(scaleY);

            // note: pivot value must be untransformed, i. e. without scaling
            mazeDisplayer.setPivot(fy * dx, fy * dy);
            mazeDisplayer.draw();
            event.consume();
        }
    }

    private double isInScale( double value, double min, double max) {
            if( value < min)
                return min;
            if( value > max)
                return max;
            return value;
            }

}


