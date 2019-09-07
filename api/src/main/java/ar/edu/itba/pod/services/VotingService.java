package ar.edu.itba.pod.services;

import ar.edu.itba.pod.models.Vote;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface VotingService extends Remote {

    void vote (Vote vote) throws RemoteException;
    List<Vote> getVotes() throws RemoteException;
}
