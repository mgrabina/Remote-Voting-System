package ar.edu.itba.pod.server;

import ar.edu.itba.pod.InspectionService;
import ar.edu.itba.pod.InspectorCallback;

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
