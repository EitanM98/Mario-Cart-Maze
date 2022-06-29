package View;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class WinnerStageController {
    MyViewController view;
    @FXML
    public Button btnSaveMaze;

    public void setView(MyViewController myViewController) {
        view=myViewController;
    }

    public void closeWindow() {
        MyViewController.winnerSoundPlayer.stop();
        Stage stage = (Stage) btnSaveMaze.getScene().getWindow();
        stage.close();
    }

    public void saveMaze() {
        closeWindow();
        view.saveMaze();
    }

    public void newMaze() {
        closeWindow();
        view.generateMaze();
    }
}
