package org.example;

import org.example.interfaces.IEmployeeRepo;
import org.example.interfaces.IReservationRepo;
import org.example.interfaces.ITripRepo;
import org.example.server.ServicesImpl;
import org.example.utils.AbstractServer;
import org.example.utils.RpcConcurrentServer;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class StartRpcServer {
    private static int defaultPort = 55555;

    public static void main(String[] args) {
        Properties props = new Properties();

        try {
            props.load(new FileReader("C:\\Users\\Vasi\\Desktop\\Client-Server-JavaFX\\Server\\bd.properties"));
        } catch (IOException e) {
            System.out.println("Cannot find bd.config "+e);
        }

        Properties propertiesServer = new Properties();
        try{
            propertiesServer.load(new FileReader("C:\\Users\\Vasi\\Desktop\\Client-Server-JavaFX\\Server\\server.properties"));
        } catch (Exception e){
            throw new RuntimeException("Cannot find server properties " + e);
        }

        IEmployeeRepo employeeRepo = new EmployeeRepo(props);
        ITripRepo tripRepo = new TripRepo(props);
        IReservationRepo reservationRepo = new ResevationRepo(props);
        IService service = new ServicesImpl(employeeRepo, reservationRepo, tripRepo);

        String port = propertiesServer.getProperty("port");

        AbstractServer server = new RpcConcurrentServer(port, service);

        try{
            server.start();
        } catch (Exception e){
            System.out.println("Error starting the server " + e.getMessage());
        }
    }
}