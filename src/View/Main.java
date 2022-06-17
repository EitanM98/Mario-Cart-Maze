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
        primaryStage.setScene(new Scene(root, 600, 600));

        IModel model = new MyModel();
        MyViewModel viewModel = new MyViewModel(model);
        MyViewController view = fxmlLoader.getController();
        view.setViewModel(viewModel);
        primaryStage.show();
//        String backgroundImage = getClass().getResource("MarioKart_background.jpg").toExternalForm();
//        if(backgroundImage!=null){
//            root.setStyle("-fx-background-image: url('" + backgroundImage + "'); " +
//                    "-fx-background-position: center center; " +
//                    "-fx-background-repeat: stretch;");
//        }

    }


    public static void main(String[] args) {
        launch(args);
    }
}
