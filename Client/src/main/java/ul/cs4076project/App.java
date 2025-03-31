package ul.cs4076project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import ul.cs4076project.Controller.AddALecturePopupDialogueController;
import ul.cs4076project.Controller.MainController;
import ul.cs4076project.Controller.TimetableController;
import ul.cs4076project.Model.TCPClient;

import java.io.IOException;

/**
 * Main application class that handles the JavaFX GUI application lifecycle and
 * scene management.
 * This class is responsible for initializing the application, loading views,
 * managing scene transitions,
 * and handling popup dialogues.
 *
 * @see ul.cs4076project.Controller.MainController
 * @see ul.cs4076project.Controller.TimetableController
 * @see ul.cs4076project.Model.TCPClient
 */
public class App extends Application {
    /**
     * The primary stage of the application that serves as the main window.
     */
    private static Stage primaryStage;

    /**
     * Stage for the "Add a Lecture" popup dialogue.
     */
    private static Stage addALecturePopupStage;

    /**
     * Scene containing the main menu view.
     */
    private static Scene mainScene;

    /**
     * Scene containing the timetable view.
     */
    private static Scene timetableScene;

    /**
     * Controller for managing the main menu view.
     *
     * @see ul.cs4076project.Controller.MainController
     */
    private static MainController mainController;

    /**
     * Controller for managing the timetable view.
     *
     * @see ul.cs4076project.Controller.AddALecturePopupDialogueController
     */
    private static TimetableController timetableController;

    /**
     * Controller for managing the "Add a Lecture" popup dialogue.
     *
     * @see ul.cs4076project.Controller.AddALecturePopupDialogueController
     */
    private static AddALecturePopupDialogueController addALecturePopupDialogueController;

    /**
     * TCP client instance for handling network communications.
     *
     * @see ul.cs4076project.Model.TCPClient
     */
    private static TCPClient client;

    /**
     * Default constructor for the {@code App} class. Initializes a new instance of
     * the application.
     */
    public App() {

    }

    /**
     * Initializes the JavaFX application, loads the main scenes, and sets up the
     * controllers. This method is called automatically by the JavaFX runtime.
     *
     * @param stage The primary stage for this application.
     * @throws IOException If an error occurs while loading the FXML files.
     */
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

    /**
     * Displays the main menu view in the primary stage.
     *
     * @see #mainScene
     * @see #primaryStage
     */
    public static void loadMainView() {
        primaryStage.setTitle("Main Menu");
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    /**
     * Displays the timetable view in the primary stage and loads the timetable
     * data.
     *
     * @see #timetableScene
     * @see #primaryStage
     */
    public static void loadTimetableView() {
        primaryStage.setTitle("Lecture Timetable");
        primaryStage.setScene(timetableScene);

        timetableController.loadTimetableData();

        primaryStage.show();
    }

    /**
     * Updates the TCP client reference across all controllers that need it.
     *
     * @param newClient The new TCP client instance to be used.
     * @see ul.cs4076project.Model.TCPClient
     */
    public static void updateClientReference(TCPClient newClient) {
        timetableController.initializeWithClient(newClient);
        client = newClient;
    }

    /**
     * Opens a modal popup dialogue for adding a new lecture.
     * The dialogue is initialized with the current TCP client instance.
     *
     * @see #addALecturePopupStage
     * @see #addALecturePopupDialogueController
     */
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

    /**
     * Configures a stage with common settings for popup dialogues.
     *
     * @param stage The stage to be configured.
     * @param scene The scene to be displayed in the stage.
     */
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

    /**
     * Returns the timetable controller instance.
     *
     * @return The current TimetableController instance.
     * @see ul.cs4076project.Controller.TimetableController
     */
    public static TimetableController getTimetableController() {
        return timetableController;
    }

    /**
     * The main entry point for the application.
     *
     * @param args CLI arguments.
     */
    public static void main(String[] args) {
        launch();
    }
}