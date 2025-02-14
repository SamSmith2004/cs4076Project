package ul.cs4076project.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import ul.cs4076project.Model.TCPClient;

import javax.json.JsonObject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainController {
    private TCPClient client;
    
    @FXML private Label stopText;
    @FXML private Label debugLabel;
    
    public MainController() throws IOException {
        try {
            client = new TCPClient();
        } catch (IOException e) {
            System.err.println("Error creating TCPClient: " + e.getMessage());
        }
    }

    // EXAMPLE FUNCTION TO SEND TO SERVER

    // @FXML
    // protected void onLM05125ButtonClick() {
    // try {

    // JsonObject response = client.post("Hello from JavaFX!");
    // welcomeText.setText("Server response: " + response.toString());
    // } catch (IOException e) {
    // System.err.println("Error sending message: " + e.getMessage());
    // welcomeText.setText("Error sending message to server!");
    // }
    // }

    @FXML
    protected void onLM05125ButtonClick() {
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("getTimetable", "true");

            JsonObject response = client.get("LM05125", headers);
            //System.out.println("Server response: " + response.toString());
            debugLabel.setText(response.toString());
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
            debugLabel.setText("Error sending message to server!");
        }
    }

    @FXML
    protected void onLM11025ButtonClick() {

    }

    @FXML
    protected void onLK04925ButtonClick() {

    }

    @FXML
    protected void onStopClick() {
        try {
            client.close();
            stopText.setText("Server Stopped!");
        } catch (Exception e) {
            System.err.println("Error stopping server: " + e.getMessage());
            stopText.setText("Error Stopping Server!");
        }
    }
}
