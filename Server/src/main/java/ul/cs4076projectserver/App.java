package ul.cs4076projectserver;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ul.cs4076projectserver.Controllers.TimetableController;
import ul.cs4076projectserver.Models.Lecture;

import java.io.IOException;
import java.util.ArrayList;

public class App extends Application {
    private static Server server;
    private static ArrayList<Lecture> lectureList;

    private static Stage primaryStage;
    private static Scene timetableScene;
    private static TimetableController timetableController;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader timetableLoader = new FXMLLoader(App.class.getResource("Views/timetable.fxml"));
        timetableScene = new Scene(timetableLoader.load(), 1280, 720);
        timetableController = timetableLoader.getController();
        primaryStage = stage;
        loadTimetableView();

        server = new Server();
        new Thread(() -> {
            lectureList = server.getLectureList();
            Platform.runLater(() -> timetableController.updateTimetableGrid(lectureList).run());
            server.startServer();
        }).start();
    }

    @Override
    public void stop() throws Exception {
        if (server != null) {
            server.stopServer();
        }
        super.stop();
    }

    public static void main(String[] args) {
        launch();
    }

    public void loadTimetableView() {
        primaryStage.setTitle("Server GUI");
        primaryStage.setScene(timetableScene);
        primaryStage.show();
    }

    public static synchronized void fillLectureList() {
        if (server != null) {
            ArrayList<Lecture> updatedLectures = server.getLectureList();
            System.out.println(updatedLectures);
            Platform.runLater(() -> timetableController.updateTimetableGrid(updatedLectures).run());
        }
    }
}