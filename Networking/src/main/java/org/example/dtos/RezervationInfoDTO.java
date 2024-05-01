package org.example.dtos;

import java.io.Serializable;

public class RezervationInfoDTO implements Serializable {
    private String first_name;
    private String last_name;
    private String trip_id;
    private String number_of_seat;

    public RezervationInfoDTO(String first_name, String last_name, String trip_id, String number_of_seat){
        this.first_name = first_name;
        this.last_name = last_name;
        this.trip_id = trip_id;
        this.number_of_seat = number_of_seat;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getTrip_id() {
        return trip_id;
    }

    public String getNumber_of_seat() {
        return number_of_seat;
    }

    @Override
    public String toString() {
        return "RezervationInfoDTO{" +
                "first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", trip_id='" + trip_id + '\'' +
                ", number_of_seat='" + number_of_seat + '\'' +
                '}';
    }
}
