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
        public TextField textField_mazeSize;
        @FXML
        public ComboBox<String> comboBox_difficulty=new ComboBox<>();
        @FXML
        public RadioButton radioBtnRand=new RadioButton();
        @FXML
        public RadioButton radioBtnCustom=new RadioButton();
        @FXML
        public Button btnGenerateMaze=new Button();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        comboBox_difficulty.getItems().addAll(
                "Easy","Medium","Hard"
        );
        comboBox_difficulty.setValue("Easy");
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


    public void generateMaze() {
        String easy_lvl="15";
        String medium_lvl="30";
        String hard_lvl="50";
        try {
            if(radioBtnCustom.isSelected())
                viewModel.generateMaze(textField_mazeSize.getText(),textField_mazeSize.getText());
            else if(radioBtnRand.isSelected()){
                String choice=comboBox_difficulty.getValue();
                String level="";
                switch (choice){
                    case "Easy"->level=easy_lvl;
                    case "Medium"-> level=medium_lvl;
                    case "Hard"->level=hard_lvl;
                }
                viewModel.generateMaze(level,level);
            }
        } catch (Exception e) {//Wrong parameters
            MyViewController.showErrorAlert(e.getMessage(),"Invalid Parameters");
        }
    }

    public void closeWindow(){
        Stage stage = (Stage) btnGenerateMaze.getScene().getWindow();
        stage.close();
        MyViewController.setGenerateMazeController(null);
    }
}
