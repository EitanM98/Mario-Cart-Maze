package View;

import ViewModel.MyViewModel;
import algorithms.mazeGenerators.Maze;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.Observable;
import java.util.Observer;
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
    StringProperty update_player_position_row = new SimpleStringProperty();
    StringProperty update_player_position_col = new SimpleStringProperty();
    private Maze maze;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lbl_player_row.textProperty().bind(update_player_position_row);
        lbl_player_column.textProperty().bind(update_player_position_col);
    }

    public void setViewModel(MyViewModel viewModel) {
        this.viewModel = viewModel;
    }


    public String get_update_player_position_row() {
        return update_player_position_row.get();
    }

    public void set_update_player_position_row(String update_player_position_row) {
        this.update_player_position_row.set(update_player_position_row);
    }

    public String get_update_player_position_col() {
        return update_player_position_col.get();
    }

    public void set_update_player_position_col(String update_player_position_col) {
        this.update_player_position_col.set(update_player_position_col);
    }



    public void generateMaze()
    {
        try {
            viewModel.generateMaze(textField_mazeRows.getText(),textField_mazeColumns.getText());
//            set_update_player_position_col(viewModel.getCurCol()+"");
//            set_update_player_position_row(viewModel.getCurRow()+"");
//            this.mazeDisplayer.set_player_position(viewModel.getCurRow(),viewModel.getCurCol());
        } catch (Exception e) {//Wrong parameters
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText(e.getMessage());;
            alert.show();
        }
    }

    public void solveMaze()
    {
        viewModel.solveMaze(this.maze);

    }


    public void showAlert(String message)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.show();
    }


    public void keyPressed(KeyEvent keyEvent) {

        viewModel.moveCharacter(keyEvent);
        keyEvent.consume();

    }

    public void mouseClicked(MouseEvent mouseEvent) {
        mazeDisplayer.requestFocus();
    }


    @Override
    public void update(Observable o, Object arg) {
        if(o instanceof MyViewModel)
        {
            if(maze == null)//generateMaze
            {
                this.maze = viewModel.getMaze();
                drawMaze();
            }
            else {
                Maze maze = viewModel.getMaze();

                if (maze == this.maze)//Not generateMaze
                {
                    int rowChar = mazeDisplayer.getRow_player();
                    int colChar = mazeDisplayer.getCol_player();
                    int rowFromViewModel = viewModel.getCurRow();
                    int colFromViewModel = viewModel.getCurCol();

                    if(rowFromViewModel == rowChar && colFromViewModel == colChar)//Solve Maze
                    {
                        viewModel.getSolution();
                        showAlert("Solving Maze ... ");
                    }
                    else//Update location
                    {
                        set_update_player_position_row(rowFromViewModel + "");
                        set_update_player_position_col(colFromViewModel + "");
                        this.mazeDisplayer.set_player_position(rowFromViewModel,colFromViewModel);
                        if(mazeDisplayer.checkIfSolved())
                            showAlert("Congrats you won !!!");
                    }


                }
                else//GenerateMaze
                {
                    this.maze = maze;
                    drawMaze();
                }
            }
        }
    }

    public void drawMaze()
    {
        mazeDisplayer.drawMaze(maze);
    }
}


