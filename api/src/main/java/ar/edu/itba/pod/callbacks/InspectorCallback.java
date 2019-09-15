package ar.edu.itba.pod.callbacks;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InspectorCallback extends Remote {
    void inspect() throws RemoteException;
}
