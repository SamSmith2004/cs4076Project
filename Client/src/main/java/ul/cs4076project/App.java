package ul.cs4076project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import ul.cs4076project.Controller.AddALecturePopupDialogueController;
import ul.cs4076project.Controller.MainController;
import ul.cs4076project.Controller.ReplaceLecturePopupDialogueController;
import ul.cs4076project.Controller.TimetableController;
import ul.cs4076project.Model.Lecture;
import ul.cs4076project.Model.TCPClient;

import java.io.IOException;

public class App extends Application {
    private static Stage primaryStage;
    private static Stage addALecturePopupStage;
    private static Stage replaceLecturePopupStage;
    private static Scene mainScene;
    private static Scene timetableScene;
    private static MainController mainController;
    private static TimetableController timetableController;
    private static AddALecturePopupDialogueController addALecturePopupDialogueController;
    private static ReplaceLecturePopupDialogueController replaceLecturePopupDialogueController;
    private static TCPClient client;

    public App() {

    }

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
        mainController = mainLoader.getController();
        timetableController = timetableLoader.getController();
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

        timetableController.loadTimetableData();

        primaryStage.show();
    }

    public static void updateClientReference(TCPClient newClient) {
        timetableController.initializeWithClient(newClient);
        client = newClient;
    }

    public static void openAddALecturePopupDialogue(String day, String fromTime, String toTime) {
        try {
            FXMLLoader addALecturePopupLoader = new FXMLLoader(
                    App.class.getResource("View/popup-dialogues/add-lecture-popup-dialogue.fxml"));
            Scene scene = new Scene(addALecturePopupLoader.load());

            if (addALecturePopupStage == null) {
                addALecturePopupStage = new Stage();
                addALecturePopupStage.initStyle(StageStyle.UTILITY);
                addALecturePopupStage.initModality(Modality.WINDOW_MODAL);
                addALecturePopupStage.initOwner(primaryStage);
                addALecturePopupStage.setResizable(false);
            }

            addALecturePopupStage.setTitle("ADD Lecture");
            addALecturePopupStage.setScene(scene);

            addALecturePopupDialogueController = addALecturePopupLoader.getController();
            addALecturePopupDialogueController.initializeWithClient(client);
            addALecturePopupDialogueController.setDialogStage(addALecturePopupStage);
            addALecturePopupDialogueController.setDateTime(day, fromTime, toTime);

            addALecturePopupStage.showAndWait();
        } catch (IOException | NullPointerException e) {
            System.err.println("Error loading AddALecturePopupDialogue: " + e.getMessage());
        }
    }

    public static void openReplaceLecturePopupDialogue(Lecture l) {
        try {
            FXMLLoader replaceLecturePopupLoader = new FXMLLoader(
                    App.class.getResource("View/popup-dialogues/replace-lecture-popup-dialogue.fxml"));
            Scene scene = new Scene(replaceLecturePopupLoader.load());

            if (replaceLecturePopupStage == null) {
                replaceLecturePopupStage = new Stage();
                replaceLecturePopupStage.initStyle(StageStyle.UTILITY);
                replaceLecturePopupStage.initModality(Modality.WINDOW_MODAL);
                replaceLecturePopupStage.initOwner(primaryStage);
                replaceLecturePopupStage.setResizable(false);
            }

            replaceLecturePopupStage.setTitle("ADD Lecture");
            replaceLecturePopupStage.setScene(scene);

            replaceLecturePopupDialogueController = replaceLecturePopupLoader.getController();
            replaceLecturePopupDialogueController.initializeWithClient(client);
            replaceLecturePopupDialogueController.setDialogStage(replaceLecturePopupStage);
            replaceLecturePopupDialogueController.preparePassedData(l);

            replaceLecturePopupStage.showAndWait();
        } catch (IOException | NullPointerException e) {
            System.err.println("Error loading AddALecturePopupDialogue: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void stop() throws Exception {
        if (client != null) {
            client.close();
        }
        super.stop();
    }
}