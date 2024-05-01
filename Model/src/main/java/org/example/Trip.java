package org.example;

import java.io.Serializable;
import java.sql.Date;
import java.util.Objects;

public class Trip implements Serializable {
    private String id;
    private String destination;
    private Date date;
    private String departure_time;
    private String arrival_time;
    private Integer available_seats;

    public Trip(String destination, Date date, String departure_time, String arrival_time, Integer available_seats) {
        this.destination = destination;
        this.date = date;
        this.departure_time = departure_time;
        this.arrival_time = arrival_time;
        this.available_seats = available_seats;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDeparture_time() {
        return departure_time;
    }

    public void setDeparture_time(String departure_time) {
        this.departure_time = departure_time;
    }

    public String getArrival_time() {
        return arrival_time;
    }

    public void setArrival_time(String arrival_time) {
        this.arrival_time = arrival_time;
    }

    public Integer getAvailable_seats() {
        return available_seats;
    }

    public void setAvailable_seats(Integer available_seats) {
        this.available_seats = available_seats;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trip trip = (Trip) o;
        return Objects.equals(id, trip.id) && Objects.equals(destination, trip.destination) && Objects.equals(date, trip.date) && Objects.equals(departure_time, trip.departure_time) && Objects.equals(arrival_time, trip.arrival_time) && Objects.equals(available_seats, trip.available_seats);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, destination, date, departure_time, arrival_time, available_seats);
    }

    @Override
    public String toString() {
        return "Trip{" +
                "id='" + id + '\'' +
                ", destination='" + destination + '\'' +
                ", date=" + date +
                ", departure_time='" + departure_time + '\'' +
                ", arrival_time='" + arrival_time + '\'' +
                ", available_seats=" + available_seats +
                '}';
    }
}
