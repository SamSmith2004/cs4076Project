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
            FXMLLoader addALecturePopupLoader = new FXMLLoader(App.class.getResource("View/popup-dialogues/add-lecture-popup-dialogue.fxml"));
            Scene scene = new Scene(addALecturePopupLoader.load(), 350, 450);
            addALecturePopupStage = new Stage();
            addALecturePopupStage.setTitle("ADD Lecture");
            addALecturePopupStage.initStyle(StageStyle.UTILITY);
            addALecturePopupStage.initModality(Modality.WINDOW_MODAL);
            addALecturePopupStage.initOwner(primaryStage);
            addALecturePopupStage.setScene(scene);

            // Get the controller and set the dialog stage
            AddALecturePopupDialogueController controller = addALecturePopupLoader.getController();
            controller.setDialogStage(addALecturePopupStage);

            addALecturePopupStage.showAndWait();
        } catch (IOException e) {
            System.err.println("Error loading AddALecturePopupDialogue: " + e.getMessage());
        }
    }

    public static void openRemoveALecturePopupDialogue() {
        try {
            FXMLLoader removeALecturePopupLoader = new FXMLLoader(App.class.getResource("View/popup-dialogues/remove-lecture-popup-dialogue.fxml"));
            Scene scene = new Scene(removeALecturePopupLoader.load(), 350, 500);
            removeALecturePopupStage = new Stage();
            removeALecturePopupStage.setTitle("REMOVE Lecture");
            removeALecturePopupStage.initStyle(StageStyle.UTILITY);
            removeALecturePopupStage.initModality(Modality.WINDOW_MODAL);
            removeALecturePopupStage.initOwner(primaryStage);
            removeALecturePopupStage.setScene(scene);

            // Get the controller and set the dialog stage
            RemoveALecturePopupDialogueController controller = removeALecturePopupLoader.getController();
            controller.setDialogStage(removeALecturePopupStage);

            removeALecturePopupStage.showAndWait();
        } catch (IOException e) {
            System.err.println("Error loading RemoveALecturePopupDialogue: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch();
    }
}