package ul.cs4076project.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import ul.cs4076project.Model.TCPClient;

public class RemoveALecturePopupDialogueController {
    private Stage removeALecturePopupStage;
    private TCPClient client;

    @FXML
    private Button okButton;

    public void setDialogStage(Stage dialogStage) {
        this.removeALecturePopupStage = dialogStage;
    }

    public void initializeWithClient(TCPClient client) {
        this.client = client;
    }

    @FXML
    private void handleOKButton() {
        removeALecturePopupStage.close();
    }
}