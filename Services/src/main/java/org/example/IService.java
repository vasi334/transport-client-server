package org.example;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IService {
    Optional<Employee> login(String email, String password, IEmployeeObserver client);
    void logout(String email);
    List<Trip> getAllTrips();
    Trip findTripByDestinationDateTime(String destination, String date, String time);
    String findTripIdByDestinationDateTime(String destination, String date, String time);
    List<Integer> findBookedSeatsForTrip(String tripId);
    String findCustomerNameForSeat(String searchedTripId, int parseInt);
    boolean checkIfSeatReserved(Integer numberOfSeats, String tripId);
    void saveReservation(Reservation reservation);
    void changeClient(IEmployeeObserver employeeObserver);
    Map<Integer, String> findAllSeatsWithCustomers(String tripID);
}