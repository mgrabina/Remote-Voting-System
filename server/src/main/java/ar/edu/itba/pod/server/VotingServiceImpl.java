package ar.edu.itba.pod.server;

import ar.edu.itba.pod.models.Vote;
import ar.edu.itba.pod.services.VotingService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class VotingServiceImpl extends UnicastRemoteObject implements VotingService {

    protected VotingServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public void vote(Vote vote) throws RemoteException {

    }
}
