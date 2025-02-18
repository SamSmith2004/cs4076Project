package ul.cs4076project.Controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import ul.cs4076project.App;
import ul.cs4076project.Model.Lecture;
import ul.cs4076project.Model.TCPClient;

import javax.json.JsonException;

public class RemoveALecturePopupDialogueController implements Initializable {
    private Stage removeALecturePopupStage;
    private TCPClient client;
    private List<String> originalToTimes;

    @FXML
    private Button okButton;

    @FXML
    private ComboBox<String> comboBoxFromTimeField;

    @FXML
    private ComboBox<String> comboBoxToTimeField;

    @FXML
    private ComboBox<String> comboBoxDayField;

    @FXML
    private Label noticeLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        comboBoxDayField.getItems().addAll("Monday", "Tuesday", "Wednesday", "Thursday", "Friday");
        comboBoxFromTimeField.getItems().addAll("0900", "1000", "1100", "1200", "1300", "1400", "1500", "1600", "1700");

        originalToTimes = new ArrayList<>();
        originalToTimes.addAll(List.of("1000", "1100", "1200", "1300", "1400", "1500", "1600", "1700", "1800"));
        comboBoxToTimeField.getItems().addAll(originalToTimes);

        // Add a listener to the "From Time" ComboBox
        comboBoxFromTimeField.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->

        {
            if (newValue != null) {
                comboBoxToTimeField.getItems().setAll(originalToTimes);

                List<String> validToTimes = originalToTimes.stream()
                        .filter(time -> time.compareTo(newValue) > 0)
                        .collect(Collectors.toList());

                comboBoxToTimeField.getItems().setAll(validToTimes);

                if (comboBoxToTimeField.getSelectionModel().getSelectedItem() == null ||
                        comboBoxToTimeField.getSelectionModel().getSelectedItem().compareTo(newValue) <= 0) {
                    comboBoxToTimeField.getSelectionModel().clearSelection();
                }
            }
        });
    }

    public void setDialogStage(Stage dialogStage) {
        this.removeALecturePopupStage = dialogStage;
    }

    public void initializeWithClient(TCPClient client) {
        this.client = client;
    }

    @FXML
    private void handleOKButton() {
        if (client == null) {
            noticeLabel.setText("Not connected to server");
            System.out.println("Client is not connected to server");
            return;
        }

        // Check if all fields are filled
        if (comboBoxFromTimeField.getSelectionModel().isEmpty() ||
                comboBoxToTimeField.getSelectionModel().isEmpty() ||
                comboBoxDayField.getSelectionModel().isEmpty()) {
            noticeLabel.setText("All fields must be filled");
            return;
        }

        String fromTime = comboBoxFromTimeField.getValue().substring(0, 2) + ":00";
        String toTime = comboBoxToTimeField.getValue().substring(0, 2) + ":00";

        try {
            HashMap<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "removeLecture");

            Lecture lecture = new Lecture(
                    "test",
                    "test",
                    "test",
                    fromTime,
                    toTime,
                    comboBoxDayField.getValue()
            );

            String response = (String)client.post(lecture.toJson().toString(), headers);
            noticeLabel.setText("Lecture removed");

            if (response.equals("Lecture removed")) {
                App.loadTimetableView();
                removeALecturePopupStage.close();
            } else {
                noticeLabel.setText("Error occurred while removing lecture");
            }
        } catch (IOException e) {
            System.err.println("IOException occurred: " + e.getMessage());
            noticeLabel.setText("Error occurred while removing lecture");
        } catch (JsonException e) {
            System.err.println("JsonException occurred: " + e.getMessage());
            noticeLabel.setText("Error occurred while removing lecture");
        }
    }
}