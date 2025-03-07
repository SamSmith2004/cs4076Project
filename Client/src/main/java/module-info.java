/**
 * Module definition for the ul.cs4076project application.
 */
module ul.cs4076project {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires jakarta.json;
    requires org.eclipse.parsson;

    opens ul.cs4076project to javafx.fxml;

    exports ul.cs4076project;
    exports ul.cs4076project.Controller;
    exports ul.cs4076project.Model;

    opens ul.cs4076project.Controller to javafx.fxml;
}