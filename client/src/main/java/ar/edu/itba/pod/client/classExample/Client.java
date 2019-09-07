package ar.edu.itba.pod.client.classExample;

import ar.edu.itba.pod.classExamples.GenericService;
import ar.edu.itba.pod.classExamples.User;
import ar.edu.itba.pod.classExamples.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Client {
    private static Logger logger = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) throws RemoteException, MalformedURLException, NotBoundException {
        GenericService gs = (GenericService) Naming.lookup("//127.0.0.1/genericServer");
        UserService us = (UserService) Naming.lookup("//127.0.0.1/userServer");
        logger.info(String.valueOf(gs.getVisitCount()));    // 0
        gs.addVisit();
        logger.info(String.valueOf(gs.getVisitCount()));    // 1
        User one = new User("1", "Juan");
        User two = new User("2", "Lito");
        us.pollUser(new UserAvailableCallbackHandlerImpl());
        us.offerUser(one);
        us.offerUser(two);
    }
}
