package ar.edu.itba.pod.client.handlers;

import ar.edu.itba.pod.callbacks.InspectorCallback;
import ar.edu.itba.pod.models.Inspector;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class InspectorCallbackHandlerImpl extends UnicastRemoteObject implements InspectorCallback {

    private Inspector inspector;

    public InspectorCallbackHandlerImpl(Inspector inspector) throws RemoteException {
        super();
        this.inspector = inspector;
    }

    @Override
    public void inspect() throws RemoteException{
        System.out.println("INSPECTED for party " + inspector.getParty() + " and table " + inspector.getTable());
        // TODO: Implement
    }
}
