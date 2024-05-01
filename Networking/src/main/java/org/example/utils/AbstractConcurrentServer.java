package org.example.utils;

import java.net.Socket;

public abstract class AbstractConcurrentServer extends AbstractServer{

    public AbstractConcurrentServer(String port){
        super(port);
        System.out.println("AbstractConcurrentServer");
    }

    @Override
    protected void processRequest(Socket client) {
        Thread tw = createWorker(client);
        tw.start();
    }

    protected abstract Thread createWorker(Socket client);
}
