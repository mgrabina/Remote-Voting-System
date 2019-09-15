package ar.edu.itba.pod.services;

import ar.edu.itba.pod.exceptions.IllegalActionException;
import ar.edu.itba.pod.models.Vote;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VotingService extends Remote {

    void vote (Vote vote) throws RemoteException, IllegalActionException;
}
