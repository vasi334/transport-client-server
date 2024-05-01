package org.example;

import java.io.Serializable;
import java.util.Objects;

public class Reservation implements Serializable {
    private String id;
    private String trip_id;
    private Integer seat_number;
    private String customer_first_name;
    private String customer_last_name;

    public Reservation(String trip_id, Integer seat_number, String customer_first_name, String customer_last_name) {
        this.trip_id = trip_id;
        this.seat_number = seat_number;
        this.customer_first_name = customer_first_name;
        this.customer_last_name = customer_last_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(String trip_id) {
        this.trip_id = trip_id;
    }

    public int getSeat_number() {
        return seat_number;
    }

    public void setSeat_number(Integer seat_number) {
        this.seat_number = seat_number;
    }

    public String getCustomer_first_name() {
        return customer_first_name;
    }

    public void setCustomer_first_name(String customer_first_name) {
        this.customer_first_name = customer_first_name;
    }

    public String getCustomer_last_name() {
        return customer_last_name;
    }

    public void setCustomer_last_name(String customer_last_name) {
        this.customer_last_name = customer_last_name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return Objects.equals(id, that.id) && Objects.equals(trip_id, that.trip_id) &&
                Objects.equals(seat_number, that.seat_number) &&
                Objects.equals(customer_first_name, that.customer_first_name) &&
                Objects.equals(customer_last_name, that.customer_last_name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, trip_id, seat_number, customer_first_name, customer_last_name);
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id='" + id + '\'' +
                ", trip_id='" + trip_id + '\'' +
                ", seat_number=" + seat_number +
                ", customer_first_name='" + customer_first_name + '\'' +
                ", customer_last_name='" + customer_last_name + '\'' +
                '}';
    }
}
