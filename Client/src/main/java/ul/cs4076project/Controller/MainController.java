package ul.cs4076project.Controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import ul.cs4076project.App;
import ul.cs4076project.Model.ResponseType;
import ul.cs4076project.Model.TCPClient;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    private TCPClient client;
    private boolean isConnectedToServer = false;

    @FXML
    private Label serverStatus;
    @FXML
    private Label serverStatusButton;
    @FXML
    private Label noticeLabel;

    public MainController() {
    }

    public void initializeWithClient(TCPClient client) {
        this.client = client;
        this.isConnectedToServer = (client != null);
        updateServerStatus();
    }

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

    private void updateServerStatus() {
        if (serverStatus != null && serverStatusButton != null) {
            if (isConnectedToServer) {
                serverStatus.setText("Connected to Server!");
                serverStatusButton.setText("STOP Server");
            } else {
                serverStatus.setText("Disconnected from Server...");
                serverStatusButton.setText("Attempt to Connect to Server");
            }
        }
    }

    public void clearNoticeLabels() {
        noticeLabel.setText("");
    }

    @FXML
    protected void onLM05125ButtonClick() {
        App.loadTimetableView();
        
        clearNoticeLabels();
    }

    @FXML
    protected void onLM11025ButtonClick() {
        if (!client.isConnected() || client == null) {
            noticeLabel.setText("Not Connected to Server");
            return;
        }

        try {
            ResponseType response = client.create("LM11025", new HashMap<>());
            if (response instanceof ResponseType.StringResponse(String value)
                    && value.equals("Invalid Action Exception")) {
                noticeLabel.setText("Invalid Action");
            } else {
                noticeLabel.setText("Unknown Error");
            }
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
            noticeLabel.setText("ERROR Sending Message to Server");
        }
    }

    @FXML
    protected void onLK04925ButtonClick() {
        if (!client.isConnected() || client == null) {
            noticeLabel.setText("Not Connected to Server");
            return;
        }

        try {
            ResponseType response = client.create("LM11025", new HashMap<>());
            if (response instanceof ResponseType.StringResponse(String value)) {
                if (value.equals("Invalid Action Exception")) {
                    noticeLabel.setText("Invalid Action");
                } else {
                    noticeLabel.setText("Unknown Error");
                }
            } else {
                noticeLabel.setText("Unknown Error");
            }
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
            noticeLabel.setText("ERROR Sending Message to Server!");
        }
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
                System.err.println("Error Stopping Server: " + e.getMessage());
                serverStatus.setText("Error Stopping Server");
            }
            serverStatusButton.setText("Attempt to Connect to Server");
        } else {
            try {
                client = new TCPClient();
                App.updateClientReference(client);

                serverStatusButton.setText("STOP Server");
                serverStatus.setText("Connected to Server");
                isConnectedToServer = true;
            } catch (IOException e) {
                System.err.println("Error creating TCPClient: " + e.getMessage());
                serverStatus.setText("ERROR Unable to Establish Connection with the Server");
            }
        }
    }
}
