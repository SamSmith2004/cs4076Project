package ul.cs4076project.Controller;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledExecutorService;

import jakarta.json.Json;
import jakarta.json.JsonException;
import jakarta.json.JsonObject;
import javafx.application.Platform;
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
        this.client.setUpdateListener(response -> {
            if (response instanceof ResponseType.TimetableResponse(Lecture[][] lectureArray)) {
                lectures = lectureArray;
                updateTimetableGrid(lectures);
                noticeLabel.setText("Timetable Updated");
            }
        });
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

                    // Get cell data for adding
                    final int finalRow = row - 1;
                    Map<String, String> cellDateTime = getCellDateTime(col, finalRow);
                    // ADD press event:
                    addItem.setOnAction(event ->
                            App.openAddALecturePopupDialogue(
                                    cellDateTime.get("day"),
                                    cellDateTime.get("fromTime"),
                                    cellDateTime.get("toTime")));

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

        noticeLabel.setText("Loading Timetable Data...");

        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "timetable");

        // GET Promise
        client.get("timetable", headers)
                .thenAccept(response -> Platform.runLater(() -> {
                    if (response instanceof ResponseType.TimetableResponse(Lecture[][] value)) {
                        lectures = value;
                        timetable = new String[5][9];
                        updateTimetableGrid(lectures);
                        noticeLabel.setText("");
                    } else {
                        noticeLabel.setText("ERROR: Invalid Response Type");
                    }
                }))
                .exceptionally(e -> {
                    Platform.runLater(() -> {
                        System.err.println("ERROR sending message: " + e.getMessage());
                        noticeLabel.setText("ERROR Sending Message to Server!");
                    });
                    return null;
                });
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

                    // Lecture index
                    final int finalCol = col;
                    final int finalRow = row - 1;
                    // passes lecture at cell index to events
                    removeItem.setOnAction(event -> removeLecture(lectures[finalCol][finalRow]));
                    replaceItem.setOnAction(event ->
                            App.openReplaceLecturePopupDialogue(lectures[finalCol][finalRow]));

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
     * Navigates back to the main menu view.
     *
     * @see ul.cs4076project.App
     * @see ul.cs4076project.Controller.MainController
     */
    @FXML
    protected void onBackToMenuClick() {
        App.loadMainView();
    }

    private void removeLecture(Lecture lecture) {
        if (client == null || !client.isConnected()) {
            noticeLabel.setText("Not Connected to Server");
            return;
        }

        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "removeLecture");

        try {
            JsonObject removeJson = Json.createObjectBuilder()
                    .add("id", Integer.parseInt(lecture.getId()))
                    .build();

            // POST Promise
            client.post(removeJson.toString(), headers)
                    .thenAccept(response -> Platform.runLater(() -> {
                        if (response instanceof ResponseType.StringResponse(String value) &&
                                value.equals("Lecture removed")) {
                            noticeLabel.setText("Lecture Removed");
                            loadTimetableData();
                        } else {
                            noticeLabel.setText("ERROR Occurred While Removing Lecture");
                        }
                    }))
                    .exceptionally(e -> {
                        // Substitute for try catch block
                        Platform.runLater(() -> {
                            Throwable cause = e.getCause();
                            if (cause instanceof IOException) {
                                System.err.println("IOException occurred: " + cause.getMessage());
                            } else if (cause instanceof JsonException) {
                                System.err.println("JsonException occurred: " + cause.getMessage());
                            } else {
                                System.err.println("Error occurred: " + cause.getMessage());
                            }
                            noticeLabel.setText("ERROR Occurred While Removing Lecture");
                        });
                        return null;
                    });
        } catch (JsonException e) {
            System.err.println("JsonException occurred: " + e.getMessage());
            noticeLabel.setText("ERROR Occurred While Removing Lecture");
        } catch (NumberFormatException e) {
            System.err.println("NumberFormatException occurred: " + e.getMessage());
            noticeLabel.setText("ERROR Occurred While Removing Lecture");
        }
    }

    @FXML
    private void onEarlyLectureButtonClick() {
        if (client == null || !client.isConnected()) {
            noticeLabel.setText("Not Connected to Server");
            return;
        }

        noticeLabel.setText("Processing early lectures...");

        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "earlyLecture");

        // UPDATE Promise
        client.update("message", headers)
                .thenAccept(response -> Platform.runLater(() -> {
                    if (response instanceof ResponseType.StringResponse(String value)) {
                        if (value.equals("earlyLecture")) {
                            System.out.println("Early Lecture Updated");
                            noticeLabel.setText("Lectures brought forward");
                            loadTimetableData();
                        } else if (value.equals("No early lectures found")) {
                            System.out.println("No lectures to bring forward");
                            noticeLabel.setText("No lectures to bring forward");
                        } else {
                            noticeLabel.setText("Unexpected response: " + value);
                        }
                    } else {
                        noticeLabel.setText("ERROR: Unexpected Response Type");
                    }
                }))
                .exceptionally(e -> {
                    Platform.runLater(() -> {
                        // Substitute for try catch block
                        Throwable cause = e.getCause();
                        if (cause instanceof JsonException) {
                            System.err.println("JsonException occurred: " + cause.getMessage());
                        } else if (cause instanceof IOException) {
                            System.err.println("IOException occurred: " + cause.getMessage());
                        } else {
                            System.err.println("Error occurred: " + cause.getMessage());
                        }
                        noticeLabel.setText("ERROR Occurred While Attempting Early Times");
                    });
                    return null;
                });
    }

    private Map<String, String> getCellDateTime(int col, int row) {
        Map<String, String> dateTime = new HashMap<>();

        // col -> day
        String day = switch (col) {
            case 0 -> "Monday";
            case 1 -> "Tuesday";
            case 2 -> "Wednesday";
            case 3 -> "Thursday";
            case 4 -> "Friday";
            default -> "";
        };

        // row -> time
        String fromTime = String.format("%02d00", row + 9);
        String toTime = String.format("%02d00", row + 10);

        dateTime.put("day", day);
        dateTime.put("fromTime", fromTime);
        dateTime.put("toTime", toTime);
        return dateTime;
    }
}