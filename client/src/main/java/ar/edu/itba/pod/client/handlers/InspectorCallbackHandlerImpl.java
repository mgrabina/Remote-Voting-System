package ar.edu.itba.pod.client.handlers;

import ar.edu.itba.pod.callbacks.InspectorCallback;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class InspectorCallbackHandlerImpl extends UnicastRemoteObject implements InspectorCallback {

    private String table;
    private String party;

    public InspectorCallbackHandlerImpl(String table, String party) throws RemoteException {

        super();
        this.table = table;
        this.party = party;
    }

    @Override
    public void inspect() throws RemoteException{
        System.out.println("New vote for " + this.party + " on polling place " + this.table);
    }
}
