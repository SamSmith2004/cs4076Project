package ul.cs4076project;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
    private static Stage primaryStage;
    private static Scene mainScene;
    private static Scene timetableScene;

    @Override
    public void start(Stage stage) throws IOException {
        mainScene = new Scene(new FXMLLoader(App.class.getResource("View/main-view.fxml")).load(), 1280, 720);
        timetableScene = new Scene(new FXMLLoader(App.class.getResource("View/timetable-view.fxml")).load(), 1280, 720);
        primaryStage = stage;
        loadMainView();
    }

    public static void loadMainView() throws IOException {
        primaryStage.setTitle("Main Menu");
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    public static void loadTimetableView() throws IOException {
        primaryStage.setTitle("Lecture Timetable");
        primaryStage.setScene(timetableScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}