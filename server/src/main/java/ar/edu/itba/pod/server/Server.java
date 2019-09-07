package ar.edu.itba.pod.server;

import ar.edu.itba.pod.server.serviceClients.AdministrationServiceImpl;
import ar.edu.itba.pod.server.serviceClients.InspectionServiceImpl;
import ar.edu.itba.pod.server.serviceClients.QueryServiceImpl;
import ar.edu.itba.pod.server.serviceClients.VotingServiceImpl;
import ar.edu.itba.pod.services.AdministrationService;
import ar.edu.itba.pod.services.InspectionService;
import ar.edu.itba.pod.services.QueryService;
import ar.edu.itba.pod.services.VotingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public static void main(String[] args) throws RemoteException {
        logger.info("Initializing services...");
        AdministrationService administrationService = new AdministrationServiceImpl();
        InspectionService inspectionService = new InspectionServiceImpl();
        QueryService queryService = new QueryServiceImpl();
        VotingService votingService = new VotingServiceImpl();
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
