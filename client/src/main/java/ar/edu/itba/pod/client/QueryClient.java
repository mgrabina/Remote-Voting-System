package ar.edu.itba.pod.client;

import ar.edu.itba.pod.constants.Constants;
import ar.edu.itba.pod.constants.VotingDimension;
import ar.edu.itba.pod.services.AdministrationService;
import ar.edu.itba.pod.services.QueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class QueryClient {
    private static Logger logger = LoggerFactory.getLogger(QueryClient.class);

    public static void main(String[] args) throws RemoteException, MalformedURLException, NotBoundException {
        QueryService queryService = (QueryService) Naming.lookup(Constants.queryServiceHost + "queryService");

        // Can get results
        queryService.getResults(VotingDimension.NATIONAL, null);
    }

}
