package org.example;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.interfaces.ITripRepo;
import org.example.utils.JDBCUtils;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class TripRepo implements ITripRepo {

        private JDBCUtils dbUtils;

        private static final Logger logger= LogManager.getLogger();

        public TripRepo(Properties props) {
            logger.info("Initializing TripDBRepository with properties: {} ", props);
            dbUtils = new JDBCUtils(props);
        }

        @Override
        public void save(Trip entity) {
                logger.traceEntry("Saving task {}", entity);

                Connection con = dbUtils.getConnection();

                try(PreparedStatement preSmt = con.prepareStatement("insert into trip " +
                        "(destination, date, departure_time, arrival_time, available_seats) values (?, ?, ?, ?, ?)")) {
                        preSmt.setString(1, entity.getDestination());
                        preSmt.setString(2, entity.getDate().toString());
                        preSmt.setString(3, entity.getDeparture_time());
                        preSmt.setString(4, entity.getArrival_time());
                        preSmt.setInt(5, entity.getAvailable_seats());

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

                try(PreparedStatement preSmt = con.prepareStatement("delete from trip where id = ?")) {
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

                try(PreparedStatement preSmt = con.prepareStatement("delete from trip")) {

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
        public void update(Trip entity) {

        }

        @Override
        public boolean existsById(String id) {
                logger.traceEntry("Exists by id task");

                Connection con = dbUtils.getConnection();
                boolean exists = false;

                try(PreparedStatement preSmt = con.prepareStatement("SELECT trip.id FROM trip WHERE trip.id = ? LIMIT 1")) {

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
        public Trip findById(String id) {
                logger.traceEntry("Find by id task");

                Connection con = dbUtils.getConnection();
                Trip trip = null;

                try (PreparedStatement preSmt = con.prepareStatement("SELECT trip.destination, " +
                        "trip.date, trip.departure_time, trip.arrival_time, trip. available_seats FROM trip WHERE trip.id = ?")) {

                        preSmt.setString(1, id);

                        try (ResultSet rs = preSmt.executeQuery()) {
                                while (rs.next()) {
                                        logger.trace("Found 1 record with the given id.");
                                        String destination = rs.getString("destination");
                                        Date date = rs.getDate("date");
                                        String departure_time = rs.getString("departure_time");
                                        String arrival_time = rs.getString("arrival_time");
                                        int available_seats = Integer.parseInt(rs.getString("available_seats"));
                                        logger.trace("Trip: Destination: {}, Date: {}, Departure time: {}," +
                                                " Arrival time: {}, Available seats: {}", destination, date, departure_time, arrival_time, available_seats);
                                        trip = new Trip(destination, date, departure_time, arrival_time, available_seats);
                                }
                        }
                } catch (SQLException ex) {
                        logger.error(ex);
                        System.err.println("Error db" + ex);
                }
                logger.traceExit();

                return trip;
        }

        @Override
        public long count() {
                logger.traceEntry("Count task");

                Connection con = dbUtils.getConnection();
                int count = 0;

                try(PreparedStatement preSmt = con.prepareStatement("SELECT COUNT(*) AS total FROM trip")) {

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
        public Iterable<Trip> findAll() {
                logger.traceEntry("Find all trips");

                Connection con = dbUtils.getConnection();
                List<Trip> trips = new ArrayList<>();

                try (PreparedStatement preSmt = con.prepareStatement("SELECT * FROM trip")) {
                        try (ResultSet rs = preSmt.executeQuery()) {
                                while (rs.next()) {
                                        String id = rs.getString("id");
                                        String destination = rs.getString("destination");
                                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                        Date date = new Date(dateFormat.parse(rs.getString("date")).getTime());
                                        String departure_time = rs.getString("departure_time");
                                        String arrival_time = rs.getString("arrival_time");
                                        int available_seats = rs.getInt("available_seats");

                                        Trip trip = new Trip(destination, date, departure_time, arrival_time, available_seats);
                                        trips.add(trip);
                                }
                        } catch (ParseException e) {
                                throw new RuntimeException(e);
                        }
                } catch (SQLException ex) {
                        logger.error(ex);
                        System.err.println("Error db" + ex);
                }
                logger.traceExit();

                return trips;
        }

        public Trip findByDestinationDateTime(String destination, String date, String time) {
                logger.traceEntry("Find by destination, date, and time: Destination={}, Date={}, Time={}", destination, date, time);

                Connection con = dbUtils.getConnection();
                Trip trip = null;

                try (PreparedStatement preSmt = con.prepareStatement("SELECT * FROM trip WHERE destination = ? AND date = ? AND arrival_time = ?")) {
                        preSmt.setString(1, destination);
                        preSmt.setString(2, date);
                        preSmt.setString(3, time);

                        try (ResultSet rs = preSmt.executeQuery()) {
                                while (rs.next()) {
                                        String tripDestination = rs.getString("destination");
                                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                        Date date1 = new Date(dateFormat.parse(rs.getString("date")).getTime());
                                        String departureTime = rs.getString("departure_time");
                                        String arrivalTime = rs.getString("arrival_time");
                                        int availableSeats = rs.getInt("available_seats");

                                        trip = new Trip(tripDestination, date1, departureTime, arrivalTime, availableSeats);
                                }
                        } catch (ParseException e) {
                                throw new RuntimeException(e);
                        }
                } catch (SQLException ex) {
                        logger.error(ex);
                        System.err.println("Error retrieving trip from the database: " + ex);
                }
                logger.traceExit();

                return trip;
        }

        public String findTripIdByDestinationDateTime(String destination, String date, String time) {
                logger.traceEntry("Find trip ID by destination, date, and time: Destination={}, Date={}, Time={}", destination, date, time);

                Connection con = dbUtils.getConnection();
                String tripId = null;

                try (PreparedStatement preSmt = con.prepareStatement("SELECT id FROM trip WHERE destination = ? AND date = ? AND arrival_time = ?")) {
                        preSmt.setString(1, destination);
                        preSmt.setString(2, date);
                        preSmt.setString(3, time);

                        try (ResultSet rs = preSmt.executeQuery()) {
                                if (rs.next()) {
                                        tripId = rs.getString("id");
                                }
                        }
                } catch (SQLException ex) {
                        logger.error(ex);
                        System.err.println("Error retrieving trip ID from the database: " + ex);
                }
                logger.traceExit();

                return tripId;
        }

        @Override
        public Trip findTripByDestinationDateTime(String destination, String date, String time) {
                logger.traceEntry("Find trip by destination, date, and time: Destination={}, Date={}, Time={}", destination, date, time);

                Connection con = dbUtils.getConnection();
                Trip trip = null;

                try (PreparedStatement preSmt = con.prepareStatement("SELECT * FROM trip WHERE destination = ? AND date = ? AND arrival_time = ?")) {
                        preSmt.setString(1, destination);
                        preSmt.setString(2, date);
                        preSmt.setString(3, time);

                        try (ResultSet rs = preSmt.executeQuery()) {
                                if (rs.next()) {
                                        String tripDestination = rs.getString("destination");
                                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                        Date date1 = new Date(dateFormat.parse(rs.getString("date")).getTime());
                                        String departureTime = rs.getString("departure_time");
                                        String arrivalTime = rs.getString("arrival_time");
                                        int availableSeats = rs.getInt("available_seats");

                                        trip = new Trip(tripDestination, date1, departureTime, arrivalTime, availableSeats);
                                }
                        } catch (ParseException e) {
                                throw new RuntimeException(e);
                        }
                } catch (SQLException ex) {
                        logger.error(ex);
                        System.err.println("Error retrieving trip from the database: " + ex);
                }
                logger.traceExit();

                return trip;
        }
}