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

public class RemoveALecturePopupDialogueController implements Initializable {
    private Stage removeALecturePopupStage;
    private TCPClient client;

    @FXML private Button okButton;
    @FXML private ComboBox<String> comboBoxFromTimeField;
    @FXML private ComboBox<String> comboBoxDayField;

    @FXML private Label confirmLabel;
    @FXML private Label noticeLabel;
    @FXML private Label moduleName;
    @FXML private Label lecturerName;
    @FXML private Label roomNumber;
    @FXML private Label timeOfLecture;
    @FXML private Label dayOfLecture;

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

    public void setDialogStage(Stage dialogStage) {
        this.removeALecturePopupStage = dialogStage;
    }

    public void initializeWithClient(TCPClient client) {
        this.client = client;
    }

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

    private void removeEvent(String newValue) {
        try {
            if (client == null || !client.isConnected()) {
                noticeLabel.setText("Not Connected to Server");
                System.out.println("Client is not connected to server");
                return;
            }

            if (newValue != null) {
                if (!comboBoxFromTimeField.getSelectionModel().isEmpty() && !comboBoxDayField.getSelectionModel().isEmpty() ) {
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

    private void resizeStage(int h) {
        Stage stage = (Stage) moduleName.getScene().getWindow();
        stage.setHeight(h);
    }
}