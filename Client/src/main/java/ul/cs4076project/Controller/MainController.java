package ul.cs4076project.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import ul.cs4076project.Model.TCPClient;

import javax.json.JsonObject;
import java.io.IOException;

public class MainController {
    private TCPClient client;

    @FXML private Label welcomeText;
    @FXML private Label stopText;

    public MainController() throws IOException {
        try {
            client = new TCPClient();
        } catch (IOException e) {
            System.err.println("Error creating TCPClient: " + e.getMessage());
        }
    }

    @FXML
    protected void onHelloButtonClick() {
        try {
            JsonObject response = client.post("Hello from JavaFX!");
            welcomeText.setText("Server response: " + response.toString());
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
            welcomeText.setText("Error sending message to server!");
        }
    }

    @FXML
    protected void onStopButtonClick() {
        try {
            client.close();
            stopText.setText("Server stopped!");
        } catch (Exception e) {
            System.err.println("Error stopping server: " + e.getMessage());
            stopText.setText("Error stopping server!");
        }
    }
}
