package ar.edu.itba.pod.client.helpers;

import java.rmi.RemoteException;

public interface VoteCreator {
    void create(String table, String province, String firstVote, String secondVote, String thirdVote) throws RemoteException;
}
