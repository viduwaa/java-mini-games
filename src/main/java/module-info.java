module com.viduwa.minigames {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;

    opens com.viduwa.minigames to javafx.fxml;
    exports com.viduwa.minigames;
}