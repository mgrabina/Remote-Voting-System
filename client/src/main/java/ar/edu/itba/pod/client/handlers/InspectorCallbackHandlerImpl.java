package ar.edu.itba.pod.client.handlers;

import ar.edu.itba.pod.callbacks.InspectorCallback;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class InspectorCallbackHandlerImpl extends UnicastRemoteObject implements InspectorCallback {

    public InspectorCallbackHandlerImpl() throws RemoteException {
        super();
    }

    @Override
    public void inspect() throws RemoteException{
        System.out.println("INSPECTED");    // TODO: Implement
    }
}
