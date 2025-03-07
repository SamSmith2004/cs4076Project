package ul.cs4076project.Controller;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import ul.cs4076project.App;
import ul.cs4076project.Model.Lecture;
import ul.cs4076project.Model.ResponseType;
import ul.cs4076project.Model.TCPClient;

import jakarta.json.Json;
import jakarta.json.JsonException;
import jakarta.json.JsonObject;

/**
 * Controller class for managing the "Remove a Lecture" popup dialogue.
 * Handles user input and validation for removing lectures from the timetable.
 */
public class RemoveALecturePopupDialogueController implements Initializable {
    /**
     * Stage for the remove lecture popup dialogue.
     */
    private Stage removeALecturePopupStage;

    /**
     * The {@link TCPClient} instance for server communication.
     */
    private TCPClient client;

    /**
     * Button for confirming the removal of a lecture.
     */
    @FXML
    private Button okButton;

    /**
     * ComboBox for selecting the start time of the lecture to be removed.
     */
    @FXML
    private ComboBox<String> comboBoxFromTimeField;

    /**
     * ComboBox for selecting the day of the week of the lecture to be removed.
     */
    @FXML
    private ComboBox<String> comboBoxDayField;

    /**
     * Label for displaying confirmation message.
     */
    @FXML
    private Label confirmLabel;

    /**
     * Label for displaying status messages to the user.
     */
    @FXML
    private Label noticeLabel;

    /**
     * Label for displaying the module name of the lecture to be removed.
     */
    @FXML
    private Label moduleName;

    /**
     * Label for displaying the lecturer's name of the lecture to be removed.
     */
    @FXML
    private Label lecturerName;

    /**
     * Label for displaying the room number of the lecture to be removed.
     */
    @FXML
    private Label roomNumber;

    /**
     * Label for displaying the time of the lecture to be removed.
     */
    @FXML
    private Label timeOfLecture;

    /**
     * Label for displaying the day of the lecture to be removed.
     */
    @FXML
    private Label dayOfLecture;

    /**
     * Default constructor for the {@code RemoveALecturePopupDialogueController}.
     * Initializes a new instance of the controller.
     */
    public RemoveALecturePopupDialogueController() {

    }

    /**
     * Initializes the controller and populates dropdown menus with available
     * options. Sets up listeners for time and day selection validation.
     *
     * @param location  The location used to resolve relative paths for the root
     *                  object
     * @param resources The resources used to localize the root object
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        comboBoxDayField.getItems().addAll("Monday", "Tuesday", "Wednesday", "Thursday", "Friday");
        comboBoxFromTimeField.getItems().addAll("0900", "1000", "1100", "1200", "1300", "1400", "1500", "1600", "1700");

        // Add a listener to the "From Time" ComboBox
        comboBoxFromTimeField.getSelectionModel().selectedItemProperty().addListener((
                observable, oldValue, newValue) -> removeEvent(newValue));

        comboBoxDayField.getSelectionModel().selectedItemProperty().addListener((
                observable, oldValue, newValue) -> removeEvent(newValue));
    }

    /**
     * Retrieves the lecture object based on the selected day and time.
     *
     * @return The {@link Lecture} object corresponding to the selected day and time
     * @see ul.cs4076project.Controller.TimetableController
     */

    private Lecture getLecture() {
        int row = switch (comboBoxDayField.getValue()) {
            case "Monday" -> 0;
            case "Tuesday" -> 1;
            case "Wednesday" -> 2;
            case "Thursday" -> 3;
            case "Friday" -> 4;
            default -> -1;
        };
        int col = switch (comboBoxFromTimeField.getValue().replaceFirst("^0", "")) {
            case "900" -> 0;
            case "1000" -> 1;
            case "1100" -> 2;
            case "1200" -> 3;
            case "1300" -> 4;
            case "1400" -> 5;
            case "1500" -> 6;
            case "1600" -> 7;
            case "1700" -> 8;
            default -> -1;
        };
        return App.getTimetableController().getLectures()[row][col];
    }

    /**
     * Sets the stage for this dialogue.
     *
     * @param dialogStage The stage for this popup dialogue
     */
    public void setDialogStage(Stage dialogStage) {
        this.removeALecturePopupStage = dialogStage;
    }

    /**
     * Initializes the controller with a TCP client for server communication.
     *
     * @param client The {@link TCPClient} instance to be used
     */
    public void initializeWithClient(TCPClient client) {
        this.client = client;
    }

    /**
     * Handles the OK button click event. Validates input fields and sends the
     * remove lecture request to the server. Updates the timetable view if
     * successful.
     *
     * @see ul.cs4076project.Model.TCPClient
     * @see ul.cs4076project.Model.ResponseHandler
     * @see ul.cs4076project.Model.ResponseType
     */
    @FXML
    private void handleOKButton() {
        if (client == null || !client.isConnected()) {
            noticeLabel.setText("Not Connected to Server");
            System.out.println("Client is not connected to server");
            return;
        }

        // Check if all fields are filled
        if (comboBoxFromTimeField.getSelectionModel().isEmpty() ||
                comboBoxDayField.getSelectionModel().isEmpty()) {
            noticeLabel.setText("All Fields Must Be Filled");
            return;
        }

        try {
            HashMap<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "removeLecture");

            Lecture lecture = getLecture();
            if (lecture == null) {
                noticeLabel.setText("No lecture found at selected time");
                return;
            }

            JsonObject removeJson = Json.createObjectBuilder()
                    .add("id", Integer.parseInt(lecture.getId()))
                    .build();

            ResponseType response = client.post(removeJson.toString(), headers);
            if (response instanceof ResponseType.StringResponse(String value) && value.equals("Lecture removed")) {
                noticeLabel.setText("Lecture Removed");
                App.loadTimetableView();
                removeALecturePopupStage.close();
            } else {
                noticeLabel.setText("ERROR Occurred While Removing Lecture");
            }
        } catch (IOException e) {
            System.err.println("IOException occurred: " + e.getMessage());
            noticeLabel.setText("ERROR Occurred While Removing Lecture");
        } catch (JsonException e) {
            System.err.println("JsonException occurred: " + e.getMessage());
            noticeLabel.setText("ERROR Occurred While Removing Lecture");
        } catch (NumberFormatException e) {
            System.err.println("NumberFormatException occurred: " + e.getMessage());
            noticeLabel.setText("ERROR Occurred While Removing Lecture");
        }
    }

    /**
     * Handles the event when a new value is selected in the ComboBoxes. Updates the
     * confirmation labels and visibility of the OK button.
     *
     * @param newValue The new value selected in the ComboBox
     */
    private void removeEvent(String newValue) {
        try {
            if (client == null || !client.isConnected()) {
                noticeLabel.setText("Not Connected to Server");
                System.out.println("Client is not connected to server");
                return;
            }

            if (newValue != null) {
                if (!comboBoxFromTimeField.getSelectionModel().isEmpty()
                        && !comboBoxDayField.getSelectionModel().isEmpty()) {
                    okButton.setVisible(true);

                    Lecture lecture = getLecture();
                    if (lecture == null) {
                        noticeLabel.setText("No Lecture at That Time Found");
                        confirmLabel.setText("");
                        moduleName.setText("");
                        lecturerName.setText("");
                        roomNumber.setText("");
                        timeOfLecture.setText("");
                        dayOfLecture.setText("");
                        okButton.setVisible(false);
                        return;
                    }

                    confirmLabel.setText("Confirm Removal:");
                    moduleName.setText("Module: " + lecture.getModuleString());
                    lecturerName.setText("Lecturer: " + lecture.getLecturer());
                    roomNumber.setText("Room: " + lecture.getRoom());
                    timeOfLecture.setText("Time: " + lecture.getTime());
                    dayOfLecture.setText("Day: " + lecture.getDayString());
                    noticeLabel.setText("");

                    resizeStage(500);
                }
            }
        } catch (NullPointerException e) {
            System.err.println("NullPointerException occurred: " + e.getMessage());
            noticeLabel.setText("ERROR Occurred While Removing Lecture");
        }
    }

    /**
     * Resizes the stage to the specified height.
     *
     * @param h The new height of the stage
     */
    private void resizeStage(int h) {
        Stage stage = (Stage) moduleName.getScene().getWindow();
        stage.setHeight(h);
    }
}