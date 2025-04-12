package ul.cs4076projectserver.Controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import ul.cs4076projectserver.Models.Lecture;

import java.net.URL;
import java.util.ResourceBundle;

public class TimetableController implements Initializable {
    private Lecture[][] lectures;
    private String[][] timetable;

    @FXML
    private GridPane timetableGrid;

    @FXML
    private Label noticeLabel;

    public TimetableController() {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        createEmptyCells();
        noticeLabel.setText("Waiting For Client Initialization...");
    }

    private void createEmptyCells() {
        try {
            for (int row = 1; row < 10; row++) {
                for (int col = 0; col < 5; col++) {
                    StackPane cellPane = new StackPane();
                    cellPane.setStyle("-fx-background-color: white; -fx-border-color: lightgray;");
                    cellPane.setMinSize(100, 50);
                    cellPane.setPrefSize(100, 50);

                    timetableGrid.add(cellPane, col, row);
                }
            }
        } catch (NullPointerException | IllegalArgumentException e) {
            noticeLabel.setText("ERROR Creating Timetable");
        }
    }

    public Runnable updateTimetableGrid(Lecture[][] lectures) {
        return new Runnable() {
            @Override
            public void run() {
                createEmptyCells();

                for (int row = 1; row < 10; row++) {
                    for (int col = 0; col < 5; col++) {
                        Lecture lecture = lectures[col][row - 1];
                        if (lecture != null) {
                            // do stuff
                        }
                    }
                }
                Platform.runLater(() -> noticeLabel.setText(""));
            }
        };
    }
}

// Reference:
/**
 * private void updateTimetableGrid(Lecture[][] lectures) {
 *         createEmptyCells();
 *
 *         for (int row = 1; row < 10; row++) {
 *             for (int col = 0; col < 5; col++) {
 *                 Lecture lecture = lectures[col][row - 1];
 *                 if (lecture != null) {
 *                     timetable[col][row - 1] = lecture.getModuleString();
 *
 *                     StackPane cellPane = new StackPane();
 *                     cellPane.setStyle("-fx-background-color: white; -fx-border-color: lightgray;");
 *                     cellPane.setMinSize(100, 50);
 *                     cellPane.setPrefSize(100, 50);
 *
 *                     VBox labelContainer = new VBox(2);
 *                     labelContainer.setStyle("-fx-padding: 5;");
 *                     labelContainer.setAlignment(javafx.geometry.Pos.CENTER);
 *                     labelContainer.getChildren().addAll(
 *                             createStyledLabel(lecture.getModuleString(), "-fx-font-size: 16; -fx-font-weight: bold;"),
 *                             createStyledLabel(lecture.getLecturer(), "-fx-font-size: 16;"),
 *                             createStyledLabel(lecture.getRoom(), "-fx-font-size: 16;"),
 *                             createStyledLabel(lecture.getTime(), "-fx-font-size: 16;"));
 *
 *                     // Create context menu for content cells
 *                     ContextMenu contextMenu = new ContextMenu();
 *                     MenuItem removeItem = new MenuItem("REMOVE");
 *                     MenuItem replaceItem = new MenuItem("REPLACE");
 *
 *                     String menuItemStyle = "-fx-font-size: 14px; -fx-font-weight: bold;";
 *                     removeItem.setStyle(menuItemStyle);
 *                     replaceItem.setStyle(menuItemStyle);
 *
 *                     // Lecture index
 *                     final int finalCol = col;
 *                     final int finalRow = row - 1;
 *                     // passes lecture at cell index to events
 *                     removeItem.setOnAction(event -> removeLecture(lectures[finalCol][finalRow]));
 *                     replaceItem.setOnAction(event ->
 *                             App.openReplaceLecturePopupDialogue(lectures[finalCol][finalRow]));
 *
 *                     contextMenu.getItems().addAll(removeItem, replaceItem);
 *                     contextMenu.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-radius: 3px;");
 *
 *                     cellPane.setOnMouseClicked(event -> {
 *                         if (event.getButton() == MouseButton.SECONDARY) {
 *                             contextMenu.show(cellPane, event.getScreenX(), event.getScreenY());
 *                         }
 *                     });
 *
 *                     cellPane.getChildren().add(labelContainer);
 *                     StackPane.setAlignment(labelContainer, javafx.geometry.Pos.CENTER);
 *                     timetableGrid.add(cellPane, col, row);
 *                 }
 *             }
 *         }
 *         noticeLabel.setText("");
 *     }
 */