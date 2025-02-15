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
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import ul.cs4076project.App;
import ul.cs4076project.Model.Lecture;
import ul.cs4076project.Model.TCPClient;

public class TimetableController implements Initializable {
    private TCPClient client;
    private String[][] timetable;

    @FXML private Label debugLabel;
    @FXML private GridPane timetableGrid;

    public TimetableController() {}

    public void initializeWithClient(TCPClient client) {
        this.client = client;
        loadTimetableData();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        createEmptyCells();
        debugLabel.setText("Waiting for client initialization...");
    }

    private void createEmptyCells() {
        try {
            for (int row = 1; row < 10; row++) {
                for (int col = 0; col < 5; col++) {
                    Pane cellPane = new Pane();
                    cellPane.setStyle("-fx-background-color: white; -fx-border-color: lightgray;");
                    cellPane.setMinSize(100, 50);
                    cellPane.setPrefSize(100, 50);
                    timetableGrid.add(cellPane, col, row);
                }
            }
        } catch (Exception e) {
            debugLabel.setText("Error creating timetable cells: " + e.getMessage());
        }
    }

    private void loadTimetableData() {
        if (client == null) {
            debugLabel.setText("Error: Client not initialized!");
            return;
        }

        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "timetable");

            Object response = client.get("LM05125", headers);
            if (response instanceof Lecture[][] lectures) {
                timetable = new String[5][9];
                updateTimetableGrid(lectures);
            } else {
                debugLabel.setText("Error: Invalid response type");
            }
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
            debugLabel.setText("Error sending message to server!");
        }
    }

    private void updateTimetableGrid(Lecture[][] lectures) {
        for (int row = 1; row < 10; row++) {
            for (int col = 0; col < 5; col++) {
                Lecture lecture = lectures[col][row - 1];
                if (lecture != null) {
                    timetable[col][row - 1] = lecture.getModule();

                    Pane cellPane = new Pane();
                    cellPane.setStyle("-fx-background-color: white; -fx-border-color: lightgray;");
                    cellPane.setMinSize(100, 50);
                    cellPane.setPrefSize(100, 50);

                    VBox labelContainer = new VBox(2);
                    labelContainer.setStyle("-fx-padding: 5;");
                    labelContainer.getChildren().addAll(
                            createStyledLabel(lecture.getModule(), "-fx-font-weight: bold;"),
                            createStyledLabel(lecture.getLecturer(), "-fx-font-size: 10;"),
                            createStyledLabel(lecture.getRoom(), "-fx-font-size: 10;"),
                            createStyledLabel(lecture.getTime(), "-fx-font-size: 10;")
                    );

                    cellPane.getChildren().add(labelContainer);
                    timetableGrid.add(cellPane, col, row);
                }
            }
        }
        debugLabel.setText("");
    }

    private Label createStyledLabel(String text, String style) {
        Label label = new Label(text);
        label.setStyle(style);
        return label;
    }

    @FXML
    protected void onAddLectureClick() {
    }

    @FXML
    protected void onRemoveLectureClick() {
    }

    @FXML
    protected void onBackToMenuClick() {
        App.loadMainView();
    }
}
