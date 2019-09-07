package ar.edu.itba.pod.client.classExample;

import ar.edu.itba.pod.classExamples.User;
import ar.edu.itba.pod.classExamples.UserAvailableCallbackHandler;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class UserAvailableCallbackHandlerImpl extends UnicastRemoteObject implements UserAvailableCallbackHandler {

    public UserAvailableCallbackHandlerImpl() throws RemoteException {
        super();
    }

    @Override
    public void userAvailable(User user) throws RemoteException {
        System.out.println(user.getNombre());
    }
}
