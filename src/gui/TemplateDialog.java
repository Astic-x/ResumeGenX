package gui;

import javafx.scene.control.Alert;

public class TemplateDialog {

    public static void show(String templateName, String type, String fileName) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("ResumeGenX — " + type + " Ready");
        a.setHeaderText("\"" + templateName + "\" compiled successfully");
        a.setContentText("Resume generated as " + type + ".\nSource: " + fileName + "\n\nClick OK to download.");
        a.showAndWait();
    }
}