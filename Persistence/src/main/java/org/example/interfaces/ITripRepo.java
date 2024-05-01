package org.example.interfaces;


import org.example.Trip;

public interface ITripRepo extends Repository<Trip, String> {

    String findTripIdByDestinationDateTime(String destination, String date, String time);

    Trip findTripByDestinationDateTime(String destination, String date, String time);
}
