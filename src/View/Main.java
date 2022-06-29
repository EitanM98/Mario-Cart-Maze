package View;

import Model.IModel;
import Model.MyModel;
import ViewModel.MyViewModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MyView.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("Mario Kart Maze");
        primaryStage.setScene(new Scene(root, 1200, 900));
        IModel model = new MyModel();
        MyViewModel viewModel = new MyViewModel(model);
        MyViewController view = fxmlLoader.getController();
        view.setHostServices(getHostServices());
        view.setViewModel(viewModel);
        primaryStage.show();
        view.stageResized();
        primaryStage.setOnCloseRequest(event->{view.exitGameAlert();});

    }


    public static void main(String[] args) {
        launch(args);
    }
}
