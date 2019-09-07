package ar.edu.itba.pod.server;

import ar.edu.itba.pod.services.AdministrationService;
import ar.edu.itba.pod.services.InspectionService;
import ar.edu.itba.pod.services.QueryService;
import ar.edu.itba.pod.services.VotingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
    private static Logger logger = LoggerFactory.getLogger(Server.class);


    /**
     *
     * Starts the server binding all the service's implementations
     *
     */
    public static void main(String[] args) throws RemoteException, MalformedURLException, NotBoundException {
        logger.info("Initializing services...");
        AdministrationService administrationService = new Servant();
        InspectionService inspectionService = new Servant();
        QueryService queryService = new Servant();
        VotingService votingService = new Servant();
        final Registry registry = LocateRegistry.getRegistry();
        registry.rebind("administrationService", administrationService);
        logger.info(" ... ");
        registry.rebind("inspectionService", inspectionService);
        logger.info(" ... ");
        registry.rebind("queryService", queryService);
        logger.info(" ... ");
        registry.rebind("votingService", votingService);
        logger.info(" DONE! ");
    }

}
