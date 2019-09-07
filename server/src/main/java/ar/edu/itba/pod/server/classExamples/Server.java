package ar.edu.itba.pod.server.classExamples;

import ar.edu.itba.pod.classExamples.GenericService;
import ar.edu.itba.pod.classExamples.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
    private static Logger logger = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) throws RemoteException {
        System.out.println("Services initializing");
        GenericService gs = new GenericServiceImpl();
        UserService us = new UserServiceImpl();
        final Registry registry = LocateRegistry.getRegistry();
        registry.rebind("genericServer", gs);
        registry.rebind("userServer", us);
        System.out.println("Services bound");
    }
}
