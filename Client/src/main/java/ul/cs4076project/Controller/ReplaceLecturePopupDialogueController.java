package ul.cs4076project.Controller;

import jakarta.json.Json;
import jakarta.json.JsonException;
import jakarta.json.JsonObject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import ul.cs4076project.App;
import ul.cs4076project.Model.Lecture;
import ul.cs4076project.Model.ResponseType;

import java.io.IOException;
import java.util.HashMap;

public class ReplaceLecturePopupDialogueController extends AddALecturePopupDialogueController {
    Lecture lectureToRemove;

    public ReplaceLecturePopupDialogueController() {
        super();
    }

    public void preparePassedData(Lecture l) {
        this.lectureToRemove = l;
        comboBoxDayField.setValue(l.getDay().toString());
        comboBoxFromTimeField.setValue(l.getFromTime());
        comboBoxToTimeField.setValue(l.getToTime());

        comboBoxDayField.setDisable(true);
        comboBoxFromTimeField.setDisable(true);
        comboBoxToTimeField.setDisable(true);
    }

    @FXML
    private void handleSubmitBtn() {
        if (client == null || !client.isConnected()) {
            noticeLabel.setText("Not Connected to Server");
            System.out.println("Client is Not Connected to Server");
            return;
        }

        // Check if all fields are filled
        if (comboBoxModuleField.getSelectionModel().isEmpty() ||
                comboBoxDayField.getValue() == null ||
                comboBoxFromTimeField.getValue() == null ||
                comboBoxToTimeField.getValue() == null ||
                roomNumberField.getText().isEmpty() || lecturerField.getText().isEmpty()) {
            noticeLabel.setText("All Fields Must Be Filled");
            return;
        }

        try {
            // Use the colon index to determine the substring endpoint
            int colonIndexFrom = comboBoxFromTimeField.getValue().indexOf(":");
            if (colonIndexFrom == -1) {
                colonIndexFrom = 2;
            }
            int fromTimeInt = Integer.parseInt(comboBoxFromTimeField.getValue().substring(0, colonIndexFrom));

            int colonIndexTo = comboBoxToTimeField.getValue().indexOf(":");
            if (colonIndexTo == -1) {
                colonIndexTo = 2;
            }
            int toTimeInt = Integer.parseInt(comboBoxToTimeField.getValue().substring(0, colonIndexTo));

            String fromTime = String.format("%02d:00", fromTimeInt);
            String toTime = String.format("%02d:00", toTimeInt);
            String moduleCode = comboBoxModuleField.getValue().split(" - ")[0];
            String day = comboBoxDayField.getValue().toUpperCase();

            JsonObject lectureJson = Json.createObjectBuilder()
                    .add("id", lectureToRemove.getId())
                    .add("module", moduleCode)
                    .add("lecturer", lecturerField.getText())
                    .add("room", roomNumberField.getText())
                    .add("fromTime", fromTime)
                    .add("toTime", toTime)
                    .add("day", day)
                    .build();

            HashMap<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "replaceLecture");

            client.update(lectureJson.toString(), headers)
                    .thenAccept(response -> Platform.runLater(() -> {
                        if (response instanceof ResponseType.StringResponse(String value)) {
                            switch (value) {
                                case "Lecture added" -> {
                                    noticeLabel.setText("Lecture Replaced Successfully");
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
                        Platform.runLater(() -> {
                            Throwable cause = e.getCause();
                            switch (cause) {
                                case JsonException jsonException -> {
                                    noticeLabel.setText("An unexpected error occurred");
                                    System.err.println("JSON error: " + e.getMessage());
                                }
                                case NullPointerException nullPointerException -> {
                                    noticeLabel.setText("An unexpected error occurred");
                                    System.err.println("NullPointerException: " + e.getMessage());
                                }
                                case IOException ioException -> {
                                    System.err.println("IOError: " + cause.getMessage());
                                    noticeLabel.setText("An unexpected error occurred");
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
            noticeLabel.setText("An unexpected error occurred");
            System.err.println("JSON error occurred: " + e.getMessage());
        } catch (NullPointerException e) {
            noticeLabel.setText("An unexpected error occurred");
            System.err.println("NullPointerException occurred: " + e.getMessage());
        } catch (Exception e) {
            noticeLabel.setText("An unexpected error occurred");
            System.err.println("Unknown error occurred: " + e.getMessage());
        }
    }
}
