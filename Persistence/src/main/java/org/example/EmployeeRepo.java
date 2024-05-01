package org.example;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.interfaces.IEmployeeRepo;
import org.example.utils.JDBCUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Properties;


public class EmployeeRepo implements IEmployeeRepo {

    private JDBCUtils dbUtils;

    private static final Logger logger = LogManager.getLogger();

    public EmployeeRepo(Properties props) {

        logger.info("Initializing EmployeeDBRepository with properties: {} ", props);

        dbUtils = new JDBCUtils(props);
    }

    @Override
    public void save(Employee entity) {
        logger.traceEntry("Saving task {}", entity);

        Connection con = dbUtils.getConnection();

        try(PreparedStatement preSmt = con.prepareStatement("insert into employee " +
                "(email, first_name, last_name, password, phone_number) values (?, ?, ?, ?, ?)")) {
            preSmt.setString(1, entity.getEmail());
            preSmt.setString(2, entity.getFirst_name());
            preSmt.setString(3, entity.getLast_name());
            preSmt.setString(4, entity.getPassword());
            preSmt.setString(5, entity.getPhone_number());

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

        try(PreparedStatement preSmt = con.prepareStatement("delete from employee where id = ?")) {
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

        try(PreparedStatement preSmt = con.prepareStatement("delete from employee")) {

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
    public void update(Employee entity) {

    }

    @Override
    public boolean existsById(String id) {
        logger.traceEntry("Exists by id task");

        Connection con = dbUtils.getConnection();
        boolean exists = false;

        try(PreparedStatement preSmt = con.prepareStatement("SELECT employee.id FROM employee WHERE employee.id = ? LIMIT 1")) {

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
    public Employee findById(String id) {
        logger.traceEntry("Find by id task");

        Connection con = dbUtils.getConnection();
        Employee employee = null;

        try (PreparedStatement preSmt = con.prepareStatement("SELECT employee.password, employee.first_name, " +
                "employee.last_name, employee.email, employee.phone_number FROM employee WHERE employee.id = ?")) {
            preSmt.setString(1, id);

            try (ResultSet rs = preSmt.executeQuery()) {
                while (rs.next()) {
                    logger.trace("Found 1 record with the given id.");
                    String firstName = rs.getString("first_name");
                    String lastName = rs.getString("last_name");
                    String email = rs.getString("email");
                    String password = rs.getString("password");
                    String phone_number = rs.getString("phone_number");
                    logger.trace("Employee: First Name: {}, Last Name: {}, Email: {}, Phone Number: {}", firstName, lastName, email, phone_number);
                    employee = new Employee(email, firstName, lastName, password, phone_number);
                }
            }
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error db" + ex);
        }
        logger.traceExit();

        return employee;
    }

    @Override
    public long count() {
        logger.traceEntry("Count task");

        Connection con = dbUtils.getConnection();
        int count = 0;

        try(PreparedStatement preSmt = con.prepareStatement("SELECT COUNT(*) AS total FROM employee")) {

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
    public Iterable<Employee> findAll(){
        return null;
    }

    @Override
    public Optional<Employee> findEmployee(String email, String password) {
        logger.traceEntry("Find employee by email and password: {}, {}", email, password);

        Connection con = dbUtils.getConnection();
        Employee employee = null;

        try (PreparedStatement preSmt = con.prepareStatement("SELECT * FROM employee WHERE email = ? AND password = ?")) {
            preSmt.setString(1, email);
            preSmt.setString(2, password);

            try (ResultSet rs = preSmt.executeQuery()) {
                if (rs.next()) {
                    String firstName = rs.getString("first_name");
                    String lastName = rs.getString("last_name");
                    String phoneNumber = rs.getString("phone_number");

                    // Construct the Employee object
                    employee = new Employee(email, firstName, lastName, password, phoneNumber);
                    logger.trace("Found employee: {}", employee);
                }
            }
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error db" + ex);
        }
        logger.traceExit();

        return Optional.ofNullable(employee);
    }

}
