package ul.cs4076project.Controller;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import ul.cs4076project.App;
import ul.cs4076project.Model.Lecture;
import ul.cs4076project.Model.ResponseType;
import ul.cs4076project.Model.TCPClient;

/**
 * Controller class for managing the timetable view and its interactions.
 * Handles displaying, updating, and managing the grid-based timetable
 * interface.
 */
public class TimetableController implements Initializable {
    /**
     * The TCP client instance used for server communication.
     *
     * @see ul.cs4076project.Model.TCPClient
     */
    private TCPClient client;

    /**
     * 2D array storing string representations of timetable entries.
     */
    private String[][] timetable;

    /**
     * 2D array storing {@link Lecture} objects for the timetable grid.
     */
    private Lecture[][] lectures;

    /**
     * Label for displaying status messages to the user.
     */
    @FXML
    private Label noticeLabel;

    /**
     * GridPane container for the timetable display.
     */
    @FXML
    private GridPane timetableGrid;

    /**
     * Default constructor for the TimetableController class.
     */
    public TimetableController() {
    }

    /**
     * Initializes the controller with a TCP client connection. Triggers loading of
     * timetable data from the server.
     *
     * @param client The TCP client used for server communication
     */
    public void initializeWithClient(TCPClient client) {
        this.client = client;
        loadTimetableData();
    }

    /**
     * Initializes the controller with an empty timetable grid and notification
     * message.
     * 
     * @param location
     *                  The location used to resolve relative paths for the root
     *                  object, or
     *                  {@code null} if the location is not known.
     *
     * @param resources
     *                  The resources used to localize the root object, or
     *                  {@code null} if
     *                  the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        createEmptyCells();
        noticeLabel.setText("Waiting For Client Initialization...");
    }

    /**
     * Creates the initial empty timetable grid structure. Sets up a 5x9 grid of
     * empty white cells with light gray borders.
     */
    private void createEmptyCells() {
        try {
            for (int row = 1; row < 10; row++) {
                for (int col = 0; col < 5; col++) {
                    StackPane cellPane = new StackPane();
                    cellPane.setStyle("-fx-background-color: white; -fx-border-color: lightgray;");
                    cellPane.setMinSize(100, 50);
                    cellPane.setPrefSize(100, 50);

                    // Context menu for empty cells
                    ContextMenu contextMenu = new ContextMenu();
                    MenuItem addItem = new MenuItem("ADD");
                    addItem.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
                    contextMenu.getItems().add(addItem);
                    contextMenu.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-radius: 3px;");

                    // Context menu handler
                    cellPane.setOnMouseClicked(event -> {
                        if (event.getButton() == MouseButton.SECONDARY) {
                            contextMenu.show(cellPane, event.getScreenX(), event.getScreenY());
                        }
                    });

                    timetableGrid.add(cellPane, col, row);
                }
            }
        } catch (NullPointerException | IllegalArgumentException e) {
            noticeLabel.setText("ERROR Creating Timetable");
        }
    }

    /**
     * Fetches and loads timetable data from the server. Updates the grid view with
     * lecture information if successful.
     *
     * @see ul.cs4076project.Model.TCPClient
     * @see ul.cs4076project.Model.ResponseHandler
     * @see ul.cs4076project.Model.ResponseType
     */
    public void loadTimetableData() {
        if (client == null) {
            noticeLabel.setText("ERROR: Client not Initialized!");
            createEmptyCells();
            return;
        }

        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "timetable");

            ResponseType response = client.get("timetable", headers);
            if (response instanceof ResponseType.TimetableResponse(Lecture[][] value)) {
                lectures = value;
                timetable = new String[5][9];
                updateTimetableGrid(lectures);
            } else {
                noticeLabel.setText("ERROR: Invalid Response Type");
            }

        } catch (IOException e) {
            System.err.println("ERROR sending message: " + e.getMessage());
            noticeLabel.setText("ERROR Sending Message to Server!");
        }
    }

    /**
     * Updates the timetable grid with lecture information. Populates cells with
     * module, lecturer, room, and time details.
     *
     * @param lectures 2D array of {@link Lecture} objects representing the
     *                 timetable
     */
    private void updateTimetableGrid(Lecture[][] lectures) {
        createEmptyCells();

        for (int row = 1; row < 10; row++) {
            for (int col = 0; col < 5; col++) {
                Lecture lecture = lectures[col][row - 1];
                if (lecture != null) {
                    timetable[col][row - 1] = lecture.getModuleString();

                    StackPane cellPane = new StackPane();
                    cellPane.setStyle("-fx-background-color: white; -fx-border-color: lightgray;");
                    cellPane.setMinSize(100, 50);
                    cellPane.setPrefSize(100, 50);

                    VBox labelContainer = new VBox(2);
                    labelContainer.setStyle("-fx-padding: 5;");
                    labelContainer.setAlignment(javafx.geometry.Pos.CENTER);
                    labelContainer.getChildren().addAll(
                            createStyledLabel(lecture.getModuleString(), "-fx-font-size: 16; -fx-font-weight: bold;"),
                            createStyledLabel(lecture.getLecturer(), "-fx-font-size: 16;"),
                            createStyledLabel(lecture.getRoom(), "-fx-font-size: 16;"),
                            createStyledLabel(lecture.getTime(), "-fx-font-size: 16;"));

                    // Create context menu for content cells
                    ContextMenu contextMenu = new ContextMenu();
                    MenuItem removeItem = new MenuItem("REMOVE");
                    MenuItem replaceItem = new MenuItem("REPLACE");

                    String menuItemStyle = "-fx-font-size: 14px; -fx-font-weight: bold;";
                    removeItem.setStyle(menuItemStyle);
                    replaceItem.setStyle(menuItemStyle);

                    contextMenu.getItems().addAll(removeItem, replaceItem);
                    contextMenu.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-radius: 3px;");

                    cellPane.setOnMouseClicked(event -> {
                        if (event.getButton() == MouseButton.SECONDARY) {
                            contextMenu.show(cellPane, event.getScreenX(), event.getScreenY());
                        }
                    });

                    cellPane.getChildren().add(labelContainer);
                    StackPane.setAlignment(labelContainer, javafx.geometry.Pos.CENTER);
                    timetableGrid.add(cellPane, col, row);
                }
            }
        }
        noticeLabel.setText("");
    }

    /**
     * Creates a styled label with specified text and CSS styling.
     *
     * @param text  The text content for the label
     * @param style CSS styling to be applied
     * @return A styled Label object
     */
    private Label createStyledLabel(String text, String style) {
        Label label = new Label(text);
        label.setStyle(style);
        label.setAlignment(javafx.geometry.Pos.CENTER);
        return label;
    }

    /**
     * Opens the dialog for adding a new lecture.
     *
     * @see ul.cs4076project.App
     * @see ul.cs4076project.Controller.AddALecturePopupDialogueController
     */
    @FXML
    protected void onAddLectureClick() {
        App.openAddALecturePopupDialogue();
    }

    /**
     * Opens the dialog for removing an existing lecture.
     *
     * @see ul.cs4076project.App
     * @see ul.cs4076project.Controller.RemoveALecturePopupDialogueController
     */
    @FXML
    protected void onRemoveLectureClick() {
        App.openRemoveALecturePopupDialogue();
    }

    /**
     * Navigates back to the main menu view.
     *
     * @see ul.cs4076project.App
     * @see ul.cs4076project.Controller.MainController
     */
    @FXML
    protected void onBackToMenuClick() {
        App.loadMainView();
    }

    /**
     * Retrieves the current lecture array representing the timetable.
     *
     * @return 2D array of {@link Lecture} objects
     */
    public Lecture[][] getLectures() {
        return lectures;
    }
}