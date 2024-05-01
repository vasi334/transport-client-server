package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.controller.SignInController;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class StartRpcClient extends Application {
    private static final int defaultPort = 55555;

    private static final String defaultServer = "localhost";

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StartRpcClient.class.getResource("/signin.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 750);
        SignInController signInController = fxmlLoader.getController();
        Properties propertiesClient = new Properties();
        try {
            propertiesClient.load(new FileReader("client.config"));
        } catch (Exception e) {
            throw new RuntimeException("Cannot find client.config " + e);
        }
        var portInt = Integer.parseInt(propertiesClient.getProperty("port"));
        var host = propertiesClient.getProperty("host");
        IService server = new EmployeeRpcProxyService(host, portInt);
        signInController.setServer(server);
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}