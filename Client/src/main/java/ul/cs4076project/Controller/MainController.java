package ul.cs4076project.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import ul.cs4076project.Model.TCPClient;

import javax.json.JsonObject;
import java.io.IOException;

public class MainController {
    private TCPClient client;

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

    }

    @FXML
    protected void onLM11025ButtonClick() {

    }

    @FXML
    protected void onLK04925ButtonClick() {

    }

    @FXML
    protected void onStop() {

    }
}
