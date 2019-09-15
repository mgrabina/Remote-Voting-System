package ar.edu.itba.pod.services;

import ar.edu.itba.pod.callbacks.InspectorCallback;
import ar.edu.itba.pod.exceptions.IllegalActionException;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InspectionService extends Remote {

    void registerInspector(String table, String party, InspectorCallback callback) throws RemoteException, IllegalActionException;
}
