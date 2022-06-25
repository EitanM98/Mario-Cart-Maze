package View;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class HelpController implements Initializable {

    @FXML
    public Label how_to_play_lbl;
    @FXML
    public Label instructions_lbl;
    @FXML
    public Label qa_lbl;
    @FXML
    public Label qa_content_lbl;

    private final String howTo="How to play?";
    private final String instructions="* The goal of the game is to get\n  to the goal point.\n" +
            "* You can't go through walls.\n" +
            "* To move the player use keyboard arrows\n" +
            "  or use the Numpad as follows:\n" +
            "  2,4,6,8- To move down,left,right,up \n" +
            "  1,3,7,9- To move diagonally.\n";
    private final String qAndA="Frequently asked questions: ";
    private final String qa_content="Q: The maze was great how can I save it?\n" +
            "A: On the menu toolbar:File -> Save Maze.\n\n" +
            "Q: I like Luigi more than Mario, can I change the player?\n" +
            "A: Of course, On the menu toolbar: Setting -> Preferences.\n";


    public void closeWindow(){
        Stage stage = (Stage) how_to_play_lbl.getScene().getWindow();
        stage.close();
        MyViewController.setHelpController(null);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        how_to_play_lbl.setText(howTo);
        instructions_lbl.setText(instructions);
        qa_lbl.setText(qAndA);
        qa_content_lbl.setText(qa_content);
    }
}
