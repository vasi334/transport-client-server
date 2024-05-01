package org.example;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.interfaces.IReservationRepo;
import org.example.utils.JDBCUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ResevationRepo implements IReservationRepo {

        private JDBCUtils dbUtils;

        private static final Logger logger= LogManager.getLogger();

        public ResevationRepo(Properties props) {
            logger.info("Initializing ReservationDBRepository with properties: {} ", props);
            dbUtils = new JDBCUtils(props);
        }

        @Override
        public void save(Reservation entity) {
                logger.traceEntry("Saving task {}", entity);

                Connection con = dbUtils.getConnection();

                try(PreparedStatement preSmt = con.prepareStatement("insert into reservation " +
                        "(trip_id, seat_number, customer_first_name, customer_last_name) values (?, ?, ?, ?)")) {
                        preSmt.setString(1, String.valueOf(entity.getTrip_id()));
                        preSmt.setString(2, String.valueOf(entity.getSeat_number()));
                        preSmt.setString(3, entity.getCustomer_first_name());
                        preSmt.setString(4, entity.getCustomer_last_name());

                        int result = preSmt.executeUpdate();

                        logger.trace("Saved {} instances", result);
                }
                catch (SQLException ex) {
                        logger.error(ex);
                        System.err.println("Error db" + ex);
                }
                logger.traceExit();
        }

        @Override
        public void delete(String id) {
                logger.traceEntry("Deleting task with id {}", id);

                Connection con = dbUtils.getConnection();

                try(PreparedStatement preSmt = con.prepareStatement("delete from reservation where id = ?")) {
                        preSmt.setString(1, id);

                        int result = preSmt.executeUpdate();

                        logger.trace("Deleted {} instances", result);
                }
                catch (SQLException ex) {
                        logger.error(ex);
                        System.err.println("Error db" + ex);
                }
                logger.traceExit();
        }

        @Override
        public void deleteAll() {
                logger.traceEntry("Deleting task");

                Connection con = dbUtils.getConnection();

                try(PreparedStatement preSmt = con.prepareStatement("delete from reservation")) {

                        int result = preSmt.executeUpdate();

                        logger.trace("Deleted {} instances", result);
                }
                catch (SQLException ex) {
                        logger.error(ex);
                        System.err.println("Error db" + ex);
                }
                logger.traceExit();
        }

        @Override
        public void update(Reservation entity) {

        }

        @Override
        public boolean existsById(String id) {
                logger.traceEntry("Exists by id task");

                Connection con = dbUtils.getConnection();
                boolean exists = false;

                try(PreparedStatement preSmt = con.prepareStatement("SELECT reservation.id FROM" +
                        " reservation WHERE reservation.id = ? LIMIT 1")) {

                        preSmt.setString(1, id);

                        try (ResultSet rs = preSmt.executeQuery()) {
                                if (rs.next()) {
                                        exists = true;
                                        logger.trace("Found 1 record with the given id.");
                                }
                        }
                }
                catch (SQLException ex) {
                        logger.error(ex);
                        System.err.println("Error db" + ex);
                }
                logger.traceExit();

                return exists;
        }

        @Override
        public Reservation findById(String id) {
                logger.traceEntry("Find by id task");

                Connection con = dbUtils.getConnection();
                Reservation reservation = null;

                try (PreparedStatement preSmt = con.prepareStatement("SELECT reservation.trip_id, reservation.seat_number, reservation.customer_first_name, " +
                        "reservation.customer_last_name FROM reservation WHERE reservation.id = ?")) {
                        preSmt.setString(1, id);

                        try (ResultSet rs = preSmt.executeQuery()) {
                                while (rs.next()) {
                                        logger.trace("Found 1 record with the given id.");
                                        Integer trip_id = Integer.parseInt(rs.getString("trip_id"));
                                        String seat_number = rs.getString("seat_number");
                                        String customer_first_name = rs.getString("customer_first_name");
                                        String customer_last_name = rs.getString("customer_last_name");

                                        logger.trace("Reservation: Seat number: {}, Customer First Name: {}, Customer Last Name: {}", seat_number, customer_first_name, customer_last_name);

                                        reservation = new Reservation(seat_number, trip_id, customer_first_name, customer_last_name);
                                }
                        }
                } catch (SQLException ex) {
                        logger.error(ex);
                        System.err.println("Error db" + ex);
                }
                logger.traceExit();

                return reservation;
        }

        @Override
        public long count() {
                logger.traceEntry("Count task");

                Connection con = dbUtils.getConnection();
                int count = 0;

                try(PreparedStatement preSmt = con.prepareStatement("SELECT COUNT(*) AS total FROM reservation")) {

                        try (ResultSet rs = preSmt.executeQuery()) {
                                if (rs.next()) {
                                        count = rs.getInt("total");
                                        logger.trace("Found {} records.", count);
                                }
                        }
                }
                catch (SQLException ex) {
                        logger.error(ex);
                        System.err.println("Error db" + ex);
                }
                logger.traceExit();

                return count;
        }

        @Override
        public Iterable<Reservation> findAll() {
                return null;
        }

        public boolean checkIfSeatReserved(Integer seatNumber, String trip_id){
                logger.traceEntry("Check if seat is reserved");

                Connection con = null;
                boolean seatReserved = false;

                try {
                        con = dbUtils.getConnection();
                        PreparedStatement preparedStatement = con.prepareStatement("SELECT COUNT(*) AS total FROM reservation WHERE seat_number = ? AND trip_id = ?");
                        preparedStatement.setInt(1, seatNumber);
                        preparedStatement.setString(2, trip_id);

                        ResultSet rs = preparedStatement.executeQuery();

                        if (rs.next()) {
                                int count = rs.getInt("total");
                                seatReserved = count > 0;
                                logger.trace("Seat {} on trip {} is reserved: {}", seatNumber, trip_id, seatReserved);
                        }

                        rs.close();
                        preparedStatement.close();
                } catch (SQLException ex) {
                        logger.error("Error executing SQL query: {}", ex.getMessage());
                } finally {
                        if (con != null) {
                                try {
                                        con.close();
                                } catch (SQLException e) {
                                        logger.error("Error closing connection: {}", e.getMessage());
                                }
                        }
                }

                logger.traceExit();
                return seatReserved;
        }

        public List<Integer> findBookedSeatsForTrip(String tripId) {
                logger.traceEntry("Find booked seats for trip: {}", tripId);

                Connection con = dbUtils.getConnection();
                List<Integer> bookedSeats = new ArrayList<>();

                try (PreparedStatement preSmt = con.prepareStatement("SELECT seat_number FROM reservation WHERE trip_id = ?")) {
                        preSmt.setString(1, tripId);

                        try (ResultSet rs = preSmt.executeQuery()) {
                                while (rs.next()) {
                                        bookedSeats.add(rs.getInt("seat_number"));
                                }
                        }
                } catch (SQLException ex) {
                        logger.error(ex);
                        System.err.println("Error retrieving booked seats from the database: " + ex);
                }
                logger.traceExit();

                return bookedSeats;
        }

        public String findCustomerNameForSeat(String tripId, int seatNumber) {
                Connection con = dbUtils.getConnection();
                String customerName = "";

                try (PreparedStatement preSmt = con.prepareStatement("SELECT customer_first_name, customer_last_name FROM reservation WHERE trip_id = ? AND seat_number = ?")) {
                        preSmt.setString(1, tripId);
                        preSmt.setInt(2, seatNumber);

                        try (ResultSet rs = preSmt.executeQuery()) {
                                if (rs.next()) {
                                        String firstName = rs.getString("customer_first_name");
                                        String lastName = rs.getString("customer_last_name");
                                        customerName = firstName + " " + lastName;
                                }
                        }
                } catch (SQLException ex) {
                        logger.error(ex);
                        System.err.println("Error retrieving customer name from the database: " + ex);
                }
                return customerName;
        }

        @Override
        public String getNumberOfAvailableSeats(String tripId){
                return "1";
        }

        public Map<Integer, String> findAllSeatsWithCustomers(String tripId) {
                logger.traceEntry("Find all seats with customers for trip: {}", tripId);

                Connection con = dbUtils.getConnection();
                Map<Integer, String> seatsWithCustomers = new HashMap<>();

                try (PreparedStatement preSmt = con.prepareStatement("SELECT seat_number, customer_first_name, customer_last_name FROM reservation WHERE trip_id = ?")) {
                        preSmt.setString(1, tripId);

                        try (ResultSet rs = preSmt.executeQuery()) {
                                while (rs.next()) {
                                        int seatNumber = rs.getInt("seat_number");
                                        String firstName = rs.getString("customer_first_name");
                                        String lastName = rs.getString("customer_last_name");
                                        String customerName = firstName + " " + lastName;
                                        seatsWithCustomers.put(seatNumber, customerName);
                                }
                        }
                } catch (SQLException ex) {
                        logger.error(ex);
                        System.err.println("Error retrieving seats with customers from the database: " + ex);
                }
                logger.traceExit();

                return seatsWithCustomers;
        }
}
