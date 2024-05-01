package org.example.interfaces;


import org.example.Reservation;

import java.util.List;
import java.util.Map;

public interface IReservationRepo extends Repository<Reservation, String> {
    String getNumberOfAvailableSeats(String tripId);

    List<Integer> findBookedSeatsForTrip(String tripId);

    String findCustomerNameForSeat(String searchedTripId, int parseInt);

    boolean checkIfSeatReserved(Integer seatNumber, String trip_id);

    Map<Integer, String> findAllSeatsWithCustomers(String tripId);
}
