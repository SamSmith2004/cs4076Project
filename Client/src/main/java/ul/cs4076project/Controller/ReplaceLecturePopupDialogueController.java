package ul.cs4076project.Controller;

import javafx.fxml.FXML;
import ul.cs4076project.Model.Lecture;

public class ReplaceLecturePopupDialogueController extends AddALecturePopupDialogueController {
    public ReplaceLecturePopupDialogueController() {
        super();
    }

    public void preparePassedData(Lecture l) {
        comboBoxDayField.setValue(l.getDay().toString());
        comboBoxFromTimeField.setValue(l.getFromTime());
        comboBoxToTimeField.setValue(l.getToTime());

        comboBoxDayField.setDisable(true);
        comboBoxFromTimeField.setDisable(true);
        comboBoxToTimeField.setDisable(true);
    }

    @FXML
    private void handleSubmitBtn() {

    }
}
