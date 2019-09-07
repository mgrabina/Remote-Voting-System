package ar.edu.itba.pod;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VotingService extends Remote {

    void vote (Vote vote) throws RemoteException;
}
