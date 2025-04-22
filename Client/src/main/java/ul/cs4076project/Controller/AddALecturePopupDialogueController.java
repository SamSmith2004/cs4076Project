package ul.cs4076project.Controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ul.cs4076project.App;
import ul.cs4076project.Model.ResponseType;
import ul.cs4076project.Model.TCPClient;

import jakarta.json.Json;
import jakarta.json.JsonException;
import jakarta.json.JsonObject;

public class AddALecturePopupDialogueController implements Initializable {
    private Map<String, String> lm05125_sem1_modules;
    protected Stage addALecturePopupStage;
    private List<String> originalToTimes;
    protected TCPClient client;

    @FXML
    protected ComboBox<String> comboBoxFromTimeField;

    @FXML
    protected ComboBox<String> comboBoxToTimeField;

    @FXML
    protected ComboBox<String> comboBoxDayField;

    @FXML
    protected ComboBox<String> comboBoxModuleField;

    @FXML
    protected TextField roomNumberField;

    @FXML
    protected TextField lecturerField;

    @FXML
    protected Label noticeLabel;

    public AddALecturePopupDialogueController() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Add module_id and module title to HashMap
        lm05125_sem1_modules = new HashMap<>();
        lm05125_sem1_modules.put("CS4006", "CS4006 - Intelligent Systems");
        lm05125_sem1_modules.put("CS4076", "CS4076 - Event Driven Programming");
        lm05125_sem1_modules.put("CS4115", "CS4115 - Data Structures and Algorithms");
        lm05125_sem1_modules.put("CS4815", "CS4815 - Computer Graphics");
        lm05125_sem1_modules.put("MA4413", "MA4413 - Statistics for Computing");

        comboBoxDayField.getItems().addAll("Monday", "Tuesday", "Wednesday", "Thursday", "Friday");
        comboBoxFromTimeField.getItems().addAll("0900", "1000", "1100", "1200", "1300", "1400", "1500", "1600", "1700");

        originalToTimes = new ArrayList<>();
        originalToTimes.addAll(List.of("1000", "1100", "1200", "1300", "1400", "1500", "1600", "1700", "1800"));
        comboBoxToTimeField.getItems().addAll(originalToTimes);

        for (Map.Entry<String, String> module : lm05125_sem1_modules.entrySet()) {
            comboBoxModuleField.getItems().add(module.getValue());
        }

        // Add an event listener to the "From Time" ComboBox
        comboBoxFromTimeField.getSelectionModel().selectedItemProperty()
                // Remove invalid end times when a start time is selected
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

    public void setDateTime(String day, String fromTime, String toTime) {
        comboBoxDayField.setValue(day);
        comboBoxFromTimeField.setValue(fromTime);
        comboBoxToTimeField.setValue(toTime);

        // I think it's nicer to make it have filled in values that are unchangeable rather than separate labels hence this:
        comboBoxDayField.setDisable(true);
        comboBoxFromTimeField.setDisable(true);
        comboBoxToTimeField.setDisable(true);
    }

    @FXML
    private void handleOKButton() {
        if (client == null || !client.isConnected()) {
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
        String moduleCode = comboBoxModuleField.getValue().split(" - ")[0];
        String day = comboBoxDayField.getValue().toUpperCase();

        JsonObject lectureJson = Json.createObjectBuilder()
                .add("id", -1) // ID placeholder
                .add("module", moduleCode)
                .add("lecturer", lecturerField.getText())
                .add("room", roomNumberField.getText())
                .add("fromTime", fromTime)
                .add("toTime", toTime)
                .add("day", day)
                .build();

        try {
            HashMap<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "addLecture");

            noticeLabel.setText("Processing...");

            // POST Promise
            client.post(lectureJson.toString(), headers)
                    .thenAccept(response -> Platform.runLater(() -> {
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
                    }))
                    .exceptionally(e -> {
                        // Substitute for the try catch block
                        Platform.runLater(() -> {
                            Throwable cause = e.getCause();
                            switch (cause) {
                                case JsonException jsonException -> {
                                    System.err.println("JSON error occurred: " + cause.getMessage());
                                    noticeLabel.setText("Error occurred while processing response");
                                }
                                case NullPointerException nullPointerException -> {
                                    System.err.println("NullPointerException occurred: " + cause.getMessage());
                                    noticeLabel.setText("Error occurred while processing response");
                                }
                                case IOException ioException -> {
                                    System.err.println("IOError occurred: " + cause.getMessage());
                                    noticeLabel.setText("Error occurred while communicating with server");
                                }
                                default -> {
                                    System.err.println("Unknown error occurred: " + cause.getMessage());
                                    noticeLabel.setText("An unexpected error occurred");
                                }
                            }
                        });
                        return null;
                    });
        } catch (JsonException e) {
            noticeLabel.setText("Error creating JSON object: " + e.getMessage());
        } catch (NullPointerException e) {
            noticeLabel.setText("Error: " + e.getMessage());
        }
    }
}