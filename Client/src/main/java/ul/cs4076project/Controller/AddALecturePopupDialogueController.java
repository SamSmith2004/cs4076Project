package ul.cs4076project.Controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ul.cs4076project.App;
import ul.cs4076project.Model.ResponseType;
import ul.cs4076project.Model.TCPClient;

import javax.json.Json;
import javax.json.JsonObject;

public class AddALecturePopupDialogueController implements Initializable {
    private Map<String, String> lm05125_sem1_modules;
    private Stage addALecturePopupStage;
    private List<String> originalToTimes;
    private TCPClient client;

    @FXML
    private ComboBox<String> comboBoxFromTimeField;

    @FXML
    private ComboBox<String> comboBoxToTimeField;

    @FXML
    private ComboBox<String> comboBoxDayField;

    @FXML
    private ComboBox<String> comboBoxModuleField;

    @FXML
    private TextField roomNumberField;

    @FXML
    private TextField lecturerField;

    @FXML
    private Label noticeLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Add module_id and module title to HashMap
        lm05125_sem1_modules = new HashMap<>();
        lm05125_sem1_modules.put("CS4006", "CS4006 - Intelligent Systems");
        lm05125_sem1_modules.put("CS4076", "CS4076 - Event Driven Programming");
        lm05125_sem1_modules.put("CS4115", "CS4115 - Data Structures and Algorithms");
        lm05125_sem1_modules.put("CS4185", "CS4815 - Computer Graphics");
        lm05125_sem1_modules.put("MA4413", "MA4413 - Statistics for Computing");

        comboBoxDayField.getItems().addAll("Monday", "Tuesday", "Wednesday", "Thursday", "Friday");
        comboBoxFromTimeField.getItems().addAll("0900", "1000", "1100", "1200", "1300", "1400", "1500", "1600", "1700");

        originalToTimes = new ArrayList<>();
        originalToTimes.addAll(List.of("1000", "1100", "1200", "1300", "1400", "1500", "1600", "1700", "1800"));
        comboBoxToTimeField.getItems().addAll(originalToTimes);

        for (Map.Entry<String, String> module : lm05125_sem1_modules.entrySet()) {
            comboBoxModuleField.getItems().add(module.getValue());
        }

        // Add a listener to the "From Time" ComboBox
        comboBoxFromTimeField.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
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

    public void initializeWithClient(TCPClient client) {
        this.client = client;
    }

    public void setDialogStage(Stage addALecturePopupStage) {
        this.addALecturePopupStage = addALecturePopupStage;
    }

    @FXML
    private void handleOKButton() {
        if (!client.isConnected() || client == null) {
            noticeLabel.setText("Not Connected to Server");
            System.out.println("Client is Not Connected to Server");
            return;
        }

        // Check if all fields are filled
        if (comboBoxModuleField.getSelectionModel().isEmpty() ||
                comboBoxFromTimeField.getSelectionModel().isEmpty() ||
                comboBoxToTimeField.getSelectionModel().isEmpty() ||
                comboBoxDayField.getSelectionModel().isEmpty() ||
                roomNumberField.getText().isEmpty() || lecturerField.getText().isEmpty()) {
            noticeLabel.setText("All Fields Must Be Filled");
            return;
        }

        String fromTime = comboBoxFromTimeField.getValue().substring(0, 2) + ":00";
        String toTime = comboBoxToTimeField.getValue().substring(0, 2) + ":00";

        JsonObject lectureJson = Json.createObjectBuilder()
                .add("module", comboBoxModuleField.getValue().split(" - ")[0])
                .add("lecturer", lecturerField.getText())
                .add("room", roomNumberField.getText())
                .add("fromTime", fromTime)
                .add("toTime", toTime)
                .add("day", comboBoxDayField.getValue())
                .build();

        try {
            HashMap<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "addLecture");
            ResponseType response = client.post(lectureJson.toString(), headers);

            if (response instanceof ResponseType.StringResponse(String value)) {
                switch (value) {
                    case "Lecture added" -> {
                        noticeLabel.setText("Lecture Added Successfully");
                        addALecturePopupStage.close();
                        App.loadTimetableView();
                    }
                    case "Timeslot already taken" -> noticeLabel.setText("Timeslot Already Taken");
                    default -> noticeLabel.setText("Failed to add lecture: " + value);
                }
            } else {
                noticeLabel.setText("Unexpected Response Type");
            }
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
            noticeLabel.setText("ERROR Adding Lecture");
        }
    }
}