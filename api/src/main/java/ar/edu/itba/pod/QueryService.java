package ar.edu.itba.pod;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface QueryService extends Remote {
    Map<String, Double> getResults(VotingDimension dimension) throws RemoteException;
}
