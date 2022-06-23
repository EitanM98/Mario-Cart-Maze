package View;

import ViewModel.MyViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Observer;
import java.util.ResourceBundle;

public class GenerateMazeController implements Initializable,IView{
       private MyViewModel viewModel;
        @FXML
        public TextField textField_mazeRows;
        @FXML
        public TextField textField_mazeColumns;
        @FXML
        public ComboBox comboBox_difficulty=new ComboBox<>();
        @FXML
        public RadioButton radioBtnRand=new RadioButton();
        @FXML
        public RadioButton radioBtnCustom=new RadioButton();
        @FXML
        public Button btnGenerateMaze=new Button();
//        StringProperty update_player_position_row = new SimpleStringProperty();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        comboBox_difficulty.getItems().addAll(
                "Easy","Medium","Hard"
        );
        comboBox_difficulty.setPromptText("Please select one");
        radioBtnRand.setSelected(false);
        radioBtnCustom.setSelected(true);
    }

    public void setViewModel(MyViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public void setRadioBtnCustom(){
            radioBtnCustom.setSelected(true);
            radioBtnRand.setSelected(false);
    }

    public void setRadioBtnRand(){
        radioBtnCustom.setSelected(false);
        radioBtnRand.setSelected(true);
    }


    public void generateMaze(ActionEvent actionEvent) {
        String easy_lvl="15";
        String medium_lvl="30";
        String hard_lvl="50";
        try {
            if(radioBtnCustom.isSelected())
                viewModel.generateMaze(textField_mazeRows.getText(),textField_mazeColumns.getText());
            else if(radioBtnRand.isSelected()){
                if(comboBox_difficulty.getValue()==null)
                    throw new Exception("Please choose difficulty level");
                String choice=comboBox_difficulty.getValue().toString();
                String level="";
                switch (choice){
                    case "Easy"->level=easy_lvl;
                    case "Medium"-> level=medium_lvl;
                    case "Hard"->level=hard_lvl;
                    default -> throw new Exception("Please choose difficulty level");
                }
                viewModel.generateMaze(level,level);
            }
            closeWindow();
        } catch (Exception e) {//Wrong parameters
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText(e.getMessage());;
            alert.show();
        }
    }

    public void closeWindow(){
        Stage stage = (Stage) btnGenerateMaze.getScene().getWindow();
        stage.close();
        MyViewController.setNewMazeController(null);
    }
}
