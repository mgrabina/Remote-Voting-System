package ar.edu.itba.pod.server;

import ar.edu.itba.pod.Vote;
import ar.edu.itba.pod.VotingServer;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class VotingServerImpl extends UnicastRemoteObject implements VotingServer {

    protected VotingServerImpl() throws RemoteException {
        super();
    }

    @Override
    public void vote(Vote vote) throws RemoteException {

    }
}
