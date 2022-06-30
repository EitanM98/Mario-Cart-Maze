package View;

import ViewModel.MyViewModel;
import javafx.scene.input.KeyEvent;

public interface IView {
    void setViewModel(MyViewModel viewModel);
    void generateMaze();
    void onKeyPressed(KeyEvent keyEvent);
    void updateViewSettings(String character, boolean sound, boolean bgMusic);
    void showHideSolution();
    void giveAHint();
    void saveMaze();
    void loadMaze();
    void openHelp();
    void openPreferences();
    void openAbout();

}
