package ar.edu.itba.pod;

import ar.edu.itba.pod.User;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Optional;

public interface UserService extends Remote {
    void offerUser(User user) throws RemoteException;
    Optional<User> pollUser(UserAvailableCallbackHandler callback) throws RemoteException;
    int queueSize() throws RemoteException;
}
