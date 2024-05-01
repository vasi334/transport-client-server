package org.example.server;

import org.example.*;
import org.example.interfaces.IEmployeeRepo;
import org.example.interfaces.IReservationRepo;
import org.example.interfaces.ITripRepo;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServicesImpl implements IService {
    private IEmployeeRepo employeeRepo;
    private IReservationRepo reservationRepo;
    private ITripRepo tripRepo;
    private Map<String, IEmployeeObserver> loggedEmployees;
    private final int defaultThreadsNo = 5;

    public ServicesImpl(IEmployeeRepo employeeRepo, IReservationRepo reservationRepo, ITripRepo tripRepo) {
        this.employeeRepo = employeeRepo;
        this.reservationRepo = reservationRepo;
        this.tripRepo = tripRepo;
        this.loggedEmployees = new ConcurrentHashMap<>();
    }

    @Override
    public synchronized Optional<Employee> login(String email, String password, IEmployeeObserver client) {
        Optional<Employee> employeeOptional = employeeRepo.findEmployee(email, password);

        if (employeeOptional.isPresent()) {
            Employee employee = employeeOptional.get();
            String passwordEmployee = employee.getPassword();

            if (!passwordEmployee.equals(password)) {
                throw new IllegalArgumentException("Password incorrect!");
            } else {
                if (loggedEmployees.containsKey(email)) {
                    throw new IllegalArgumentException("Employee is already logged in!");
                }
                loggedEmployees.put(email, client);
                return Optional.of(employee);
            }
        }
        return Optional.empty();
    }

    @Override
    public synchronized void logout(String email) {
        if (!loggedEmployees.containsKey(email)) {
            throw new IllegalArgumentException("Employee is not logged in!");
        }
        loggedEmployees.remove(email);
    }

    @Override
    public List<Trip> getAllTrips() {
        return (List<Trip>) tripRepo.findAll();
    }

    @Override
    public Trip findTripByDestinationDateTime(String destination, String date, String time) {
        return tripRepo.findTripByDestinationDateTime(destination, date, time);
    }

    @Override
    public String findTripIdByDestinationDateTime(String destination, String date, String time) {
        return tripRepo.findTripIdByDestinationDateTime(destination, date, time);
    }

    @Override
    public List<Integer> findBookedSeatsForTrip(String tripId) {
        return reservationRepo.findBookedSeatsForTrip(tripId);
    }

    @Override
    public String findCustomerNameForSeat(String searchedTripId, int parseInt) {
        return reservationRepo.findCustomerNameForSeat(searchedTripId, parseInt);
    }

    @Override
    public boolean checkIfSeatReserved(Integer seatNumber, String tripId) {
        return reservationRepo.checkIfSeatReserved(seatNumber, tripId);
    }

    @Override
    public synchronized void saveReservation(Reservation reservation) {
        reservationRepo.save(reservation);
        notifyClients();
    }

    @Override
    public void changeClient(IEmployeeObserver employeeObserver) {
        ;
    }

    public Map<Integer, String> findAllSeatsWithCustomers(String tripId){
        return reservationRepo.findAllSeatsWithCustomers(tripId);
    }

    private void notifyClients() {
        ExecutorService executor = Executors.newFixedThreadPool(defaultThreadsNo);
        for(var client : loggedEmployees.values()){
            if(client == null)
                continue;
            executor.execute(client::add_reservation);
        }
    }
}
