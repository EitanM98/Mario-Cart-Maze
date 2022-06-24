package View;

import ViewModel.MyViewModel;
import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;
import java.util.ResourceBundle;

public class MyViewController implements Initializable,Observer,IView {



    private MyViewModel viewModel;
    @FXML
    public TextField textField_mazeRows;
    @FXML
    public TextField textField_mazeColumns;
    @FXML
    public MazeDisplayer mazeDisplayer;
    @FXML
    public Label lbl_Invalid_move;
    @FXML
    public Button btn_newGame=new Button();
    @FXML
    public Button btn_showSolution=new Button();
    @FXML
    public Button btn_giveHint=new Button();

    int hint_index =1;
    private HostServices hostServices ;
    //Controllers
    private static About aboutController;
    private static GenerateMazeController newMazeController;
    private static Help helpController;
    //Sounds section:
    private static boolean isSoundOn=true;
    private static boolean isMusicOn=true;
    //Sounds paths
    static String hintSoundPath = "./src/resources/Sounds/boing_sound.wav";
    static String bgMusicPath = "./src/resources/Sounds/gameMusic.mp3";
    static String errorSoundPath = "./src/resources/Sounds/oh-No.mp3";
    static String winnerSoundPath = "./src/resources/Sounds/We_Are_The_Champions.mp3";


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
    public static MediaPlayer hintSoundPlayer =new MediaPlayer(hintMedia);
    public static MediaPlayer bgMusicPlayer=new MediaPlayer(bgMusicMedia);
    public static MediaPlayer errorSoundPlayer=new MediaPlayer(errorSoundMedia);
    public static MediaPlayer winnerSoundPlayer=new MediaPlayer(winnerSoundMedia);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        playBackgroundMusic();
    }

    public static void setHelpController(Help helpController) {
        MyViewController.helpController = helpController;
    }

    public static void setNewMazeController(GenerateMazeController newMazeController) {
        MyViewController.newMazeController = newMazeController;
    }

    public static void setAboutController(About aboutController) {
        MyViewController.aboutController = aboutController;
    }

    public int getHint_index() {
        return hint_index;
    }

    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    public void setHint_index(int hint_index) {
        this.hint_index = hint_index;
    }


    public void setViewModel(MyViewModel viewModel) {
        this.viewModel = viewModel;
        viewModel.addObserver(this);
    }

    public MyViewModel getViewModel() {
        return viewModel;
    }


    public void generateMaze() {
        if (MyViewController.newMazeController == null) {
            Parent root;
            try {
                disableButtons(true);
                FXMLLoader fxmlLoader = new FXMLLoader(new File("src/View/GenerateMaze.fxml").toURI().toURL());
                root = fxmlLoader.load();
                Stage stage = new Stage();
                stage.setTitle("Generate Maze");
                stage.setScene(new Scene(root, 450, 450));
                GenerateMazeController view = fxmlLoader.getController();
                MyViewController.setNewMazeController(view);
                view.setViewModel(viewModel);
                stage.setOnCloseRequest(event -> {
                    disableButtons(false);
                    view.closeWindow();
                });
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void showInfoAlert(String message,String title)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.setTitle(title);
        alert.show();
    }

    public static void showErrorAlert(String message,String title)
    {
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

    public void mouseClicked(MouseEvent mouseEvent) {
        mazeDisplayer.requestFocus();
    }


    public void update(Observable o, Object arg) {
        if (o instanceof MyViewModel) {

            String event = (String) arg;
            switch (event) {

                case "Maze generated", "Maze loaded" -> mazeGenerated();
                case "Maze solved" -> mazeSolved();
                case "Player moved" -> playerMoved();
                case "Invalid move" -> invalidMove();
            }
        }
    }



    private void invalidMove() {
        Runnable task=()->{
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
        if(newMazeController!=null)
            newMazeController.closeWindow();
        mazeDisplayer.setStart(viewModel.getMaze().getStartPosition());
        mazeDisplayer.setGoal(viewModel.getMaze().getGoalPosition());
        mazeDisplayer.drawMaze(viewModel.getMaze());
        mazeDisplayer.setShowSol(false);
        disableButtons(false);
        hint_index =1;
        mazeDisplayer.requestFocus();
//        playerMoved();
    }

    private void disableButtons(boolean bool) {
        btn_newGame.disableProperty().setValue(bool);
        btn_giveHint.disableProperty().setValue(bool);
        btn_showSolution.disableProperty().setValue(bool);
    }



    private void playerMoved() {
        mazeDisplayer.set_player_position(viewModel.getCurRow(),viewModel.getCurCol());
        viewModel.checkIfWon();
    }


    private void mazeSolved() {
        mazeDisplayer.setSolution(viewModel.getSolution());
        mazeDisplayer.requestFocus();
//        solutionIndex=viewModel.getSolution().length;
    }

    public void showHideSolution()
    {
        if(viewModel.getSolPath()==null) {  //First use of hide or show soloution =Solve and Show Solution
            this.viewModel.solveMaze();
            hint_index =mazeDisplayer.getSolution().length;
            mazeDisplayer.showSolution();
        }
        else if(mazeDisplayer.isShowSol()) {     //Hide
            hint_index =1;
            mazeDisplayer.hideSolution();
        }
        else{ //Show solution
            hint_index =mazeDisplayer.getSolution().length;
            mazeDisplayer.showSolution();
        }
        mazeDisplayer.requestFocus();
    }

    public void giveAHint(){
        if(!mazeDisplayer.isShowSol()){ //Solution isn't shown already
            if(mazeDisplayer.getSolution()==null){ //Not solved yet
                this.viewModel.solveMaze();
                hint_index =1;
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

    public void exitGame(){
        if (viewModel != null) {
            viewModel.exitGame();
        }
        System.exit(0);
    }

    public void saveMaze(ActionEvent actionEvent) {
        if(viewModel.getMaze()==null)
            showErrorAlert("The maze is empty \n Please generate a maze first","Saving Error");
        else
            viewModel.saveMaze();
    }

    public void loadMaze(ActionEvent actionEvent) {
        this.viewModel.loadMaze();
    }

    public void openMarioKartLink(ActionEvent actionEvent) {
        hostServices.showDocument("https://mariokarttour.com/en-US");
    }


    public void openAbout(ActionEvent actionEvent) {
        if (MyViewController.aboutController == null) {
            Parent root;
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(new File("src/View/About.fxml").toURI().toURL());
                root = fxmlLoader.load();
                Stage stage = new Stage();
                stage.setTitle("About");
                stage.setScene(new Scene(root, 450, 450));
                About view = fxmlLoader.getController();
                MyViewController.setAboutController(view);
                stage.setOnCloseRequest(event -> {
                    view.closeWindow();
                });
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void openHelp(ActionEvent actionEvent) {
        if (MyViewController.helpController == null) {
            Parent root;
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(new File("src/View/Help.fxml").toURI().toURL());
                root = fxmlLoader.load();
                Stage stage = new Stage();
                stage.setTitle("Help");
                stage.setScene(new Scene(root, 450, 450));
                Help view = fxmlLoader.getController();
                MyViewController.setHelpController(view);
                stage.setOnCloseRequest(event -> {
                    view.closeWindow();
                });
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void playSound(MediaPlayer sound) {
        if(isSoundOn){
            if(isMusicOn){
                bgMusicPlayer.stop();

            }
            sound.play();
            sound.seek(sound.getStartTime());
            if(isMusicOn){//Needs To be synced!!
                bgMusicPlayer.play();
            }
        }
    }

    private void playBackgroundMusic() {
        if(isMusicOn){
            bgMusicPlayer.play();
//            bgMusicPlayer.setOnEndOfMedia(() -> {
//                bgMusicPlayer.seek(bgMusicPlayer.getStartTime());
//                bgMusicPlayer.play();
//            });
        }
        else{
            bgMusicPlayer.stop();
            bgMusicPlayer.seek(hintSoundPlayer.getStartTime());
        }
    }


}


