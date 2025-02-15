package ul.cs4076project.Controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import ul.cs4076project.App;

public class TimetableController implements Initializable {
    public TimetableController() throws IOException {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    protected void onAddLectureClick() {
    }

    @FXML
    protected void onRemoveLectureClick() {
    }

    @FXML
    protected void onBackToMenuClick() throws IOException {
        App.loadMainView();
    }
}
