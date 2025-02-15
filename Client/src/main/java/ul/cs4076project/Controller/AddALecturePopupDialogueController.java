package ul.cs4076project.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class AddALecturePopupDialogueController {
    private Stage addALecturePopupStage;

    @FXML
    private Button okButton;

    public void setDialogStage(Stage addALecturePopupStage) {
        this.addALecturePopupStage = addALecturePopupStage;
    }

    @FXML
    private void handleOKButton() {
        addALecturePopupStage.close();
    }
}