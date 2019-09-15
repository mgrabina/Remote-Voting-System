package ar.edu.itba.pod.services;

import ar.edu.itba.pod.constants.ElectionsState;
import ar.edu.itba.pod.exceptions.IllegalActionException;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AdministrationService extends Remote {
    void openElections() throws RemoteException, IllegalActionException;
    void closeElections() throws RemoteException, IllegalActionException;
    ElectionsState getElectionsState() throws RemoteException;
}
