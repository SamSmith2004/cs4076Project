package ul.cs4076project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ul.cs4076project.Controller.AddALecturePopupDialogueController;
import ul.cs4076project.Controller.MainController;
import ul.cs4076project.Controller.RemoveALecturePopupDialogueController;
import ul.cs4076project.Controller.TimetableController;
import ul.cs4076project.Model.TCPClient;

import java.io.IOException;

public class App extends Application {
    private static Stage primaryStage;
    private static Stage addALecturePopupStage;
    private static Stage removeALecturePopupStage;
    private static Scene mainScene;
    private static Scene timetableScene;
    private static MainController mainController;
    private static TimetableController timetableController;
    private static AddALecturePopupDialogueController addALecturePopupDialogueController;
    private static RemoveALecturePopupDialogueController removeALecturePopupDialogueController;
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

    public static void openAddALecturePopupDialogue() {
        try {
            FXMLLoader addALecturePopupLoader = new FXMLLoader(
                    App.class.getResource("View/popup-dialogues/add-lecture-popup-dialogue.fxml"));
            Scene scene = new Scene(addALecturePopupLoader.load(), 350, 500);
            addALecturePopupStage = new Stage();
            addALecturePopupStage.setTitle("ADD Lecture");
            setStage(addALecturePopupStage, scene);

            // Get the controller and set the dialog stage
            addALecturePopupDialogueController = addALecturePopupLoader.getController();
            addALecturePopupDialogueController.initializeWithClient(client);
            addALecturePopupDialogueController.setDialogStage(addALecturePopupStage);

            addALecturePopupStage.setResizable(false);

            addALecturePopupStage.showAndWait();
        } catch (IOException | NullPointerException e) {
            System.err.println("Error loading AddALecturePopupDialogue: " + e.getMessage());
        }
    }

    public static void openRemoveALecturePopupDialogue() {
        try {
            FXMLLoader removeALecturePopupLoader = new FXMLLoader(
                    App.class.getResource("View/popup-dialogues/remove-lecture-popup-dialogue.fxml"));
            Scene scene = new Scene(removeALecturePopupLoader.load(), 350, 300);
            removeALecturePopupStage = new Stage();
            removeALecturePopupStage.setTitle("REMOVE Lecture");
           setStage(removeALecturePopupStage, scene);

            // Get the controller and set the dialog stage
            removeALecturePopupDialogueController = removeALecturePopupLoader.getController();
            removeALecturePopupDialogueController.initializeWithClient(client);
            removeALecturePopupDialogueController.setDialogStage(removeALecturePopupStage);

            removeALecturePopupStage.setResizable(false);

            removeALecturePopupStage.showAndWait();
        } catch (IOException e) {
            System.err.println("Error loading RemoveALecturePopupDialogue: " + e.getMessage());
        }
    }

    private static void setStage(Stage stage, Scene scene) {
        try {
            stage.initStyle(StageStyle.UTILITY);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(primaryStage);
            stage.setScene(scene);
            stage.setResizable(false);
        } catch (NullPointerException | IllegalArgumentException e) {
            System.err.println("Error setting stage: " + e.getMessage());
        }
    }

    public static TimetableController getTimetableController() {
        return timetableController;
    }

    public static void main(String[] args) {
        launch();
    }
}