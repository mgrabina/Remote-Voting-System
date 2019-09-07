package ar.edu.itba.pod.client;

import ar.edu.itba.pod.constants.Constants;
import ar.edu.itba.pod.services.AdministrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class AdministrationClient {
    private static Logger logger = LoggerFactory.getLogger(AdministrationClient.class);

    public static void main(String[] args) throws RemoteException, MalformedURLException, NotBoundException {
        AdministrationService administrationService = (AdministrationService) Naming.lookup(Constants.administrationServiceHost + "administrationService");

        // Can manage elections
        administrationService.openElections();
    }

}
