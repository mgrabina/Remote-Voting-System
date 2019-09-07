package ar.edu.itba.pod;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AdministrationServer extends Remote {
    void openElections() throws RemoteException;
    void closeElections() throws RemoteException;
    ElectionsState getElectionsState() throws RemoteException;
}
