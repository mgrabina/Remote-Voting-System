package ar.edu.itba.pod.server.serviceClients;

import ar.edu.itba.pod.services.QueryService;
import ar.edu.itba.pod.constants.VotingDimension;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;

public class QueryServiceImpl extends UnicastRemoteObject implements QueryService {


    protected QueryServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public Map<String, Double> getResults(VotingDimension dimension) throws RemoteException {
        return null;
    }
}
