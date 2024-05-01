package org.example.controller;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.Employee;
import org.example.IEmployeeObserver;
import org.example.IService;
import org.example.controller.alerts.SignInControllerAlerts;

import java.io.IOException;
import java.util.Optional;

public class SignInController implements IEmployeeObserver{

    @FXML
    private Button buttonLogin, buttonSignup;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label welcomeText;

    private IService server;

    private Employee employee;

    public void setServer(IService server){
        this.server = server;
    }

    public void handleLogin() {
        String email = usernameField.getText();
        String password = passwordField.getText();
        Optional<Employee> optional = Optional.empty();

        try {
            optional = server.login(email, password, this);

            if (optional.isEmpty()) {
                SignInControllerAlerts.showLoginErrorAlert();
            } else {
                employee = optional.get();

                loadHomeScene();
            }
        } catch (Exception e) {
            SignInControllerAlerts.showLoginErrorAlert();
        }
    }

    public void handleSignup() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/signup.fxml"));
        Parent root = loader.load();

        Stage window = (Stage) buttonSignup.getScene().getWindow();
        window.setScene(new Scene(root, 600, 750));
    }

    private void loadHomeScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/home.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) buttonLogin.getScene().getWindow();

            HomeController homeController = loader.getController();

            homeController.setServer(server, employee, stage);

            homeController.populateTripsTable();

            Stage window = (Stage) buttonLogin.getScene().getWindow();
            window.setScene(new Scene(root, 600, 750));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void add_reservation() {
        ;
    }
}
