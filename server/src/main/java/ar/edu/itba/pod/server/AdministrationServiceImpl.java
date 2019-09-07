package ar.edu.itba.pod.server;

import ar.edu.itba.pod.services.AdministrationService;
import ar.edu.itba.pod.constants.ElectionsState;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class AdministrationServiceImpl extends UnicastRemoteObject implements AdministrationService {

    private ElectionsState electionsState;

    protected AdministrationServiceImpl() throws RemoteException {
        super();
        this.electionsState = ElectionsState.NON_INITIALIZED;
    }

    @Override
    public void openElections() throws RemoteException {
        if (this.electionsState != ElectionsState.NON_INITIALIZED){
            throw new IllegalStateException("Elections currently running or finished.");
        }
        this.electionsState = ElectionsState.RUNNING;
    }

    @Override
    public void closeElections() throws RemoteException {
        if (this.electionsState != ElectionsState.RUNNING){
            throw new IllegalStateException("Elections not running.");
        }
        this.electionsState = ElectionsState.FINISHED;
    }

    @Override
    public ElectionsState getElectionsState() throws RemoteException {
        return this.electionsState;
    }
}
