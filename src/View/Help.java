package View;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class Help {
    @FXML
    public TextArea content;

    public void closeWindow(){
        Stage stage = (Stage) content.getScene().getWindow();
        stage.close();
        MyViewController.setHelpController(null);
    }
}
