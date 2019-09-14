package ar.edu.itba.pod.services;

import ar.edu.itba.pod.callbacks.InspectorCallback;
import ar.edu.itba.pod.models.Vote;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InspectionService extends Remote {

    void alertInspector(Vote vote) throws RemoteException;
    void registerInspector(String table, String party, InspectorCallback callback) throws RemoteException, IllegalStateException;
}
