package ar.edu.itba.pod.services;

import ar.edu.itba.pod.constants.ElectionsState;
import ar.edu.itba.pod.constants.VotingDimension;
import ar.edu.itba.pod.exceptions.IllegalActionException;
import javafx.util.Pair;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.Optional;

public interface QueryService extends Remote {
    Pair<Map<String, Double>, ElectionsState> getResults(VotingDimension dimension, String filter) throws RemoteException, IllegalActionException;
}
