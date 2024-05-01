package org.example.utils;

import org.example.IService;
import org.example.rpcProtocol.EmployeeRpcWorker;

import java.net.Socket;

public class RpcConcurrentServer extends AbstractConcurrentServer{

    private IService service;

    public RpcConcurrentServer(String port, IService service){
        super(port);
        this.service = service;
        System.out.println("RpcConcurrentServer");
    }

    @Override
    protected Thread createWorker(Socket client){
        EmployeeRpcWorker worker = new EmployeeRpcWorker(service, client);
        return new Thread(worker);
    }
}
