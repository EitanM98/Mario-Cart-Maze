package View;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;

public class PreferencesController implements Initializable {
    @FXML
    public Button save_btn;
    @FXML
    public Button back_btn;
    @FXML
    public ComboBox<String> character_cmb;
    @FXML
    public ComboBox<String> searching_cmb;
    @FXML
    public ComboBox<String> generating_cmb;
    @FXML
    public CheckBox bgMusic_checkBox;
    @FXML
    public CheckBox sounds_checkBox;
    private String searchingAlgorithm;
    private String generatingAlgorithm;
    private String character;
    private MyViewController viewController;
    private Properties prop;
    public void setViewController(MyViewController viewController) {
        this.viewController = viewController;
    }

    public void closeWindow() {
        Stage stage = (Stage) save_btn.getScene().getWindow();
        stage.close();
    }

    private void setProps(){
        if(prop==null){
            try {
                prop =  new Properties();
                prop.load(new FileInputStream("src/resources/config.properties"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
            bgMusic_checkBox.setSelected(MyViewController.isMusicOn);
            sounds_checkBox.setSelected(MyViewController.isSoundOn);
            searching_cmb.getItems().addAll("Best First Search", "Depth First Search","Breadth First Search");
            generating_cmb.getItems().addAll("Simple Generator","My Generator");
            character_cmb.getItems().addAll("Mario","Luigi","Toad");
            setProps();
            generatingAlgorithm=prop.getProperty("mazeGeneratingAlgorithm");
            searchingAlgorithm=prop.getProperty("mazeSearchingAlgorithm");
            character=MazeDisplayer.getMazeCharacter();
            if(Objects.equals(generatingAlgorithm, "SimpleMazeGenerator"))
                generating_cmb.setValue("Simple Generator");
            else
                generating_cmb.setValue("My Generator");
            if(Objects.equals(searchingAlgorithm, "BreadthFirstSearch"))
                searching_cmb.setValue("Breadth First Search");
            else if (Objects.equals(searchingAlgorithm, "DepthFirstSearch"))
                searching_cmb.setValue("Depth First Search");
            else
                searching_cmb.setValue("Best First Search");
            character_cmb.setValue(character);
    }

    public void saveSettings() {
        boolean algorithmsChanged;
        if(generating_cmb.getValue().equals(generatingAlgorithm) && searching_cmb.getValue().equals(searchingAlgorithm))
            algorithmsChanged =false;
        else{
            algorithmsChanged =true;
        }
        character=character_cmb.getValue();
        searchingAlgorithm=searching_cmb.getValue();
        generatingAlgorithm=generating_cmb.getValue();
        if(algorithmsChanged)
            viewController.getViewModel().updateSettings(generatingAlgorithm,searchingAlgorithm);
        boolean bgMusic= bgMusic_checkBox.isSelected();
        boolean sound=sounds_checkBox.isSelected();
        viewController.updateViewSettings(character,sound,bgMusic);
        closeWindow();
    }
}
