package ar.edu.itba.pod.classExamples;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface UserAvailableCallbackHandler extends Remote {
    void userAvailable(User user) throws RemoteException;
}
