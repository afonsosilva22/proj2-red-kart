module com.example.desktop {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires java.net.http;
    requires com.google.gson;
    requires com.fasterxml.jackson.annotation;

    opens com.example.desktop to javafx.fxml;
    opens com.example.desktop.controllers to javafx.fxml;
    opens com.example.desktop.models to javafx.fxml, com.google.gson, javafx.base;
    exports com.example.desktop;
}