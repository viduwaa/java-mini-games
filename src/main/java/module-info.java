module com.viduwa.minigames {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql; // This is good for database access if you're using it


    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql.rowset;

    // Open packages that contain FXML controllers or classes with @FXML fields
    // This allows javafx.fxml to use reflection on them
    opens com.viduwa.minigames to javafx.fxml; // Keep this if your Main class or other FXML files have controllers in the base package
    opens com.viduwa.minigames.controllers to javafx.fxml; // <--- ADD THIS LINE!

    // Export packages that should be publicly accessible by other modules that 'require' this module
    exports com.viduwa.minigames;
    exports com.viduwa.minigames.controllers; // This is correct for making the controller class itself visible

}