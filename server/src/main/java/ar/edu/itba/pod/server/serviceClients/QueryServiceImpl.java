package ar.edu.itba.pod.server.serviceClients;

import ar.edu.itba.pod.constants.Constants;
import ar.edu.itba.pod.constants.ElectionsState;
import ar.edu.itba.pod.constants.VotingSystems;
import ar.edu.itba.pod.services.AdministrationService;
import ar.edu.itba.pod.services.QueryService;
import ar.edu.itba.pod.constants.VotingDimension;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.Optional;

public class QueryServiceImpl extends UnicastRemoteObject implements QueryService {

    private AdministrationService administrationService;


    public QueryServiceImpl() throws RemoteException, MalformedURLException, NotBoundException {
        super();
        this.administrationService = (AdministrationService) Naming.lookup(Constants.administrationServiceHost + "administrationService");
    }

    @Override
    public Map<String, Double> getResults(VotingDimension dimension, Optional<String> filter) throws RemoteException {
        switch (administrationService.getElectionsState()){
            case NON_INITIALIZED: throw new IllegalStateException("Elections didn't started yet.");
            case RUNNING: return calculateResults(dimension, VotingSystems.FPTP, filter);
            case FINISHED:
                switch (dimension){
                    case NATIONAL: return calculateResults(dimension, VotingSystems.AV, filter);
                    case PROVINCE: return calculateResults(dimension, VotingSystems.STV, filter);
                    case TABLE: return calculateResults(dimension, VotingSystems.FPTP, filter);
                    default: throw new IllegalStateException("Invalid Dimension.");
                }
            default: throw new IllegalStateException("Invalid Election State.");
        }
    }

    private Map<String, Double> calculateResults(VotingDimension dimension, VotingSystems system, Optional<String> filter){
        if (dimension == VotingDimension.NATIONAL && filter.isPresent()){
            throw new IllegalArgumentException("Unnecessary argument for National Voting.");
        }

        // Calculate results

        return null;
    }


}
