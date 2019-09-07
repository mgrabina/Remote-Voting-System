package ar.edu.itba.pod.services;

import ar.edu.itba.pod.constants.VotingDimension;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.Optional;

public interface QueryService extends Remote {
    Map<String, Double> getResults(VotingDimension dimension, Optional<String> filter) throws RemoteException;
}
