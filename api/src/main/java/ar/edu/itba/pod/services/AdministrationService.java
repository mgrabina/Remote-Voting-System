package ar.edu.itba.pod.services;

import ar.edu.itba.pod.constants.ElectionsState;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AdministrationService extends Remote {
    void openElections() throws RemoteException, IllegalStateException;
    void closeElections() throws RemoteException, IllegalStateException;
    ElectionsState getElectionsState() throws RemoteException;
}
