package org.example.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.*;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.example.controller.alerts.HomeControllerAlerts;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class HomeController implements IEmployeeObserver {

    private IService server;
    private static final Logger logger = LogManager.getLogger();

    @FXML
    private Button buttonLogout;

    @FXML
    private TableView<Trip> tripsTable;

    @FXML
    private TableColumn<Trip, String> destinationColumn;

    @FXML
    private TableColumn<Trip, String> dateColumn;

    @FXML
    private TableColumn<Trip, String> timeColumn;

    @FXML
    private TableColumn<Trip, Integer> availableSeatsColumn;

    @FXML
    private TableView<Trip> singleTripTable;

    @FXML
    private TableColumn<Trip, String> singleDestinationColumn;

    @FXML
    private TableColumn<Trip, String> singleDateColumn;

    @FXML
    private TableColumn<Trip, String> singleTimeColumn;

    @FXML
    private TableColumn<Trip, Integer> singleAvailableSeatsColumn;

    @FXML
    private TextField searchDestinationField;

    @FXML
    private TextField searchDateTimeField;

    @FXML
    private TextField searchTimeField, destinationDateField, destinationTimeField;

    @FXML
    private TextField customerFirstNameField, customerLastNameField, destinationField, numberOfSeatsField;

    @FXML
    private TableView<String> seatInformationTable;

    @FXML
    private TableColumn<String, String> seatNumberColumn;

    @FXML
    private TableColumn<String, String> customerNameColumn;

    private Employee employee;


    public void setServer(IService server,  Employee employee, Stage stage){
        this.server = server;
        this.employee = employee;
        setClient();
    }

    public void setClient(){
        server.changeClient(this);
    }

    @FXML
    private void handleSearch() {
        String destination = searchDestinationField.getText();
        String date = searchDateTimeField.getText();
        String time = searchTimeField.getText();

        logger.debug("Searching for trip with destination: {}, date: {}, time: {}", destination, date, time);

        String searchedTripId = server.findTripIdByDestinationDateTime(destination, date, time);

        Map<Integer, String> bookedSeats = server.findAllSeatsWithCustomers(searchedTripId);

        Trip searchedTrip = server.findTripByDestinationDateTime(destination, date, time);

        singleTripTable.getItems().clear();
        if (searchedTrip != null) {
            logger.info("Trip found: {}", searchedTrip);

            ObservableList<Trip> tripObservableList = FXCollections.observableArrayList(searchedTrip);
            singleTripTable.setItems(tripObservableList);

            singleDestinationColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDestination()));
            singleDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate().toString()));
            singleTimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getArrival_time()));
            singleAvailableSeatsColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getAvailable_seats()).asObject());

            populateSeatInformationTable(searchedTrip, bookedSeats);
        } else {
            logger.warn("No trip found for destination: {}, date: {}, time: {}", destination, date, time);
            HomeControllerAlerts.showNoTripFoundErrorAlert();
        }
    }

    @FXML
    private void handleBookSeat() {
        logger.debug("Handling booking seat...");

        String customerFirstName = customerFirstNameField.getText();
        String customerLastName = customerLastNameField.getText();
        String destinationDate = destinationDateField.getText();
        String destinationTime = destinationTimeField.getText();
        String destination = destinationField.getText();
        Integer numberOfSeats = Integer.valueOf(numberOfSeatsField.getText());

        String tripId = server.findTripIdByDestinationDateTime(destination, destinationDate, destinationTime);

        if (!server.checkIfSeatReserved(numberOfSeats, tripId)) {

            Reservation reservation = new Reservation(tripId, numberOfSeats, customerFirstName, customerLastName);

            logger.info("Creating reservation: {}", reservation);

            server.saveReservation(reservation);
        } else {
            logger.warn("Seat already reserved for trip: {}", tripId);
            HomeControllerAlerts.showBookSeatErrorAlert();
        }
    }

    private void populateSeatInformationTable(Trip searchedTrip, Map<Integer, String> seatsWithCustomers) {
        logger.debug("Populating seat information table...");

        ObservableList<String> seatInfoList = FXCollections.observableArrayList();

        int totalSeats = searchedTrip.getAvailable_seats();

        for (int i = 1; i <= totalSeats; i++) {
            String seatInfo = "Seat " + i;
            seatInfoList.add(seatInfo);
        }

        seatInformationTable.setItems(seatInfoList);

        seatNumberColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));

        customerNameColumn.setCellValueFactory(cellData -> {
            String seatInfo = cellData.getValue();
            int seatNumber = Integer.parseInt(seatInfo.substring(seatInfo.indexOf("Seat ") + 5).split(" ")[0]);
            String customerName = seatsWithCustomers.get(seatNumber);
            return new SimpleStringProperty(customerName != null ? customerName : "");
        });

        logger.debug("Seat information table populated.");
    }

    public void populateTripsTable() {
        logger.debug("Populating trips table...");

        List<Trip> trips = server.getAllTrips();

        ObservableList<Trip> tripObservableList = FXCollections.observableArrayList(trips);

        tripsTable.setItems(tripObservableList);

        destinationColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDestination()));
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate().toString()));
        timeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getArrival_time()));
        availableSeatsColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getAvailable_seats()).asObject());

        logger.debug("Trips table populated.");
    }

    @FXML
    private void handleLogout() {
        logger.info("Logging out...");

        if(employee == null){
            return;
        }
        try {
            server.logout(employee.getEmail());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/signin.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            Stage currentStage = (Stage) buttonLogout.getScene().getWindow();
            currentStage.close();
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void add_reservation() {
        Platform.runLater(this::handleSearch);
    }
}