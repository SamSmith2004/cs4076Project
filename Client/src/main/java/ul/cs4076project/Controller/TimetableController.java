package ul.cs4076project.Controller;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import ul.cs4076project.App;
import ul.cs4076project.Model.Lecture;
import ul.cs4076project.Model.ResponseType;
import ul.cs4076project.Model.TCPClient;

public class TimetableController implements Initializable {
    private TCPClient client;
    private String[][] timetable;
    private Lecture[][] lectures;

    @FXML
    private Label noticeLabel;
    @FXML
    private GridPane timetableGrid;

    public TimetableController() {
    }

    public void initializeWithClient(TCPClient client) {
        this.client = client;
        loadTimetableData();
    }

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

    private void updateTimetableGrid(Lecture[][] lectures) {
        createEmptyCells();

        for (int row = 1; row < 10; row++) {
            for (int col = 0; col < 5; col++) {
                Lecture lecture = lectures[col][row - 1];
                if (lecture != null) {
                    timetable[col][row - 1] = lecture.getModule();

                    StackPane cellPane = new StackPane();
                    cellPane.setStyle("-fx-background-color: white; -fx-border-color: lightgray;");
                    cellPane.setMinSize(100, 50);
                    cellPane.setPrefSize(100, 50);

                    VBox labelContainer = new VBox(2);
                    labelContainer.setStyle("-fx-padding: 5;");
                    labelContainer.setAlignment(javafx.geometry.Pos.CENTER);
                    labelContainer.getChildren().addAll(
                            createStyledLabel(lecture.getModule(), "-fx-font-size: 16; -fx-font-weight: bold;"),
                            createStyledLabel(lecture.getLecturer(), "-fx-font-size: 16;"),
                            createStyledLabel(lecture.getRoom(), "-fx-font-size: 16;"),
                            createStyledLabel(lecture.getTime(), "-fx-font-size: 16;"));

                    cellPane.getChildren().add(labelContainer);
                    StackPane.setAlignment(labelContainer, javafx.geometry.Pos.CENTER);
                    timetableGrid.add(cellPane, col, row);
                }
            }
        }
        noticeLabel.setText("");
    }

    private Label createStyledLabel(String text, String style) {
        Label label = new Label(text);
        label.setStyle(style);
        label.setAlignment(javafx.geometry.Pos.CENTER);
        return label;
    }

    @FXML
    protected void onAddLectureClick() {
        App.openAddALecturePopupDialogue();
    }

    @FXML
    protected void onRemoveLectureClick() {
        App.openRemoveALecturePopupDialogue();
    }

    @FXML
    protected void onBackToMenuClick() {
        App.loadMainView();
    }

    public Lecture[][] getLectures() {
        return lectures;
    }
}