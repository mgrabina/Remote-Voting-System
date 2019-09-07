package ar.edu.itba.pod.services;

import ar.edu.itba.pod.constants.VotingDimension;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface QueryService extends Remote {
    Map<String, Double> getResults(VotingDimension dimension) throws RemoteException;
}
