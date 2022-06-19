package View;

import ViewModel.MyViewModel;
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

    public int getHint_index() {
        return hint_index;
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

    //    public String get_update_player_position_row() {
//        return update_player_position_row.get();
//    }
//
//    public void set_update_player_position_row(int update_player_position_row) {
//        this.update_player_position_row.set(update_player_position_row+"");
//    }
//
//    public String get_update_player_position_col() {
//        return update_player_position_col.get();
//    }
//
//    public void set_update_player_position_col(int update_player_position_col) {
//        this.update_player_position_col.set(update_player_position_col+"");
//    }



    public void generateMaze()
    {
        Parent root;
        try {
            disableButtons(true);
            FXMLLoader fxmlLoader = new FXMLLoader(new File("src/View/GenerateMaze.fxml").toURI().toURL());
            root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Generate Maze");
            stage.setScene(new Scene(root, 450, 450));
            GenerateMazeController view = fxmlLoader.getController();
            view.setViewModel(viewModel);
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void showAlert(String message)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
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

                case "Maze generated" -> mazeGenerated();
                case "Player moved" -> playerMoved();
                case "Maze solved" -> mazeSolved();
                case "Invalid move" -> invalidMove();
//                case "Load maze" -> loadedMaze();
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
        mazeDisplayer.setSolved(false);
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
//        set_update_player_position_row(viewModel.getCurRow());
//        set_update_player_position_col(viewModel.getCurCol());
        mazeDisplayer.set_player_position(viewModel.getCurRow(),viewModel.getCurCol());
        if(viewModel.isSolved())
            playerWon();
    }

    private void playerWon() {
        mazeDisplayer.setSolved(true);
        showAlert("Congrats you won");
        //add here music and images
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

    public void exitGame(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to exit?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            if (viewModel != null) {
                System.exit(0);
            }
        }
    }
}


