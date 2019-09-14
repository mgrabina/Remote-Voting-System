package ar.edu.itba.pod.client.helpers;

import java.rmi.RemoteException;

public interface VoteCreator {
    public void create(String table, String province, String firstVote, String secondVote, String thirdVote) throws RemoteException;
}
