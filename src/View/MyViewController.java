package View;

import ViewModel.MyViewModel;
import javafx.application.HostServices;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
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
    public Label lbl_player_row;
    @FXML
    public Label lbl_player_column;
    @FXML
    public Label lbl_Invalid_move;
    @FXML
    public Button btn_newGame=new Button();
    @FXML
    public Button btn_showSolution=new Button();
    @FXML
    public Button btn_giveHint=new Button();
    StringProperty update_player_position_row = new SimpleStringProperty();
    StringProperty update_player_position_col = new SimpleStringProperty();
    int hint_index =1;
    private HostServices hostServices ;
    //Controllers
    private static About aboutController;
    private static GenerateMazeController newMazeController;
    private static Help helpController;

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        lbl_player_row.textProperty().bind(update_player_position_row);
//        lbl_player_column.textProperty().bind(update_player_position_col);
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
        }
        mazeDisplayer.requestFocus();
    }

//    private void playSound(){
//        AudioClip buzzer = new AudioClip(getClass().getResource("/audio/buzzer.mp3").toExternalForm());
//    }

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
}


