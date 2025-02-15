package ul.cs4076project.Controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import ul.cs4076project.App;
import ul.cs4076project.Model.TCPClient;

import javax.json.JsonObject;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.HashMap;
import java.util.Map;

public class MainController implements Initializable {
    private TCPClient client;
    private boolean isConnectedToServer = false;

    @FXML
    private Label serverStatus;
    @FXML
    private Label serverStatusButton;
    @FXML private Label debugLabel;

    public MainController() throws IOException {
        try {
            client = new TCPClient();
            isConnectedToServer = true;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (isConnectedToServer) {
            serverStatus.setText("Connected to Server!");
            serverStatusButton.setText("STOP Server");
        } else {
            serverStatus.setText("Disconnected from Server...");
            serverStatusButton.setText("Attempt to Connect to Server");

        }
    }

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
    protected void onLM11025ButtonClick() throws IOException {
        App.loadTimetableView();
    }

    @FXML
    protected void onLK04925ButtonClick() {

    }

    @FXML
    protected void onServerFunctionButtonClick() {
        if (isConnectedToServer) {
            serverStatusButton.setText("STOP Server");
            isConnectedToServer = false;

            try {
                client.close();
                serverStatus.setText("Server Stopped!");
            } catch (Exception e) {
                System.err.println("Error stopping server: " + e.getMessage());
                serverStatus.setText("Error Stopping Server!");
            }
            serverStatusButton.setText("Attempt to Connect to Server");
        } else {
            try {
                client = new TCPClient();

                serverStatusButton.setText("STOP Server");
                serverStatus.setText("Connected to Server!");
                isConnectedToServer = true;
            } catch (IOException e) {
                System.err.println("Error creating TCPClient: " + e.getMessage());
                serverStatus.setText("ERROR Unable to Establish Connection with the Server");
            }
        }
    }
}
