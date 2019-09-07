package ar.edu.itba.pod.server;

import ar.edu.itba.pod.services.InspectionService;
import ar.edu.itba.pod.callbacks.InspectorCallback;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class InspectionServiceImpl extends UnicastRemoteObject implements InspectionService {

    protected InspectionServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public void registerInspector(InspectorCallback callback) throws RemoteException {

    }
}
