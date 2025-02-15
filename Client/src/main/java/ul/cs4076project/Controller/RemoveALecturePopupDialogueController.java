package ul.cs4076project.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class RemoveALecturePopupDialogueController {
    private Stage removeALecturePopupStage;

    @FXML
    private Button okButton;

    public void setDialogStage(Stage dialogStage) {
        this.removeALecturePopupStage = dialogStage;
    }

    @FXML
    private void handleOKButton() {
        removeALecturePopupStage.close();
    }
}