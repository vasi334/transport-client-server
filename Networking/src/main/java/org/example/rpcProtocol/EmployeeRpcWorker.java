package org.example.rpcProtocol;


import org.example.Employee;
import org.example.IEmployeeObserver;
import org.example.IService;
import org.example.Reservation;
import org.example.dtos.RezervationInfoDTO;
import org.example.rpcProtocol.request.Request;
import org.example.rpcProtocol.request.RequestType;
import org.example.rpcProtocol.response.Response;
import org.example.rpcProtocol.response.ResponseType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EmployeeRpcWorker implements Runnable, IEmployeeObserver {
    private IService server;
    private Socket connection;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private volatile boolean connected;

    public EmployeeRpcWorker(IService service, Socket connection){
        this.server = service;
        this.connection = connection;

        try{
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            connected = true;
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void run() {
        while(connected){
            try{
                Object request = input.readObject();
                Response response = handleRequest((Request) request);
                if (response != null){
                    sendResponse(response);
                }
            } catch (IOException | ClassNotFoundException e){
                e.printStackTrace();
            }

            try {
                Thread.sleep(1000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }

        try {
            input.close();
            output.close();
            connection.close();
        } catch (
                IOException e) {
            System.out.println("Error " + e);
        }
    }

    private static Response okResponse = new Response.Builder().responseType(ResponseType.OK).build();

    private Response handleRequest(Request request){
        Response response = null;

        if (request.getType() == RequestType.LOGIN) {
            System.out.println("Login request ...");
            Employee userDTO = (Employee) request.getData();
            try {
                var optional = server.login(userDTO.getEmail(), userDTO.getPassword(), this);
                if (optional.isPresent()) {
                    return new Response.Builder().responseType(ResponseType.OK).data(userDTO).build();
                }
                else{
                    connected = false;
                    return new Response.Builder().responseType(ResponseType.ERROR).data("Invalid username or password").build();
                }
            } catch (Exception e) {
                connected = false;
                return new Response.Builder().responseType(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }
        if (request.getType() == RequestType.LOGOUT) {
            System.out.println("Logout request ...");
            String username = (String) request.getData();
            try {
                server.logout(username);
                return okResponse;
            } catch (Exception e) {
                connected = false;
                return new Response.Builder().responseType(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }

        if (request.getType() == RequestType.SIGNUP) {
            System.out.println("Register request ...");
            return null;
        }

        if (request.getType() == RequestType.GET_TRIPS) {
            System.out.println("GetTrips request ...");
            try{
                return new Response.Builder().responseType(ResponseType.GET_BOOKED_SEATS).data(server.getAllTrips()).build();
            } catch (Exception e) {
                connected = false;
                return new Response.Builder().responseType(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }

        if (request.getType() == RequestType.GET_ALL_RESERVATIONS) {
            System.out.println("GetAllReservations request ...");
            try {
                String tripID = (String) request.getData();
                Map<Integer, String> seatsWithCustomers = server.findAllSeatsWithCustomers(tripID);
                return new Response.Builder().responseType(ResponseType.GET_ALL_RESERVATIONS).data(seatsWithCustomers).build();
            } catch (Exception e) {
                connected = false;
                return new Response.Builder().responseType(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }

        if (request.getType() == RequestType.GET_CHECK_IF_SEAT_RESERVED) {
            System.out.println("GetIfSeatReserved request ...");
            try{
                String noOfSeat = ((String[]) request.getData())[0];
                String tripId = ((String[]) request.getData())[1];
                return new Response.Builder().responseType(ResponseType.GET_CHECK_IF_SEAT_RESERVED).data(server.checkIfSeatReserved(Integer.valueOf(noOfSeat), tripId)).build();
            } catch (Exception e) {
                connected = false;
                return new Response.Builder().responseType(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }

        if(request.getType() == RequestType.GET_CUSTOMER_NAME){
            System.out.println("GetCustomerName request ...");
            try{
                String tripId = ((String[]) request.getData())[0];
                String parseInt = ((String[]) request.getData())[1];
                return new Response.Builder().responseType(ResponseType.GET_BOOKED_SEATS).data(server.findCustomerNameForSeat(tripId, Integer.parseInt(parseInt))).build();
            } catch (Exception e) {
                connected = false;
                return new Response.Builder().responseType(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }

        if(request.getType() == RequestType.GET_BOOKED_SEATS){
            System.out.println("GetBookedSeats request ...");
            try{
                String tripId = (String) request.getData();
                return new Response.Builder().responseType(ResponseType.GET_BOOKED_SEATS).data(server.findBookedSeatsForTrip(tripId)).build();
            } catch (Exception e) {
                connected = false;
                return new Response.Builder().responseType(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }
        if (request.getType() == RequestType.GET_TRIP) {
            System.out.println("GetTrip request ...");
            try {
                String destination = ((String[]) request.getData())[0];
                String date = ((String[]) request.getData())[1];
                String time = ((String[]) request.getData())[2];
                return new Response.Builder().responseType(ResponseType.GET_TRIP).data(server.findTripByDestinationDateTime(destination, date, time)).build();
            } catch (Exception e) {
                connected = false;
                return new Response.Builder().responseType(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }
        if (request.getType() == RequestType.GET_TRIP_ID) {
            System.out.println("GetTripID request ...");
            try {
                String destination = ((String[]) request.getData())[0];
                String date = ((String[]) request.getData())[1];
                String time = ((String[]) request.getData())[2];
                return new Response.Builder().responseType(ResponseType.GET_TRIP_ID).data(server.findTripIdByDestinationDateTime(destination, date, time)).build();
            } catch (Exception e) {
                connected = false;
                return new Response.Builder().responseType(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }

        if (request.getType() == RequestType.BUY_TICKET) {
            System.out.println("Buy ticket request ...");
            Reservation reservation = (Reservation) request.getData();
            try {
                server.saveReservation(reservation);
                return okResponse;
            } catch (Exception e) {
                connected = false;
                return new Response.Builder().responseType(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }
        return response;
    }

    private void sendResponse(Response response) {
        try {
            output.writeObject(response);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void add_reservation() {
        Response response = new Response.Builder().responseType(ResponseType.UPDATE).data(null).build();
        sendResponse(response);
    }
}
