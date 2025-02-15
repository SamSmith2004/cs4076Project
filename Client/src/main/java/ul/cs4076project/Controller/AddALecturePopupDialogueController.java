package ul.cs4076project.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddALecturePopupDialogueController {
    private Stage addALecturePopupStage;

    // FXML fields for the input controls
    @FXML
    private TextField timeField;

    @FXML
    private TextField roomNumberField;

    @FXML
    private TextField moduleNameField;

    @FXML
    private Button okButton;

    // Set the dialog stage
    public void setDialogStage(Stage addALecturePopupStage) {
        this.addALecturePopupStage = addALecturePopupStage;
    }

    // Handle the OK button click
    @FXML
    private void handleOKButton() {
        String time = timeField.getText();
        String roomNumber = roomNumberField.getText();
        String moduleName = moduleNameField.getText();

        if (time.isEmpty() || roomNumber.isEmpty() || moduleName.isEmpty()) {
            System.out.println("Please fill in all fields.");
            return; // Do not close the dialog if fields are empty
        }

        addALecturePopupStage.close();
    }
}