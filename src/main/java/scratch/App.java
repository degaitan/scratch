package scratch;

import scratch.model.Character;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import scratch.model.Coordinates;
import scratch.model.Program;
import scratch.view.MainView;
import scratch.viewmodel.MainViewModel;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {

        Coordinates origin = new Coordinates(195, 195);
        Character pen = new Character(-90, origin);
        Program program = new Program(pen);

        MainViewModel mainViewModel = new MainViewModel(program);

        MainView mainView = new MainView(mainViewModel);

        Scene scene = new Scene(mainView, 800, 600);
        primaryStage.setTitle("Scratch");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}