package View;

import Server.Configurations;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import javax.security.auth.login.Configuration;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
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
    private boolean algorithmsChanged=false;
    private String searchingAlgorithm;
    private String generatingAlgorithm;
    private String character;
    private MyViewController viewController;

    public void setViewController(MyViewController viewController) {
        this.viewController = viewController;
    }

    public void closeWindow() {
        Stage stage = (Stage) save_btn.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
            searching_cmb.getItems().addAll("Best First Search", "Depth First Search","Breadth First Search");
            generating_cmb.getItems().addAll("Simple","MyGenerator");
            character_cmb.getItems().addAll("Mario","Luigi","Toad");
            try {
                Properties prop =  new Properties();
                prop.load(new FileInputStream("src/resources/config.properties"));
                prop.setProperty("Character","Mario");
                generatingAlgorithm=prop.getProperty("mazeGeneratingAlgorithm");
                searchingAlgorithm=prop.getProperty("mazeSearchingAlgorithm");
                character=prop.getProperty("Character");
                generating_cmb.setValue(generatingAlgorithm);
                searching_cmb.setValue(searchingAlgorithm);
                character_cmb.setValue(character);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    public void saveSettings() {
        if(generating_cmb.getValue().equals(generatingAlgorithm) && searching_cmb.getValue().equals(searchingAlgorithm))
            algorithmsChanged=false;
        else{
            algorithmsChanged=true;
        }

        character=character_cmb.getValue();
        searchingAlgorithm=searching_cmb.getValue();
        generatingAlgorithm=generating_cmb.getValue();
        if(algorithmsChanged)
            viewController.getViewModel().updateSettings(generatingAlgorithm,searchingAlgorithm);
        boolean bgMusic= bgMusic_checkBox.isSelected();
        boolean sound=bgMusic_checkBox.isSelected();
        viewController.updateViewSettings(character,sound,bgMusic);
        closeWindow();
    }
}
