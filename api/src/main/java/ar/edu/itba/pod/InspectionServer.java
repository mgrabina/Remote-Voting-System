package ar.edu.itba.pod;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InspectionServer extends Remote {

    void registerInspector(InspectorCallback callback) throws RemoteException;
}
