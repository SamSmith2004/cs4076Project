package ul.cs4076projectserver.Controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.concurrent.Task;
import ul.cs4076projectserver.Models.Lecture;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class TimetableController implements Initializable {
    @FXML
    private GridPane timetableGrid;

    @FXML
    private Label noticeLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        createEmptyCells();
        noticeLabel.setText("Waiting For Client Initialization...");
    }

    private void createEmptyCells() {
        try {
            for (int row = 1; row < 10; row++) {
                for (int col = 0; col < 5; col++) {
                    StackPane cellPane = new StackPane();
                    cellPane.setStyle("-fx-background-color: white; -fx-border-color: lightgray;");
                    cellPane.setMinSize(100, 50);
                    cellPane.setPrefSize(100, 50);

                    timetableGrid.add(cellPane, col, row);
                }
            }
        } catch (NullPointerException | IllegalArgumentException e) {
            noticeLabel.setText("ERROR Creating Timetable");
        }
    }

    public Runnable updateTimetableGrid(ArrayList<Lecture> lectures) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                if (lectures == null) {
                    System.out.println("Lecture List is null");
                    Platform.runLater(() -> noticeLabel.setText("No lectures available"));
                    return null;
                }
                Platform.runLater(() -> createEmptyCells());

                // time, day -> row, col
                Lecture[][] newLectures = new Lecture[5][9];
                for (Lecture lecture : lectures) {
                    int col = switch (lecture.getDay()) {
                        case MONDAY -> 0;
                        case TUESDAY -> 1;
                        case WEDNESDAY -> 2;
                        case THURSDAY -> 3;
                        case FRIDAY -> 4;
                        default -> -1;
                    };
                    String timeString = lecture.getFromTime().replaceAll(":", "");
                    int row = switch (timeString) {
                        case "900", "0900" -> 0;
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
                    if (col != -1 && row != -1) {
                        newLectures[col][row] = lecture;
                    }
                }

                // Fill grid
                Platform.runLater(() -> {
                    for (int row = 1; row < 10; row++) {
                        for (int col = 0; col < 5; col++) {
                            Lecture lecture = newLectures[col][row - 1];
                            if (lecture != null) {
                                StackPane cellPane = new StackPane();
                                cellPane.setStyle("-fx-background-color: white; -fx-border-color: lightgray;");
                                cellPane.setMinSize(100, 50);
                                cellPane.setPrefSize(100, 50);

                                VBox labelContainer = new VBox(2);
                                labelContainer.setStyle("-fx-padding: 5;");
                                labelContainer.setAlignment(javafx.geometry.Pos.CENTER);
                                labelContainer.getChildren().addAll(
                                        createStyledLabel(lecture.getModuleString(),
                                                "-fx-font-size: 16; -fx-font-weight: bold;"),
                                        createStyledLabel(lecture.getLecturer(), "-fx-font-size: 16;"),
                                        createStyledLabel(lecture.getRoom(), "-fx-font-size: 16;"),
                                        createStyledLabel(lecture.getTime(), "-fx-font-size: 16;"));

                                cellPane.getChildren().add(labelContainer);
                                timetableGrid.add(cellPane, col, row);
                            }
                        }
                    }
                    noticeLabel.setText("");
                });
                return null;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
        return task;
    }

    private Label createStyledLabel(String text, String style) {
        Label label = new Label(text);
        label.setStyle(style);
        label.setAlignment(javafx.geometry.Pos.CENTER);
        return label;
    }
}