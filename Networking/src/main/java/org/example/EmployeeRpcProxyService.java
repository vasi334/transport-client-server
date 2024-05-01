package org.example;

import org.example.rpcProtocol.request.Request;
import org.example.rpcProtocol.request.RequestType;
import org.example.rpcProtocol.response.Response;
import org.example.rpcProtocol.response.ResponseType;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class EmployeeRpcProxyService implements IService {
    private String host;
    private int port;

    private IEmployeeObserver client;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket connection;
    private volatile boolean finished;
    private BlockingQueue<Response> queueResponses;

    public EmployeeRpcProxyService(String host, int port) {
        this.host = host;
        this.port = port;
        queueResponses = new LinkedBlockingQueue<>();
    }

    private void initializeConnection() {
        try {
            connection = new Socket(host, port);
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            finished = false;
            startReader();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void closeConnection() {
        finished = true;
        try {
            input.close();
            output.close();
            connection.close();
            client = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Employee> login(String email, String password, IEmployeeObserver client) {
        initializeConnection();
        Employee employee = new Employee(email, "", "", password, "");
        Request request = new Request.Builder().type(RequestType.LOGIN).data(employee).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.getResponseType() == ResponseType.OK) {
            this.client = client;
            return Optional.of((Employee) response.getData());
        }
        if (response.getResponseType() == ResponseType.ERROR) {
            String error = response.getData().toString();
            closeConnection();
            throw new IllegalArgumentException(error);
        }
        return Optional.empty();
    }

    @Override
    public void logout(String username) {
        Request request = new Request.Builder().type(RequestType.LOGOUT).data(username).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.getResponseType() == ResponseType.ERROR) {
            String error = response.getData().toString();
            throw new IllegalArgumentException(error);
        }
    }

    public void changeClient(IEmployeeObserver employeeObserver) {
        this.client = employeeObserver;
    }

    private void sendRequest(Request request) {
        try {
            output.writeObject(request);
            output.flush();
        } catch (Exception e) {
            throw new IllegalArgumentException("Error sending object " + e);
        }
    }

    private Response readResponse() {
        Response response = null;
        try {
            System.out.println(queueResponses);
            response = queueResponses.take();
            System.out.println(queueResponses.size());
            System.out.println(response);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return response;
    }

    private void startReader() {
        Thread tw = new Thread(new ReaderThread());
        tw.start();
    }

    @Override
    public List<Trip> getAllTrips() {
        Request request = new Request.Builder().type(RequestType.GET_TRIPS).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.getResponseType() == ResponseType.ERROR) {
            String error = response.getData().toString();
            throw new IllegalArgumentException(error);
        }
        return (List<Trip>) response.getData();
    }

    @Override
    public Trip findTripByDestinationDateTime(String destination, String date, String time) {
        Request request = new Request.Builder()
                .type(RequestType.GET_TRIP)
                .data(new String[]{destination, date, time})
                .build();
        sendRequest(request);
        Response response = readResponse();
        if (response.getResponseType() == ResponseType.ERROR) {
            String error = response.getData().toString();
            throw new IllegalArgumentException(error);
        } else if (response.getData() instanceof Trip) {
            return (Trip) response.getData();
        } else {
            System.out.println(response.getData());
            throw new IllegalArgumentException("Response data is not a Trip object");
        }
    }

    @Override
    public String findTripIdByDestinationDateTime(String destination, String date, String time) {
        Request request = new Request.Builder()
                .type(RequestType.GET_TRIP_ID)
                .data(new String[]{destination, date, time})
                .build();
        sendRequest(request);
        Response response = readResponse();
        if (response.getResponseType() == ResponseType.ERROR) {
            String error = response.getData().toString();
            throw new IllegalArgumentException(error);
        }
        return (String) response.getData();
    }

    @Override
    public List<Integer> findBookedSeatsForTrip(String tripId) {
        Request request = new Request.Builder()
                .type(RequestType.GET_BOOKED_SEATS)
                .data(tripId)
                .build();
        sendRequest(request);
        Response response = readResponse();
        if (response.getResponseType() == ResponseType.ERROR) {
            String error = response.getData().toString();
            throw new IllegalArgumentException(error);
        }
        return (List<Integer>) response.getData();
    }

    @Override
    public String findCustomerNameForSeat(String searchedTripId, int parseInt) {
        Request request = new Request.Builder()
                .type(RequestType.GET_CUSTOMER_NAME)
                .data(new String[]{searchedTripId, String.valueOf(parseInt)})
                .build();
        sendRequest(request);
        Response response = readResponse();
        if (response.getResponseType() == ResponseType.ERROR) {
            String error = response.getData().toString();
            throw new IllegalArgumentException(error);
        }
        return (String) response.getData();
    }

    @Override
    public boolean checkIfSeatReserved(Integer numberOfSeat, String tripId) {
        Request request = new Request.Builder()
                .type(RequestType.GET_CHECK_IF_SEAT_RESERVED)
                .data(new String[]{String.valueOf(numberOfSeat), tripId})
                .build();
        sendRequest(request);
        Response response = readResponse();
        if (response.getResponseType() == ResponseType.ERROR) {
            String error = response.getData().toString();
            throw new IllegalArgumentException(error);
        }
        return (boolean) response.getData();
    }

    @Override
    public Map<Integer, String> findAllSeatsWithCustomers(String tripID) {
        Request request = new Request.Builder()
                .type(RequestType.GET_ALL_RESERVATIONS)
                .data(String.valueOf(tripID))
                .build();
        sendRequest(request);
        Response response = readResponse();
        if (response.getResponseType() == ResponseType.ERROR) {
            String error = response.getData().toString();
            throw new IllegalArgumentException(error);
        }
        if (response.getData() instanceof Map) {
            try {
                return (Map<Integer, String>) response.getData();
            } catch (ClassCastException e) {
                throw new IllegalArgumentException("Received data is not of type Map<Integer, String>");
            }
        } else {
            throw new IllegalArgumentException("Received data is not a Map");
        }
    }


    @Override
    public void saveReservation(Reservation reservation) {
        Request request = new Request.Builder()
                .type(RequestType.BUY_TICKET)
                .data(reservation)
                .build();
        sendRequest(request);
        Response response = readResponse();
        if (response.getResponseType() == ResponseType.ERROR) {
            String error = response.getData().toString();
            throw new IllegalArgumentException(error);
        }
    }

    private void handleUpdate(Response response) {
        if (response.getResponseType() == ResponseType.UPDATE) {
            client.add_reservation();
        }
    }
    private boolean isUpdateResponse(Response response) {
        return response.getResponseType() == ResponseType.UPDATE;
    }

    private class ReaderThread implements Runnable {
        public void run() {
            while (!finished) {
                try {
                    Object response = input.readObject();
                    System.out.println("Response received " + response);
                    if (response instanceof Response) {
                        Response response1 = (Response) response;
                        if (isUpdateResponse(response1)) {
                            handleUpdate(response1);
                        } else {
                            try {
                                queueResponses.put((Response) response);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Reading error " + e);
                }
            }
        }
    }
}
