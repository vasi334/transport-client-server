package org.example.controller.alerts;

import javafx.scene.control.Alert;

public class HomeControllerAlerts {
    public static void showBookSeatErrorAlert() {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setHeaderText("Seat already booked");
        errorAlert.setContentText("Please select another seat, this seat was already booked by another person.");
        errorAlert.showAndWait();
    }

    public static void showNoTripFoundErrorAlert() {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setHeaderText("No trip found");
        errorAlert.setContentText("Please select another trip, this trip was not found.");
        errorAlert.showAndWait();
    }
}
