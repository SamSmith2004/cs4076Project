package ul.cs4076projectserver;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ul.cs4076projectserver.Controllers.TimetableController;
import ul.cs4076projectserver.Models.Lecture;

import java.io.IOException;

public class App extends Application {
    private Server server;
    private Lecture[][] lectures;

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

        new Thread(() -> {
            try {
                server = new Server();
                synchronized (this) {
                    lectures = server.getLectures();
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }).start();
    }

    public static void main(String[] args) {
        launch();
    }

    public void loadTimetableView() {
        primaryStage.setTitle("Server GUI");
        primaryStage.setScene(timetableScene);

        Platform.runLater(timetableController.updateTimetableGrid(lectures));

        primaryStage.show();
    }

    public Lecture[][] getLectures() { return lectures; }

    public synchronized void setLectures(Lecture[][] lectures) {
        this.lectures = lectures;
        Platform.runLater(timetableController.updateTimetableGrid(lectures));
    }
}