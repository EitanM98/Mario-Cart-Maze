package View;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class AboutController implements Initializable {

    @FXML
    public Label content;
    @FXML
    public Label copyrights_lbl;
    @FXML
    public Label contactUs_lbl;
    private final String aboutUs="Mario Kart Maze was created and designed by\n" +
            "Eitan Markman & Ariel Dawidowicz.\n\n" +
            "Mario Kart Maze is our final project of\n" +
            "Advanced Topics In Programming Course.\n\n";
    private final String contactUs= "For any reviews or collaborations you can contact us on:\n" +
            "eitanmarkman8@gmail.com\n" +
            "arieldawidowicz1@gmail.com \n\n";
    private final String copyrights="\t\t\t© 2022, Mario Kart Maze ©";


    public void closeWindow(){
        Stage stage = (Stage) content.getScene().getWindow();
        stage.close();
        MyViewController.setAboutController(null);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        content.setText(aboutUs);
        contactUs_lbl.setText(contactUs);
        copyrights_lbl.setText(copyrights);
    }
}
