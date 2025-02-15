package ul.cs4076project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ul.cs4076project.Controller.MainController;
import ul.cs4076project.Controller.TimetableController;
import ul.cs4076project.Model.TCPClient;

import java.io.IOException;

public class App extends Application {
    private static Stage primaryStage;
    private static Scene mainScene;
    private static Scene timetableScene;
    private static TCPClient client;

    @Override
    public void start(Stage stage) throws IOException {
        try {
            client = new TCPClient();
        } catch (IOException e) {
            System.err.println("Error creating TCPClient: " + e.getMessage());
        }

        // Load the scenes
        FXMLLoader mainLoader = new FXMLLoader(App.class.getResource("View/main-view.fxml"));
        FXMLLoader timetableLoader = new FXMLLoader(App.class.getResource("View/timetable-view.fxml"));
        mainScene = new Scene(mainLoader.load(), 1280, 720);
        timetableScene = new Scene(timetableLoader.load(), 1280, 720);

        // Get the controllers after loading and initialize the TCPClient
        MainController mainController = mainLoader.getController();
        TimetableController timetableController = timetableLoader.getController();
        mainController.initializeWithClient(client);
        timetableController.initializeWithClient(client);

        primaryStage = stage;
        loadMainView();
    }

    public static void loadMainView() {
        primaryStage.setTitle("Main Menu");
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    public static void loadTimetableView() {
        primaryStage.setTitle("Lecture Timetable");
        primaryStage.setScene(timetableScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}