module ul.cs4076projectserver {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires jakarta.json;
    requires org.eclipse.parsson;
    requires java.sql;
    requires org.postgresql.jdbc;

    opens ul.cs4076projectserver to javafx.fxml;

    exports ul.cs4076projectserver;
    exports ul.cs4076projectserver.Controllers;

    opens ul.cs4076projectserver.Controllers to javafx.fxml;
}