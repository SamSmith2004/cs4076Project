module ul.cs4076project {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;

    opens ul.cs4076project to javafx.fxml;
    exports ul.cs4076project;
    exports ul.cs4076project.Controller;
    opens ul.cs4076project.Controller to javafx.fxml;
}