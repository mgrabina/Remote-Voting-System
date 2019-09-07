package ar.edu.itba.pod.server;

import ar.edu.itba.pod.AdministrationService;
import ar.edu.itba.pod.ElectionsState;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class AdministrationServiceImpl extends UnicastRemoteObject implements AdministrationService {

    protected AdministrationServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public void openElections() throws RemoteException {

    }

    @Override
    public void closeElections() throws RemoteException {

    }

    @Override
    public ElectionsState getElectionsState() throws RemoteException {
        return null;
    }
}
