package ar.edu.itba.pod.callbacks;

import java.rmi.RemoteException;

public interface InspectorCallback {
    void inspect() throws RemoteException;
}
