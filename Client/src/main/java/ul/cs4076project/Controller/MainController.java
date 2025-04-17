package ul.cs4076project.Controller;

import javafx.application.Platform;
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

/**
 * Controller class for the main application view. Manages server connection
 * status and navigation between different course timetables.
 */
public class MainController implements Initializable {

    /**
     * The {@link TCPClient} used for server communication.
     */
    private TCPClient client;

    /**
     * Flag indicating whether the client is connected to the server.
     */
    private boolean isConnectedToServer = false;

    /**
     * JavaFX UI elements for displaying server connection status.
     */
    @FXML
    private Label serverStatus;

    /**
     * JavaFX UI elements for displaying server connection status.
     */
    @FXML
    private Label serverStatusButton;

    /**
     * Label for displaying status messages to the user.
     */
    @FXML
    private Label noticeLabel;

    /**
     * Default constructor for MainController.
     */
    public MainController() {
    }

    /**
     * Initializes the controller with a TCP client connection. Updates the server
     * connection status display.
     *
     * @param client The {@link TCPClient} used for server communication
     */
    public void initializeWithClient(TCPClient client) {
        this.client = client;
        this.isConnectedToServer = (client != null);
        updateServerStatus();
    }

    /**
     * Initializes the controller for JavaFX. Sets up initial server status display
     * based on connection state.
     *
     * @param location  The location used to resolve relative paths for the root
     *                  object
     * @param resources The resources used to localize the root object
     *
     * @see ul.cs4076project.Model.TCPClient
     */
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

    /**
     * Updates the visual indicators of server connection status. Changes text
     * labels based on connection state.
     *
     * @see ul.cs4076project.Model.TCPClient
     */
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

    /**
     * Clears any notification messages displayed to the user.
     */
    public void clearNoticeLabels() {
        noticeLabel.setText("");
    }

    /**
     * Navigates to the LM05125 timetable view.
     *
     * @see ul.cs4076project.App
     */
    @FXML
    protected void onLM05125ButtonClick() {
        App.loadTimetableView();

        clearNoticeLabels();
    }

    /**
     * Attempts to access the LM11025 timetable. Currently, returns an invalid
     * action response.
     *
     * @see ul.cs4076project.Model.TCPClient
     * @see ul.cs4076project.Model.ResponseType
     */
    @FXML
    protected void onLM11025ButtonClick() {
        if (client == null || !client.isConnected()) {
            noticeLabel.setText("Not Connected to Server");
            return;
        }

        // CREATE Promise
        client.create("LM11025", new HashMap<>())
                .thenAccept(response -> Platform.runLater(() -> {
                    if (response instanceof ResponseType.StringResponse(String value)
                            && value.equals("Invalid Action Exception")) {
                        noticeLabel.setText("Invalid Action");
                    } else {
                        noticeLabel.setText("Unknown Error");
                    }
                }))
                .exceptionally(e -> {
                    Platform.runLater(() -> {
                        System.err.println("Error sending message: " + e.getMessage());
                        noticeLabel.setText("ERROR Sending Message to Server");
                    });
                    return null;
                });
    }

    /**
     * Attempts to access the LK04925 timetable. Currently, returns an invalid
     * action response.
     *
     * @see ul.cs4076project.Model.TCPClient
     * @see ul.cs4076project.Model.ResponseType
     */
    @FXML
    protected void onLK04925ButtonClick() {
        if (client == null || !client.isConnected()) {
            noticeLabel.setText("Not Connected to Server");
            return;
        }

        // CREATE Promise
        client.create("LM11025", new HashMap<>())
                .thenAccept(response -> Platform.runLater(() -> {
                    if (response instanceof ResponseType.StringResponse(String value)) {
                        if (value.equals("Invalid Action Exception")) {
                            noticeLabel.setText("Invalid Action");
                        } else {
                            noticeLabel.setText("Unknown Error");
                        }
                    } else {
                        noticeLabel.setText("Unknown Error");
                    }
                }))
                .exceptionally(e -> {
                    Platform.runLater(() -> {
                        System.err.println("Error sending message: " + e.getMessage());
                        noticeLabel.setText("ERROR Sending Message to Server!");
                    });
                    return null;
                });
    }

    /**
     * Handles the server connection button click event. Toggles between connecting
     * to and disconnecting from the server.
     *
     * @see ul.cs4076project.Model.TCPClient
     */
    @FXML
    protected void onServerFunctionButtonClick() {
        if (isConnectedToServer) {
            serverStatusButton.setText("STOP Server");
            isConnectedToServer = false;

            client.close();
            serverStatus.setText("Server Stopped!");
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
