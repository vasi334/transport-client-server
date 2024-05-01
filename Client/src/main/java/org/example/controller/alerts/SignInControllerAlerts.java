package org.example.controller.alerts;

import javafx.scene.control.Alert;

public class SignInControllerAlerts {

    public static void showLoginErrorAlert() {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setHeaderText("Invalid Credentials");
        errorAlert.setContentText("Please enter valid credentials!");
        errorAlert.showAndWait();
    }
}
