package ar.edu.itba.pod.server;

import ar.edu.itba.pod.InspectionServer;
import ar.edu.itba.pod.InspectorCallback;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class InspectionServiceImpl extends UnicastRemoteObject implements InspectionServer {

    protected InspectionServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public void regiterInspector(InspectorCallback callback) throws RemoteException {

    }
}
