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
import ul.cs4076project.App;
import ul.cs4076project.Model.TCPClient;

import javax.json.JsonObject;

public class TimetableController implements Initializable {
    private TCPClient client;

    @FXML private Label debugLabel;
    @FXML private GridPane timetableGrid;

    public TimetableController() {}

    public void initializeWithClient(TCPClient client) {
        this.client = client;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Create empty cells for the timetable (excluding header row)
        for (int row = 1; row < 10; row++) {  // 9 time slots
            for (int col = 0; col < 5; col++) {  // 5 days
                Pane cellPane = new Pane();
                cellPane.setStyle("-fx-background-color: white; -fx-border-color: lightgray;");
                cellPane.setMinSize(100, 50);
                cellPane.setPrefSize(100, 50);

                timetableGrid.add(cellPane, col, row);
            }
        }
    }

    @FXML
    protected void onAddLectureClick() {
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("getTimetable", "true");

            JsonObject response = client.get("LM05125", headers);
            debugLabel.setText(response.toString());
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
            debugLabel.setText("Error sending message to server!");
        }
    }

    @FXML
    protected void onRemoveLectureClick() {
    }

    @FXML
    protected void onBackToMenuClick() {
        App.loadMainView();
    }
}
